-- =============================================
-- CURSOS (Datos Maestros - Estos sí deben cargarse)
-- =============================================
INSERT INTO CURSOS (id, nombre, instructor, duracion, costo)
VALUES (1, 'Desarrollo Web con Java', 'Carlos López', 40, 150000.00);

INSERT INTO CURSOS (id, nombre, instructor, duracion, costo)
VALUES (2, 'Spring Boot Avanzado', 'Ana Martínez', 30, 120000.00);

INSERT INTO CURSOS (id, nombre, instructor, duracion, costo)
VALUES (3, 'Cloud Native con AWS', 'Pedro Soto', 20, 90000.00);

-- =============================================
-- INSCRIPCIONES 
-- (Comentadas para que H2 genere los IDs desde el inicio sin chocar)
-- =============================================
--INSERT INTO INSCRIPCIONES (id, nombre_estudiante, fecha_inscripcion, total_pagar)
--VALUES (1, 'María González', '2025-05-30T10:00:00', 270000.00);

--INSERT INTO INSCRIPCIONES (id, nombre_estudiante, fecha_inscripcion, total_pagar)
--VALUES (2, 'Juan Pérez', '2025-05-30T11:00:00', 210000.00);

--INSERT INTO INSCRIPCIONES (id, nombre_estudiante, fecha_inscripcion, total_pagar)
--VALUES (3, 'Valentina Torres', '2025-05-30T12:00:00', 90000.00);

-- =============================================
-- RELACIÓN INSCRIPCION - CURSOS 
-- (Comentadas porque dependen de las inscripciones de arriba)
-- =============================================
-- INSERT INTO INSCRIPCION_CURSOS (inscripcion_id, curso_id) VALUES (1, 1);
-- INSERT INTO INSCRIPCION_CURSOS (inscripcion_id, curso_id) VALUES (1, 2);

-- INSERT INTO INSCRIPCION_CURSOS (inscripcion_id, curso_id) VALUES (2, 2);
-- INSERT INTO INSCRIPCION_CURSOS (inscripcion_id, curso_id) VALUES (2, 3);

-- INSERT INTO INSCRIPCION_CURSOS (inscripcion_id, curso_id) VALUES (3, 3);
