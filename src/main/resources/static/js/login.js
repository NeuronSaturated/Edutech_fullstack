// Obtener referencia al formulario de login y al div donde mostraremos mensajes
const loginForm = document.getElementById('loginForm');
const mensajeDiv = document.getElementById('mensajeLogin');

// Agregar evento al enviar el formulario (submit)
loginForm.addEventListener('submit', async (e) => {
  e.preventDefault(); // Evitar que el formulario recargue la página por defecto

  // Capturar y limpiar los valores de correo y contraseña ingresados
  const datos = {
    correo: document.getElementById('correo').value.trim(),
    password: document.getElementById('password').value.trim()
  };

  try {
    // Hacer una petición POST a la API para login, enviando datos en formato JSON
    const res = await fetch('/api/estudiantes/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos)
    });

    if (res.ok) { // Si la respuesta es exitosa (status 200-299)
      const usuario = await res.json(); // Parsear respuesta JSON

      // Guardar información del usuario en localStorage para sesión
      localStorage.setItem('usuario', JSON.stringify(usuario));

      // Redirigir al usuario según su rol dentro de la aplicación
      if (usuario.rol === 'DOCENTE') {
        window.location.href = 'perfilDocente.html'; // Página para docentes
      } else if (usuario.rol === 'ESTUDIANTE') {
        window.location.href = 'perfil.html'; // Página para estudiantes
      } else if (usuario.rol === 'ADMIN') {
        window.location.href = 'adminDashboard.html'; // Panel de administración
      } else {
        window.location.href = 'perfil.html'; // Redirección por defecto
      }

    } else { // Si la respuesta no fue exitosa
      const error = await res.text(); // Obtener texto con mensaje de error
      mensajeDiv.style.color = 'red'; // Cambiar color del mensaje a rojo
      mensajeDiv.textContent = 'Error: ' + error; // Mostrar error al usuario
    }
  } catch (error) { // Capturar errores de conexión o inesperados
    mensajeDiv.style.color = 'red'; // Color rojo para el mensaje
    mensajeDiv.textContent = 'Error en la conexión con el servidor.'; // Mensaje genérico
  }
});
