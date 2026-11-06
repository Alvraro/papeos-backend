package com.alvraro.papeos_backend.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "comidas")
public class Comida {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer id;

	@Column
	Date fecha;
	
	@Column(columnDefinition = "'comida','cena'")
	@Enumerated(EnumType.STRING)
	TipoComida tipo;
	
	@ManyToMany
	@JoinTable(
		name = "comidas_platos",
		joinColumns = @JoinColumn(name = "comida_id"),
		inverseJoinColumns = @JoinColumn(name = "plato_id")
	)
	private List<Plato> platos;
}
