package com.duoc.plataforma_educativa.service;

import com.duoc.plataforma_educativa.model.Curso;
import com.duoc.plataforma_educativa.model.Inscripcion;
import com.duoc.plataforma_educativa.repository.InscripcionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que encapsula todas las operaciones sobre AWS S3:
 *   - Generar contenido del resumen de inscripción como texto
 *   - Subir archivo al bucket
 *   - Descargar archivo desde el bucket
 *   - Reemplazar archivo en el bucket
 *   - Eliminar archivo del bucket
 */
@Service
public class S3Service {

    private final S3Client s3Client;
    private final InscripcionRepository inscripcionRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public S3Service(S3Client s3Client, InscripcionRepository inscripcionRepository) {
        this.s3Client               = s3Client;
        this.inscripcionRepository  = inscripcionRepository;
    }

    // ── Key S3 ─────────────────────────────────────────────────────────────────

    /**
     * Construye la key S3 para una inscripción:
     * resumen-{id}/resumen_inscripcion_{id}.txt
     */
    public String buildS3Key(Long id) {
        return String.format("%d/resumen_inscripcion_%d.txt", id, id);
    }

    // ── Generar contenido del resumen ──────────────────────────────────────────

    /**
     * Genera el contenido textual del resumen de inscripción.
     * Se usa tanto para guardar en disco como para subir a S3.
     */
    public String generarContenidoResumen(Inscripcion ins) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================\n");
        sb.append("   RESUMEN DE INSCRIPCIÓN - PLATAFORMA EDUCATIVA\n");
        sb.append("================================================\n\n");
        sb.append(String.format("ID de Inscripción : %d%n", ins.getId()));
        sb.append(String.format("Estudiante        : %s%n", ins.getNombreEstudiante()));
        sb.append(String.format("Fecha             : %s%n",
                ins.getFechaInscripcion().format(FORMATTER)));
        sb.append("\n------------------------------------------------\n");
        sb.append("CURSOS INSCRITOS:\n");
        sb.append("------------------------------------------------\n");

        for (Curso curso : ins.getCursos()) {
            sb.append(String.format("  - Nombre     : %s%n", curso.getNombre()));
            sb.append(String.format("    Instructor : %s%n", curso.getInstructor()));
            sb.append(String.format("    Duración   : %d horas%n", curso.getDuracion()));
            sb.append(String.format("    Costo      : $%.2f%n", curso.getCosto()));
            sb.append("\n");
        }

        sb.append("------------------------------------------------\n");
        sb.append(String.format("TOTAL A PAGAR     : $%.2f%n", ins.getTotalPagar()));
        sb.append("================================================\n");
        return sb.toString();
    }

    // ── Generar archivo físico ─────────────────────────────────────────────────

    /**
     * Genera un archivo .txt físico en el sistema de archivos temporal del servidor
     * y retorna el Path para que el controller pueda servirlo como descarga.
     */
    public Path generarArchivoFisico(Long id) throws IOException {
        Inscripcion ins = obtenerInscripcion(id);
        String contenido = generarContenidoResumen(ins);

        Path tempFile = Files.createTempFile(
                "resumen_inscripcion_" + id + "_", ".txt");
        Files.writeString(tempFile, contenido, StandardCharsets.UTF_8);
        return tempFile;
    }

    // ── Subir a S3 ─────────────────────────────────────────────────────────────

    /**
     * Genera el resumen de la inscripción y lo sube al bucket S3.
     * Key: resumen-{id}/resumen_inscripcion_{id}.txt
     */
    public String subirResumen(Long id) {
        Inscripcion ins = obtenerInscripcion(id);
        String contenido = generarContenidoResumen(ins);
        String key = buildS3Key(id);

        byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain; charset=utf-8")
                .contentLength((long) bytes.length)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));

        return String.format("Resumen de inscripción %d subido exitosamente. Key: %s", id, key);
    }

    // ── Descargar desde S3 ─────────────────────────────────────────────────────

    /**
     * Descarga el archivo de resumen desde S3 y retorna sus bytes.
     * Lanza NoSuchKeyException si el archivo no existe.
     */
    public byte[] descargarResumen(Long id) {
        String key = buildS3Key(id);
        validarExistencia(key, id);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request)) {
            return response.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Error al leer el archivo desde S3 para la inscripción " + id, e);
        }
    }

    // ── Reemplazar en S3 ───────────────────────────────────────────────────────

    /**
     * Reemplaza el archivo de resumen en S3 con el contenido del MultipartFile recibido.
     * Usa la misma key para sobreescribir el objeto existente.
     */
    public String reemplazarResumen(Long id, MultipartFile archivo) throws IOException {
        // Validar que la inscripción exista en BD
        obtenerInscripcion(id);
        String key = buildS3Key(id);

        byte[] bytes = archivo.getBytes();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain; charset=utf-8")
                .contentLength((long) bytes.length)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));

        return String.format("Resumen de inscripción %d reemplazado exitosamente. Key: %s", id, key);
    }

    // ── Eliminar de S3 ─────────────────────────────────────────────────────────

    /**
     * Elimina el archivo de resumen de S3.
     * Valida primero que el objeto exista; si no, lanza IllegalStateException (→ 404).
     */
    public String eliminarResumen(Long id) {
        String key = buildS3Key(id);
        validarExistencia(key, id);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(request);

        return String.format("Resumen de inscripción %d eliminado exitosamente del bucket.", id);
    }

    // ── Listar de S3 ──────────────────────────────────────────────────────────

    /**
     * Lista todos los archivos (keys) existentes dentro del bucket S3.
     */
    public List<String> listarArchivosS3() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        
        return response.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Busca la inscripción en la base de datos; lanza excepción si no existe.
     */
    private Inscripcion obtenerInscripcion(Long id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una inscripción con ID: " + id));
    }

    /**
     * Verifica que un objeto exista en S3 usando HeadObject.
     * Si no existe (HTTP 404), lanza IllegalStateException con mensaje descriptivo.
     * Si ocurre otro error de S3, lo relanza.
     */
    private void validarExistencia(String key, Long id) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.headObject(headRequest);
        } catch (S3Exception e) {
            // El SDK v2 devuelve un status 404 dentro de S3Exception cuando el objeto no existe en un headObject
            if (e.statusCode() == 404) {
                throw new IllegalStateException(
                        "No se encontró el resumen de la inscripción " + id +
                        " en S3. Primero debe subirlo usando POST /inscripciones/" + id + "/resumen/subir");
            }
            // Si es otro error (ej. credenciales inválidas, bucket no existe), relanzamos la excepción
            throw e;
        }
    }
}