package com.alvraro.papeos_backend.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alvraro.papeos_backend.model.Comida;

public interface ComidaRepository extends JpaRepository<Comida, Integer> {
	List<Comida> findByFechaBetween(Date minDate, Date maxDate);
}
