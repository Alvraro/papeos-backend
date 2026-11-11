package com.alvraro.papeos_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alvraro.papeos_backend.model.Alimento;

import lombok.NonNull;

public interface AlimentoRepository extends JpaRepository<Alimento, Integer> {
	List<Alimento> findByNombreContainingIgnoreCase(String partialName);

	Optional<Alimento> findByNombre(@NonNull String nombre);
}
