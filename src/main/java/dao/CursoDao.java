package dao;

import model.Curso;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CursoDao implements Tabela<Curso>{
    private ConexaoBD conexao;

    @Override
    public void criar(Curso curso) {
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
    public <Curso> void buscarId(Long id) {
        String sql="SELECT * FROM 'Curso' ";
    }

    @Override
    public List<Curso> listarTabela() {
        return List.of();
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
            statement.setDate(7, curso.getDataInicio());
            statement.setDate (8, curso.getDataFim());
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
