package com.alvraro.papeos_backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.alvraro.papeos_backend.model.Alimento;
import com.alvraro.papeos_backend.repository.AlimentoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = PapeosBackendApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class PapeosBackendApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private AlimentoRepository alimentoRepository;

	@Test
	public void contextLoads() {
	}

	@AfterEach
	public void cleanUp() {
		alimentoRepository.deleteAll();
	}

	// Create
	@Test
	public void givenAlimentos_whenCreateAlimento_thenStatus200() throws Exception {
		final String nombreAlimento = "Nombre alimento test";

		mvc.perform(MockMvcRequestBuilders.post("/alimentos/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"" + nombreAlimento + "\"}"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void givenAlimentos_whenCreateAlimentoWithExistingNombre_thenStatus400() throws Exception {
		final String nombreAlimento = "Nombre alimento test";
		createTestAlimento(nombreAlimento);

		mvc.perform(MockMvcRequestBuilders.post("/alimentos/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"" + nombreAlimento + "\"}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().string("Alimento with the same nombre already exists"));
	}

	// Get
	@Test
	public void givenAlimentos_whenGetAlimentos_thenStatus200() throws Exception {
		final String nombreAlimento = "Nombre alimento test";
		Alimento alimento = createTestAlimento(nombreAlimento);
		
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/get")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", alimento.getId().toString()))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value(nombreAlimento))
		.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(alimento.getId()));
	}
	
	@Test
	public void givenAlimentos_whenGetAlimentosWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/get")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", "999"))
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	// Search
	@Test
	public void givenAlimentos_whenSearchAlimentos_thenStatus200() throws Exception {
		final String nombreAlimento = "Nombre alimento test";
		Alimento alimento = createTestAlimento(nombreAlimento);
		
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/search")
				.contentType(MediaType.APPLICATION_JSON)
				.content(nombreAlimento.substring(0, 6)))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value(nombreAlimento))
		.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(alimento.getId()));
	}
	
	@Test
	public void givenAlimentos_whenSearchAlimentosWithNonExistingPartialName_thenStatus200AndEmptyList() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/search")
				.contentType(MediaType.APPLICATION_JSON)
				.content("NonExistingPartialName"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
	}
	
	// Update
	@Test
	public void givenAlimentos_whenUpdateAlimento_thenStatus200() throws Exception {
		final String nombreAlimento = "Nombre alimento test";
		Alimento alimento = createTestAlimento(nombreAlimento);
		alimento.setNombre("Updated nombre");
		
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":" + alimento.getId() + ", \"nombre\":\"" + alimento.getNombre() + "\"}"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value(alimento.getNombre()))
		.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(alimento.getId()));
	}
	
	@Test
	public void givenAlimentos_whenUpdateAlimentoWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":999, \"nombre\":\"Updated nombre\"}"))
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	// Delete
	@Test
	public void givenAlimentos_whenDeleteAlimento_thenStatus200() throws Exception {
		final String nombreAlimento = "Nombre alimento test";
		Alimento alimento = createTestAlimento(nombreAlimento);
		
		mvc.perform(MockMvcRequestBuilders.delete("/alimentos/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", alimento.getId().toString()))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("Alimento deleted successfully"));
	}
	
	@Test
	public void givenAlimentos_whenDeleteAlimentoWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/alimentos/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", "999"))
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	private Alimento createTestAlimento(String nombre) {
		Alimento alimento = new Alimento(nombre);
		return alimentoRepository.save(alimento);
	}	
}