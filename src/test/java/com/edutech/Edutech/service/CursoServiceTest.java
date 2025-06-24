package com.edutech.Edutech.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;

import com.edutech.Edutech.model.Curso;
import com.edutech.Edutech.repository.CursoRepository;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository repo;

    @InjectMocks
    private CursoService service;

    @Test
    @DisplayName("1. listarTodos_deberia_devolver_lista_de_cursos")
    void listarTodos_deberia_devolver_lista_de_cursos() {
        // 1. Preparar datos
        Curso c1 = new Curso(); c1.setId(1L); c1.setNombre("Curso 1"); c1.setDescripcion("Desc 1");
        Curso c2 = new Curso(); c2.setId(2L); c2.setNombre("Curso 2"); c2.setDescripcion("Desc 2");

        // 2. Mockear repositorio
        given(repo.findAll()).willReturn(List.of(c1, c2));

        // 3. Ejecutar método bajo prueba
        List<Curso> resultado = service.listarTodos();

        // 4. Verificar resultados
        assertThat(resultado).isNotNull().hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Curso 1");

        // 5. Verificar interacción
        then(repo).should(times(1)).findAll();
    }

    @Test
    @DisplayName("2. buscarPorId_existente_deberia_devolver_curso")
    void buscarPorId_existente_deberia_devolver_curso() {
        // 1. Preparar
        Curso c = new Curso(); c.setId(5L); c.setNombre("X"); c.setDescripcion("Y");
        given(repo.findById(5L)).willReturn(Optional.of(c));

        // 2. Ejecutar
        Optional<Curso> opt = service.buscarPorId(5L);

        // 3. Verificar
        assertThat(opt).isPresent().get().isSameAs(c);
        then(repo).should().findById(5L);
    }

    @Test
    @DisplayName("3. buscarPorId_no_existente_deberia_devolver_empty")
    void buscarPorId_no_existente_deberia_devolver_empty() {
        // 1. Mockear
        given(repo.findById(99L)).willReturn(Optional.empty());

        // 2. Ejecutar
        Optional<Curso> opt = service.buscarPorId(99L);

        // 3. Verificar
        assertThat(opt).isEmpty();
        then(repo).should().findById(99L);
    }

    @Test
    @DisplayName("4. guardar_deberia_delegar_en_repo_save")
    void guardar_deberia_delegar_en_repo_save() {
        // 1. Preparar
        Curso c = new Curso(); c.setNombre("Nuevo"); c.setDescripcion("Desc");
        given(repo.save(c)).willAnswer(inv -> {
            Curso saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        // 2. Ejecutar
        Curso creado = service.guardar(c);

        // 3. Verificar
        assertThat(creado.getId()).isEqualTo(10L);
        then(repo).should().save(c);
    }

    @Test
    @DisplayName("5. actualizar_existente_deberia_actualizar_y_devolver")
    void actualizar_existente_deberia_actualizar_y_devolver() {
        // 1. Preparar
        Curso orig = new Curso(); orig.setId(2L); orig.setNombre("A"); orig.setDescripcion("a");
        Curso upd  = new Curso(); upd.setNombre("B"); upd.setDescripcion("b");
        given(repo.findById(2L)).willReturn(Optional.of(orig));
        given(repo.save(any())).willAnswer(inv -> inv.getArgument(0));

        // 2. Ejecutar
        Optional<Curso> opt = service.actualizar(2L, upd);

        // 3. Verificar
        assertThat(opt).isPresent();
        Curso resultado = opt.get();
        assertThat(resultado.getNombre()).isEqualTo("B");
        assertThat(resultado.getDescripcion()).isEqualTo("b");

        then(repo).should().findById(2L);
        then(repo).should().save(resultado);
    }

    @Test
    @DisplayName("6. actualizar_no_existente_deberia_devolver_empty")
    void actualizar_no_existente_deberia_devolver_empty() {
        // 1. Mockear
        given(repo.findById(44L)).willReturn(Optional.empty());

        // 2. Ejecutar
        Optional<Curso> opt = service.actualizar(44L, new Curso());

        // 3. Verificar
        assertThat(opt).isEmpty();
        then(repo).should().findById(44L);
        then(repo).should(never()).save(any());
    }

    @Test
    @DisplayName("7. eliminar_existente_deberia_devolver_true")
    void eliminar_existente_deberia_devolver_true() {
        // 1. Preparar
        given(repo.existsById(3L)).willReturn(true);
        willDoNothing().given(repo).deleteById(3L);

        // 2. Ejecutar
        boolean ok = service.eliminar(3L);

        // 3. Verificar
        assertThat(ok).isTrue();
        then(repo).should().existsById(3L);
        then(repo).should().deleteById(3L);
    }

    @Test
    @DisplayName("8. eliminar_no_existente_deberia_devolver_false")
    void eliminar_no_existente_deberia_devolver_false() {
        // 1. Mockear
        given(repo.existsById(99L)).willReturn(false);

        // 2. Ejecutar
        boolean ok = service.eliminar(99L);

        // 3. Verificar
        assertThat(ok).isFalse();
        then(repo).should().existsById(99L);
        then(repo).should(never()).deleteById(any());
    }
}
