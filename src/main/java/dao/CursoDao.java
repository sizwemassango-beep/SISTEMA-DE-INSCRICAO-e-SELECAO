package dao;

import model.Administrador;
import model.Curso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDao implements Tabela<Curso>{
    private ConexaoBD conexao;

    public CursoDao() {
    }

    @Override
    public void criar() {
        String query="Create TABLE 'Curso' (" +
                "id INT PRIMARY KEY AUTO_INCREMENT)," +
                "Nome VARCHAR(50) NOT NULL," +
                "NumeroVagas INT NOT NULL," +
                "descricao VARCHAR(200) NOT NULL," +
                "percentualMerito DECIMAL NOT NULL," +
                "percentualNecessidade DECIMAL NOT NULL," +
                "DataInicio DATE NOT NULL," +
                "Datafim DATE NOT NULL";
        try {
            ConexaoBD.getConexao();
            Statement stmt = conexao.getConexao().createStatement();
            stmt.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public void salvar(Curso curso) {
        String sql="INSERT INTO curso (nome, descricao, numero_vagas, percentual_merito, percentual_necessidade,\n" +
                "                    inicio_inscricoes, fim_inscricoes)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stamt= conexao.getConexao().prepareStatement(sql);
            stamt.setString(1, curso.getNome());
            stamt.setString(2, curso.getDescricao());
            stamt.setInt(3, curso.getNumeroVagas());
            stamt.setDouble(4, curso.getPercentualMerito());
            stamt.setDouble(5, curso.getPercentualNecessidade());
            stamt.setString(6, curso.getDataInicio().toString());
            stamt.setDouble(7, curso.getPercentualNecessidade());

            stamt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Administrador buscarId(Long id) {
        String sql = "SELECT id, nome, descricao, numerovagas, percentualMerito, "
                + "percentualNecessidade, inicioInscricoes, fimInscricoes "
                + "FROM curso WHERE id = ?";

        try (Connection conn = conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {

                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar curso", e);
        }
    }


        @Override
        public List<Curso> listarTabela() {
            List<Curso> lista = new ArrayList<>();
            Curso c = new Curso();
            String sql = "SELECT id, nome, descricao, numerovagas, percentualMerito, "
                    + "percentualNecessidade, inicioInscricoes, fimInscricoes "
                    + "FROM curso ORDER BY nome";

            try (Connection conn = conexao.getConexao();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    c.setId(rs.getLong("id"));
                    c.setNome(rs.getString("nome"));
                    c.setDescricao(rs.getString("descricao"));
                    c.setNumeroVagas(rs.getInt("numero_vagas"));
                    c.setPercentualMerito(rs.getDouble("percentual_merito"));
                    c.setPercentualNecessidade(rs.getDouble("percentual_necessidade"));
                    c.setDataInicio(rs.getDate("inicio_inscricoes").toLocalDate());
                    c.setDataFim(rs.getDate("fim_inscricoes").toLocalDate());
                    lista.add(c);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao listar cursos", e);
            }
            return lista;
        }

    @Override
    public void atualizar(Curso curso) {
        String sql="UPDATE 'Curso' SET nome = ?, descricao = ?, numeroVagas = ?, descricao = ?, percentualMerito = ?, percentualNecessidade = ?, inicioInscricoes = ?," +
                " fimInscricoes= ? WHERE id = ?";

        try {
            PreparedStatement statement=ConexaoBD.getConexao().prepareStatement(sql);
            statement.setString(1, curso.getNome());
            statement.setString(2, curso.getDescricao());
            statement.setString(3, curso.getDescricao());
            statement.setInt(4, curso.getNumeroVagas());
            statement.setDouble(5, curso.getPercentualMerito());
            statement.setDouble(6, curso.getPercentualNecessidade());
            statement.setDate(7, java.sql.Date.valueOf(curso.getDataInicio()));
            statement.setDate(8, java.sql.Date.valueOf(curso.getDataFim()));
            statement.executeUpdate(sql);


        } catch  (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void eleminar(Curso curso) {
        String sql="DELETE FROM Curso WHERE id = ?";
        try {
            PreparedStatement statement=ConexaoBD.getConexao().prepareStatement(sql);
            statement.setLong(0,curso.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}





