package com.alvraro.papeos_backend.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "alimentos")
public class Alimento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, nullable = false)
	@NonNull
	private String nombre;

	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private CategoriaAlimento categoria;
	
	@ManyToMany
	@JoinTable(
		name = "alimentos_platos",
		joinColumns = @JoinColumn(name = "alimento_id"),
		inverseJoinColumns = @JoinColumn(name = "plato_id")
	)
	private List<Plato> platos;
}
