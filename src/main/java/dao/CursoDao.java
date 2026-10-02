package dao;

import model.Curso;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CursoDao implements Tabela<Curso> {

    private final ConexaoBD conexao;

    public CursoDao(ConexaoBD conexao) {
        this.conexao = conexao;
    }

    @Override
    public void criar(Curso curso) {
        String sql = "CREATE TABLE IF NOT EXISTS curso ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "nome VARCHAR(100) NOT NULL, "
                + "descricao VARCHAR(200) NOT NULL, "
                + "numero_vagas INT NOT NULL, "
                + "percentual_merito DECIMAL(5,2) NOT NULL, "
                + "percentual_necessidade DECIMAL(5,2) NOT NULL, "
                + "inicio_inscricoes DATE NOT NULL, "
                + "fim_inscricoes DATE NOT NULL)";

        try (Connection conn = conexao.getConexao();
             Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela curso", e);
        }
    }

    @Override
    public void salvar(Curso curso) {
        String sql = "INSERT INTO curso (nome, descricao, numero_vagas, percentual_merito, "
                + "percentual_necessidade, inicio_inscricoes, fim_inscricoes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencher(ps, curso);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    curso.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar curso", e);
        }
    }

    @Override
    public void registrarTabela(Curso curso) {
        if (curso.getId() == null) {
            salvar(curso);
        } else {
            atualizar(curso);
        }
    }

    @Override
    public Optional<Curso> buscarId(Long id) {
        String sql = "SELECT * FROM curso WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar curso", e);
        }
    }

    @Override
    public List<Curso> listarTabela() {
        return listar("SELECT * FROM curso ORDER BY nome");
    }

    public List<Curso> listarComInscricoesAbertas() {
        return listar("SELECT * FROM curso WHERE CURDATE() BETWEEN inicio_inscricoes AND fim_inscricoes ORDER BY nome");
    }

    @Override
    public void atualizar(Curso curso) {
        String sql = "UPDATE curso SET nome = ?, descricao = ?, numero_vagas = ?, percentual_merito = ?, "
                + "percentual_necessidade = ?, inicio_inscricoes = ?, fim_inscricoes = ? WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            preencher(ps, curso);
            ps.setLong(8, curso.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar curso", e);
        }
    }

    @Override
    public void eleminar(Curso curso) {
        String sql = "DELETE FROM curso WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, curso.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao eliminar curso", e);
        }
    }

    private List<Curso> listar(String sql) {
        List<Curso> lista = new ArrayList<>();

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cursos", e);
        }
        return lista;
    }

    private void preencher(PreparedStatement ps, Curso curso) throws SQLException {
        ps.setString(1, curso.getNome());
        ps.setString(2, curso.getDescricao());
        ps.setInt(3, curso.getNumeroVagas());
        ps.setDouble(4, curso.getPercentualMerito());
        ps.setDouble(5, curso.getPercentualNecessidade());
        ps.setDate(6, Date.valueOf(curso.getDataInicio()));
        ps.setDate(7, Date.valueOf(curso.getDataFim()));
    }

    private Curso mapear(ResultSet rs) throws SQLException {
        Curso curso = new Curso();
        curso.setId(rs.getLong("id"));
        curso.setNome(rs.getString("nome"));
        curso.setDescricao(rs.getString("descricao"));
        curso.setNumeroVagas(rs.getInt("numero_vagas"));
        curso.setPercentualMerito(rs.getDouble("percentual_merito"));
        curso.setPercentualNecessidade(rs.getDouble("percentual_necessidade"));
        curso.setDataInicio(rs.getDate("inicio_inscricoes").toLocalDate());
        curso.setDataFim(rs.getDate("fim_inscricoes").toLocalDate());
        return curso;
    }
}