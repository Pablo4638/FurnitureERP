fwindow.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    const hasError = urlParams.get('error') === 'true';
    if (hasError) {
        const popup = document.getElementById('popup');
        popup.classList.add('show');

        setTimeout(() => {
            popup.classList.remove('show');
        }, 4000);
    }
};

// popup.js
window.onload = function() {
    const urlParams = new URLSearchParams(window.location.search);
    const hasError = urlParams.get('error') === 'false';
    if (hasError) {
        const popup = document.getElementById('popup');
        popup.classList.add('show');

        // Ocultar el popup después de 4 segundos
        setTimeout(() => {
            popup.classList.remove('show');
        }, 4000);
    }
};
