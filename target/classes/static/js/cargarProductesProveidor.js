function obtenirProductesPerProveeidor(proveedorId) {
  if (proveedorId) {
      fetch(`/productos/proveedor/{proveedorId}`)
          .then((response) => {
              if (!response.ok) {
                  throw new Error("Error al cargar los productos del proveedor");
              }
              return response.json();
          })
          .then((data) => {
              const tablaProductos = document.getElementById("tablaProductos");
              tablaProductos.innerHTML = ""; // Limpiamos la tabla antes de añadir los productos

              // Verificamos si los datos están vacíos
              if (data.length === 0) {
                  tablaProductos.innerHTML = "<tr><td colspan='4'>No se encontraron productos.</td></tr>";
              }

              // Añadimos los productos a la tabla
              data.forEach((producte) => {
                  const row = document.createElement("tr");
                  row.innerHTML = `
                      <td>${producte.nom_producte}</td>
                      <td>${producte.descripcio}</td>
                      <td>${producte.preu}</td>
                      <td>${producte.stcok_actual}</td>
                      <td>
                          <input type="number" name="productos[${producte.id_producte}]" min="1" value="1">
                      </td>
                  `;
                  tablaProductos.appendChild(row);
              });
          })
          .catch((error) => {
              console.error("Error fetching productos:", error);
          });
  }
}
