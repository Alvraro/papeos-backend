package com.alvraro.papeos_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alvraro.papeos_backend.model.Alimento;

public interface AlimentoRepository extends JpaRepository<Alimento, Integer> {
	List<Alimento> findByNombreContainingIgnoreCase(String partialName);
}
