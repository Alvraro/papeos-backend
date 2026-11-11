package com.alvraro.papeos_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvraro.papeos_backend.model.Plato;
import com.alvraro.papeos_backend.repository.PlatoRepository;

@RestController
@RequestMapping("/platos")
public class PlatoController {
	@Autowired
	private PlatoRepository platoRepository;
	
	@PostMapping("/create")
	public ResponseEntity<Plato> createPlato(@RequestBody Plato plato) {
		Plato savedPlato = platoRepository.save(plato);
		return ResponseEntity.ok(savedPlato);
	}

	@PostMapping("/get")
	public ResponseEntity<Plato> getPlatoById(Integer id) {
		return platoRepository
			.findById(id)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/get-all")
	public ResponseEntity<List<Plato>> getAllPlatos() {
		return ResponseEntity.ok(platoRepository.findAll());
	}

	@PostMapping("/search")
	public ResponseEntity<List<Plato>> findByNombreContainingIgnoreCase(@RequestBody String partialName) {
		if(partialName == null || partialName.isBlank())
			return ResponseEntity.ok(platoRepository.findAll());
		return ResponseEntity.ok(platoRepository.findByNombreContainingIgnoreCase(partialName));
	}

	@PostMapping("/update")
	public ResponseEntity<Plato> updatePlato(@RequestBody Plato plato) {
		return platoRepository.findById(plato.getId())
			.map(_ -> {
				Plato updatedPlato = platoRepository.save(plato);
				return ResponseEntity.ok(updatedPlato);
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deletePlato(Integer id) {
		return platoRepository.findById(id)
			.map(plato -> {
				platoRepository.delete(plato);
				return ResponseEntity.ok("Plato deleted successfully");
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
