// Función para abrir/cerrar el chatbox
function toggleChatbox() {
  const chatbox = document.getElementById("chatbox");
  chatbox.style.display =
    chatbox.style.display === "none" || chatbox.style.display === ""
      ? "flex"
      : "none";
}

// Función para enviar mensajes a la API
function enviarMensaje() {
  const input = document.getElementById("chat-input");
  const mensaje = input.value;

  if (mensaje.trim() === "") return;

  // Agregar el mensaje del usuario al chat
  agregarMensaje("Tú", mensaje);

  // Mostrar mensaje de carga
  agregarMensaje("NaturaMobles Suport", "Cargando...");

  // Enviar petición a la API
  fetch(`/api/groq/preguntar?mensaje=${encodeURIComponent(mensaje)}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Error del servidor: ${response.statusText}`);
      }
      return response.text(); // Leer la respuesta como texto siempre
    })
    .then((text) => {
      let respuesta;
    
      // Intentar detectar si es JSON
      try {
        const parsedData = JSON.parse(text);
        if (parsedData && parsedData.choices && parsedData.choices[0] && parsedData.choices[0].message) {
          respuesta = parsedData.choices[0].message.content;
        } else {
          respuesta = JSON.stringify(parsedData, null, 2); // JSON formateado
        }
      } catch (error) {
        respuesta = text; // Si no es JSON, usarlo como texto
      }
    
      // Ajustar el texto al ancho fijo del chatbox
      respuesta = ajustarTextoFijo(respuesta);
    
      // Actualizar el mensaje del soporte
      actualizarMensajeSoporte(respuesta);
    })
    .catch((error) => {
      console.error("Error procesando la solicitud:", error);
      actualizarMensajeSoporte("Hubo un error con la solicitud.");
    });

  input.value = ""; // Limpiar el campo
}

// Actualizar mensaje de soporte al final de la conversación
function actualizarMensajeSoporte(respuesta) {
  const chatBody = document.getElementById("chat-body");
  const lastMessage = chatBody.querySelector("div:last-child");
  
  if (lastMessage && lastMessage.querySelector("strong").textContent === "NaturaMobles Suport:") {
    lastMessage.querySelector("pre").textContent = respuesta; // Actualizar el mensaje
  } else {
    agregarMensaje("NaturaMobles Suport", respuesta); // Agregar un nuevo mensaje si no existe
  }
}

// Detectar la tecla Enter en el campo de entrada
document.getElementById("chat-input").addEventListener("keydown", (event) => {
  if (event.key === "Enter") {
    enviarMensaje(); // Llamar a la función de enviar mensaje
    event.preventDefault(); // Evitar saltos de línea en el campo de texto
  }
});

// Función para agregar mensajes al chat
function agregarMensaje(autor, mensaje) {
  const chatBody = document.getElementById("chat-body");
  const mensajeHTML = `
      <div>
        <strong>${autor}:</strong>
        <pre>${mensaje}</pre>
      </div>
    `;
  chatBody.innerHTML += mensajeHTML;
  chatBody.scrollTop = chatBody.scrollHeight; // Hacer scroll hacia abajo
}

// Función para ajustar texto al ancho fijo del chatbox
function ajustarTextoFijo(texto) {
  const anchoFijo = 420; // Ancho fijo del chatbox
  const font = "16px Arial"; // Fuente base (ajustar según CSS)
  const canvas = document.createElement("canvas");
  const context = canvas.getContext("2d");
  context.font = font;

  return texto.split(" ").reduce((lines, word) => {
    const currentLine = lines[lines.length - 1] || "";
    const width = context.measureText(`${currentLine} ${word}`).width;

    if (width < anchoFijo) {
      lines[lines.length - 1] = `${currentLine} ${word}`.trim();
    } else {
      lines.push(word); // Crear una nueva línea si se excede el ancho
    }
    return lines;
  }, [""]).join("\n");
}
