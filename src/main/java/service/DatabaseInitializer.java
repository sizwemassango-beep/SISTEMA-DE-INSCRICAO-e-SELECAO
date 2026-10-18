package service;

import dao.*;
import model.Administrador;
import model.Candidato;
import model.Curso;
import model.QuestionarioCarencia;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseInitializer {

    private final ConexaoBD conexaoBD;

    public DatabaseInitializer(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    public boolean testarConexao() {
        try (Connection conn = conexaoBD.getConexao()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public void inicializarEstrutura() {
        if (!testarConexao()) {
            return;
        }

        try {
            CandidatoDao candidatoDao = new CandidatoDao(conexaoBD);
            candidatoDao.criar(new Candidato());

            AdministradorDao administradorDao = new AdministradorDao(conexaoBD);
            administradorDao.criar(new Administrador());

            CursoDao cursoDao = new CursoDao(conexaoBD);
            cursoDao.criar(new Curso());

            QuestionarioCarenciaDao questionarioDao = new QuestionarioCarenciaDao(conexaoBD);
            questionarioDao.criar(new QuestionarioCarencia());

            InscricaoDao inscricaoDao = new InscricaoDao(conexaoBD);
            inscricaoDao.criar(new model.Inscricao());
        } catch (Exception e) {
            System.err.println("Aviso durante inicialização de tabelas: " + e.getMessage());
        }
    }
}
