package org.example;

import View.fx.Navegador;
import dao.ConexaoBD;
import javafx.application.Application;
import javafx.stage.Stage;
import service.DatabaseInitializer;

/**

 * Representa App no sistema de inscrição e seleção.

 */

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Inicializa a estrutura de tabelas se a base estiver acessível
        new Thread(() -> {
            DatabaseInitializer init = new DatabaseInitializer(new ConexaoBD());
            init.inicializarEstrutura();
        }).start();

        Navegador.inicializar(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
