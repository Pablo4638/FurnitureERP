// Gráfico de ventas anuales
const ctxAnual = document.getElementById('salesChartAnual').getContext('2d');
const salesChartAnual = new Chart(ctxAnual, {
    type: 'line',
    data: {
        labels: [], // Inicializa vacío
        datasets: [{
            label: 'Ventas Anuales',
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

function updateSalesChartAnual() {
    const years = updateYears();

    $.ajax({
        url: '/api/ventasAnuales', // Endpoint para ventas anuales (de facturas)
        method: 'GET',
        success: function (data) {
            if (data.length > 4) {
                data = data.slice(-4);
            }

            salesChartAnual.data.labels = years;
            salesChartAnual.data.datasets[0].data = data;
            salesChartAnual.update();
        },
        error: function () {
            console.error("Error al obtener las ventas anuales.");
        }
    });
}

updateSalesChartAnual();
setInterval(updateSalesChartAnual, 30000);