package org.example;

import View.Tema;
import View.TelaInicial;

import javax.swing.*;
import java.awt.*;

/**
 * Ponto de entrada da aplicação.
 * Activa o FlatLaf antes de qualquer componente Swing ser criado.
 *
 * @author 01 Capital
 */
public class Main {

    public static void main(String[] args) {
        Tema.aplicar();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Inscrição e Selecção");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setMinimumSize(new Dimension(900, 600));
            frame.setLocationRelativeTo(null); // centrar no ecrã

            TelaInicial telaInicial = new TelaInicial();
            frame.setContentPane(telaInicial);

            try {
                java.net.URL iconUrl = Main.class.getResource("/View/iamgens/Logotipo Minimalista para Laboratório.png");
                if (iconUrl != null) {
                    frame.setIconImage(new ImageIcon(iconUrl).getImage());
                }
            } catch (Exception ignored) {}

            frame.setVisible(true);
        });
    }
}
