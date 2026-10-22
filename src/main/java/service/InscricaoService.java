package service;

import dao.CandidatoDao;
import dao.ConexaoBD;
import dao.InscricaoDao;
import dao.QuestionarioCarenciaDao;
import model.Candidato;
import model.Curso;
import model.Inscricao;
import model.QuestionarioCarencia;
import model.StatusInscricao;

import java.time.LocalDate;

/**

 * Implementa serviços de aplicação relacionados a Inscricao.

 */

public class InscricaoService {

    private final CandidatoDao candidatoDao;
    private final QuestionarioCarenciaDao carenciaDao;
    private final InscricaoDao inscricaoDao;
    private final RelatorioPdfService relatorioPdfService;

    public InscricaoService(ConexaoBD conexaoBD) {
        this.candidatoDao = new CandidatoDao(conexaoBD);
        this.carenciaDao = new QuestionarioCarenciaDao(conexaoBD);
        this.inscricaoDao = new InscricaoDao(conexaoBD);
        this.relatorioPdfService = new RelatorioPdfService();
    }

    public void processarNovaInscricao(Candidato candidato, QuestionarioCarencia questionario) throws IllegalArgumentException {
        processarNovaInscricao(candidato, questionario, null, 0.0);
    }

    public void processarNovaInscricao(Candidato candidato, QuestionarioCarencia questionario, Curso curso) throws IllegalArgumentException {
        processarNovaInscricao(candidato, questionario, curso, 0.0);
    }

    public void processarNovaInscricao(Candidato candidato, QuestionarioCarencia questionario, Curso curso, double nota12) throws IllegalArgumentException {
        if (candidato.getNomeCompleto() == null || candidato.getNomeCompleto().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome completo é obrigatório.");
        }
        if (candidato.getNumeroBI() == null || candidato.getNumeroBI().trim().isEmpty()) {
            throw new IllegalArgumentException("O número de BI é obrigatório.");
        }
        if (candidato.getEmail() == null || candidato.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O email é obrigatório.");
        }
        if (candidato.getSenhaHash() == null || candidato.getSenhaHash().trim().isEmpty()) {
            throw new IllegalArgumentException("A senha de acesso é obrigatória.");
        }
        if (candidato.getDataDeNascimento() == null) {
            throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        }

        if (candidatoDao.existeEmail(candidato.getEmail())) {
            throw new IllegalArgumentException("Este email já se encontra cadastrado no sistema.");
        }
        if (candidatoDao.existeNumeroBI(candidato.getNumeroBI())) {
            throw new IllegalArgumentException("Este número de BI já se encontra cadastrado no sistema.");
        }

        candidatoDao.salvar(candidato);

        if (questionario != null && candidato.getId() != null) {
            questionario.setCandidatoId(candidato.getId());
            carenciaDao.salvar(questionario);
        }

        if (candidato.getId() != null) {
            Inscricao inscricao = new Inscricao();
            inscricao.setCandidato(candidato);
            inscricao.setDataInscricao(LocalDate.now());
            inscricao.setStatus(StatusInscricao.PENDENTE);
            inscricao.setNota(nota12 > 0 ? nota12 : (questionario != null ? questionario.calcularPontuacao() : 0.0));
            inscricao.setNota12(nota12);
            inscricao.setDocumentosSubmetidos(false);
            inscricao.setDocumentosValidados(false);
            if (curso != null) {
                inscricao.setCurso(curso);
            }
            inscricaoDao.salvar(inscricao);
            candidato.adicionarInscricao(inscricao);
        }
    }

    public InscricaoDao getInscricaoDao() {
        return inscricaoDao;
    }

    public RelatorioPdfService getRelatorioPdfService() {
        return relatorioPdfService;
    }
}
