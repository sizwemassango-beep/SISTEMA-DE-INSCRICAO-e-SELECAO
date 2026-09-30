

import dao.ConexaoBD;
import dao.Tabela;
import model.Candidato;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatoDao implements Tabela<Candidato> {
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
                + "(id, numero_bi, data_nascimento, telefone, morada) VALUES (?, ?, ?, ?, ?)";

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
                psC.setString(5, c.getMorada());
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
    public void buscarId(Long id) {


    }

    @Override
    public List<Candidato> listarTabela() {
        List<Candidato> lista = new ArrayList<>();
        String sql="SELECT u.id, u.nome, u.email, u.senha, c.numero_bi, c.data_nascimento, c.telefone, c.morada "
                + "FROM utilizador u JOIN candidato c ON c.id = u.id ORDER BY u.nome";
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
                + "telefone = ?, morada = ? WHERE id = ?";

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
                psC.setString(4, c.getMorada());
                psC.setLong(5, c.getId());
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
}

private Candidato mapear(ResultSet rs) throws SQLException {
    Candidato c = new Candidato();
    c.setId(rs.getLong("id"));
    c.setNomeCompleto(rs.getString("nome"));
    c.setEmail(rs.getString("email"));
    c.setSenhaHash(rs.getString("senha"));
    c.setNumeroBI(rs.getString("numero_bi"));
    c.setDataDeNascimento(rs.getDate("data_nascimento").toLocalDate());
    c.setTelefone(rs.getString("telefone"));
    c.setMorada(rs.getString("morada"));
    return c;
}

void main() {
}







