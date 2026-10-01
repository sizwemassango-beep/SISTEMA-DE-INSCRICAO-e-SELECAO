package dao;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public Administrador buscarId(Long id) {
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
        String sql = "SELECT id, candidatoId, cursoId, nota, status, data_inscricao, "
                + "documentos_validados FROM inscricao ORDER BY data_inscricao";

        List<Inscricao> lista = new ArrayList<>();
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar inscrições", e);
        }
        return lista;
    }

    @Override
    public void atualizar(Inscricao inscricao) {
        String sql = "UPDATE inscricao SET nota = ?, status = ?, documentos_validados = ? "
                + "WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, inscricao.getNota());
            ps.setString(2, inscricao.getStatus().name());
            ps.setBoolean(3, inscricao.isDocumentosValidados());
            ps.setLong(4, inscricao.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar inscrição", e);
        }
    }

    @Override
    public void eleminar(Inscricao inscricao) {
        String sql = "DELETE FROM inscricao WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, inscricao.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao eliminar inscrição", e);
        }
    }

    private Inscricao mapear(ResultSet rs) throws SQLException {
        Inscricao i = new Inscricao();
        i.setId(rs.getLong("id"));
       //i.setCandidato(Candidato.setId(rs.getLong("candidato_id")));
        i.setNota(rs.getDouble("nota"));
       // i.setCurso(CursoD);
        i.setStatus(StatusInscricao.valueOf(rs.getString("status")));
        i.setDataInscricao(rs.getDate("data_inscricao").toLocalDate());
        i.setDocumentosValidados(rs.getBoolean("documentos_validados"));
        return i;
    }
}
