package dao;

import model.Candidato;
import model.Inscricao;
import model.StatusInscricao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InscricaoDao implements Tabela<Inscricao> {

    private final ConexaoBD conexao;
    private final CandidatoDao candidatoDao;

    public InscricaoDao(ConexaoBD conexao) {
        this.conexao = conexao;
        this.candidatoDao = new CandidatoDao(conexao);
    }

    @Override
    public void criar(Inscricao inscricao) {
        String sql = "CREATE TABLE IF NOT EXISTS inscricao ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "candidato_id BIGINT NOT NULL, "
                + "nota DOUBLE NOT NULL DEFAULT 0.0, "
                + "status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE', "
                + "data_inscricao DATE NOT NULL, "
                + "documentos_validados BOOLEAN NOT NULL DEFAULT FALSE, "
                + "FOREIGN KEY (candidato_id) REFERENCES candidato(id) ON DELETE CASCADE)";

        try (Connection conn = conexao.getConexao();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela inscricao", e);
        }
    }

    @Override
    public void salvar(Inscricao inscricao) {
        validar(inscricao);
        String sql = "INSERT INTO inscricao (candidato_id, nota, status, data_inscricao, documentos_validados) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencher(ps, inscricao);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    inscricao.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar inscrição", e);
        }
    }

    @Override
    public void registrarTabela(Inscricao inscricao) {
        if (inscricao.getId() == null) {
            salvar(inscricao);
        } else {
            atualizar(inscricao);
        }
    }

    @Override
    public Optional<Inscricao> buscarId(Long id) {
        return buscarUnico("SELECT * FROM inscricao WHERE id = ?", id);
    }

    public Optional<Inscricao> buscarPorCandidatoId(Long candidatoId) {
        return buscarUnico("SELECT * FROM inscricao WHERE candidato_id = ? ORDER BY id DESC LIMIT 1", candidatoId);
    }

    public List<Inscricao> listarPorStatus(StatusInscricao status) {
        return listar("SELECT * FROM inscricao WHERE status = ? ORDER BY data_inscricao DESC", status.name());
    }

    public void atualizarStatus(Long id, StatusInscricao status) {
        String sql = "UPDATE inscricao SET status = ? WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da inscrição", e);
        }
    }

    public void atualizarNota(Long id, double nota) {
        String sql = "UPDATE inscricao SET nota = ? WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nota);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar nota da inscrição", e);
        }
    }

    public void atualizarValidacaoDocumentos(Long id, boolean validado) {
        String sql = "UPDATE inscricao SET documentos_validados = ? WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, validado);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar validação de documentos", e);
        }
    }

    public long contarPorStatus(StatusInscricao status) {
        String sql = "SELECT COUNT(*) FROM inscricao WHERE status = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar inscrições por status", e);
        }
    }

    public long contarTotal() {
        String sql = "SELECT COUNT(*) FROM inscricao";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar total de inscrições", e);
        }
    }

    @Override
    public List<Inscricao> listarTabela() {
        return listar("SELECT * FROM inscricao ORDER BY data_inscricao DESC, id DESC", null);
    }

    @Override
    public void atualizar(Inscricao inscricao) {
        validar(inscricao);
        if (inscricao.getId() == null) {
            throw new IllegalArgumentException("Inscrição sem ID para atualizar.");
        }
        String sql = "UPDATE inscricao SET candidato_id = ?, nota = ?, status = ?, "
                + "data_inscricao = ?, documentos_validados = ? WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            preencher(ps, inscricao);
            ps.setLong(6, inscricao.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar inscrição", e);
        }
    }

    @Override
    public void eleminar(Inscricao inscricao) {
        if (inscricao == null || inscricao.getId() == null) {
            return;
        }
        String sql = "DELETE FROM inscricao WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, inscricao.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao eliminar inscrição", e);
        }
    }

    private void validar(Inscricao inscricao) {
        if (inscricao == null) {
            throw new IllegalArgumentException("Inscrição não pode ser nula.");
        }
        if (inscricao.getCandidato() == null || inscricao.getCandidato().getId() == null) {
            throw new IllegalArgumentException("Inscrição deve ter um candidato persistido com ID.");
        }
        if (inscricao.getDataInscricao() == null) {
            inscricao.setDataInscricao(LocalDate.now());
        }
        if (inscricao.getStatus() == null) {
            inscricao.setStatus(StatusInscricao.PENDENTE);
        }
    }

    private void preencher(PreparedStatement ps, Inscricao inscricao) throws SQLException {
        ps.setLong(1, inscricao.getCandidato().getId());
        ps.setDouble(2, inscricao.getNota());
        ps.setString(3, inscricao.getStatus().name());
        ps.setDate(4, Date.valueOf(inscricao.getDataInscricao()));
        ps.setBoolean(5, inscricao.isDocumentosValidados());
    }

    private Optional<Inscricao> buscarUnico(String sql, Long parametro) {
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar inscrição", e);
        }
    }

    private List<Inscricao> listar(String sql, Object parametro) {
        List<Inscricao> lista = new ArrayList<>();

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (parametro != null) {
                ps.setObject(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar inscrições", e);
        }
        return lista;
    }

    private Inscricao mapear(ResultSet rs) throws SQLException {
        Inscricao inscricao = new Inscricao();
        inscricao.setId(rs.getLong("id"));

        long candidatoId = rs.getLong("candidato_id");
        Optional<Candidato> candOpt = candidatoDao.buscarId(candidatoId);
        candOpt.ifPresent(inscricao::setCandidato);

        inscricao.setNota(rs.getDouble("nota"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                inscricao.setStatus(StatusInscricao.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                inscricao.setStatus(StatusInscricao.PENDENTE);
            }
        }

        Date dataDb = rs.getDate("data_inscricao");
        if (dataDb != null) {
            inscricao.setDataInscricao(dataDb.toLocalDate());
        }
        inscricao.setDocumentosValidados(rs.getBoolean("documentos_validados"));

        return inscricao;
    }
}
