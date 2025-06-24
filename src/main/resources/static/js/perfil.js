// Referencias a elementos del DOM que se usarán
const nombreUsuario = document.getElementById('nombreUsuario');
const welcomeMenu = document.getElementById('welcomeMenu');
const toggleMenuBtn = document.getElementById('toggleMenuBtn');
const welcomeContent = document.getElementById('welcomeContent');
const cursosDisponibles = document.getElementById('cursosDisponibles');
const tituloSeccion = document.getElementById('tituloSeccion');

// Al cargar el contenido de la página (DOMContentLoaded)
window.addEventListener('DOMContentLoaded', () => {
  // Obtener usuario guardado en localStorage (sesión)
  const usuarioGuardado = localStorage.getItem('usuario');

  // Si no hay usuario, redirigir a login
  if (!usuarioGuardado) {
    window.location.href = 'login.html';
    return; // Termina la ejecución del evento
  }

  // Parsear datos del usuario
  const usuario = JSON.parse(usuarioGuardado);

  // Mostrar menú de bienvenida
  welcomeMenu.style.display = 'block';

  // Mostrar nombre del usuario en el menú
  nombreUsuario.textContent = usuario.nombre;

  // Cargar y mostrar cursos disponibles para inscripción al cargar la página
  cargarCursosDisponibles();
});

// Evento para abrir/cerrar el menú al hacer click en el botón
toggleMenuBtn.addEventListener('click', () => {
  welcomeContent.classList.toggle('show'); // Mostrar u ocultar contenido del menú
  toggleMenuBtn.classList.toggle('active'); // Cambiar estilo del botón
});

// Eventos para las opciones del menú
document.getElementById('inscripcionCursos').addEventListener('click', (e) => {
  e.preventDefault(); // Prevenir comportamiento por defecto del enlace
  cargarCursosDisponibles(); // Mostrar cursos disponibles para inscribirse
});

document.getElementById('misCursosInscritos').addEventListener('click', (e) => {
  e.preventDefault();
  cargarCursosInscritos(); // Mostrar cursos en los que el estudiante ya está inscrito
});

document.getElementById('gestionarCuenta').addEventListener('click', (e) => {
  e.preventDefault();
  tituloSeccion.textContent = 'Gestionar Cuenta'; // Cambiar título de la sección
  cursosDisponibles.innerHTML = `<p>Funcionalidad Gestionar Cuenta en construcción...</p>`; // Mensaje temporal
});

document.getElementById('evaluaciones').addEventListener('click', (e) => {
  e.preventDefault();
  tituloSeccion.textContent = 'Evaluaciones'; // Cambiar título
  cursosDisponibles.innerHTML = `<p>Funcionalidad Evaluaciones en construcción...</p>`; // Mensaje temporal
});

document.getElementById('cerrarSesion').addEventListener('click', (e) => {
  e.preventDefault();
  localStorage.removeItem('usuario'); // Eliminar sesión guardada
  window.location.href = 'login.html'; // Redirigir a login
});

// Función para cargar cursos disponibles desde el backend
function cargarCursosDisponibles() {
  tituloSeccion.textContent = 'Cursos disponibles para inscripción'; // Título
  fetch('http://localhost:8080/api/cursos') // Petición GET a la API de cursos
    .then(res => {
      if (!res.ok) throw new Error('Error al cargar cursos'); // Error si falla la petición
      return res.json(); // Convertir respuesta a JSON
    })
    .then(cursos => {
      // Si no hay cursos disponibles, mostrar mensaje
      if (cursos.length === 0) {
        cursosDisponibles.innerHTML = `<p>No hay cursos disponibles en este momento.</p>`;
        return;
      }

      // Construir lista HTML con cursos y botón para inscribirse
      let html = '<ul>';
      cursos.forEach(curso => {
        html += `
          <li>
            <strong>${curso.nombre}</strong>: ${curso.descripcion}
            <button class="btnInscribir" data-id="${curso.id}">Inscribirse</button>
          </li>`;
      });
      html += '</ul>';

      // Insertar lista en el contenedor
      cursosDisponibles.innerHTML = html;

      // Agregar evento a cada botón "Inscribirse"
      document.querySelectorAll('.btnInscribir').forEach(btn => {
        btn.disabled = false; // Asegurar que el botón esté habilitado
        btn.textContent = 'Inscribirse'; // Texto por defecto

        // Al hacer click, llamar a función para inscribir curso
        btn.addEventListener('click', () => {
          const cursoId = btn.getAttribute('data-id'); // Obtener id del curso
          inscribirCurso(cursoId, btn); // Ejecutar inscripción
        });
      });
    })
    .catch(() => {
      // Mostrar mensaje de error en caso de fallo
      cursosDisponibles.innerHTML = `<p>Error cargando cursos, intenta nuevamente.</p>`;
    });
}

// Función para inscribir usuario en un curso
function inscribirCurso(cursoId, btnInscribir) {
  const usuario = JSON.parse(localStorage.getItem('usuario')); // Obtener usuario actual

  // Petición POST a la API para inscribir curso
  fetch(`http://localhost:8080/api/inscripciones/estudiante/${usuario.id}/curso/${cursoId}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
  })
  .then(res => {
    if (!res.ok) throw new Error('Error inscribiendo curso'); // Manejar error
    alert('Inscripción realizada con éxito'); // Mensaje éxito
    btnInscribir.disabled = true; // Deshabilitar botón para evitar múltiples inscripciones
    btnInscribir.textContent = 'Inscrito'; // Cambiar texto a 'Inscrito'
  })
  .catch(() => {
    alert('No se pudo inscribir al curso, intenta nuevamente.'); // Mensaje error
  });
}

// Función para cargar cursos en los que el estudiante ya está inscrito
function cargarCursosInscritos() {
  tituloSeccion.textContent = 'Mis cursos inscritos'; // Cambiar título
  const usuario = JSON.parse(localStorage.getItem('usuario')); // Usuario actual

  // Petición GET para obtener cursos inscritos
  fetch(`http://localhost:8080/api/inscripciones/estudiante/${usuario.id}`)
    .then(res => {
      if (!res.ok) throw new Error('Error al cargar cursos inscritos'); // Error si falla
      return res.json(); // Parsear JSON
    })
    .then(cursosInscritos => {
      // Mostrar mensaje si no hay cursos inscritos
      if (cursosInscritos.length === 0) {
        cursosDisponibles.innerHTML = `<p>No estás inscrito en ningún curso aún.</p>`;
        return;
      }

      // Construir lista con cursos inscritos
      let html = '<ul>';
      cursosInscritos.forEach(curso => {
        html += `
          <li>
            <strong>${curso.nombre}</strong>: ${curso.descripcion}
          </li>`;
      });
      html += '</ul>';

      // Insertar lista en contenedor
      cursosDisponibles.innerHTML = html;
    })
    .catch(() => {
      // Mostrar mensaje de error si falla la carga
      cursosDisponibles.innerHTML = `<p>Error cargando cursos inscritos, intenta nuevamente.</p>`;
    });
}
