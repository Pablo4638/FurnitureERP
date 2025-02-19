function calcularTotalProducto(productId, precio) {
    let cantidad = document.getElementById(`quantitat-${productId}`).value;
    cantidad = parseFloat(cantidad) || 0;  // Si no es un número, usamos 0

    // Calcular el subtotal para este producto
    let subtotal = cantidad * precio;

    // Redondear a dos decimales
    subtotal = subtotal.toFixed(2);

    // Actualizar el subtotal en la tabla
    document.getElementById(`total-producto-${productId}`).textContent = `${subtotal} €`;

    // Actualizar el total de la compra
    calcularTotal();
}

function calcularTotal() {
    let total = 0;

    // Recorremos todos los productos para calcular el total
    let productos = document.querySelectorAll('tr[id^="producto-"]');
    productos.forEach(function (producto) {
        let subtotalText = producto.querySelector('span[id^="total-producto-"]').textContent;
        let subtotal = parseFloat(subtotalText.replace(' €', '')) || 0;  // Extraemos el valor numérico y manejamos el caso de 0
        total += subtotal;
    });

    // Redondear el total a dos decimales
    total = total.toFixed(2);

    // Mostrar el total de la compra
    document.getElementById('total-compra').textContent = `${total} €`;
}