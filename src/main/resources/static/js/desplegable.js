function toggleDropdown() {
    const dropdownMenu = document.getElementById("profile-dropdown-menu");
    dropdownMenu.style.display = (dropdownMenu.style.display === "block") ? "none" : "block";
  }
  
  window.onclick = function(event) {
    if (!event.target.matches('.profile, .profile *')) {
      const dropdownMenu = document.getElementById("profile-dropdown-menu");
      if (dropdownMenu.style.display === "block") {
        dropdownMenu.style.display = "none";
      }
    }
  }