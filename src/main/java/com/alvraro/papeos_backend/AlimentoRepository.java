package com.alvraro.papeos_backend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlimentoRepository extends JpaRepository<Alimento, Integer> {
	List<Alimento> findByNombreContainingIgnoreCase(String nombre);
}
