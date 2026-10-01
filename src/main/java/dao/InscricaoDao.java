package dao;

import model.Candidato;
import model.Curso;
import model.Inscricao;

import java.sql.*;
import java.util.List;
import dao.ConexaoBD;

public class InscricaoDao implements Tabela<Inscricao>{
    private ConexaoBD conexao;

    @Override
    public void criar() {
        String sql = "CREATE TABLE IF NOT EXISTS inscricao ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "candidatoId BIGINT NOT NULL,"
                + "cursoId BIGINT NOT NULL "
                + "nota DOUBLE NOT NULL DEFAULT 0, "
                + "status VARCHAR(20) NOT NULL, "
                + "data_inscricao DATE NOT NULL, "
                + "documentos_validados BOOLEAN NOT NULL DEFAULT FALSE, "
                + "FOREIGN KEY (candidato_id) REFERENCES candidato(id))";

        try (Connection conn = conexao.getConexao();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela inscricao", e);
        }
    }

    @Override
    public void salvar(Inscricao inscricao) {
        String sql = "INSERT INTO inscricao (candidatoId, nota, cursoId ,status, data_inscricao, "
                + "documentos_validados) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, inscricao.getCandidato().getId());
            ps.setDouble(2, inscricao.getNota());
            ps.setLong(3,inscricao.getCurso().getId());
            ps.setString(4, inscricao.getStatus().name());
            ps.setDate(5, java.sql.Date.valueOf(inscricao.getDataInscricao()));
            ps.setBoolean(6, inscricao.isDocumentosValidados());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) inscricao.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar inscrição", e);
        }

    }

    @Override
    public Curso buscarId(Long id) {
        String sql = "SELECT id, candidatoId, cursoId, nota, status, datainscricao, "
                + "documentos_validados FROM inscricao WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar inscrição", e);
        }
    }

    public boolean existe(Candidato candidato) {
        String sql = "SELECT COUNT(*) FROM inscricao WHERE candidato_id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, candidato.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar inscrição existente", e);
        }
    }

    @Override
    public List<Inscricao> listarTabela() {
        return List.of();
    }

    @Override
    public void atualizar(Inscricao object) {

    }

    @Override
    public void eleminar(Inscricao object) {

    }
}
