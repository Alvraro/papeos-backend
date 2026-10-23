## Rol
Actúa como desarrollador fullstack senior que escribe código simple, claro y fácil de entender para alguien con conocimientos de programación backend pero no de frontend.
## Contexto
### Intro
Estoy creando "Papeos", una Web en la que poder registrar planes semanales de comidas principales (mediodía y cenas) para cada día y dé sugerencias según diferentes criterios (e.g. platos que no se hacen en mucho tiempo, platos con alimentos variados, etc.) para los siguientes días.
Distintos usuarios podrán usar la Web. La idea es que se puedan reutilizar al máximo los platos y los alimentos, reduciendo duplicados, reduciendo labor de edición de datos para el usuario y favoreciendo técnicas de recomendación para las sugerencias.
### Arquitectura
La web consta de un backend, un frontend y una base de datos.
El Backend es Java Spring Boot 4.0, con datos en MariaDB y desplegado todo mediante Docker Compose. Consiste en un API Rest que el frontend deberá consultar. Está disponible en la URL `http://localhost:8080` y puedes consultar documentación con Swagger UI en `http://localhost:8080/swagger-ui/index.html`. Tiene endpoints para todas las operaciones CRUD de comidas, platos, alimentos y usuarios.
El Frontend es HTML, CSS y JavaScript, sin frameworks y las librerías imprescindibles (¿React?) que se ejecuta en el navegador del usuario. La Web debe ser reactiva y tener aspecto minimalista. La comunicación con el backend se realiza mediante llamadas HTTP a la API Rest.
### Modelo de datos
Los 3 conceptos principales son **comida**, **plato** y **alimento**. Cada comida consta de uno o más platos, y un plato puede repetirse en distintas comidas. Un plato consta de varios alimentos, y un alimento puede estar presente en distintos platos.
Una comida pertenece a un usuario y tiene un tipo (comida o cena) y una fecha. Clave primaria: id_usuario, tipo_comida, fecha.
Un plato pertenece a un usuario y tiene un nombre y una descripción. Clave primaria: id_usuario, nombre_plato.
Un alimento tiene un nombre y un tipo (vegetal, carne, pescado, legumbre, etc.). Clave primaria: nombre_alimento.
Un usuario tiene un id y un nombre. Clave primaria: id_usuario.
### Funcionalidad Web
Inicialmente la web mínima debe tener 3 páginas: **Plan**, **Platos** y **Alimentos**.
En Plan se edita la planficación diaria de las 2 comidas para una semana.
En Platos se edita la composición de alimentos de cada plato y se borran o añaden nuevos platos.
En Alimentos se modifica el tipo o nombre de cada alimento y se borran o añaden nuevos alimentos.
## Tarea
Crea cada una de las páginas que componen el Frontend de la Web: Plan, platos y alimentos. Todas las operaciones (creación, modificación, borrado, renombrado, etc.) deben realizar llamadas al backend. Si una llamada al backend falla no debe actualizarse la interfaz, saltando un aviso con un mensaje que indique que no ha podido reflejarse el cambio en la base de datos. Mantén coherencia de funcionamiento y aspecto para todo lo que realice operaciones similares (e.g. cuadro de texto para filtros parciales, botón de borrar, botón de añadir, etc.).
- Página Plan
    1. La parte superior izquierda indica los datos que se muestran a qué semana natural (Lunes a Domingo) corresponden. Ejemplo: `19/10/2026 a 25/10/2026`
    2. La parte superior derecha añade un botón para pasar a la semana previa y otro para la semana siguiente. Utiliza iconos de flechas hacia la izquierda (semana previa) o derecha (semana siguiente)
    3. La zona principal muestra la planificación día por día de la semana actual, de Lunes a Domingo con dos filas por comida de cada día. De izquierda a derecha las columnas (ancho fijo para todas las filas) de la primera fila de cada día:
        1. Nombre del día de la semana (e.g. "Lunes") una única vez, al principio de la primera fila del día
        2. Cuadro de texto vacío o, en caso de haber plato para comida tipo "Mediodía" para esa fecha y el usuario actual: nombre del plato. Al cambiar el foco al cuadro deben salir como sugerencias seleccionables en un menú desplegable los platos de cuyo nombre el texto sea coincidencia parcial ignorando mayúsculas (e.g. si pone "cho" aparecerán platos como "Lentejas con chorizo"). Al seleccionar una opción se rellena el cuadro de texto con el nombre del plato elegido y se guarda en la base de datos el plato y comida. El desplegable debe contener una opción con un icono para añadir plato nuevo (e.g. un "+"), que tendrá como nombre el contenido actual del cuadro de texto
        3. Icono clickeable para desplegar un menú de selección de plato. Las opciones serán todos los platos del usuario actual
        4. Quitar plato actual. Al quitar debe actualizarse la fila para mostrarse como cuando ya no hay comida de "Mediodía" para la fecha actual
    4. La siguiente fila debe ser igual al punto anterior, pero cambiando "Mediodía" por "Cena"
- Página Platos
    1. Muestra el listado de platos (TODO paginación, ordenación) en base de datos
    2. Arriba a la derecha hay un cuadro de texto que sirve de filtro para búsquedas parciales por nombre de plato. Similar al de la página Plan, al cambiar el foco al cuadro deben salir como sugerencias seleccionables en un menú desplegable los platos de cuyo nombre el texto sea coincidencia parcial ignorando mayúsculas. Si no hay coincidencias, el desplegable debe contener una opción con un icono para añadir plato nuevo (e.g. un "+"), que tendrá como nombre el contenido actual del cuadro de texto
    3. Cada plato se muestra en un bloque con una primera fila con el nombre del plato más una fila por cada uno de los alimentos que lo componen
    4. El nombre del plato debe ser editable al clickear sobre él y se guardará en base de datos
    5. Al final de cada fila de alimento hay una icono de "x" para borrar el alimento del plato
    6. Tras la última fila de los alimentos del plato habrá un cuadro de texto de funcionamiento similar al de "Plan" que, en este caso, permite elegir un alimento ya existente o crear uno nuevo y añadirlos al plato
- Página Alimentos
    1. Muestra el listado de alimentos (TODO paginación, ordenación) en base de datos
    2. Arriba a la derecha hay un cuadro de texto que sirve de filtro para búsquedas parciales por nombre de alimento. Similar al de la página Plan, al cambiar el foco al cuadro deben salir como sugerencias seleccionables en un menú desplegable los alimentos de cuyo nombre el texto sea coincidencia parcial ignorando mayúsculas. Si no hay coincidencias, el desplegable debe contener una opción con un icono para añadir alimento nuevo (e.g. un "+"), que tendrá como nombre el contenido actual del cuadro de texto
    3. Cada alimento se muestra en un bloque con una primera fila con el nombre del alimento y su tipo, editable al clickear sobre él y se guardará en base de datos
    4. Al final de cada fila de alimento hay un icono de "x" para borrar el alimento

### Backend
## Restricciones y reglas
Racha:
- Un día cuenta si tiene al menos una sesión.
- La racha son los días consecutivos con sesión que terminan hoy.
- Si hoy todavía no he estudiado pero ayer sí, la racha sigue viva: no se rompe hasta que
termina el día.
- Usa siempre la fecha local del usuario, nunca UTC.
Técnicas:
- HTML, CSS y JavaScript, sin frameworks, sin librerías y sin compilar nada.
- Solo tres archivos: index.html, styles.css y app.js.
- Tiene que funcionar abriendo index.html con doble clic, sin servidor ni instalación.
- No añadas nada que no aparezca en este mensaje.
- Diseño limpio y moderno, que se vea bien en el móvil.
- Todos los textos de la interfaz en español.
## Formato de salida
1. Crea los archivos en la carpeta "frontend" del proyecto.
2. Al terminar, responde con:
 - Un resumen de 3-4 líneas de lo que has creado.
 - Los pasos para probarlo.
 - Cualquier decisión que hayas tomado por tu cuenta y que yo deba revisar. 