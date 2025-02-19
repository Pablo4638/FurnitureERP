const darkMode = document.querySelector(".dark-mode");

// Verificar si el tema oscuro está habilitado en localStorage
if (localStorage.getItem("darkMode") === "enabled") {
  document.body.classList.add("dark-mode-variables");
  darkMode.querySelector("span:nth-child(1)").classList.toggle("active");
  darkMode.querySelector("span:nth-child(2)").classList.toggle("active");
}

darkMode.addEventListener("click", () => {
  // Alternar la clase del tema oscuro en el body
  document.body.classList.toggle("dark-mode-variables");

  // Alternar la clase active en los íconos
  darkMode.querySelector("span:nth-child(1)").classList.toggle("active");
  darkMode.querySelector("span:nth-child(2)").classList.toggle("active");

  // Guardar la preferencia en localStorage
  if (document.body.classList.contains("dark-mode-variables")) {
    localStorage.setItem("darkMode", "enabled");
  } else {
    localStorage.setItem("darkMode", "disabled");
  }
});
