// Elementos HTML
const profilePic = document.getElementById('profilePic');
const modal = document.getElementById('modal');
const closeModal = document.getElementById('closeModal');

// Mostrar el modal al hacer clic en la imagen
profilePic.addEventListener('click', () => {
    modal.style.display = 'flex'; // Mostrar el modal
});

// Ocultar el modal al hacer clic en el botón de cerrar
closeModal.addEventListener('click', () => {
    modal.style.display = 'none'; // Ocultar el modal
});

// Ocultar el modal al hacer clic fuera del contenido
window.addEventListener('click', (e) => {
    if (e.target === modal) {
        modal.style.display = 'none';
    }
});

const openModalBtn = document.getElementById('openModalBtn');
const passwordModal = document.getElementById('passwordModal');
const closeModalBtn = document.getElementById('closeModalBtn');

// Mostrar el modal cuando se hace clic en el botón
openModalBtn.addEventListener('click', function() {
    passwordModal.style.display = 'flex';
});

// Cerrar el modal cuando se hace clic en el botón de cerrar
closeModalBtn.addEventListener('click', function() {
    passwordModal.style.display = 'none';
});

// Cerrar el modal si el usuario hace clic fuera del contenido del modal
window.addEventListener('click', function(event) {
    if (event.target === passwordModal) {
        passwordModal.style.display = 'none';
    }
});

const openDeleteModalBtn = document.getElementById('openDeleteModalBtn');
    const deleteModal = document.getElementById('deleteModal');
    const closeDeleteModalBtn = document.getElementById('closeDeleteModalBtn');

    // Mostrar el modal cuando se hace clic en el botón
    openDeleteModalBtn.addEventListener('click', function() {
        deleteModal.style.display = 'flex';
    });

    // Cerrar el modal cuando se hace clic en el botón de cerrar
    closeDeleteModalBtn.addEventListener('click', function() {
        deleteModal.style.display = 'none';
    });

    // Cerrar el modal si el usuario hace clic fuera del contenido del modal
    window.addEventListener('click', function(event) {
        if (event.target === deleteModal) {
            deleteModal.style.display = 'none';
        }
    });