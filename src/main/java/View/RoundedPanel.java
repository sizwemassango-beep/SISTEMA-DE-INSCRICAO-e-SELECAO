package View;

import javax.swing.*;
import java.awt.*;

/**
 * Painel personalizado com bordas arredondadas e sombra suave.
 * Substitui os JPanel standard para um visual moderno e consistente.
 *
 * @author 01 Capital
 */
public class RoundedPanel extends JPanel {

    private int cornerRadius;
    private Color shadowColor;
    private boolean shadowEnabled;
    private int shadowSize;

    // ── Construtores ──────────────────────────────────────────────────────────

    public RoundedPanel(int cornerRadius) {
        this(cornerRadius, true);
    }

    public RoundedPanel(int cornerRadius, boolean shadow) {
        this.cornerRadius   = cornerRadius;
        this.shadowEnabled  = shadow;
        this.shadowSize     = 6;
        this.shadowColor    = new Color(0, 0, 0, 40);
        setOpaque(false);
    }

    // ── Pintura ───────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 0, y = 0;
        int w = getWidth();
        int h = getHeight();

        // Sombra suave (multi-camada)
        if (shadowEnabled) {
            for (int i = shadowSize; i > 0; i--) {
                int alpha = (int) (40.0 * (shadowSize - i + 1) / shadowSize);
                g2.setColor(new Color(0, 0, 0, Math.min(alpha, 60)));
                g2.fillRoundRect(x + i, y + i, w - i * 2, h - i * 2, cornerRadius + i, cornerRadius + i);
            }
        }

        // Fundo principal
        g2.setColor(getBackground());
        g2.fillRoundRect(x, y, w - shadowSize, h - shadowSize, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Tema.border());
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, getWidth() - shadowSize - 1, getHeight() - shadowSize - 1, cornerRadius, cornerRadius);
        g2.dispose();
    }

    @Override
    public Insets getInsets() {
        int extra = shadowEnabled ? shadowSize : 0;
        return new Insets(12, 16, 12 + extra, 16 + extra);
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public void setCornerRadius(int r) { this.cornerRadius = r; repaint(); }
    public void setShadowEnabled(boolean e) { this.shadowEnabled = e; repaint(); }
    public void setShadowSize(int s) { this.shadowSize = s; repaint(); }
}
