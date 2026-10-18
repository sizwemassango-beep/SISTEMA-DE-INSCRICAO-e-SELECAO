package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    private static final String URL = "jdbc:mysql://172.24.20.32/sis?serverTimezone=Africa/Maputo";
    private static final String USER = "vilker";
    private static final String PASS = "S123";

    public Connection getConexao() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com a base de dados: " + e.getMessage(), e);
        }
    }
}