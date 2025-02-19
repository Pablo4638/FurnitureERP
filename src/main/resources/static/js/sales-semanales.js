const ctxSemanal = document.getElementById('salesChartSemanal').getContext('2d');

const salesChartSemanal = new Chart(ctxSemanal, {
    type: 'line', // Mantener el tipo 'line' para las líneas
    data: {
        labels: ['Semana 1', 'Semana 2', 'Semana 3', 'Semana 4'], // Etiquetas para las semanas
        datasets: [{
            label: 'Ventas Semanales',
            data: [], // Inicializa vacío
            borderColor: 'rgb(160, 11, 247)',
            backgroundColor: 'rgba(177, 49, 189, 0.2)',
            fill: true,
            borderWidth: 1
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            y: {
                beginAtZero: true, // Comenzar el eje Y desde 0
            }
        }
    }
});

function updateSalesChartSemanal() {
    $.ajax({
        url: '/api/ventasSemanales',
        method: 'GET',
        success: function (data) {
            if (Array.isArray(data) && data.length === 4) { // Verificar que haya 4 semanas
                // Mapear los datos a un formato adecuado para Chart.js
                const formattedData = data.map((ventas, index) => ventas);

                salesChartSemanal.data.datasets[0].data = formattedData;
                salesChartSemanal.update();
            } else {
                console.error('Datos inválidos recibidos:', data);
            }
        },
        error: function (error) {
            console.error('Error al obtener los datos:', error);
        }
    });
}

// Llamar a la función para actualizar el gráfico al cargar la página
updateSalesChartSemanal();

// Actualizar el gráfico cada 4 semanas (28 días en milisegundos)
setInterval(updateSalesChartSemanal, 28 * 24 * 60 * 60 * 1000); // 28 días
