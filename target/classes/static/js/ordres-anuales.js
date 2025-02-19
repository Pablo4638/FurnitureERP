// Gráfico de órdenes de compra anuales
document.addEventListener('DOMContentLoaded', function () { // Asegura que el DOM esté cargado
    const ctxAnual = document.getElementById('ordresChartAnual')?.getContext('2d');

    if (!ctxAnual) {
        console.error("El elemento con id 'ordresChartAnual' no se encontró en el DOM.");
        return; // Termina si no encuentra el canvas
    }

    const ordresChartAnual = new Chart(ctxAnual, {
        type: 'line',
        data: {
            labels: [], // Inicializa vacío
            datasets: [{
                label: 'Órdenes de Compra Anuales',
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
                    beginAtZero: true
                }
            }
        }
    });

    function updateYears() {
        const currentYear = new Date().getFullYear();
        return [currentYear - 3, currentYear - 2, currentYear - 1, currentYear];
    }

    function updateOrdresChartAnual() {
        const years = updateYears();

        $.ajax({
            url: '/api/ordresAnuales', // Endpoint para obtener datos de órdenes anuales
            method: 'GET',
            success: function (data) {
                if (!Array.isArray(data)) {
                    console.error("El formato de la respuesta no es un array:", data);
                    return;
                }

                if (data.length > 4) {
                    data = data.slice(-4);
                }

                ordresChartAnual.data.labels = years;
                ordresChartAnual.data.datasets[0].data = data;
                ordresChartAnual.update();
                console.log("Datos actualizados:", { years, data });
            },
            error: function (xhr, status, error) {
                console.error("Error al obtener las órdenes anuales:", error);
            }
        });
    }

    updateOrdresChartAnual();
    setInterval(updateOrdresChartAnual, 30000); // Actualiza cada 30 segundos
});
