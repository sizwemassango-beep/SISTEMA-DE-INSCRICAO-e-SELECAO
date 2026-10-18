package dao;

import model.Candidato;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CandidatoDao implements Tabela<Candidato> {

    private static final String SELECT_BASE =
            "SELECT u.id, u.nome, u.email, u.senha, c.numero_bi, c.data_nascimento, "
                    + "c.telefone, c.provincia, c.distrito, c.morada "
                    + "FROM utilizador u JOIN candidato c ON c.id = u.id ";

    private final ConexaoBD conexao;

    public CandidatoDao(ConexaoBD conexao) {
        this.conexao = conexao;
    }

    @Override
    public void criar(Candidato candidato) {
        String sqlUtilizador =
                "CREATE TABLE IF NOT EXISTS utilizador ("
                        + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                        + "nome VARCHAR(100) NOT NULL, "
                        + "email VARCHAR(100) NOT NULL UNIQUE, "
                        + "senha VARCHAR(255) NOT NULL)";
        String sqlCandidato =
                "CREATE TABLE IF NOT EXISTS candidato ("
                        + "id BIGINT PRIMARY KEY, "
                        + "numero_bi VARCHAR(20) NOT NULL UNIQUE, "
                        + "data_nascimento DATE NOT NULL, "
                        + "telefone VARCHAR(20), "
                        + "provincia VARCHAR(50), "
                        + "distrito VARCHAR(100), "
                        + "morada VARCHAR(200), "
                        + "FOREIGN KEY (id) REFERENCES utilizador(id))";

        try (Connection conn = conexao.getConexao();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sqlUtilizador);
            st.executeUpdate(sqlCandidato);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabelas", e);
        }
    }

    @Override
    public void salvar(Candidato c) {
        String sqlU = "INSERT INTO utilizador (nome, email, senha) VALUES (?, ?, ?)";
        String sqlC = "INSERT INTO candidato "
                + "(id, numero_bi, data_nascimento, telefone, provincia, distrito, morada) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexao.getConexao()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psU = conn.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psC = conn.prepareStatement(sqlC)) {

                psU.setString(1, c.getNomeCompleto());
                psU.setString(2, c.getEmail());
                psU.setString(3, c.getSenhaHash());
                psU.executeUpdate();

                try (ResultSet keys = psU.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Não foi gerado o id.");
                    c.setId(keys.getLong(1));
                }

                psC.setLong(1, c.getId());
                psC.setString(2, c.getNumeroBI());
                psC.setDate(3, Date.valueOf(c.getDataDeNascimento()));
                psC.setString(4, c.getTelefone());
                psC.setString(5, c.getProvincia());
                psC.setString(6, c.getDistrito());
                psC.setString(7, c.getMorada());
                psC.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar candidato", e);
        }
    }

    @Override
    public void registrarTabela(Candidato candidato) {
        if (candidato.getId() == null) {
            salvar(candidato);
        } else {
            atualizar(candidato);
        }
    }

    @Override
    public Optional<Candidato> buscarId(Long id) {
        return buscarUnico(SELECT_BASE + "WHERE u.id = ?", id);
    }

    public Optional<Candidato> buscarPorEmail(String email) {
        return buscarUnico(SELECT_BASE + "WHERE u.email = ?", email);
    }

    public boolean existeEmail(String email) {
        return existe("SELECT 1 FROM utilizador WHERE email = ?", email);
    }

    public boolean existeNumeroBI(String numeroBI) {
        return existe("SELECT 1 FROM candidato WHERE numero_bi = ?", numeroBI);
    }

    @Override
    public List<Candidato> listarTabela() {
        List<Candidato> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY u.nome";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar candidatos", e);
        }
        return lista;
    }

    @Override
    public void atualizar(Candidato c) {
        String sqlU = "UPDATE utilizador SET nome = ?, email = ?, senha = ? WHERE id = ?";
        String sqlC = "UPDATE candidato SET numero_bi = ?, data_nascimento = ?, "
                + "telefone = ?, provincia = ?, distrito = ?, morada = ? WHERE id = ?";

        try (Connection conn = conexao.getConexao()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psU = conn.prepareStatement(sqlU);
                 PreparedStatement psC = conn.prepareStatement(sqlC)) {

                psU.setString(1, c.getNomeCompleto());
                psU.setString(2, c.getEmail());
                psU.setString(3, c.getSenhaHash());
                psU.setLong(4, c.getId());
                psU.executeUpdate();

                psC.setString(1, c.getNumeroBI());
                psC.setDate(2, Date.valueOf(c.getDataDeNascimento()));
                psC.setString(3, c.getTelefone());
                psC.setString(4, c.getProvincia());
                psC.setString(5, c.getDistrito());
                psC.setString(6, c.getMorada());
                psC.setLong(7, c.getId());
                psC.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar candidato", e);
        }
    }

    @Override
    public void eleminar(Candidato c) {
        try (Connection conn = conexao.getConexao()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psC = conn.prepareStatement("DELETE FROM candidato WHERE id = ?");
                 PreparedStatement psU = conn.prepareStatement("DELETE FROM utilizador WHERE id = ?")) {

                psC.setLong(1, c.getId());
                psC.executeUpdate();
                psU.setLong(1, c.getId());
                psU.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao eliminar candidato", e);
        }
    }

    private Optional<Candidato> buscarUnico(String sql, Object parametro) {
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar candidato", e);
        }
    }

    private boolean existe(String sql, String valor) {
        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar duplicado", e);
        }
    }

    private Candidato mapear(ResultSet rs) throws SQLException {
        Candidato c = new Candidato();
        c.setId(rs.getLong("id"));
        c.setNomeCompleto(rs.getString("nome"));
        c.setEmail(rs.getString("email"));
        c.setSenhaHash(rs.getString("senha"));
        c.setNumeroBI(rs.getString("numero_bi"));
        Date nascimento = rs.getDate("data_nascimento");
        c.setDataDeNascimento(nascimento == null ? null : nascimento.toLocalDate());
        c.setTelefone(rs.getString("telefone"));
        c.setProvincia(rs.getString("provincia"));
        c.setDistrito(rs.getString("distrito"));
        c.setMorada(rs.getString("morada"));
        return c;
    }
}