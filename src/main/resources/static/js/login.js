document.addEventListener("DOMContentLoaded", () => {
  const passwordField = document.getElementById("login__password");
  const toggleIcon = document.getElementById("toggle-password-icon");

  toggleIcon.addEventListener("click", () => {
    // Alternar el tipo de input entre 'password' y 'text'
    if (passwordField.type === "password") {
      passwordField.type = "text";
      toggleIcon.classList.remove("fa-eye-slash");
      toggleIcon.classList.add("fa-eye");
    } else {
      passwordField.type = "password";
      toggleIcon.classList.remove("fa-eye");
      toggleIcon.classList.add("fa-eye-slash");
    }
  });
});
