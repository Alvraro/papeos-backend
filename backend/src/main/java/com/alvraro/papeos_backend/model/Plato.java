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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "platos")
public class Plato {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String nombre;

	@Column
	private String descripcion;
	
	@ManyToMany
	@JoinTable(
		name = "alimentos_platos",
		joinColumns = @JoinColumn(name = "plato_id"),
		inverseJoinColumns = @JoinColumn(name = "alimento_id")
	)
	private List<Alimento> alimentos;
	
	@ManyToMany
	@JoinTable(
		name = "comidas_platos",
		joinColumns = @JoinColumn(name = "plato_id"),
		inverseJoinColumns = @JoinColumn(name = "comida_id")
	)
	private List<Comida> comidas;
}
