const API_BASE = "http://localhost:8080";

const API = {
  foods: "/alimentos",
  dishes: "/platos",
  meals: "/comidas"
};

const WEEKDAY_NAMES = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];

let foods = [];
let dishes = [];
let meals = [];
let foodCategories = [];
let weekStart = mondayOf(new Date());

const byId = (id) => document.getElementById(id);
const escapeHtml = (value) => String(value ?? "").replace(/[&<>"']/g, (character) => ({
  "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
}[character]));

async function request(path, { method = "GET", body, query } = {}) {
  const url = new URL(`${API_BASE}${path}`);
  if (query) Object.entries(query).forEach(([key, value]) => url.searchParams.set(key, value));
  let response;
  try {
    response = await fetch(url, {
      method,
      headers: body === undefined ? {} : { "Content-Type": "application/json" },
      body: body === undefined ? undefined : JSON.stringify(body)
    });
  } catch (error) {
    throw new Error("No se pudo conectar con el backend. Comprueba que está iniciado y que permite llamadas desde el navegador.");
  }
  if (!response.ok) {
    const details = await response.text().catch(() => "");
    throw new Error(details || `El servidor respondió con el estado ${response.status}.`);
  }
  if (response.status === 204) return null;
  const text = await response.text();
  if (!text) return null;
  try { return JSON.parse(text); } catch { return text; }
}

function notifyFailure(error) {
  window.alert(`No se ha podido reflejar el cambio en la base de datos.\n\n${error.message}`);
}

async function loadFoods(search = "") {
  const result = await request(`${API.foods}/search`, { method: "POST", body: search });
  return Array.isArray(result) ? result : [];
}

async function loadDishes(search = "") {
  const result = await request(`${API.dishes}/search`, { method: "POST", body: search });
  return Array.isArray(result) ? result : [];
}

function foodName(food) { return food.nombre ?? food.name ?? ""; }
function foodType(food) {
  return food.categoria?.nombre ?? food.tipo ?? food.tipoAlimento ?? food.categoria ?? food.type ?? "";
}
function dishName(dish) { return dish.nombre ?? dish.name ?? ""; }
function dishFoods(dish) { return dish.alimentos ?? dish.foods ?? dish.ingredientes ?? []; }
function dishId(dish) { return dish.id ?? dish.platoId; }
function foodId(food) { return food.id ?? food.alimentoId; }

function dishPayload(dish, changes = {}) {
  const { comidas, ...fields } = dish;
  const alimentos = changes.alimentos ?? dishFoods(dish);
  return {
    ...fields,
    ...changes,
    alimentos: alimentos.map(({ platos, ...food }) => food)
  };
}

function foodPayload(food, changes = {}) {
  const { platos, ...fields } = food;
  return { ...fields, ...changes };
}

function mondayOf(date) {
  const monday = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  const day = (monday.getDay() + 6) % 7;
  monday.setDate(monday.getDate() - day);
  return monday;
}

function dateKey(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
}

function weekQuery() {
  const end = new Date(weekStart);
  end.setDate(end.getDate() + 6);
  return {
    desde: dateKey(weekStart).replace(/-/g, "/"),
    hasta: dateKey(end).replace(/-/g, "/")
  };
}

async function loadMealsForWeek() {
  const result = await request(`${API.meals}/get`, { method: "POST", query: weekQuery() });
  return Array.isArray(result) ? result : [];
}

function displayDate(date) {
  return new Intl.DateTimeFormat("es-ES", { day: "2-digit", month: "2-digit", year: "numeric" }).format(date);
}

function renderWeekRange() {
  const end = new Date(weekStart);
  end.setDate(end.getDate() + 6);
  byId("week-range").textContent = `${displayDate(weekStart)} a ${displayDate(end)}`;
}

function mealFor(day, type) {
  return meals.find((meal) =>
    String(meal.fecha ?? meal.date ?? "").slice(0, 10) === dateKey(day) &&
    String(meal.tipoComida ?? meal.tipo_comida ?? meal.tipo ?? "").toLowerCase() === type
  );
}

function selectedDishName(meal) {
  if (!meal) return "";
  const dish = meal.plato ?? meal.dish ?? meal.platos?.[0] ?? dishes.find((item) => String(dishId(item)) === String(meal.platoId ?? meal.plato_id));
  return typeof dish === "string" ? dish : dishName(dish ?? {});
}

function renderPlan() {
  renderWeekRange();
  const root = byId("weekly-plan");
  root.innerHTML = WEEKDAY_NAMES.map((name, index) => {
    const day = new Date(weekStart);
    day.setDate(day.getDate() + index);
    const rows = ["comida", "cena"].map((type) => {
      const meal = mealFor(day, type);
      const current = selectedDishName(meal);
      const mealId = meal?.id ?? "";
      return `<div class="meal-row" data-date="${dateKey(day)}" data-type="${type}" data-meal-id="${escapeHtml(mealId)}">
        <span class="meal-label">${type === "comida" ? "Mediodía" : "Cena"}</span>
        <div class="suggestion-wrap">
          <input class="meal-input" type="text" value="${escapeHtml(current)}" placeholder="Añadir un plato…" autocomplete="off" aria-label="${name}, ${type === "comida" ? "mediodía" : "cena"}">
          <div class="suggestions" role="listbox" hidden></div>
        </div>
        <button class="small-icon-button open-dish-menu" type="button" aria-label="Elegir plato" title="Elegir plato">▾</button>
        <button class="small-icon-button danger clear-meal" type="button" aria-label="Quitar plato" title="Quitar plato" ${current ? "" : "disabled"}>×</button>
      </div>`;
    }).join("");
    return `<article class="day-card">
      <div class="day-heading"><strong>${name}</strong><time datetime="${dateKey(day)}">${displayDate(day).slice(0, 5)}</time></div>
      <div class="meal-list">${rows}</div>
    </article>`;
  }).join("");
}

function findMatches(list, query, getName) {
  const normalized = query.trim().toLocaleLowerCase("es");
  return list.filter((item) => getName(item).toLocaleLowerCase("es").includes(normalized));
}

function showSuggestions(container, items, getName, onChoose, query, createLabel, allowCreate, createAlways = false) {
  const matches = findMatches(items, query, getName);
  const options = matches.map((item, index) =>
    `<button class="suggestion-option" type="button" role="option" data-option-index="${index}">${escapeHtml(getName(item))}</button>`
  );
  if (allowCreate && query.trim() && (createAlways || !matches.length)) {
    options.push(`<button class="suggestion-option create-option" type="button" role="option" data-create="true"><span class="plus-mark">+</span> Crear ${createLabel} «${escapeHtml(query.trim())}»</button>`);
  }
  if (!options.length) {
    container.hidden = true;
    return;
  }
  container.innerHTML = options.join("");
  container.hidden = false;
  container.querySelectorAll("[data-option-index]").forEach((button) => {
    button.addEventListener("mousedown", (event) => event.preventDefault());
    button.addEventListener("click", () => onChoose(matches[Number(button.dataset.optionIndex)]));
  });
  const createButton = container.querySelector("[data-create]");
  if (createButton) {
    createButton.addEventListener("mousedown", (event) => event.preventDefault());
    createButton.addEventListener("click", () => onChoose(null));
  }
}

function mealPayload(row, dish) {
  return {
    fecha: `${row.dataset.date}T00:00:00.000Z`,
    tipo: row.dataset.type.toUpperCase(),
    platos: [{ id: dishId(dish) }]
  };
}

async function saveMeal(row, dish) {
  const oldMeal = meals.find((item) => String(item.id ?? "") === row.dataset.mealId && row.dataset.mealId);
  const body = mealPayload(row, dish);
  if (oldMeal) body.id = oldMeal.id;
  const saved = await request(`${API.meals}/${oldMeal ? "update" : "create"}`, { method: "POST", body });
  meals = meals.filter((item) => !(String(item.fecha ?? "").slice(0, 10) === row.dataset.date &&
    String(item.tipoComida ?? item.tipo_comida ?? item.tipo ?? "").toLowerCase() === row.dataset.type));
  meals.push(saved && typeof saved === "object" ? saved : {
    ...body, id: oldMeal?.id, plato: dish
  });
  renderPlan();
}

async function choosePlanDish(row, input, suggestions, dish) {
  try {
    let selected = dish;
    if (!selected) {
      const name = input.value.trim();
      if (!name) return;
      selected = await request(`${API.dishes}/create`, { method: "POST", body: { nombre: name, descripcion: "" } });
      dishes = [...dishes, selected];
    }
    await saveMeal(row, selected);
  } catch (error) {
    notifyFailure(error);
  } finally {
    suggestions.hidden = true;
  }
}

function wirePlan() {
  byId("previous-week").addEventListener("click", () => changeWeek(-7));
  byId("next-week").addEventListener("click", () => changeWeek(7));
  byId("weekly-plan").addEventListener("input", (event) => {
    const input = event.target.closest(".meal-input");
    if (!input) return;
    const row = input.closest(".meal-row");
    showSuggestions(input.nextElementSibling, dishes, dishName, (dish) => choosePlanDish(row, input, input.nextElementSibling, dish), input.value, "plato", true, true);
  });
  byId("weekly-plan").addEventListener("focusin", (event) => {
    const input = event.target.closest(".meal-input");
    if (input) input.dispatchEvent(new Event("input", { bubbles: true }));
  });
  byId("weekly-plan").addEventListener("focusout", (event) => {
    if (event.target.matches(".meal-input")) setTimeout(() => {
      const suggestions = event.target.nextElementSibling;
      if (suggestions) suggestions.hidden = true;
    }, 140);
  });
  byId("weekly-plan").addEventListener("change", (event) => {
    if (!event.target.matches(".meal-input")) return;
    const row = event.target.closest(".meal-row");
    const match = findMatches(dishes, event.target.value, dishName).find((dish) => dishName(dish).toLocaleLowerCase("es") === event.target.value.trim().toLocaleLowerCase("es"));
    if (match) choosePlanDish(row, event.target, event.target.nextElementSibling, match);
    else {
      event.target.value = selectedDishName(mealFor(new Date(`${row.dataset.date}T00:00:00`), row.dataset.type));
    }
  });
  byId("weekly-plan").addEventListener("click", async (event) => {
    const row = event.target.closest(".meal-row");
    if (!row) return;
    if (event.target.closest(".open-dish-menu")) {
      const input = row.querySelector(".meal-input");
      const menu = input.nextElementSibling;
      showSuggestions(menu, dishes, dishName, (dish) => choosePlanDish(row, input, menu, dish), "", "plato", false);
      input.focus();
    }
    if (event.target.closest(".clear-meal")) {
      const existing = meals.find((item) =>
        String(item.fecha ?? "").slice(0, 10) === row.dataset.date &&
        String(item.tipoComida ?? item.tipo_comida ?? item.tipo ?? "").toLowerCase() === row.dataset.type
      );
      if (!existing) return;
      try {
        await request(`${API.meals}/delete`, { method: "DELETE", query: { id: existing.id } });
        meals = meals.filter((item) => item !== existing);
        renderPlan();
      } catch (error) { notifyFailure(error); }
    }
  });
}

async function changeWeek(offset) {
  weekStart.setDate(weekStart.getDate() + offset);
  try {
    meals = await loadMealsForWeek();
    renderPlan();
  } catch (error) {
    weekStart.setDate(weekStart.getDate() - offset);
    notifyFailure(error);
  }
}

function buildDishCard(dish) {
  const id = dishId(dish);
  const ingredients = dishFoods(dish);
  const rows = ingredients.map((food) => `<li class="ingredient-row" data-food-id="${escapeHtml(foodId(food))}">
    <span class="ingredient-name">${escapeHtml(foodName(food))}</span>
    <button class="small-icon-button danger remove-ingredient" type="button" aria-label="Quitar ${escapeHtml(foodName(food))} del plato" title="Quitar alimento">×</button>
  </li>`).join("");
  return `<article class="item-card" data-dish-id="${escapeHtml(id)}">
    <div class="item-header">
      <h2 class="item-title"><input class="editable-title dish-title" type="text" value="${escapeHtml(dishName(dish))}" aria-label="Nombre del plato" title="Haz clic para editar y pulsa Enter para guardar"></h2>
      <button class="small-icon-button danger delete-dish" type="button" aria-label="Borrar plato ${escapeHtml(dishName(dish))}" title="Borrar plato">×</button>
    </div>
    <ul class="ingredient-list">${rows || '<li class="ingredient-row"><span class="ingredient-name">Aún no tiene alimentos.</span></li>'}</ul>
    <div class="ingredient-add suggestion-wrap">
      <label class="sr-only">Añadir alimento a ${escapeHtml(dishName(dish))}</label>
      <input class="ingredient-input" type="text" placeholder="Añadir alimento…" autocomplete="off">
      <div class="suggestions" role="listbox" hidden></div>
    </div>
  </article>`;
}

function renderDishes() {
  const root = byId("dish-list");
  const query = byId("dish-search")?.value ?? "";
  const visible = findMatches(dishes, query, dishName);
  root.innerHTML = visible.length ? visible.map(buildDishCard).join("") : '<div class="empty-state">No hay platos que coincidan. Usa el buscador para crear uno.</div>';
}

async function createDish(name) {
  const saved = await request(`${API.dishes}/create`, { method: "POST", body: { nombre: name, descripcion: "" } });
  dishes = [...dishes, saved];
  renderDishes();
  byId("dish-search").value = "";
}

function wireDishSearch() {
  const input = byId("dish-search");
  const menu = byId("dish-suggestions");
  const update = () => {
    renderDishes();
    showSuggestions(menu, dishes, dishName, (dish) => {
    if (!dish) {
      createDish(input.value.trim()).catch(notifyFailure);
    } else {
      input.value = dishName(dish);
      menu.hidden = true;
      renderDishes();
      document.querySelector(`[data-dish-id="${CSS.escape(String(dishId(dish)))}"]`)?.scrollIntoView({ behavior: "smooth", block: "center" });
    }
    }, input.value, "plato", true);
  };
  input.addEventListener("focus", update);
  input.addEventListener("input", update);
  input.addEventListener("blur", () => setTimeout(() => { menu.hidden = true; }, 140));
  input.addEventListener("keydown", (event) => {
    if (event.key === "Enter" && input.value.trim() && !findMatches(dishes, input.value, dishName).length) {
      event.preventDefault();
      createDish(input.value.trim()).catch(notifyFailure);
      menu.hidden = true;
    }
  });
}

function wireDishList() {
  const root = byId("dish-list");
  root.addEventListener("keydown", async (event) => {
    const input = event.target.closest(".dish-title");
    if (!input || event.key !== "Enter") return;
    event.preventDefault();
    await saveDishName(input);
  });
  root.addEventListener("focusout", (event) => {
    if (event.target.matches(".dish-title")) saveDishName(event.target);
  });
  root.addEventListener("input", (event) => {
    const input = event.target.closest(".ingredient-input");
    if (!input) return;
    const menu = input.nextElementSibling;
    showSuggestions(menu, foods, foodName, (food) => addFoodToDish(input, food), input.value, "alimento", true, true);
  });
  root.addEventListener("focusin", (event) => {
    if (event.target.matches(".ingredient-input")) event.target.dispatchEvent(new Event("input", { bubbles: true }));
  });
  root.addEventListener("focusout", (event) => {
    if (event.target.matches(".ingredient-input")) setTimeout(() => { event.target.nextElementSibling.hidden = true; }, 140);
  });
  root.addEventListener("click", async (event) => {
    const card = event.target.closest(".item-card");
    if (!card) return;
    if (event.target.closest(".delete-dish")) {
      try {
        await request(`${API.dishes}/delete`, { method: "DELETE", query: { id: card.dataset.dishId } });
        dishes = dishes.filter((dish) => String(dishId(dish)) !== card.dataset.dishId);
        renderDishes();
      } catch (error) { notifyFailure(error); }
    }
    if (event.target.closest(".remove-ingredient")) {
      const row = event.target.closest(".ingredient-row");
      const dish = dishes.find((item) => String(dishId(item)) === card.dataset.dishId);
      try {
        const updated = await request(`${API.dishes}/update`, {
          method: "POST",
          body: dishPayload(dish, {
            alimentos: dishFoods(dish).filter((food) => String(foodId(food)) !== row.dataset.foodId)
          })
        });
        dishes = dishes.map((dish) => String(dishId(dish)) === card.dataset.dishId
          ? updated
          : dish);
        renderDishes();
      } catch (error) { notifyFailure(error); }
    }
  });
}

async function saveDishName(input) {
  const card = input.closest(".item-card");
  const dish = dishes.find((item) => String(dishId(item)) === card.dataset.dishId);
  const name = input.value.trim();
  if (!dish || !name || name === dishName(dish)) {
    if (dish) input.value = dishName(dish);
    return;
  }
  try {
    const saved = await request(`${API.dishes}/update`, { method: "POST", body: dishPayload(dish, { nombre: name }) });
    dishes = dishes.map((item) => String(dishId(item)) === card.dataset.dishId ? saved : item);
    renderDishes();
  } catch (error) {
    input.value = dishName(dish);
    notifyFailure(error);
  }
}

async function addFoodToDish(input, food) {
  const card = input.closest(".item-card");
  let createdFood = null;
  try {
    let selected = food;
    if (!selected) {
      const name = input.value.trim();
      if (!name) return;
      selected = await createFoodRecord(name);
      if (!selected) return;
      createdFood = selected;
    }
    const dish = dishes.find((item) => String(dishId(item)) === card.dataset.dishId);
    const updated = await request(`${API.dishes}/update`, {
      method: "POST",
      body: dishPayload(dish, { alimentos: [...dishFoods(dish), selected] })
    });
    if (createdFood) {
      foods = [...foods, createdFood];
      categoriesFromFoods();
    }
    dishes = dishes.map((dish) => String(dishId(dish)) === card.dataset.dishId
      ? updated
      : dish);
    renderDishes();
  } catch (error) {
    if (createdFood) {
      try {
        await request(`${API.foods}/delete`, { method: "DELETE", query: { id: foodId(createdFood) } });
      } catch (cleanupError) {
        notifyFailure(new Error(`${error.message}\nNo se pudo deshacer la creación del alimento: ${cleanupError.message}`));
        return;
      }
    }
    notifyFailure(error);
  }
}

function buildFoodCard(food) {
  const type = foodType(food);
  return `<article class="item-card food-card" data-food-id="${escapeHtml(foodId(food))}">
    <div class="item-header">
      <h2 class="item-title"><input class="editable-title food-title" type="text" value="${escapeHtml(foodName(food))}" aria-label="Nombre del alimento" title="Haz clic para editar y pulsa Enter para guardar"></h2>
      <span class="food-type-label">Tipo</span>
      <input class="type-select" type="text" list="food-types" value="${escapeHtml(type)}" aria-label="Tipo de ${escapeHtml(foodName(food))}">
      <button class="small-icon-button danger delete-food" type="button" aria-label="Borrar alimento ${escapeHtml(foodName(food))}" title="Borrar alimento">×</button>
    </div>
  </article>`;
}

function renderFoods() {
  const root = byId("food-list");
  const query = byId("food-search")?.value ?? "";
  const visible = findMatches(foods, query, foodName);
  root.innerHTML = visible.length ? visible.map(buildFoodCard).join("") : '<div class="empty-state">No hay alimentos que coincidan. Usa el buscador para crear uno.</div>';
}

function wireFoodSearch() {
  const input = byId("food-search");
  const menu = byId("food-suggestions");
  const update = () => {
    renderFoods();
    showSuggestions(menu, foods, foodName, (food) => {
    if (!food) createFood(input.value.trim()).catch(notifyFailure);
    else {
      input.value = foodName(food);
      menu.hidden = true;
      renderFoods();
      document.querySelector(`[data-food-id="${CSS.escape(String(foodId(food)))}"]`)?.scrollIntoView({ behavior: "smooth", block: "center" });
    }
    }, input.value, "alimento", true);
  };
  input.addEventListener("focus", update);
  input.addEventListener("input", update);
  input.addEventListener("blur", () => setTimeout(() => { menu.hidden = true; }, 140));
  input.addEventListener("keydown", (event) => {
    if (event.key === "Enter" && input.value.trim() && !findMatches(foods, input.value, foodName).length) {
      event.preventDefault();
      createFood(input.value.trim()).catch(notifyFailure);
      menu.hidden = true;
    }
  });
}

async function createFood(name) {
  const saved = await createFoodRecord(name);
  if (!saved) return;
  foods = [...foods, saved];
  categoriesFromFoods();
  renderFoods();
  byId("food-search").value = "";
}

function wireFoodList() {
  const root = byId("food-list");
  root.addEventListener("keydown", async (event) => {
    if (event.target.matches(".food-title") && event.key === "Enter") {
      event.preventDefault();
      await saveFood(event.target);
    }
  });
  root.addEventListener("focusout", (event) => {
    if (event.target.matches(".food-title")) saveFood(event.target);
  });
  root.addEventListener("change", async (event) => {
    if (!event.target.matches(".type-select")) return;
    const card = event.target.closest(".food-card");
    const food = foods.find((item) => String(foodId(item)) === card.dataset.foodId);
    const oldType = foodType(food);
    const category = categoriesFromFoods().find((item) => item.nombre.toLocaleLowerCase("es") === event.target.value.trim().toLocaleLowerCase("es"));
    if (!category) {
      event.target.value = oldType;
      window.alert("Ese tipo no existe en la base de datos. Selecciona una categoría disponible.");
      return;
    }
    try {
      const saved = await request(`${API.foods}/update`, { method: "POST", body: foodPayload(food, { categoria: category }) });
      foods = foods.map((item) => String(foodId(item)) === card.dataset.foodId ? saved : item);
      byId("food-types").innerHTML = categoriesFromFoods().map((item) => `<option value="${escapeHtml(item.nombre)}"></option>`).join("");
      renderFoods();
    } catch (error) {
      event.target.value = oldType;
      notifyFailure(error);
    }
  });
  root.addEventListener("click", async (event) => {
    const button = event.target.closest(".delete-food");
    if (!button) return;
    const card = button.closest(".food-card");
    try {
      await request(`${API.foods}/delete`, { method: "DELETE", query: { id: card.dataset.foodId } });
      foods = foods.filter((food) => String(foodId(food)) !== card.dataset.foodId);
      renderFoods();
    } catch (error) { notifyFailure(error); }
  });
}

async function saveFood(input) {
  const card = input.closest(".food-card");
  const food = foods.find((item) => String(foodId(item)) === card.dataset.foodId);
  const name = input.value.trim();
  if (!food || !name || name === foodName(food)) {
    if (food) input.value = foodName(food);
    return;
  }
  try {
    const saved = await request(`${API.foods}/update`, { method: "POST", body: foodPayload(food, { nombre: name }) });
    foods = foods.map((item) => String(foodId(item)) === card.dataset.foodId ? saved : item);
    renderFoods();
  } catch (error) {
    input.value = foodName(food);
    notifyFailure(error);
  }
}

function categoriesFromFoods() {
  const byCategoryId = new Map(foodCategories.map((category) => [String(category.id), category]));
  foods.forEach((food) => {
    if (food.categoria?.id !== undefined) byCategoryId.set(String(food.categoria.id), food.categoria);
  });
  foodCategories = [...byCategoryId.values()].sort((first, second) => first.nombre.localeCompare(second.nombre, "es"));
  return foodCategories;
}

function chooseExistingCategory() {
  const categories = categoriesFromFoods();
  if (!categories.length) {
    throw new Error("No se pueden crear alimentos: el backend no ofrece categorías y todavía no hay ninguna disponible.");
  }
  if (categories.length === 1) return categories[0];
  const names = categories.map((category) => category.nombre).join(", ");
  const selectedName = window.prompt(`Indica una categoría existente (${names}):`, categories[0].nombre);
  if (selectedName === null) return null;
  const category = categories.find((item) => item.nombre.toLocaleLowerCase("es") === selectedName.trim().toLocaleLowerCase("es"));
  if (!category) throw new Error("Selecciona una categoría existente para crear el alimento.");
  return category;
}

async function createFoodRecord(name) {
  const category = chooseExistingCategory();
  if (!category) return null;
  const saved = await request(`${API.foods}/create`, { method: "POST", body: { nombre: name, categoria: category } });
  return saved;
}

async function initializePlan() {
  try {
    [dishes, meals] = await Promise.all([loadDishes(), loadMealsForWeek()]);
    wirePlan();
    renderPlan();
  } catch (error) {
    byId("plan-status").textContent = "No se pudo cargar el plan. Comprueba que el backend esté disponible.";
    notifyFailure(error);
  }
}

async function initializeDishes() {
  try {
    [dishes, foods] = await Promise.all([loadDishes(), loadFoods()]);
    renderDishes();
    wireDishSearch();
    wireDishList();
  } catch (error) {
    byId("dish-status").textContent = "No se pudieron cargar los platos. Comprueba que el backend esté disponible.";
    notifyFailure(error);
  }
}

async function initializeFoods() {
  try {
    foods = await loadFoods();
    categoriesFromFoods();
    byId("food-types").innerHTML = categoriesFromFoods().map((category) => `<option value="${escapeHtml(category.nombre)}"></option>`).join("");
    renderFoods();
    wireFoodSearch();
    wireFoodList();
  } catch (error) {
    byId("food-status").textContent = "No se pudieron cargar los alimentos. Comprueba que el backend esté disponible.";
    notifyFailure(error);
  }
}

if (document.body.dataset.page === "plan") initializePlan();
if (document.body.dataset.page === "platos") initializeDishes();
if (document.body.dataset.page === "alimentos") initializeFoods();
