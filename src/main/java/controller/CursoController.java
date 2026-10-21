package controller;

import dao.ConexaoBD;
import model.Curso;

import java.util.List;

/**

 * Coordena as ações da interface relacionadas a Curso.

 */

public class CursoController extends ControllerSecretaria {

    public CursoController() {
        super();
    }

    public CursoController(ConexaoBD conexaoBD) {
        super(conexaoBD);
    }

    public List<Curso> listarCursos() {
        return dashboardService.listarTodosCursos();
    }

    public void cadastrarCurso(Curso curso) {
        dashboardService.registrarCurso(curso);
    }
}
