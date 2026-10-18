package View.fx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navegador {

    private static Stage palcoPrincipal;
    private static Scene cenaPrincipal;
    private static boolean modoEscuro = false;

    public static void inicializar(Stage stage) {
        palcoPrincipal = stage;
        palcoPrincipal.setTitle("SIS - Sistema de Inscrição e Seleção");
        palcoPrincipal.setMinWidth(1000);
        palcoPrincipal.setMinHeight(650);

        irParaTelaInicial();
        palcoPrincipal.show();
    }

    public static void navegarPara(Parent novaRaiz) {
        if (cenaPrincipal == null) {
            cenaPrincipal = new Scene(novaRaiz, 1200, 750);
            aplicarEstilo(cenaPrincipal);
            palcoPrincipal.setScene(cenaPrincipal);
        } else {
            cenaPrincipal.setRoot(novaRaiz);
            aplicarTemaAtual(novaRaiz);
        }
    }

    public static void alternarTema() {
        modoEscuro = !modoEscuro;
        if (cenaPrincipal != null && cenaPrincipal.getRoot() != null) {
            aplicarTemaAtual(cenaPrincipal.getRoot());
        }
    }

    public static boolean isModoEscuro() {
        return modoEscuro;
    }

    private static void aplicarEstilo(Scene scene) {
        try {
            String cssPath = Navegador.class.getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Aviso ao carregar CSS: " + e.getMessage());
        }
    }

    private static void aplicarTemaAtual(Parent root) {
        if (modoEscuro) {
            if (!root.getStyleClass().contains("dark-theme")) {
                root.getStyleClass().add("dark-theme");
            }
        } else {
            root.getStyleClass().remove("dark-theme");
        }
    }

    public static void irParaTelaInicial() {
        navegarPara(new TelaAutenticacaoFX());
    }

    public static void irParaLogin(boolean perfilSecretaria) {
        navegarPara(new TelaLoginFX(perfilSecretaria));
    }

    public static void irParaNovaInscricao() {
        navegarPara(new TelaNovaInscricaoFX());
    }

    public static void irParaDashboardSecretaria() {
        navegarPara(new TelaDashboardSecretariaFX());
    }

    public static void irParaDashboardCandidato(model.Candidato candidato) {
        navegarPara(new TelaDashboardCandidatoFX(candidato));
    }

    public static Stage getPalcoPrincipal() {
        return palcoPrincipal;
    }
}
