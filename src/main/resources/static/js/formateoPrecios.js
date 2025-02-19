// Función para formatear el precio
function formatearPrecios() {
    const precios = document.querySelectorAll('.precio');
    
    precios.forEach(function(precio) {
        let valor = parseFloat(precio.textContent.trim());
        if (!isNaN(valor)) {
            // Formatear el precio con 2 decimales y separadores de miles
            let precioFormateado = valor.toLocaleString('es-ES', { 
                minimumFractionDigits: 2, 
                maximumFractionDigits: 2 
            });
            
            // Eliminar los decimales si son ".00"
            if (precioFormateado.endsWith(',00')) {
                precioFormateado = precioFormateado.slice(0, -3); // Elimina ",00"
            }

            // Añadir el símbolo €
            precio.textContent = `${precioFormateado} €`;
        }
    });
}

// Ejecutar la función cuando la página esté completamente cargada
window.onload = formatearPrecios;
