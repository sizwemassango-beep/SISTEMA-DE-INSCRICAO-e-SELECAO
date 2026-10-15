package View;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * Border arredondada simples.
 * Usada nos painéis gerados pelo Form Editor para dar visual de card moderno
 * sem precisar de substituir o JPanel por uma classe custom.
 *
 * @author 01 Capital
 */
public class RoundedBorder extends AbstractBorder {

    private final int radius;
    private Color color;
    private final float thickness;

    public RoundedBorder(int radius) {
        this(radius, null, 1.5f);
    }

    public RoundedBorder(int radius, Color color, float thickness) {
        this.radius    = radius;
        this.color     = color;
        this.thickness = thickness;
    }

    public void setColor(Color c) { this.color = c; }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(color != null ? color : Tema.border());
        g2.drawRoundRect(x + 1, y + 1, width - 2, height - 2, radius, radius);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = radius / 2;
        return insets;
    }
}
