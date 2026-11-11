package com.alvraro.papeos_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alvraro.papeos_backend.model.Alimento;
import com.alvraro.papeos_backend.repository.AlimentoRepository;

@RestController
@RequestMapping("/alimentos")
public class AlimentoController {
	@Autowired
	private AlimentoRepository alimentoRepository;

	@PostMapping("/create")
	public ResponseEntity<String> createAlimento(@RequestBody Alimento alimento) {
		return alimentoRepository.findByNombre(alimento.getNombre()).map(_ -> {
			return ResponseEntity.badRequest().body("Alimento with the same nombre already exists");
		}).orElseGet(() -> {
			alimentoRepository.save(alimento);
			return ResponseEntity.ok().build();
		});
	}

	@PostMapping("/get")
	public ResponseEntity<Alimento> getAlimentoById(@RequestParam Integer id) {
		return alimentoRepository
			.findById(id)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/search")
	public ResponseEntity<List<Alimento>> findByNombreContainingIgnoreCase(@RequestBody String partialName) {
		List<Alimento> listAlimentos = alimentoRepository.findByNombreContainingIgnoreCase(partialName);
		return ResponseEntity.ok(listAlimentos);
	}

	@PostMapping("/update")
	public ResponseEntity<Alimento> updateAlimento(@RequestBody Alimento alimento) {
		return alimentoRepository.findById(alimento.getId())
			.map(_ -> {
				Alimento updatedAlimento = alimentoRepository.save(alimento);
				return ResponseEntity.ok(updatedAlimento);
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteAlimento(Integer id) {
		return alimentoRepository.findById(id)
			.map(alimento -> {
				alimentoRepository.delete(alimento);
				return ResponseEntity.ok("Alimento deleted successfully");
			})
			.orElseGet(() -> ResponseEntity.notFound().build());
	}
}
