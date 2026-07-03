package com.duoc.plataforma_educativa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.plataforma_educativa.model.ResumenCompra;
import com.duoc.plataforma_educativa.repository.ResumenCompraRepository;

/**
 * Controlador de solo lectura para verificar los resúmenes de compra que el
 * CONSUMIDOR de la cola RabbitMQ ha guardado de forma asíncrona en la tabla
 * RESUMEN_COMPRA (Oracle Cloud). Útil para evidenciar en el video que el
 * mensaje efectivamente fue consumido y persistido.
 *
 *  GET /resumenes-compra          -> lista todos los resúmenes procesados
 *  GET /resumenes-compra/{id}     -> obtiene un resumen puntual
 */
@RestController
@RequestMapping("/resumenes-compra")
public class ResumenCompraController {

    private final ResumenCompraRepository resumenCompraRepository;

    public ResumenCompraController(ResumenCompraRepository resumenCompraRepository) {
        this.resumenCompraRepository = resumenCompraRepository;
    }

    @GetMapping
    public ResponseEntity<List<ResumenCompra>> listar() {
        return ResponseEntity.ok(resumenCompraRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        return resumenCompraRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No existe un resumen de compra con id " + id));
    }
}