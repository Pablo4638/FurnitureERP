function changeLanguage() {
  const selectedLanguage = document.getElementById('languageSelector').value;

  fetch(`/setLanguage?lang=${selectedLanguage}`, {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
      },
  }).then(response => {
      if (response.ok) {
          window.location.reload(); // Recargar para aplicar el idioma
      } else {
          alert('Error al cambiar el idioma.');
      }
  }).catch(error => {
      console.error('Error:', error);
      alert('Error al cambiar el idioma.');
  });
}
