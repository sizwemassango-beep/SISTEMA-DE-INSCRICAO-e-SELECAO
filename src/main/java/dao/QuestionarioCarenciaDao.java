package dao;

import model.QuestionarioCarencia;
import model.QuestionarioCarencia.FaixaRendimento;
import model.QuestionarioCarencia.NivelCarencia;
import model.QuestionarioCarencia.SituacaoLaboral;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuestionarioCarenciaDao implements Tabela<QuestionarioCarencia> {

    private final ConexaoBD conexao;

    public QuestionarioCarenciaDao(ConexaoBD conexao) {
        this.conexao = conexao;
    }

    @Override
    public void criar(QuestionarioCarencia q) {
        String sql = "CREATE TABLE IF NOT EXISTS questionario_carencia ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "candidato_id BIGINT NOT NULL UNIQUE, "
                + "rendimento_familiar VARCHAR(30) NOT NULL, "
                + "situacao_laboral VARCHAR(30) NOT NULL, "
                + "distancia_km DOUBLE NOT NULL, "
                + "orfao_ou_chefe_familia BOOLEAN NOT NULL, "
                + "recebe_apoio_financeiro BOOLEAN NOT NULL, "
                + "pontuacao INT NOT NULL, "
                + "nivel_carencia VARCHAR(10) NOT NULL, "
                + "FOREIGN KEY (candidato_id) REFERENCES candidato(id) ON DELETE CASCADE)";

        try (Connection conn = conexao.getConexao();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela questionario_carencia", e);
        }
    }

    @Override
    public void salvar(QuestionarioCarencia q) {
        validar(q);
        String sql = "INSERT INTO questionario_carencia "
                + "(candidato_id, rendimento_familiar, situacao_laboral, distancia_km, "
                + "orfao_ou_chefe_familia, recebe_apoio_financeiro, pontuacao, nivel_carencia) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencher(ps, q);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    q.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar questionário de carência", e);
        }
    }

    @Override
    public void registrarTabela(QuestionarioCarencia q) {
        if (q.getId() == null) {
            salvar(q);
        } else {
            atualizar(q);
        }
    }

    @Override
    public Optional<QuestionarioCarencia> buscarId(Long id) {
        return buscarUnico("SELECT * FROM questionario_carencia WHERE id = ?", id);
    }

    public Optional<QuestionarioCarencia> buscarPorCandidato(Long candidatoId) {
        return buscarUnico("SELECT * FROM questionario_carencia WHERE candidato_id = ?", candidatoId);
    }

    public boolean existeParaCandidato(Long candidatoId) {
        String sql = "SELECT 1 FROM questionario_carencia WHERE candidato_id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, candidatoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar questionário de carência", e);
        }
    }

    @Override
    public List<QuestionarioCarencia> listarTabela() {
        return listar("SELECT * FROM questionario_carencia ORDER BY pontuacao DESC", null);
    }

    public List<QuestionarioCarencia> listarPorNivel(NivelCarencia nivel) {
        return listar("SELECT * FROM questionario_carencia WHERE nivel_carencia = ? ORDER BY pontuacao DESC",
                nivel.name());
    }

    public List<QuestionarioCarencia> listarElegiveisParaVagaReservada() {
        return listar("SELECT * FROM questionario_carencia WHERE pontuacao >= ? ORDER BY pontuacao DESC",
                QuestionarioCarencia.LIMIAR_CARENCIA_MEDIA);
    }

    @Override
    public void atualizar(QuestionarioCarencia q) {
        validar(q);
        if (q.getId() == null) {
            throw new IllegalArgumentException("Questionário sem id para atualizar.");
        }
        String sql = "UPDATE questionario_carencia SET candidato_id = ?, rendimento_familiar = ?, "
                + "situacao_laboral = ?, distancia_km = ?, orfao_ou_chefe_familia = ?, "
                + "recebe_apoio_financeiro = ?, pontuacao = ?, nivel_carencia = ? WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            preencher(ps, q);
            ps.setLong(9, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar questionário de carência", e);
        }
    }

    @Override
    public void eleminar(QuestionarioCarencia q) {
        String sql = "DELETE FROM questionario_carencia WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao eliminar questionário de carência", e);
        }
    }

    private Optional<QuestionarioCarencia> buscarUnico(String sql, Long parametro) {
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar questionário de carência", e);
        }
    }

    private List<QuestionarioCarencia> listar(String sql, Object parametro) {
        List<QuestionarioCarencia> lista = new ArrayList<>();

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
            throw new RuntimeException("Erro ao listar questionários de carência", e);
        }
        return lista;
    }

    private void validar(QuestionarioCarencia q) {
        if (q == null || !q.isValido()) {
            throw new IllegalArgumentException("Questionário de carência inválido ou incompleto.");
        }
        if (q.getCandidatoId() == null) {
            throw new IllegalArgumentException("Questionário sem candidato associado.");
        }
    }

    private void preencher(PreparedStatement ps, QuestionarioCarencia q) throws SQLException {
        ps.setLong(1, q.getCandidatoId());
        ps.setString(2, q.getRendimentoFamiliar().name());
        ps.setString(3, q.getSituacaoLaboral().name());
        ps.setDouble(4, q.getDistanciaKm());
        ps.setBoolean(5, q.isOrfaoOuChefeFamilia());
        ps.setBoolean(6, q.isRecebeApoioFinanceiro());
        ps.setInt(7, q.calcularPontuacao());
        ps.setString(8, q.classificar().name());
    }

    private QuestionarioCarencia mapear(ResultSet rs) throws SQLException {
        QuestionarioCarencia q = new QuestionarioCarencia();
        q.setId(rs.getLong("id"));
        q.setCandidatoId(rs.getLong("candidato_id"));
        q.setRendimentoFamiliar(FaixaRendimento.valueOf(rs.getString("rendimento_familiar")));
        q.setSituacaoLaboral(SituacaoLaboral.valueOf(rs.getString("situacao_laboral")));
        q.setDistanciaKm(rs.getDouble("distancia_km"));
        q.setOrfaoOuChefeFamilia(rs.getBoolean("orfao_ou_chefe_familia"));
        q.setRecebeApoioFinanceiro(rs.getBoolean("recebe_apoio_financeiro"));
        return q;
    }
}