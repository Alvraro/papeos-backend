package com.alvraro.papeos_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alvraro.papeos_backend.model.Plato;

public interface PlatoRepository extends JpaRepository<Plato, Integer> {
	List<Plato> findByNombreContainingIgnoreCase(String partialName);
}