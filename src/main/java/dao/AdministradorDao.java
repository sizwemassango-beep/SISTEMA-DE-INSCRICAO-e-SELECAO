package dao;

import model.Administrador;
import model.Curso;

import java.sql.*;
import java.util.List;

public class AdministradorDao implements Tabela<Administrador>{
    private ConexaoBD conexao;

    @Override
    public void criar() {
        String sqlAdministrador =
                "CREATE TABLE IF NOT EXISTS administrador ("
                        + "id BIGINT PRIMARY KEY, "
                        + "cargo VARCHAR(100), "
                        + "FOREIGN KEY (id) REFERENCES utilizador(id))";

        try (Connection conn = conexao.getConexao();
             Statement st = conn.createStatement()) {
            //st.executeUpdate();
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
    public <T> Curso buscarId(Long id) {

        return null;
    }

    @Override
    public List<Administrador> listarTabela() {
        return List.of();
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
}
