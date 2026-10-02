package dao;

import model.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdministradorDao implements Tabela<Administrador> {

    private static final String SELECT_BASE =
            "SELECT u.id, u.nome, u.email, u.senha, a.cargo "
                    + "FROM utilizador u JOIN administrador a ON a.id = u.id ";

    private final ConexaoBD conexao;

    public AdministradorDao(ConexaoBD conexao) {
        this.conexao = conexao;
    }

    @Override
    public void criar(Administrador administrador) {
        String sqlAdministrador =
                "CREATE TABLE IF NOT EXISTS administrador ("
                        + "id BIGINT PRIMARY KEY, "
                        + "cargo VARCHAR(100), "
                        + "FOREIGN KEY (id) REFERENCES utilizador(id))";

        try (Connection conn = conexao.getConexao();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sqlAdministrador);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabelas", e);
        }
    }

    @Override
    public void salvar(Administrador administrador) {
        String sqlU = "INSERT INTO utilizador (nome, email, senha) VALUES (?, ?, ?)";
        String sqlA = "INSERT INTO administrador (id, cargo) VALUES (?, ?)";

        try (Connection conn = conexao.getConexao()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psU = conn.prepareStatement(sqlU, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psA = conn.prepareStatement(sqlA)) {

                psU.setString(1, administrador.getNomeCompleto());
                psU.setString(2, administrador.getEmail());
                psU.setString(3, administrador.getSenhaHash());
                psU.executeUpdate();

                try (ResultSet keys = psU.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Não foi gerado o id.");
                    administrador.setId(keys.getLong(1));
                }

                psA.setLong(1, administrador.getId());
                psA.setString(2, administrador.getCargo());
                psA.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar administrador", e);
        }
    }

    @Override
    public void registrarTabela(Administrador administrador) {
        if (administrador.getId() == null) {
            salvar(administrador);
        } else {
            atualizar(administrador);
        }
    }

    @Override
    public Optional<Administrador> buscarId(Long id) {
        String sql = SELECT_BASE + "WHERE u.id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar administrador", e);
        }
    }

    @Override
    public List<Administrador> listarTabela() {
        List<Administrador> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY u.nome";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar administradores", e);
        }
        return lista;
    }

    @Override
    public void atualizar(Administrador administrador) {
        String sqlU = "UPDATE utilizador SET nome = ?, email = ?, senha = ? WHERE id = ?";
        String sqlA = "UPDATE administrador SET cargo = ? WHERE id = ?";

        try (Connection conn = conexao.getConexao()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psU = conn.prepareStatement(sqlU);
                 PreparedStatement psA = conn.prepareStatement(sqlA)) {

                psU.setString(1, administrador.getNomeCompleto());
                psU.setString(2, administrador.getEmail());
                psU.setString(3, administrador.getSenhaHash());
                psU.setLong(4, administrador.getId());
                psU.executeUpdate();

                psA.setString(1, administrador.getCargo());
                psA.setLong(2, administrador.getId());
                psA.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar administrador", e);
        }
    }

    @Override
    public void eleminar(Administrador administrador) {
        try (Connection conn = conexao.getConexao()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psA = conn.prepareStatement("DELETE FROM administrador WHERE id = ?");
                 PreparedStatement psU = conn.prepareStatement("DELETE FROM utilizador WHERE id = ?")) {

                psA.setLong(1, administrador.getId());
                psA.executeUpdate();
                psU.setLong(1, administrador.getId());
                psU.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao eliminar administrador", e);
        }
    }

    private Administrador mapear(ResultSet rs) throws SQLException {
        Administrador a = new Administrador();
        a.setId(rs.getLong("id"));
        a.setNomeCompleto(rs.getString("nome"));
        a.setEmail(rs.getString("email"));
        a.setSenhaHash(rs.getString("senha"));
        a.setCargo(rs.getString("cargo"));
        return a;
    }
}