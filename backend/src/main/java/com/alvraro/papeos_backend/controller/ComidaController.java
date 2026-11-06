package com.alvraro.papeos_backend.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alvraro.papeos_backend.model.Comida;
import com.alvraro.papeos_backend.repository.ComidaRepository;

@RestController
@RequestMapping("/comidas")
public class ComidaController {
	@Autowired
	private ComidaRepository comidaRepository;
	
	@PostMapping("/create")
	public ResponseEntity<Comida> createComida(@RequestBody Comida comida) {
		Comida savedComida = comidaRepository.save(comida);
		return ResponseEntity.ok(savedComida);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<Comida> getComidaById(@RequestParam Integer id) {
		return comidaRepository
			.findById(id)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@PostMapping("/get")
	public ResponseEntity<List<Comida>> getComidaByDateInterval(@RequestParam Date desde, @RequestParam Date hasta) {
		return ResponseEntity.ok(comidaRepository.findByFechaBetween(desde, hasta));
	}

	@PostMapping("/update")
	public ResponseEntity<Comida> updateComida(@RequestBody Comida comida) {
		return comidaRepository.findById(comida.getId())
			.map(_ -> {
				Comida updatedComida = comidaRepository.save(comida);
				return ResponseEntity.ok(updatedComida);
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteComida(Integer id) {
		return comidaRepository.findById(id)
			.map(Comida -> {
				comidaRepository.delete(Comida);
				return ResponseEntity.ok("Comida deleted successfully");
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}	
}
