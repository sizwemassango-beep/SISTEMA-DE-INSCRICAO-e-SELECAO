package service;

import dao.*;
import model.Administrador;
import model.Candidato;
import model.Curso;
import model.QuestionarioCarencia;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**

 * Representa DatabaseInitializer no sistema de inscrição e seleção.

 */

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

            garantirNovasColunas();
        } catch (Exception e) {
            System.err.println("Aviso durante inicialização de tabelas: " + e.getMessage());
        }
    }

    private void garantirNovasColunas() {
        try (Connection conn = conexaoBD.getConexao();
             Statement st = conn.createStatement()) {
            try {
                st.executeUpdate("ALTER TABLE curso ADD COLUMN pontos_minimos_carencia INT NOT NULL DEFAULT 50");
            } catch (SQLException ignored) {}

            try {
                st.executeUpdate("ALTER TABLE inscricao ADD COLUMN nota_12 DOUBLE NOT NULL DEFAULT 0.0");
            } catch (SQLException ignored) {}

            try {
                st.executeUpdate("ALTER TABLE inscricao ADD COLUMN doc_atestado_pobreza VARCHAR(255)");
            } catch (SQLException ignored) {}

            try {
                st.executeUpdate("ALTER TABLE inscricao ADD COLUMN motivo_rejeicao VARCHAR(255)");
            } catch (SQLException ignored) {}

            try {
                st.executeUpdate("ALTER TABLE inscricao ADD COLUMN carta_motivacao TEXT");
            } catch (SQLException ignored) {}
        } catch (Exception e) {
            System.err.println("Aviso ao migrar novas colunas: " + e.getMessage());
        }
    }
}
