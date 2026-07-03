package com.duoc.plataforma_educativa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duoc.plataforma_educativa.model.ResumenCompra;

@Repository
public interface ResumenCompraRepository extends JpaRepository<ResumenCompra, Long> {
}