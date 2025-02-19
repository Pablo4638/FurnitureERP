// Obtener el modal de edición
var editModal = document.getElementById("edit-modal");

// Obtener todos los botones de editar en la tabla de clientes
var editBtns = document.querySelectorAll(".edit-btn");

// Cerrar el modal si el usuario hace clic fuera del modal
window.onclick = function (event) {
    if (event.target === editModal) {
        editModal.style.display = "none";
    }
}

// Agregar el evento de clic a cada botón de editar
editBtns.forEach(function (btn) {
    btn.onclick = function () {
        var comandaId = this.getAttribute("data-comanda-id");

        // Hacer la solicitud AJAX para obtener el formulario de edición del cliente
        fetch(`/comandes/editar/${comandaId}`)
            .then(response => response.text()) // obtener el HTML como texto
            .then(html => {
                // Insertar el contenido HTML del formulario de edición dentro del modal
                editModal.querySelector(".modal-content").innerHTML = html;
                editModal.style.display = "block"; // Mostrar el modal

                var closeBtn = document.getElementById("close-edit-modal-btn");

                // Cerrar el modal al hacer clic en el botón de cierre
                closeBtn.onclick = function () {
                    editModal.style.display = "none";
                }
            })
            .catch(error => {
                console.error("Error al cargar el formulario de edición:", error);
            });
    };
});



