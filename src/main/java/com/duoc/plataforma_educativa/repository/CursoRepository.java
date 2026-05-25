package com.duoc.plataforma_educativa.repository;

import com.duoc.plataforma_educativa.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
}
