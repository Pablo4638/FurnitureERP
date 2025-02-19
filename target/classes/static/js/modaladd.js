// Obtener el modal
var modal = document.getElementById("add-modal");

// Obtener el botón que abre el modal
var btn = document.getElementById("add-btn");

// Obtener el elemento <span> que cierra el modal
var span = document.getElementById("close-modal-btn");

// Cuando el usuario hace clic en el botón, abrir el modal
btn.onclick = function () {
  modal.style.display = "block";
};

// Cuando el usuario hace clic en <span> (x), cerrar el modal
span.onclick = function () {
  modal.style.display = "none";
};

// Cuando el usuario hace clic fuera del modal, cerrarlo
window.onclick = function (event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
};
