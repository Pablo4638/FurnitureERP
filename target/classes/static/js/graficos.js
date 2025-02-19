document.querySelectorAll('.progresss').forEach((progress) => {
    const percentage = progress.getAttribute('data-percent'); // Lee el porcentaje
    const circle = progress.querySelector('circle');
    const dashArray = 2 * Math.PI * 36; // Circunferencia del círculo (radio = 36)
    const offset = dashArray - (dashArray * percentage) / 100;
  
    // Aplica el offset al círculo
    circle.style.strokeDasharray = dashArray;
    circle.style.strokeDashoffset = offset;
  
    // Actualiza el texto dentro del gráfico
    const percentageText = progress.querySelector('.percentage p');
    if (percentageText) {
      percentageText.textContent = `${percentage}%`;
    }
  });
  