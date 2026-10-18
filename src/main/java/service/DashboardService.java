package service;

import dao.CandidatoDao;
import dao.ConexaoBD;
import dao.CursoDao;
import dao.QuestionarioCarenciaDao;
import model.Candidato;
import model.Curso;

import java.util.*;
import java.util.stream.Collectors;

public class DashboardService {

    private final CandidatoDao candidatoDao;
    private final CursoDao cursoDao;
    private final QuestionarioCarenciaDao carenciaDao;

    public DashboardService(ConexaoBD conexaoBD) {
        this.candidatoDao = new CandidatoDao(conexaoBD);
        this.cursoDao = new CursoDao(conexaoBD);
        this.carenciaDao = new QuestionarioCarenciaDao(conexaoBD);
    }

    public List<Candidato> listarTodosCandidatos() {
        try {
            return candidatoDao.listarTabela();
        } catch (Exception e) {
            System.err.println("Erro ao carregar candidatos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Curso> listarTodosCursos() {
        try {
            return cursoDao.listarTabela();
        } catch (Exception e) {
            System.err.println("Erro ao carregar cursos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public int obterTotalCandidatos() {
        return listarTodosCandidatos().size();
    }

    public int obterTotalCursos() {
        return listarTodosCursos().size();
    }

    public int obterTotalVagas() {
        return listarTodosCursos().stream()
                .mapToInt(Curso::getNumeroVagas)
                .sum();
    }

    public double obterConcorrenciaGeral() {
        int vagas = obterTotalVagas();
        if (vagas == 0) return 0.0;
        return (double) obterTotalCandidatos() / vagas;
    }

    public Map<String, Integer> obterCandidatosPorProvincia() {
        Map<String, Integer> mapa = new TreeMap<>();
        List<Candidato> candidatos = listarTodosCandidatos();

        for (Candidato c : candidatos) {
            String prov = c.getProvincia();
            if (prov == null || prov.trim().isEmpty()) {
                prov = "Não informada";
            } else {
                prov = prov.trim();
            }
            mapa.put(prov, mapa.getOrDefault(prov, 0) + 1);
        }

        return mapa;
    }

    public void registrarCurso(Curso curso) {
        cursoDao.registrarTabela(curso);
    }

    public CandidatoDao getCandidatoDao() {
        return candidatoDao;
    }

    public CursoDao getCursoDao() {
        return cursoDao;
    }

    public QuestionarioCarenciaDao getCarenciaDao() {
        return carenciaDao;
    }
}
