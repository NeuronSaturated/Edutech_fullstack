// Añade un evento al formulario de registro para manejar el envío
document.getElementById('registerForm').addEventListener('submit', async (e) => {
  e.preventDefault(); // Previene que el formulario se envíe de forma tradicional (recargando la página)

  // Crea un objeto 'estudiante' con los datos ingresados en el formulario
  const estudiante = {
    nombre: document.getElementById('nombre').value.trim(),        // Obtiene y limpia el nombre
    correo: document.getElementById('correo').value.trim(),        // Obtiene y limpia el correo
    password: document.getElementById('password').value.trim(),    // Obtiene y limpia la contraseña
    direccion: document.getElementById('direccion').value.trim(),  // Obtiene y limpia la dirección
    carrera: document.getElementById('carrera').value.trim()       // Obtiene y limpia la carrera
  };

  // Referencia al elemento donde se mostrará el mensaje de éxito o error
  const mensajeDiv = document.getElementById('mensaje');

  try {
    // Envío de los datos al backend (endpoint de registro de estudiantes)
    const res = await fetch('/api/estudiantes/register', {
      method: 'POST',                             // Método HTTP POST para enviar datos
      headers: { 'Content-Type': 'application/json' }, // Cabecera indicando que se envía JSON
      body: JSON.stringify(estudiante)            // Convierte el objeto 'estudiante' a JSON
    });

    if (res.ok) {
      // Si la respuesta es exitosa (status 200–299)
      mensajeDiv.style.color = 'green'; // Cambia color del mensaje a verde
      mensajeDiv.textContent = 'Registro exitoso. Ahora puedes iniciar sesión.'; // Muestra mensaje al usuario
      e.target.reset(); // Limpia el formulario
    } else {
      // Si la respuesta no fue exitosa (ej. 400 o 500)
      const error = await res.text(); // Obtiene el mensaje de error enviado por el backend
      mensajeDiv.style.color = 'red'; // Cambia el color del mensaje a rojo
      mensajeDiv.textContent = 'Error: ' + error; // Muestra el error al usuario
    }
  } catch (error) {
    // Si ocurre un error de red o el servidor no responde
    mensajeDiv.style.color = 'red'; // Cambia el color a rojo
    mensajeDiv.textContent = 'Error en la conexión con el servidor.'; // Mensaje de error general
  }
});
