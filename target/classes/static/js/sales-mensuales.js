// Gráfico de ventas mensuales
const ctxMensual = document.getElementById('salesChartMensual').getContext('2d');

// Función para generar las etiquetas dinámicamente
function getMonthLabels() {
    return [
        'Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun',
        'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'
    ];
}

// Inicialización del gráfico
const salesChartMensual = new Chart(ctxMensual, {
    type: 'bar',
    data: {
        labels: getMonthLabels(),
        datasets: [{
            label: `Ventas Mensuales (${new Date().getFullYear()})`, // Muestra el año actual
            data: [],
            backgroundColor: 'rgb(160, 11, 247)',
            borderColor: 'rgba(177, 49, 189, 0.2)',
            borderWidth: 1
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});

// Función para actualizar el gráfico dinámicamente
function updateSalesChartMensual() {
    const currentYear = new Date().getFullYear(); // Obtener el año actual

    $.ajax({
        url: `/api/ventasMensuales?year=${currentYear}`, // Envía el año actual al backend
        method: 'GET',
        success: function (data) {
            // Asegúrate de que "data" contenga las ventas en el orden de los meses
            if (Array.isArray(data) && data.length === 12) {
                salesChartMensual.data.datasets[0].data = data;
                salesChartMensual.data.datasets[0].label = `Ventas Mensuales (${currentYear})`;
                salesChartMensual.update();
            } else {
                console.error('Datos inválidos recibidos:', data);
            }
        },
        error: function (error) {
            console.error('Error al obtener los datos:', error);
        }
    });
}

// Actualizar los datos inicialmente y periódicamente
updateSalesChartMensual();
setInterval(updateSalesChartMensual, 30000);
