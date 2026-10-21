package View.fx;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.InputStream;

public class ComponentesVisuais {

    private static final String CAMINHO_LOGO = "/images/logo.png";

    public static StackPane criarLogo(double largura, double altura) {
        StackPane container = new StackPane();
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(largura, altura);
        container.setMaxSize(largura, altura);

        try {
            InputStream stream = ComponentesVisuais.class.getResourceAsStream(CAMINHO_LOGO);
            if (stream != null) {
                Image img = new Image(stream, largura, altura, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(largura);
                iv.setFitHeight(altura);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);

                Rectangle clip = new Rectangle(largura, altura);
                clip.setArcWidth(20);
                clip.setArcHeight(20);
                iv.setClip(clip);

                container.setStyle("-fx-background-color: transparent; -fx-background-radius: 16px; -fx-cursor: hand;");
                DropShadow sombra = new DropShadow(8, 0, 3, Color.rgb(0, 0, 0, 0.18));
                iv.setEffect(sombra);

                container.getChildren().add(iv);
            }
        } catch (Exception e) {
            System.err.println("Aviso: Logo não carregado: " + e.getMessage());
        }

        configurarEasterEggLogo(container);

        return container;
    }

    public static StackPane criarLogoCompacto(double tamanho) {
        return criarLogo(tamanho, tamanho);
    }

    private static void configurarEasterEggLogo(Node logoNode) {
        PauseTransition temporizador20s = new PauseTransition(Duration.seconds(20));
        temporizador20s.setOnFinished(e -> {
            mostrarPopupToastGlobal("Eu Sou inevitável", TipoMensagem.EASTER_EGG);
        });

        logoNode.setOnMousePressed(e -> temporizador20s.playFromStart());
        logoNode.setOnMouseReleased(e -> temporizador20s.stop());
    }

    public enum TipoMensagem {
        ERRO,
        AVISO,
        SUCESSO,
        INFO,
        EASTER_EGG
    }

    private static final java.util.Set<String> chavesExibidasUmaVez = new java.util.HashSet<>();
    private static Popup popupAtual = null;

    public static void mostrarPopupToast(Node referencia, String mensagem, TipoMensagem tipo) {
        mostrarPopupToast(referencia, mensagem, tipo, false);
    }

    public static void mostrarPopupToast(Node referencia, String mensagem, TipoMensagem tipo, boolean somenteUmaVez) {
        if (referencia == null || referencia.getScene() == null || referencia.getScene().getWindow() == null) {
            return;
        }
        if (somenteUmaVez) {
            String chave = System.identityHashCode(referencia) + "_" + mensagem;
            if (chavesExibidasUmaVez.contains(chave)) {
                return;
            }
            chavesExibidasUmaVez.add(chave);
        }
        Window window = referencia.getScene().getWindow();
        mostrarPopupToastEmJanela(window, mensagem, tipo, referencia);
    }

    public static void mostrarPopupToastGlobal(String mensagem, TipoMensagem tipo) {
        mostrarPopupToastGlobal(mensagem, tipo, false);
    }

    public static void mostrarPopupToastGlobal(String mensagem, TipoMensagem tipo, boolean somenteUmaVez) {
        if (somenteUmaVez) {
            if (chavesExibidasUmaVez.contains(mensagem)) {
                return;
            }
            chavesExibidasUmaVez.add(mensagem);
        }
        if (Navegador.getPalcoPrincipal() != null) {
            mostrarPopupToastEmJanela(Navegador.getPalcoPrincipal(), mensagem, tipo, null);
        }
    }

    private static void mostrarPopupToastEmJanela(Window window, String mensagem, TipoMensagem tipo, Node nodeAlvo) {
        if (popupAtual != null && popupAtual.isShowing()) {
            popupAtual.hide();
        }

        Popup popup = new Popup();
        popupAtual = popup;
        popup.setAutoFix(true);
        popup.setAutoHide(true);

        HBox caixa = new HBox(10);
        caixa.setAlignment(Pos.CENTER_LEFT);
        caixa.setPadding(new Insets(10, 16, 10, 16));
        caixa.getStyleClass().add("toast-popup");

        String icone;
        String estiloFundo;
        switch (tipo) {
            case ERRO:
                icone = "✖";
                estiloFundo = "-fx-background-color: #EF4444; -fx-text-fill: white;";
                break;
            case AVISO:
                icone = "⚠";
                estiloFundo = "-fx-background-color: #F59E0B; -fx-text-fill: white;";
                break;
            case SUCESSO:
                icone = "✔";
                estiloFundo = "-fx-background-color: #10B981; -fx-text-fill: white;";
                break;
            case EASTER_EGG:
                icone = "🌌";
                estiloFundo = "-fx-background-color: linear-gradient(to right, #4F46E5, #9333EA); -fx-text-fill: white; -fx-font-size: 15px;";
                break;
            case INFO:
            default:
                icone = "ℹ";
                estiloFundo = "-fx-background-color: #0EA5E9; -fx-text-fill: white;";
                break;
        }

        Label lblIcone = new Label(icone);
        lblIcone.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");

        Label lblTexto = new Label(mensagem);
        lblTexto.setWrapText(true);
        lblTexto.setMaxWidth(380);
        lblTexto.setStyle("-fx-font-size: 12.5px; -fx-font-weight: 600; -fx-text-fill: white;");

        caixa.setStyle(estiloFundo + " -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.30), 12, 0, 0, 4);");
        caixa.getChildren().addAll(lblIcone, lblTexto);

        popup.getContent().add(caixa);

        double x;
        double y;
        if (nodeAlvo != null && nodeAlvo.getScene() != null) {
            javafx.geometry.Point2D coord = nodeAlvo.localToScreen(0, 0);
            if (coord != null) {
                x = coord.getX() + (nodeAlvo.getBoundsInLocal().getWidth() / 2.0) - 100;
                y = coord.getY() - 44;
            } else {
                x = window.getX() + (window.getWidth() / 2.0) - 160;
                y = window.getY() + 60;
            }
        } else {
            x = window.getX() + (window.getWidth() / 2.0) - 160;
            y = window.getY() + 60;
        }

        popup.show(window, Math.max(20, x), Math.max(20, y));

        FadeTransition fadeIn = new FadeTransition(Duration.millis(120), caixa);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition tempoEspera = new PauseTransition(Duration.seconds(tipo == TipoMensagem.EASTER_EGG ? 4.0 : 3.0));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), caixa);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            popup.hide();
            if (popupAtual == popup) {
                popupAtual = null;
            }
        });

        SequentialTransition seq = new SequentialTransition(fadeIn, tempoEspera, fadeOut);
        seq.play();
    }
}
