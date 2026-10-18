package View.fx;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

                container.setStyle("-fx-background-color: transparent; -fx-background-radius: 16px;");
                DropShadow sombra = new DropShadow(8, 0, 3, Color.rgb(0, 0, 0, 0.18));
                iv.setEffect(sombra);

                container.getChildren().add(iv);
                return container;
            }
        } catch (Exception e) {
            System.err.println("Aviso: Logo não carregado: " + e.getMessage());
        }

        return container;
    }

    public static StackPane criarLogoCompacto(double tamanho) {
        return criarLogo(tamanho, tamanho);
    }
}
