package dao;

import model.Candidato;
import model.Curso;
import model.Inscricao;
import model.StatusInscricao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**

 * Acede aos dados de Inscricao na base de dados.

 */

public class InscricaoDao implements Tabela<Inscricao> {

    private final ConexaoBD conexao;
    private final CandidatoDao candidatoDao;
    private final CursoDao cursoDao;

    public InscricaoDao(ConexaoBD conexao) {
        this.conexao = conexao;
        this.candidatoDao = new CandidatoDao(conexao);
        this.cursoDao = new CursoDao(conexao);
    }

    @Override
    public void criar(Inscricao inscricao) {
        String sql = "CREATE TABLE IF NOT EXISTS inscricao ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "candidato_id BIGINT NOT NULL, "
                + "curso_id BIGINT, "
                + "curso_nome VARCHAR(150) NOT NULL DEFAULT '', "
                + "nota DOUBLE NOT NULL DEFAULT 0.0, "
                + "nota_12 DOUBLE NOT NULL DEFAULT 0.0, "
                + "status VARCHAR(30) NOT NULL DEFAULT 'PENDENTE', "
                + "data_inscricao DATE NOT NULL, "
                + "documentos_submetidos BOOLEAN NOT NULL DEFAULT FALSE, "
                + "documentos_validados BOOLEAN NOT NULL DEFAULT FALSE, "
                + "doc_identificacao VARCHAR(255), "
                + "doc_certificado10 VARCHAR(255), "
                + "doc_certificado12_ou_recibo VARCHAR(255), "
                + "tipo_comprovativo12 VARCHAR(50) DEFAULT 'Certificado de Conclusão', "
                + "doc_atestado_pobreza VARCHAR(255), "
                + "carta_motivacao TEXT, "
                + "motivo_rejeicao VARCHAR(255), "
                + "tipo_admissao VARCHAR(30), "
                + "FOREIGN KEY (candidato_id) REFERENCES candidato(id) ON DELETE CASCADE)";

        try (Connection conn = conexao.getConexao();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
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
            try {
                st.executeUpdate("ALTER TABLE inscricao ADD COLUMN tipo_admissao VARCHAR(30)");
            } catch (SQLException ignored) {}
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela inscricao", e);
        }
    }

    @Override
    public void salvar(Inscricao inscricao) {
        validar(inscricao);
        String sql = "INSERT INTO inscricao (candidato_id, curso_id, curso_nome, nota, nota_12, status, data_inscricao, "
                + "documentos_submetidos, documentos_validados, doc_identificacao, doc_certificado10, "
                + "doc_certificado12_ou_recibo, tipo_comprovativo12, doc_atestado_pobreza, carta_motivacao, motivo_rejeicao, tipo_admissao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherCompleto(ps, inscricao);
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

    public void atualizarStatusComMotivo(Long id, StatusInscricao status, String motivoRejeicao) {
        String sql = "UPDATE inscricao SET status = ?, motivo_rejeicao = ? WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, motivoRejeicao);
            ps.setLong(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da inscrição", e);
        }
    }

    public void atualizarStatusETipo(Long id, StatusInscricao status, String tipoAdmissao) {
        String sql = "UPDATE inscricao SET status = ?, tipo_admissao = ? WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, tipoAdmissao);
            ps.setLong(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status e tipo de admissão da inscrição", e);
        }
    }

    public int contarAprovadosPorCursoETipo(Long cursoId, String tipoAdmissao) {
        if (cursoId == null) return 0;
        String sql = "SELECT COUNT(*) FROM inscricao WHERE curso_id = ? AND status = 'APROVADO' AND tipo_admissao = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cursoId);
            ps.setString(2, tipoAdmissao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao contar aprovados por curso e tipo: " + e.getMessage());
            return 0;
        }
    }

    public double buscarNota12MaisBaixaPorCurso(Long cursoId) {
        if (cursoId == null) return 0.0;
        String sql = "SELECT MIN(nota_12) FROM inscricao WHERE curso_id = ? AND nota_12 > 0";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double val = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : val;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar nota 12 mais baixa: " + e.getMessage());
        }
        return 0.0;
    }

    public double buscarNota12MaisAltaPorCurso(Long cursoId) {
        if (cursoId == null) return 0.0;
        String sql = "SELECT MAX(nota_12) FROM inscricao WHERE curso_id = ? AND nota_12 > 0";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double val = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : val;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar nota 12 mais alta: " + e.getMessage());
        }
        return 0.0;
    }

    public double buscarMediaNota12PorCurso(Long cursoId) {
        if (cursoId == null) return 0.0;
        String sql = "SELECT AVG(nota_12) FROM inscricao WHERE curso_id = ? AND nota_12 > 0";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double val = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : val;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar média nota 12: " + e.getMessage());
        }
        return 0.0;
    }

    public void atualizarNota(Long id, double nota) {
        String sql = "UPDATE inscricao SET nota = ?, nota_12 = ? WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nota);
            ps.setDouble(2, nota);
            ps.setLong(3, id);
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

    public void submeterDocumentos(Long inscricaoId, String docIdentificacao, String docCertificado10,
                                   String docCertificado12OuRecibo, String tipoComprovativo12,
                                   String docAtestadoPobreza, String cartaMotivacao) {
        String sql = "UPDATE inscricao SET doc_identificacao = ?, doc_certificado10 = ?, "
                + "doc_certificado12_ou_recibo = ?, tipo_comprovativo12 = ?, doc_atestado_pobreza = ?, "
                + "carta_motivacao = ?, documentos_submetidos = TRUE WHERE id = ?";
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, docIdentificacao);
            ps.setString(2, docCertificado10);
            ps.setString(3, docCertificado12OuRecibo);
            ps.setString(4, tipoComprovativo12 != null ? tipoComprovativo12 : "Certificado de Conclusão");
            ps.setString(5, docAtestadoPobreza);
            ps.setString(6, cartaMotivacao);
            ps.setLong(7, inscricaoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao submeter documentos", e);
        }
    }

    public void submeterDocumentos(Long inscricaoId, String docIdentificacao, String docCertificado10,
                                   String docCertificado12OuRecibo, String tipoComprovativo12,
                                   String cartaMotivacao) {
        submeterDocumentos(inscricaoId, docIdentificacao, docCertificado10, docCertificado12OuRecibo, tipoComprovativo12, null, cartaMotivacao);
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
        String sql = "UPDATE inscricao SET candidato_id = ?, curso_id = ?, curso_nome = ?, nota = ?, nota_12 = ?, status = ?, "
                + "data_inscricao = ?, documentos_submetidos = ?, documentos_validados = ?, doc_identificacao = ?, "
                + "doc_certificado10 = ?, doc_certificado12_ou_recibo = ?, tipo_comprovativo12 = ?, "
                + "doc_atestado_pobreza = ?, carta_motivacao = ?, motivo_rejeicao = ?, tipo_admissao = ? WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            preencherCompleto(ps, inscricao);
            ps.setLong(18, inscricao.getId());
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

    private void preencherCompleto(PreparedStatement ps, Inscricao inscricao) throws SQLException {
        ps.setLong(1, inscricao.getCandidato().getId());
        if (inscricao.getCursoId() != null) {
            ps.setLong(2, inscricao.getCursoId());
        } else {
            ps.setNull(2, Types.BIGINT);
        }
        ps.setString(3, inscricao.getCursoNome() != null ? inscricao.getCursoNome() : "");
        ps.setDouble(4, inscricao.getNota());
        ps.setDouble(5, inscricao.getNota12());
        ps.setString(6, inscricao.getStatus().name());
        ps.setDate(7, Date.valueOf(inscricao.getDataInscricao()));
        ps.setBoolean(8, inscricao.isDocumentosSubmetidos());
        ps.setBoolean(9, inscricao.isDocumentosValidados());
        ps.setString(10, inscricao.getDocIdentificacao());
        ps.setString(11, inscricao.getDocCertificado10());
        ps.setString(12, inscricao.getDocCertificado12OuRecibo());
        ps.setString(13, inscricao.getTipoComprovativo12() != null ? inscricao.getTipoComprovativo12() : "Certificado de Conclusão");
        ps.setString(14, inscricao.getDocAtestadoPobreza());
        ps.setString(15, inscricao.getCartaMotivacao());
        ps.setString(16, inscricao.getMotivoRejeicao());
        ps.setString(17, inscricao.getTipoAdmissao());
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
        candidatoDao.buscarId(candidatoId).ifPresent(inscricao::setCandidato);

        long cursoId = rs.getLong("curso_id");
        if (!rs.wasNull()) {
            inscricao.setCursoId(cursoId);
            cursoDao.buscarId(cursoId).ifPresent(inscricao::setCurso);
        }

        try { inscricao.setCursoNome(rs.getString("curso_nome")); } catch (SQLException ignored) {}

        inscricao.setNota(rs.getDouble("nota"));
        try { inscricao.setNota12(rs.getDouble("nota_12")); } catch (SQLException ignored) {}

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

        try { inscricao.setDocumentosSubmetidos(rs.getBoolean("documentos_submetidos")); } catch (SQLException ignored) {}
        inscricao.setDocumentosValidados(rs.getBoolean("documentos_validados"));

        try { inscricao.setDocIdentificacao(rs.getString("doc_identificacao")); } catch (SQLException ignored) {}
        try { inscricao.setDocCertificado10(rs.getString("doc_certificado10")); } catch (SQLException ignored) {}
        try { inscricao.setDocCertificado12OuRecibo(rs.getString("doc_certificado12_ou_recibo")); } catch (SQLException ignored) {}
        try { inscricao.setTipoComprovativo12(rs.getString("tipo_comprovativo12")); } catch (SQLException ignored) {}
        try { inscricao.setDocAtestadoPobreza(rs.getString("doc_atestado_pobreza")); } catch (SQLException ignored) {}
        try { inscricao.setCartaMotivacao(rs.getString("carta_motivacao")); } catch (SQLException ignored) {}
        try { inscricao.setMotivoRejeicao(rs.getString("motivo_rejeicao")); } catch (SQLException ignored) {}
        try { inscricao.setTipoAdmissao(rs.getString("tipo_admissao")); } catch (SQLException ignored) {}

        return inscricao;
    }
}
