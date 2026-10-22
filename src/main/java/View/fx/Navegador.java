package View.fx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**

 * Representa Navegador no sistema de inscrição e seleção.

 */

public class Navegador {

    private static Stage palcoPrincipal;
    private static Scene cenaPrincipal;
    private static boolean modoEscuro = false;
    private static model.Administrador administradorLogado;

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
            cenaPrincipal.getAccelerators().clear();
            cenaPrincipal.setRoot(novaRaiz);
            aplicarTemaAtual(novaRaiz);
        }
        configurarAtalhosGlobais(cenaPrincipal);
    }

    private static void configurarAtalhosGlobais(Scene scene) {
        if (scene == null) return;
        scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.F2 || (ev.isControlDown() && ev.getCode() == javafx.scene.input.KeyCode.T)) {
                alternarTema();
                ev.consume();
            } else if (ev.getCode() == javafx.scene.input.KeyCode.F1) {
                ComponentesVisuais.mostrarPopupToastGlobal("SIS • Atalhos: Ctrl+P (Pesquisar), Ctrl+O (Ver Senhas), F2/Ctrl+T (Tema), Ctrl+Enter (Submeter), Esc (Voltar)", ComponentesVisuais.TipoMensagem.INFO);
                ev.consume();
            } else if (ev.isControlDown() && ev.getCode() == javafx.scene.input.KeyCode.P) {
                if (scene.getRoot() instanceof TelaDashboardSecretariaFX) {
                    ((TelaDashboardSecretariaFX) scene.getRoot()).focarPesquisa();
                    ev.consume();
                }
            } else if (ev.isControlDown() && ev.getCode() == javafx.scene.input.KeyCode.O) {
                if (scene.getRoot() instanceof TelaAutenticacaoFX) {
                    ((TelaAutenticacaoFX) scene.getRoot()).alternarVisibilidadeSenha();
                    ev.consume();
                } else if (scene.getRoot() instanceof TelaNovaInscricaoFX) {
                    ((TelaNovaInscricaoFX) scene.getRoot()).alternarVisibilidadeSenhas();
                    ev.consume();
                }
            }
        });
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
        administradorLogado = null;
        navegarPara(new TelaAutenticacaoFX());
    }

    public static void irParaLogin(boolean perfilSecretaria) {
        navegarPara(new TelaLoginFX(perfilSecretaria));
    }

    public static void irParaNovaInscricao() {
        navegarPara(new TelaNovaInscricaoFX());
    }

    public static void irParaDashboardSecretaria() {
        navegarPara(new TelaDashboardSecretariaFX(administradorLogado));
    }

    public static void irParaDashboardSecretaria(model.Administrador admin) {
        administradorLogado = admin;
        navegarPara(new TelaDashboardSecretariaFX(admin));
    }

    public static void irParaDashboardCandidato(model.Candidato candidato) {
        navegarPara(new TelaDashboardCandidatoFX(candidato));
    }

    public static model.Administrador getAdministradorLogado() {
        return administradorLogado;
    }

    public static Stage getPalcoPrincipal() {
        return palcoPrincipal;
    }
}
