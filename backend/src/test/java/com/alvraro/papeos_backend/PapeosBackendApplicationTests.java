package com.alvraro.papeos_backend;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.alvraro.papeos_backend.model.Comida;
import com.alvraro.papeos_backend.model.Plato;
import com.alvraro.papeos_backend.model.TipoComida;
import com.alvraro.papeos_backend.repository.AlimentoRepository;
import com.alvraro.papeos_backend.repository.ComidaRepository;
import com.alvraro.papeos_backend.repository.PlatoRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = PapeosBackendApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class PapeosBackendApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private AlimentoRepository alimentoRepository;

	@Autowired
	private PlatoRepository platoRepository;

	@Autowired
	private ComidaRepository comidaRepository;

	@AfterEach
	void cleanUp() {
		alimentoRepository.deleteAll();
		platoRepository.deleteAll();
		comidaRepository.deleteAll();
	}

	private Alimento createTestAlimento(String nombre) {
		Alimento alimento = new Alimento(nombre);
		return alimentoRepository.save(alimento);
	}

	private Plato createTestPlato(String nombre, String descripcion) {
		Plato plato = new Plato();
		plato.setNombre(nombre);
		plato.setDescripcion(descripcion);
		return platoRepository.save(plato);
	}

	private Comida createTestComida(Date fecha, TipoComida tipo) {
		Comida comida = new Comida();
		comida.setFecha(fecha);
		comida.setTipo(tipo);
		return comidaRepository.save(comida);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void givenAlimentos_whenCreateAlimento_thenStatus200() throws Exception {
		final String nombreAlimento = "Nombre alimento test";

		mvc.perform(MockMvcRequestBuilders.post("/alimentos/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"" + nombreAlimento + "\"}"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	void givenAlimentos_whenCreateAlimentoWithExistingNombre_thenStatus400() throws Exception {
		final String nombreAlimento = "Nombre alimento test";
		createTestAlimento(nombreAlimento);

		mvc.perform(MockMvcRequestBuilders.post("/alimentos/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"" + nombreAlimento + "\"}"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().string("Alimento with the same nombre already exists"));
	}

	@Test
	void givenAlimentos_whenGetAlimentos_thenStatus200() throws Exception {
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
	void givenAlimentos_whenGetAlimentosWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/get")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", "999"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void givenAlimentos_whenGetAllAlimentos_thenStatus200() throws Exception {
		final String nombreAlimento1 = "Nombre alimento test 1";
		final String nombreAlimento2 = "Nombre alimento test 2";
		createTestAlimento(nombreAlimento1);
		createTestAlimento(nombreAlimento2);

		mvc.perform(MockMvcRequestBuilders.post("/alimentos/get-all")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value(nombreAlimento1))
			.andExpect(MockMvcResultMatchers.jsonPath("$[1].nombre").value(nombreAlimento2));
	}

	@Test
	void givenAlimentos_whenGetAllAlimentosWithNoAlimentos_thenStatus200AndEmpty() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/get-all")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
	}

	@Test
	void givenAlimentos_whenSearchAlimentos_thenStatus200() throws Exception {
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
	void givenAlimentos_whenSearchAlimentosWithNonExistingPartialName_thenStatus200AndEmptyList() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/search")
				.contentType(MediaType.APPLICATION_JSON)
				.content("NonExistingPartialName"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
	}

	@Test
	void givenAlimentos_whenSearchAlimentosWithBlankPartialName_thenStatus200AndAllAl() throws Exception {
		final String nombreAlimento1 = "Nombre alimento test 1";
		final String nombreAlimento2 = "Nombre alimento test 2";
		createTestAlimento(nombreAlimento1);
		createTestAlimento(nombreAlimento2);

		mvc.perform(MockMvcRequestBuilders.post("/alimentos/search")
				.contentType(MediaType.APPLICATION_JSON)
				.content(""))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value(nombreAlimento1))
			.andExpect(MockMvcResultMatchers.jsonPath("$[1].nombre").value(nombreAlimento2));
	}

	@Test
	void givenAlimentos_whenUpdateAlimento_thenStatus200() throws Exception {
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
	void givenAlimentos_whenUpdateAlimentoWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/alimentos/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":999, \"nombre\":\"Updated nombre\"}"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void givenAlimentos_whenDeleteAlimento_thenStatus200() throws Exception {
		final String nombreAlimento = "Nombre alimento test";
		Alimento alimento = createTestAlimento(nombreAlimento);

		mvc.perform(MockMvcRequestBuilders.delete("/alimentos/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", alimento.getId().toString()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string("Alimento deleted successfully"));
	}

	@Test
	void givenAlimentos_whenDeleteAlimentoWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/alimentos/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", "999"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void givenPlatos_whenCreatePlato_thenStatus200() throws Exception {
		final String nombrePlato = "Nombre plato test";

		mvc.perform(MockMvcRequestBuilders.post("/platos/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"" + nombrePlato + "\",\"descripcion\":\"Descripcion plato test\"}"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value(nombrePlato))
				.andExpect(MockMvcResultMatchers.jsonPath("$.descripcion").value("Descripcion plato test"));
	}

	@Test
	void givenPlatos_whenGetPlatoById_thenStatus200() throws Exception {
		final String nombrePlato = "Nombre plato test";
		Plato plato = createTestPlato(nombrePlato, "Descripcion plato test");

		mvc.perform(MockMvcRequestBuilders.post("/platos/get")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", plato.getId().toString()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value(nombrePlato))
			.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(plato.getId()));
	}

	@Test
	void givenPlatos_whenGetPlatoByIdWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/platos/get")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", "999"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void givenPlatos_whenGetAllPlatos_thenStatus200() throws Exception {
		final String nombrePlato1 = "Nombre plato test 1";
		final String nombrePlato2 = "Nombre plato test 2";
		createTestPlato(nombrePlato1, "Descripcion plato test 1");
		createTestPlato(nombrePlato2, "Descripcion plato test 2");

		mvc.perform(MockMvcRequestBuilders.post("/platos/get-all")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value(nombrePlato1))
			.andExpect(MockMvcResultMatchers.jsonPath("$[1].nombre").value(nombrePlato2));
	}

	@Test
	void givenPlatos_whenGetAllPlatosWithNoPlatos_thenStatus200AndEmpty() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/platos/get-all")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
	}

	@Test
	void givenPlatos_whenSearchPlatos_thenStatus200() throws Exception {
		final String nombrePlato = "Nombre plato test";
		Plato plato = createTestPlato(nombrePlato, "Descripcion plato test");

		mvc.perform(MockMvcRequestBuilders.post("/platos/search")
				.contentType(MediaType.APPLICATION_JSON)
				.content(nombrePlato.substring(0, 6)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].nombre").value(nombrePlato))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(plato.getId()));
	}

	@Test
	void givenPlatos_whenSearchPlatosWithNonExistingPartialName_thenStatus200AndEmptyList() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/platos/search")
				.contentType(MediaType.APPLICATION_JSON)
				.content("NonExistingPartialName"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
	}

	@Test
	void givenPlatos_whenUpdatePlato_thenStatus200() throws Exception {
		final String nombrePlato = "Nombre plato test";
		Plato plato = createTestPlato(nombrePlato, "Descripcion plato test");
		plato.setNombre("Updated nombre plato");
		plato.setDescripcion("Updated descripcion");

		mvc.perform(MockMvcRequestBuilders.post("/platos/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":" + plato.getId() + ",\"nombre\":\"" + plato.getNombre() + "\",\"descripcion\":\"" + plato.getDescripcion() + "\"}"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.nombre").value(plato.getNombre()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.descripcion").value(plato.getDescripcion()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(plato.getId()));
	}

	@Test
	void givenPlatos_whenUpdatePlatoWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/platos/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":999,\"nombre\":\"Updated nombre plato\",\"descripcion\":\"Updated descripcion\"}"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void givenPlatos_whenDeletePlato_thenStatus200() throws Exception {
		final String nombrePlato = "Nombre plato test";
		Plato plato = createTestPlato(nombrePlato, "Descripcion plato test");

		mvc.perform(MockMvcRequestBuilders.delete("/platos/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", plato.getId().toString()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string("Plato deleted successfully"));
	}

	@Test
	void givenPlatos_whenDeletePlatoWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/platos/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", "999"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void givenComidas_whenCreateComida_thenStatus200() throws Exception {
		final String fechaString = "2026-11-10T10:00:00.000+00:00";

		mvc.perform(MockMvcRequestBuilders.post("/comidas/create")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"fecha\":\"" + fechaString + "\",\"tipo\":\"comida\"}"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.tipo").value("comida"));
	}

	@Test
	void givenComidas_whenGetComidaById_thenStatus200() throws Exception {
		Date fecha = new Date();
		Comida comida = createTestComida(fecha, TipoComida.comida);

		mvc.perform(MockMvcRequestBuilders.get("/comidas/get/" + comida.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", comida.getId().toString()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(comida.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.tipo").value("comida"));
	}

	@Test
	void givenComidas_whenGetComidaByIdWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/comidas/get/999")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", "999"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	private String toIsoDate(Date date) {
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
		return isoFormat.format(date);
	}

	@Test
	void givenComidas_whenGetComidasByDateInterval_thenStatus200() throws Exception {
		Date fecha1 = new Date(1700000000000L);
		Date fecha2 = new Date(1700003600000L);
		Date fecha3 = new Date(1700007200000L);
		createTestComida(fecha1, TipoComida.comida);
		createTestComida(fecha2, TipoComida.cena);
		createTestComida(fecha3, TipoComida.comida);

		mvc.perform(MockMvcRequestBuilders.post("/comidas/get")
				.contentType(MediaType.APPLICATION_JSON)
				.param("desde", toIsoDate(fecha1))
				.param("hasta", toIsoDate(fecha2)))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$[0].tipo").value("comida"))
			.andExpect(MockMvcResultMatchers.jsonPath("$[1].tipo").value("cena"));
	}

	@Test
	void givenComidas_whenUpdateComida_thenStatus200() throws Exception {
		Date fecha = new Date();
		Comida comida = createTestComida(fecha, TipoComida.comida);
		comida.setTipo(TipoComida.cena);
		String fechaJson = toIsoDate(comida.getFecha());

		mvc.perform(MockMvcRequestBuilders.post("/comidas/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":" + comida.getId() + ",\"fecha\":\"" + fechaJson + "\",\"tipo\":\"" + comida.getTipo() + "\"}"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.jsonPath("$.tipo").value("cena"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(comida.getId()));
	}

	@Test
	void givenComidas_whenUpdateComidaWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/comidas/update")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":999,\"fecha\":\"2026-11-10T10:00:00.000+00:00\",\"tipo\":\"cena\"}"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	void givenComidas_whenDeleteComida_thenStatus200() throws Exception {
		Date fecha = new Date();
		Comida comida = createTestComida(fecha, TipoComida.comida);

		mvc.perform(MockMvcRequestBuilders.delete("/comidas/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", comida.getId().toString()))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string("Comida deleted successfully"));
	}

	@Test
	void givenComidas_whenDeleteComidaWithNonExistingId_thenStatus404() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/comidas/delete")
				.contentType(MediaType.APPLICATION_JSON)
				.param("id", "999"))
			.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
}
