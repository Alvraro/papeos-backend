package com.alvraro.papeos_backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alimentos")
public class Alimento {
	@Id
	private Integer id;
	
	@Column(name = "nombre_alimento")
	private String nombre;
	
	protected Alimento() {}

	public Alimento(Integer id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "Alimento [id=" + id + ", nombre=" + nombre + "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
