document.addEventListener('DOMContentLoaded', function () {
  // Modal
  const modal = document.getElementById('comandaModal');
  const openModalBtn = document.getElementById('openModalBtn');
  const closeModalBtns = document.querySelectorAll('#closeModalBtn, #closeModalBtnFooter');
  const comandesList = document.getElementById('comandesList');

  // Función para abrir el modal
  openModalBtn.addEventListener('click', function (event) {
      event.preventDefault(); // Prevenir el comportamiento por defecto del enlace
      cargarComandes(); // Cargar las comandas cuando se abre el modal
      modal.style.display = 'block'; // Mostrar el modal
  });

  // Función para cargar las comandas
  function cargarComandes() {
      // Limpiar la lista antes de cargar las nuevas comandas
      comandesList.innerHTML = '';

      // Aquí puedes utilizar AJAX o Thymeleaf. A continuación se muestra un ejemplo con Thymeleaf.
      // Si las comandas se cargan con Thymeleaf, este código se ejecutará cuando se renderice la página.
      
      // Ejemplo con Thymeleaf:
      /* 
      <li th:each="comanda : ${comandees}" class="list-group-item">
          <a th:href="@{/generarFactura/{num}(num=${comanda.id_comanda})}">
              Comanda ID: <span th:text="${comanda.id_comanda}"></span>
          </a>
      </li>
      */

      // Para hacerlo dinámico con AJAX:
      fetch('/obtenirComandes')
          .then(response => response.json())
          .then(comandes => {
              comandes.forEach(comanda => {
                  const li = document.createElement('li');
                  li.classList.add('list-group-item');
                  const a = document.createElement('a');
                  a.href = '/generarFactura/' + comanda.id_comanda;
                  a.textContent = 'Comanda ID: ' + comanda.id_comanda;
                  li.appendChild(a);
                  comandesList.appendChild(li);
              });
          })
          .catch(error => console.error('Error al cargar las comandas:', error));
  }

  // Cuando se hace clic en el botón de cerrar el modal
  closeModalBtns.forEach(function (btn) {
      btn.addEventListener('click', function () {
          modal.style.display = 'none'; // Ocultar el modal
      });
  });

  // Cuando se hace clic fuera del modal, se cierra
  window.addEventListener('click', function (event) {
      if (event.target === modal) {
          modal.style.display = 'none'; // Ocultar el modal
      }
  });
});
