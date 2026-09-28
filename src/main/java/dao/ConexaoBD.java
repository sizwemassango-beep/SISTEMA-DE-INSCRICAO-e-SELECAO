/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Sizwe Massango
 */
public class ConexaoBD {
    
    // Substitui 'banco' pelo nome exato da base de dados que criaste no Workbench
    private static final String URL = "jdbc:mysql://localhost:3306/sis?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = ""; // Senha padrão do XAMPP é vazia
    
 

    public static Connection getConexao() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com a base de dados: " + e.getMessage());
        }
    }
}