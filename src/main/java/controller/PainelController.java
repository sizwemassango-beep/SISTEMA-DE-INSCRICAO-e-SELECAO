package controller;

import com.lowagie.text.DocumentException;
import dao.ConexaoBD;
import model.Candidato;
import model.Curso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**

 * Coordena as ações da interface relacionadas a Painel.

 */

public class PainelController extends ControllerSecretaria {

    public PainelController() {
        super();
    }

    public PainelController(ConexaoBD conexaoBD) {
        super(conexaoBD);
    }

    public int obterTotalCandidatos() {
        return dashboardService.obterTotalCandidatos();
    }

    public int obterTotalCursos() {
        return dashboardService.obterTotalCursos();
    }

    public int obterTotalVagas() {
        return dashboardService.obterTotalVagas();
    }

    public double obterConcorrenciaGeral() {
        return dashboardService.obterConcorrenciaGeral();
    }

    public Map<String, Integer> obterCandidatosPorProvincia() {
        return dashboardService.obterCandidatosPorProvincia();
    }

    public List<Candidato> listarCandidatos() {
        return dashboardService.listarTodosCandidatos();
    }

    public List<Curso> listarCursos() {
        return dashboardService.listarTodosCursos();
    }

    public void exportarRelatorioGeralCandidatosPdf(File destino) throws IOException, DocumentException {
        List<Candidato> lista = listarCandidatos();
        relatorioPdfService.exportarListaCandidatos(lista, destino);
    }

    public void exportarComprovativoCandidatoPdf(Candidato candidato, File destino) throws IOException, DocumentException {
        relatorioPdfService.exportarComprovativoInscricao(candidato, destino);
    }
}
