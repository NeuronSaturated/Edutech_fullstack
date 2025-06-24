const nombreUsuario = document.getElementById('nombreUsuario');
const nombreDocente = document.getElementById('nombreDocente');
const welcomeMenu = document.getElementById('welcomeMenu');
const toggleMenuBtn = document.getElementById('toggleMenuBtn');
const welcomeContent = document.getElementById('welcomeContent');
const contenidoPrincipal = document.getElementById('contenidoPrincipal');

window.addEventListener('DOMContentLoaded', () => {
  const usuarioGuardado = localStorage.getItem('usuario');
  if (!usuarioGuardado) {
    window.location.href = 'login.html';
    return;
  }

  const usuario = JSON.parse(usuarioGuardado);

  if (usuario.rol !== 'DOCENTE') {
    alert('No tienes permisos para acceder aquí');
    window.location.href = 'login.html';
    return;
  }

  welcomeMenu.style.display = 'flex';
  nombreUsuario.textContent = usuario.nombre;
  nombreDocente.textContent = usuario.nombre;

  mostrarMisCursos(usuario.id);
});

toggleMenuBtn.addEventListener('click', () => {
  welcomeContent.classList.toggle('show');
  toggleMenuBtn.classList.toggle('active');
});

document.getElementById('evaluarAlumnos').addEventListener('click', async (e) => {
  e.preventDefault();
  const usuario = JSON.parse(localStorage.getItem('usuario'));

  // Obtener los cursos del docente
  const res = await fetch(`http://localhost:8080/api/cursos/docente/${usuario.id}`);
  if (!res.ok) {
    contenidoPrincipal.innerHTML = `<p>Error cargando cursos para evaluación.</p>`;
    return;
  }

  const cursos = await res.json();

  // Mostrar lista para seleccionar curso
  if (cursos.length === 0) {
    contenidoPrincipal.innerHTML = `<p>No tienes cursos asignados para evaluar.</p>`;
    return;
  }

  let html = `<h3>Selecciona un curso para evaluar</h3><select id="cursoSeleccionado"><option disabled selected>Selecciona...</option>`;
  cursos.forEach(curso => {
    html += `<option value="${curso.id}">${curso.nombre}</option>`;
  });
  html += `</select>
  <button id="irAEvaluacion" class="btn-ir">Ir</button>
  <button id="verNotasCurso" class="btn-ir" style="margin-left: 8px;">Ver Notas</button>`;

  document.getElementById("irAEvaluacion").addEventListener("click", () => {
  const cursoId = select.value;
  if (cursoId) mostrarFormularioEvaluacion(cursoId);
});

document.getElementById("verNotasCurso").addEventListener("click", () => {
  const cursoId = select.value;
  if (cursoId) mostrarNotasCurso(cursoId);
});

  contenidoPrincipal.innerHTML = html;

  // Escuchar click en "Ir"
  document.getElementById('irAEvaluacion').addEventListener('click', () => {
    const cursoId = document.getElementById('cursoSeleccionado').value;
    if (cursoId) {
      mostrarFormularioEvaluacion(cursoId);
    }
  });
});


document.getElementById('evaluarAlumnos').addEventListener('click', (e) => {
  e.preventDefault();
  const usuario = JSON.parse(localStorage.getItem('usuario'));

  // Mostrar select y botones
  fetch(`http://localhost:8080/api/cursos/docente/${usuario.id}`)
    .then(res => res.json())
    .then(cursos => {
      if (cursos.length === 0) {
        contenidoPrincipal.innerHTML = `<p>No tienes cursos asignados.</p>`;
        return;
      }

      let html = `
        <h3>Bienvenido, docente</h3>
        <p><strong>Selecciona un curso para evaluar</strong></p>
        <select id="selectCursoEvaluar">
          <option value="">Selecciona...</option>
      `;

      cursos.forEach(c => {
        html += `<option value="${c.id}">${c.nombre}</option>`;
      });

      html += `</select>
        <button id="btnIrEvaluar" style="margin-left: 10px;">Ir</button>
        <button id="btnVerNotas" style="margin-left: 5px;">Ver Evaluaciones</button>
      `;

      contenidoPrincipal.innerHTML = html;

      // Evento para ir a evaluar
      document.getElementById('btnIrEvaluar').addEventListener('click', () => {
        const cursoId = document.getElementById("selectCursoEvaluar").value;
        if (!cursoId) return alert("Selecciona un curso primero.");
        mostrarFormularioEvaluacion(cursoId);
      });

      // Evento para ver evaluaciones existentes
      document.getElementById('btnVerNotas').addEventListener('click', () => {
        const cursoId = document.getElementById("selectCursoEvaluar").value;
        if (!cursoId) return alert("Selecciona un curso primero.");
        verEvaluacionesRegistradas(cursoId);
      });
    });
});


document.getElementById('cerrarSesion').addEventListener('click', (e) => {
  e.preventDefault();
  localStorage.removeItem('usuario');
  window.location.href = 'login.html';
});

function mostrarMisCursos(docenteId) {
  fetch(`http://localhost:8080/api/cursos/docente/${docenteId}`)
    .then(res => {
      if (!res.ok) throw new Error('Error al cargar cursos');
      return res.json();
    })
    .then(cursos => {
      let html = `<h3>Mis Cursos</h3>`;

      if (cursos.length === 0) {
        html += `<p>No tienes cursos creados aún.</p>`;
      } else {
        html += `<div>`;
        cursos.forEach(curso => {
          html += `
            <div class="curso-card">
              <h4>${curso.nombre}</h4>
              <p>${curso.descripcion}</p>
              <button class="btnEliminar" data-id="${curso.id}">Eliminar</button>
              <button class="btnVerEstudiantes" data-id="${curso.id}">Ver estudiantes</button>
              <button class="btnEvaluar" data-id="${curso.id}">Evaluar</button>

            </div>`;
        });
        html += `</div>`;
      }

      html += `<button id="btnNuevoCurso" class="btn-primary" style="margin-top:20px;">Crear Nuevo Curso</button>`;
      contenidoPrincipal.innerHTML = html;

      document.querySelectorAll('.btnVerEstudiantes').forEach(btn => {
        btn.addEventListener('click', () => {
          const cursoId = btn.getAttribute('data-id');
          verEstudiantes(cursoId);
        });
      });

      document.getElementById('btnNuevoCurso').addEventListener('click', mostrarFormularioNuevoCurso);

      document.querySelectorAll('.btnEliminar').forEach(btn => {
        btn.addEventListener('click', () => {
          const idCurso = btn.getAttribute('data-id');
          if (confirm('¿Estás seguro que quieres eliminar este curso?')) {
            eliminarCurso(idCurso, docenteId);
          }
        });
      });
      document.querySelectorAll('.btnEvaluar').forEach(btn => {
        btn.addEventListener('click', () => {
          const cursoId = btn.getAttribute('data-id');
            mostrarFormularioEvaluacion(cursoId);
  });
});

    })
    .catch(() => {
      contenidoPrincipal.innerHTML = `<p>Error cargando cursos. Intenta de nuevo.</p>`;
    });
}

function mostrarFormularioNuevoCurso() {
  contenidoPrincipal.innerHTML = `
    <div class="formulario-grande">
      <h3>Crear Nuevo Curso</h3>
      <form id="formNuevoCurso">
        <label for="nombreCurso">Nombre:</label><br/>
        <input type="text" id="nombreCurso" name="nombreCurso" required /><br/><br/>
        <label for="descCurso">Descripción:</label><br/>
        <textarea id="descCurso" name="descCurso" rows="4" required></textarea><br/><br/>
        <button type="submit" class="btn-primary">Crear Curso</button>
        <button type="button" id="btnCancelar" style="margin-left: 12px;">Cancelar</button>
      </form>
      <div id="mensajeCurso" style="color: red; margin-top: 10px;"></div>
    </div>
  `;

  document.getElementById('formNuevoCurso').addEventListener('submit', (e) => {
    e.preventDefault();
    crearCurso();
  });

  document.getElementById('btnCancelar').addEventListener('click', () => {
    const usuario = JSON.parse(localStorage.getItem('usuario'));
    mostrarMisCursos(usuario.id);
  });
}

function crearCurso() {
  const nombre = document.getElementById('nombreCurso').value.trim();
  const descripcion = document.getElementById('descCurso').value.trim();
  const usuario = JSON.parse(localStorage.getItem('usuario'));

  if (!nombre || !descripcion) {
    document.getElementById('mensajeCurso').textContent = 'Todos los campos son obligatorios';
    return;
  }

  fetch(`http://localhost:8080/api/cursos/docente/${usuario.id}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nombre, descripcion })
  })
    .then(res => {
      if (!res.ok) throw new Error('Error creando curso');
      return res.json();
    })
    .then(() => {
      mostrarMisCursos(usuario.id);
    })
    .catch(() => {
      document.getElementById('mensajeCurso').textContent = 'No se pudo crear el curso, intenta nuevamente.';
    });
}

function eliminarCurso(idCurso, docenteId) {
  fetch(`http://localhost:8080/api/cursos/${idCurso}`, {
    method: 'DELETE'
  })
    .then(res => {
      if (!res.ok) throw new Error('Error eliminando curso');
      mostrarMisCursos(docenteId);
    })
    .catch(() => {
      alert('No se pudo eliminar el curso, intenta nuevamente.');
    });
}

function verEstudiantes(cursoId) {
  fetch(`http://localhost:8080/api/inscripciones/curso/${cursoId}/estudiantes`)
    .then(res => {
      if (!res.ok) throw new Error('Error al cargar los estudiantes');
      return res.json();
    })
    .then(estudiantes => {
      let html = `<h3>Estudiantes Inscritos</h3>`;
      if (estudiantes.length === 0) {
        html += `<p>No hay estudiantes inscritos aún.</p>`;
      } else {
        html += `<ul>`;
        estudiantes.forEach(estudiante => {
          html += `<li>${estudiante.nombre} - ${estudiante.correo}</li>`;
        });
        html += `</ul>`;
      }
      contenidoPrincipal.innerHTML = html;
    })
    .catch(() => {
      contenidoPrincipal.innerHTML = `<p>Error al cargar estudiantes, intenta nuevamente.</p>`;
    });
}

function mostrarFormularioEvaluacion(cursoId) {
  let cantidad = 0;

  fetch(`http://localhost:8080/api/inscripciones/curso/${cursoId}/estudiantes`)
    .then(res => res.json())
    .then(estudiantes => {
      if (estudiantes.length === 0) {
        contenidoPrincipal.innerHTML = `<p>No hay estudiantes inscritos en este curso.</p>`;
        return;
      }

      // Paso 1: pedir al docente cuántas notas va a poner
      contenidoPrincipal.innerHTML = `
        <h3>Evaluar Estudiantes</h3>
        <label for="cantidadNotas">¿Cuántas notas desea ingresar?</label>
        <input type="number" id="cantidadNotas" min="1" max="10" value="1" />
        <button id="generarNotas">Generar Formulario</button>
        <div id="formEvaluacion"></div>
      `;

      document.getElementById('generarNotas').addEventListener('click', () => {
        const cantidad = parseInt(document.getElementById('cantidadNotas').value);
        if (isNaN(cantidad) || cantidad < 1) return;

        const formDiv = document.getElementById('formEvaluacion');
        let html = `<form id="evaluacionForm">`;

       // Generar inputs de título y ponderación por cada nota
      for (let i = 0; i < cantidad; i++) {
      html += `
      <label for="titulo-${i}">Título Evaluación ${i + 1}</label>
      <input type="text" name="titulo-${i}" id="titulo-${i}" required />


      <label for="pond-${i}">Ponderación Nota ${i + 1} (%)</label>
      <input type="number" name="ponderaciones" id="pond-${i}" min="1" max="100" required /><br/><br/>
  `;
}


        // Tabla de estudiantes
        // Tabla de estudiantes
        html += `<table border="1"><tr><th>Estudiante</th>`;
        for (let i = 0; i < cantidad; i++) html += `<th>Nota ${i + 1}</th>`;
        html += `<th>Nota Final</th></tr>`;

        estudiantes.forEach(est => {
        html += `<tr><td>${est.nombre}</td>`;
        for (let i = 0; i < cantidad; i++) {
          html += `<td><input type="number" step="0.1" min="1" max="7" name="nota-${est.id}-${i}" required /></td>`;
        }
        html += `<td><span id="notaFinal-${est.id}">-</span></td></tr>`;
      });


        html += `</table><br/>
          <button type="submit" class="btn-primary">Guardar Evaluaciones</button>
        </form>`;

        
        
        
        
        formDiv.innerHTML = html;
        // Activar el cálculo dinámico de nota final
          estudiantes.forEach(est => {
            for (let i = 0; i < cantidad; i++) {
              const input = document.querySelector(`[name="nota-${est.id}-${i}"]`);
              input.addEventListener("input", () => calcularNotaFinal(est.id, cantidad));
            }
          });


        // Evento para enviar las evaluaciones
        document.getElementById('evaluacionForm').addEventListener('submit', e => {
          e.preventDefault();

          const ponderaciones = [...document.getElementsByName('ponderaciones')].map(input => parseFloat(input.value));
          const suma = ponderaciones.reduce((a, b) => a + b, 0);

          if (suma !== 100) {
            alert("Las ponderaciones deben sumar 100%");
            return;
          }

          const evaluaciones = [];
          estudiantes.forEach(est => {
            let notaFinal = 0;
            ponderaciones.forEach((pond, i) => {
              const nota = parseFloat(document.querySelector(`[name="nota-${est.id}-${i}"]`).value);
              const titulo = document.querySelector(`[name="titulo-${i}"]`).value;
              
              //push OJO <-------------------
              
              evaluaciones.push({
              estudiante: { id: est.id },
              curso: { id: cursoId },
              nota: nota,
              ponderacion: pond,
              fecha: new Date().toISOString().split('T')[0],
              titulo: titulo
            });

            });
            // Muestra nota final en consola (opcional, útil para pruebas)
            console.log(`Nota final para ${est.nombre}: ${notaFinal.toFixed(2)}`);
          });
          console.log("Evaluaciones a enviar:");
          console.table(evaluaciones);
          // POST al backend
          fetch("http://localhost:8080/api/evaluaciones/lote", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(evaluaciones)
          })
          .then(res => {
            if (!res.ok) throw new Error("Error guardando evaluaciones");
            alert("Notas guardadas correctamente");
            mostrarMisCursos(JSON.parse(localStorage.getItem('usuario')).id);
          })
          .catch(() => alert("Error al guardar las evaluaciones."));
        });
      });
    });



}

function calcularNotaFinal(estudianteId, cantidadNotas) {
    const ponderacionesInputs = document.getElementsByName("ponderaciones");
    const ponderaciones = [...ponderacionesInputs].map(i => parseFloat(i.value) || 0);

    let total = 0;
    for (let i = 0; i < cantidadNotas; i++) {
      const notaInput = document.querySelector(`[name="nota-${estudianteId}-${i}"]`);
      const nota = parseFloat(notaInput.value) || 0;
      total += nota * (ponderaciones[i] / 100);
    }

    const finalSpan = document.getElementById(`notaFinal-${estudianteId}`);
    finalSpan.textContent = total.toFixed(2);
}

function mostrarNotasCurso(cursoId) {
  fetch(`http://localhost:8080/api/evaluaciones/curso/${cursoId}`)
    .then(res => res.json())
    .then(evaluaciones => {
      if (evaluaciones.length === 0) {
        contenidoPrincipal.innerHTML = `<p>No hay evaluaciones registradas aún para este curso.</p>`;
        return;
      }

      /*let html = `<h3>Notas registradas</h3><table border="1">
        <tr>
          <th>Estudiante</th>
          <th>Nota</th>
          <th>Ponderación (%)</th>
          <th>Fecha</th>
        </tr>`;*/

        let html = `<h3>Notas registradas</h3><table border="1">
        <tr>
          <th>ID Estudiante</th>
          <th>Nombre</th>
          <th>Nota</th>
          <th>Ponderación (%)</th>
          <th>Fecha</th>
          <th>Título</th>
        </tr>`;


      /*evaluaciones.forEach(ev => {
        html += `<tr>
          <td>${ev.estudianteNombre || 'ID ' + ev.estudianteId}</td>
          <td>${ev.nota}</td>
          <td>${ev.ponderacion}</td>
          <td>${ev.fecha || '-'}</td>
        </tr>`;
      });*/

      evaluaciones.forEach(ev => {
      html += `<tr>
      <td>${ev.estudiante?.id || '-'}</td>
      <td>${ev.estudiante?.nombre || 'Desconocido'}</td>
      <td><input type="number" step="0.1" min="1" max="7" value="${ev.nota}" data-id="${ev.id}" class="notaInput" /></td>
      <td><input type="number" min="1" max="100" value="${ev.ponderacion}" data-id="${ev.id}" class="pondInput" /></td>
      <td>${ev.fecha || '-'}</td>
      <td><input type="text" class="tituloInput" value="${ev.titulo || ''}" data-id="${ev.id}" /></td>
      <td><input type="hidden" class="evalId" value="${ev.id}" /></td>
    </tr>`;
  });


      html += `</table>`;
      html += `<button id="btnGuardarCambios" class="btn-primary">Guardar Cambios</button>`;
      contenidoPrincipal.innerHTML = html;

    })
    .catch(() => {
      contenidoPrincipal.innerHTML = `<p>Error al cargar las evaluaciones.</p>`;
    });
}


/*function verEvaluacionesRegistradas(cursoId) {
  fetch(`http://localhost:8080/api/evaluaciones/curso/${cursoId}`)
    .then(res => res.json())
    .then(evaluaciones => {
      if (evaluaciones.length === 0) {
        contenidoPrincipal.innerHTML = `<p>No hay evaluaciones registradas para este curso.</p>`;
        return;
      }

      let html = `<h3>Evaluaciones Registradas</h3>`;
      html += `<table border="1"><tr>
      <th style="display:none;">ID</th>
      <th>Estudiante</th><th>Nota</th><th>Ponderación (%)</th><th>Fecha</th><th>Título</th>
    </tr>`;


      evaluaciones.forEach(ev => {
        html += `<tr>
          <td style="display:none;">${ev.id}</td>
          <td>${ev.estudianteNombre || 'Desconocido'}</td>
          <td><input type="number" step="0.1" min="1" max="7" value="${ev.nota}" data-id="${ev.id}" class="notaInput" /></td>
          <td><input type="number" min="1" max="100" value="${ev.ponderacion}" data-id="${ev.id}" class="pondInput" /></td>
          <td>${ev.fecha || '-'}</td>
          <td><input type="text" class="tituloInput" value="${ev.titulo || ''}" data-id="${ev.id}" /></td>

        </tr>`;
      });

      html += `</table><br/>
        <button id="btnGuardarCambios" class="btn-primary">Guardar Cambios</button>`;

      contenidoPrincipal.innerHTML = html;

      document.getElementById("btnGuardarCambios").addEventListener("click", guardarCambiosEvaluaciones);
    })
    .catch(() => {
      contenidoPrincipal.innerHTML = `<p>Error al cargar evaluaciones.</p>`;
    });
}*/

/*function verEvaluacionesRegistradas(cursoId) {
  fetch(`http://localhost:8080/api/evaluaciones/curso/${cursoId}`)
    .then(res => res.json())
    .then(evaluaciones => {
      if (evaluaciones.length === 0) {
        contenidoPrincipal.innerHTML = `<p>No hay evaluaciones registradas para este curso.</p>`;
        return;
      }

      let html = `<h3>Evaluaciones Registradas</h3>`;
      html += `<table border="1"><tr>
        <th style="display:none;">ID</th>
        <th>Estudiante</th>
        <th>Nota</th>
        <th>Ponderación (%)</th>
        <th>Fecha</th>
        <th>Título</th>
        <th>Eliminar</th>
      </tr>`;

      evaluaciones.forEach(ev => {
        html += `<tr>
          <td style="display:none;">${ev.id}</td>
          <td>${ev.estudianteNombre || 'Desconocido'}</td>
          <td><input type="number" step="0.1" min="1" max="7" value="${ev.nota}" data-id="${ev.id}" class="notaInput" /></td>
          <td><input type="number" min="1" max="100" value="${ev.ponderacion}" data-id="${ev.id}" class="pondInput" /></td>
          <td>${ev.fecha || '-'}</td>
          <td><input type="text" class="tituloInput" value="${ev.titulo || ''}" data-id="${ev.id}" /></td>
          <td><button onclick="eliminarEvaluacion(${ev.id})" style="color:red;">❌</button></td>
        </tr>`;
      });

      html += `</table><br/>
        <button id="btnGuardarCambios" class="btn-primary">Guardar Cambios</button>`;

      contenidoPrincipal.innerHTML = html;

      document.getElementById("btnGuardarCambios").addEventListener("click", guardarCambiosEvaluaciones);
    })
    .catch(() => {
      contenidoPrincipal.innerHTML = `<p>Error al cargar evaluaciones.</p>`;
    });
}*/

/*function verEvaluacionesRegistradas(cursoId) {
  // Guarda el cursoId para futuras acciones como eliminar
  localStorage.setItem("cursoSeleccionado", cursoId);

  fetch(`http://localhost:8080/api/evaluaciones/curso/${cursoId}`)
    .then(res => res.json())
    .then(evaluaciones => {
      if (evaluaciones.length === 0) {
        contenidoPrincipal.innerHTML = `<p>No hay evaluaciones registradas para este curso.</p>`;
        return;
      }

      let html = `<h3>Evaluaciones Registradas</h3>`;
      html += `<table border="1"><tr>
        <th style="display:none;">ID</th>
        <th>Estudiante</th>
        <th>Nota</th>
        <th>Ponderación (%)</th>
        <th>Fecha</th>
        <th>Título</th>
        <th>Eliminar</th>
      </tr>`;

      /*evaluaciones.forEach(ev => {
        html += `<tr>
          <td style="display:none;">${ev.id}</td>
          <td>${ev.estudianteNombre || 'Desconocido'}</td>
          <td><input type="number" step="0.1" min="1" max="7" value="${ev.nota}" data-id="${ev.id}" class="notaInput" /></td>
          <td><input type="number" min="1" max="100" value="${ev.ponderacion}" data-id="${ev.id}" class="pondInput" /></td>
          <td>${ev.fecha || '-'}</td>
          <td><input type="text" class="tituloInput" value="${ev.titulo || ''}" data-id="${ev.id}" /></td>
          <td><button onclick="eliminarEvaluacion(${ev.id})" style="color:red;">❌</button></td>
        </tr>`;
      });*/

      /*evaluaciones.forEach(ev => {
      html += `<tr data-est-id="${ev.estudianteId}"> <!-- Atributo agregado -->`;
      html += `
        <td style="display:none;">${ev.id}</td>
        <td>${ev.estudianteNombre || 'Desconocido'}</td>
        <td><input type="number" step="0.1" min="1" max="7" value="${ev.nota}" data-id="${ev.id}" class="notaInput" /></td>
        <td><input type="number" min="1" max="100" value="${ev.ponderacion}" data-id="${ev.id}" class="pondInput" /></td>
        <td>${ev.fecha || '-'}</td>
        <td><input type="text" class="tituloInput" value="${ev.titulo || ''}" data-id="${ev.id}" /></td>
        <td><button onclick="eliminarEvaluacion(${ev.id})" style="color:red;">❌</button></td>
      `;

      html += `</table><br/>
        <button id="btnGuardarCambios" class="btn-primary">Guardar Cambios</button>`;

      contenidoPrincipal.innerHTML = html;

      document.getElementById("btnGuardarCambios").addEventListener("click", guardarCambiosEvaluaciones);
    })
    .catch(() => {
      contenidoPrincipal.innerHTML = `<p>Error al cargar evaluaciones.</p>`;
    });*/

    function verEvaluacionesRegistradas(cursoId) {
      // Guarda el cursoId para futuras acciones como eliminar
      localStorage.setItem("cursoSeleccionado", cursoId);

      fetch(`http://localhost:8080/api/evaluaciones/curso/${cursoId}`)
        .then(res => res.json())
        .then(evaluaciones => {
          if (evaluaciones.length === 0) {
            contenidoPrincipal.innerHTML = `<p>No hay evaluaciones registradas para este curso.</p>`;
            return;
          }

          let html = `<h3>Evaluaciones Registradas</h3>`;
          html += `<table border="1"><tr>
            <th style="display:none;">ID</th>
            <th>Estudiante</th>
            <th>Nota</th>
            <th>Ponderación (%)</th>
            <th>Fecha</th>
            <th>Título</th>
            <th>Eliminar</th>
          </tr>`;

          /*// ✅ Aquí va el forEach completo
          evaluaciones.forEach(ev => {
            html += `<tr data-est-id="${ev.estudianteId}">
              <td style="display:none;">${ev.id}</td>
              <td>${ev.estudianteNombre || 'Desconocido'}</td>
              <td><input type="number" step="0.1" min="1" max="7" value="${ev.nota}" data-id="${ev.id}" class="notaInput" /></td>
              <td><input type="number" min="1" max="100" value="${ev.ponderacion}" data-id="${ev.id}" class="pondInput" /></td>
              <td>${ev.fecha || '-'}</td>
              <td><input type="text" class="tituloInput" value="${ev.titulo || ''}" data-id="${ev.id}" /></td>
              <td><button onclick="eliminarEvaluacion(${ev.id})" style="color:red;">❌</button></td>
            </tr>`;
          });*/

          evaluaciones.forEach(ev => {
          html += `<tr
            data-id="${ev.id}"
            data-curso-id="${cursoId}"
            data-estudiante-id="${ev.estudianteId}">
            <td>${ev.estudianteNombre || 'Desconocido'}</td>
            <td><input type="number" step="0.1" min="1" max="7" value="${ev.nota}" data-id="${ev.id}" class="notaInput" /></td>
            <td><input type="number" min="1" max="100" value="${ev.ponderacion}" data-id="${ev.id}" class="pondInput" /></td>
            <td>${ev.fecha || '-'}</td>
            <td><input type="text" class="tituloInput" value="${ev.titulo || ''}" data-id="${ev.id}" /></td>
            <td><button onclick="eliminarEvaluacion(${ev.id})" style="color:red;">❌</button></td>
          </tr>`;
        });



          // ✅ Este cierre debe ir FUERA del forEach
          html += `</table><br/>
            <button id="btnGuardarCambios" class="btn-primary">Guardar Cambios</button>`;

          contenidoPrincipal.innerHTML = html;

          // Evento botón
          document.getElementById("btnGuardarCambios").addEventListener("click", guardarCambiosEvaluaciones);
        })
        .catch(() => {
          contenidoPrincipal.innerHTML = `<p>Error al cargar evaluaciones.</p>`;
        });
    }

    



/*function eliminarEvaluacion(id) {
  if (confirm("¿Estás seguro de que quieres eliminar esta evaluación?")) {
    fetch(`http://localhost:8080/api/evaluaciones/${id}`, {
      method: "DELETE"
    })
      .then(res => {
        if (!res.ok) throw new Error("Error al eliminar");
        alert("Evaluación eliminada correctamente.");
        verEvaluacionesRegistradas(localStorage.getItem("cursoSeleccionado")); // recarga lista sin refresh
      })
      .catch(() => alert("No se pudo eliminar la evaluación."));
  }
}*/

function eliminarEvaluacion(id) {
  if (confirm("¿Estás seguro de que quieres eliminar esta evaluación?")) {
    fetch(`http://localhost:8080/api/evaluaciones/${id}`, {
      method: "DELETE"
    })
      .then(res => {
        if (!res.ok) throw new Error("Error al eliminar");
        alert("Evaluación eliminada correctamente.");
        const cursoId = parseInt(localStorage.getItem("cursoSeleccionado"));
        verEvaluacionesRegistradas(cursoId);
      })
      .catch(() => alert("No se pudo eliminar la evaluación."));
  }
}





/*function guardarCambiosEvaluaciones() {
  const inputsNota = document.querySelectorAll(".notaInput");
  const inputsPond = document.querySelectorAll(".pondInput");
  const inputsTitulo = document.querySelectorAll(".tituloInput");
  const actualizaciones = [];

  inputsNota.forEach((notaInput, i) => {
    const id = notaInput.getAttribute("data-id");
    const nota = parseFloat(notaInput.value);
    const ponderacion = parseFloat(inputsPond[i].value);
    const titulo = inputsTitulo[i].value;
    actualizaciones.push({ id, nota, ponderacion,titulo });
  });

  fetch("http://localhost:8080/api/evaluaciones/actualizar", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(actualizaciones)
  })
    .then(res => {
      if (!res.ok) throw new Error("Error actualizando");
      alert("Evaluaciones actualizadas correctamente.");
    })
    .catch(() => alert("No se pudo actualizar."));
}*/

/*function guardarCambiosEvaluaciones() {
  const notas = document.querySelectorAll(".notaInput");
  const ponderaciones = document.querySelectorAll(".pondInput");
  const titulos = document.querySelectorAll(".tituloInput");

  const actualizaciones = [];

  notas.forEach((inputNota, i) => {
    const id = inputNota.getAttribute("data-id");  // se toma desde el atributo de cada input
    const nota = parseFloat(inputNota.value);
    const ponderacion = parseFloat(ponderaciones[i].value);
    const titulo = titulos[i].value;

    actualizaciones.push({
      id,
      nota,
      ponderacion,
      titulo
    });
  });

  // Enviar al backend
  fetch("http://localhost:8080/api/evaluaciones/actualizar", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(actualizaciones)
  })
    .then(res => {
      if (!res.ok) throw new Error("Error al actualizar");
      alert("Evaluaciones actualizadas correctamente.");
    })
    .catch(() => alert("No se pudo actualizar."));
}*/

/*function guardarCambiosEvaluaciones() {
  const notas = document.querySelectorAll(".notaInput");
  const ponderaciones = document.querySelectorAll(".pondInput");
  const titulos = document.querySelectorAll(".tituloInput");

  const actualizaciones = [];

  notas.forEach((inputNota, i) => {
    const id = inputNota.getAttribute("data-id");
    const nota = parseFloat(inputNota.value);
    const ponderacion = parseFloat(ponderaciones[i].value);
    const titulo = titulos[i].value;

    // 👇 Obtener el estudianteId desde la fila <tr data-est-id="...">
    const tr = inputNota.closest("tr");
    const estudianteId = parseInt(tr.getAttribute("data-est-id"));
    const cursoId = parseInt(localStorage.getItem("cursoSeleccionado")); // obtenido de memoria local

    actualizaciones.push({
      id,
      nota,
      ponderacion,
      titulo,
      estudiante: { id: estudianteId },
      curso: { id: cursoId }
    });
  });

  fetch("http://localhost:8080/api/evaluaciones/actualizar", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(actualizaciones)
  })
    .then(res => {
      if (!res.ok) throw new Error("Error al actualizar");
      alert("Evaluaciones actualizadas correctamente.");
    })
    .catch(() => alert("No se pudo actualizar.");
}*/
/*function guardarCambiosEvaluaciones() {
  const filas = document.querySelectorAll("table tr[data-estudiante-id]");
  const actualizaciones = [];
  const cursoId = localStorage.getItem("cursoSeleccionado");


  filas.forEach(fila => {
    const idEvaluacion = fila.querySelector(".notaInput")?.getAttribute("data-id");
    const nota = parseFloat(fila.querySelector(".notaInput")?.value.replace(',', '.'));
    const ponderacion = parseFloat(fila.querySelector(".pondInput")?.value.replace(',', '.'));

    const titulo = fila.querySelector(".tituloInput")?.value;
    const estudianteId = fila.getAttribute("data-estudiante-id");

    if (idEvaluacion && !isNaN(nota) && !isNaN(ponderacion)) {
      actualizaciones.push({
        id: idEvaluacion,
        nota,
        ponderacion,
        titulo,
        estudiante: { id: estudianteId },  // 👈 Incluye el estudiante aquí
        curso: { id: cursoId }
      });
    }
  });

  console.log("📤 Enviando actualizaciones:");
  console.table(actualizaciones);
  console.log("Actualizaciones a enviar:", actualizaciones);

  fetch("http://localhost:8080/api/evaluaciones/actualizar", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(actualizaciones)
  })
    .then(res => {
      if (!res.ok) throw new Error("Error al actualizar");
      alert("Evaluaciones actualizadas correctamente.");
      verEvaluacionesRegistradas(localStorage.getItem("cursoSeleccionado")); // recarga
    })
    .catch(() => alert("No se pudo actualizar."));
}*/
function guardarCambiosEvaluaciones() {
  const cursoId = localStorage.getItem("cursoSeleccionado");
  const filas = document.querySelectorAll("table tr[data-estudiante-id]");
  const actualizaciones = [];

  filas.forEach(fila => {
    const idEvaluacion = fila.querySelector(".notaInput")?.getAttribute("data-id");
    const nota = parseFloat(fila.querySelector(".notaInput")?.value.replace(",", "."));
    const ponderacion = parseFloat(fila.querySelector(".pondInput")?.value);
    const titulo = fila.querySelector(".tituloInput")?.value;
    const estudianteId = fila.getAttribute("data-estudiante-id");

    console.log("Fila:", {
      idEvaluacion,
      nota,
      ponderacion,
      titulo,
      estudianteId,
      cursoId
    });

    if (idEvaluacion && !isNaN(nota) && !isNaN(ponderacion)) {
      actualizaciones.push({
        id: idEvaluacion,
        nota,
        ponderacion,
        titulo,
        estudiante: { id: estudianteId },
        curso: { id: cursoId }
      });
    }
  });

  console.log("📤 Enviando actualizaciones:");
  console.table(actualizaciones);

  fetch("http://localhost:8080/api/evaluaciones/actualizar", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(actualizaciones)
  })
  .then(res => {
    if (!res.ok) throw new Error("Error al actualizar");
    alert("Evaluaciones actualizadas correctamente.");
    verEvaluacionesRegistradas(localStorage.getItem("cursoSeleccionado"));
  })
  .catch(err => {
    console.error("❌ Error en fetch:", err);
    alert("No se pudo actualizar.");
  });
}


