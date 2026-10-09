package View;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerenciador de tema centralizado.
 * Suporta alternância entre Dark e Light mode com FlatLaf.
 *
 * @author 01 Capital
 */
public class Tema {

    // ── Paleta Dark ──────────────────────────────────────────────────────────
    public static final Color DARK_BG         = new Color(0x1A1A2E);
    public static final Color DARK_SURFACE    = new Color(0x16213E);
    public static final Color DARK_CARD       = new Color(0x0F3460);
    public static final Color DARK_ACCENT     = new Color(0x533483);
    public static final Color DARK_PRIMARY    = new Color(0x4361EE);
    public static final Color DARK_HOVER      = new Color(0x4CC9F0);
    public static final Color DARK_TEXT       = new Color(0xE2E8F0);
    public static final Color DARK_SUBTEXT    = new Color(0x94A3B8);
    public static final Color DARK_BORDER     = new Color(0x334155);

    // ── Paleta Light ─────────────────────────────────────────────────────────
    public static final Color LIGHT_BG        = new Color(0xF8FAFC);
    public static final Color LIGHT_SURFACE   = new Color(0xFFFFFF);
    public static final Color LIGHT_CARD      = new Color(0xFFFFFF);
    public static final Color LIGHT_PRIMARY   = new Color(0x003399);
    public static final Color LIGHT_HOVER     = new Color(0x4361EE);
    public static final Color LIGHT_TEXT      = new Color(0x1E293B);
    public static final Color LIGHT_SUBTEXT   = new Color(0x64748B);
    public static final Color LIGHT_BORDER    = new Color(0xE2E8F0);
    public static final Color LIGHT_SIDEBAR   = new Color(0x003399);

    // ── Estado atual ─────────────────────────────────────────────────────────
    private static boolean darkMode = false;

    /** Lista de listeners notificados ao trocar o tema */
    private static final List<Runnable> changeListeners = new ArrayList<>();

    // ── API pública ───────────────────────────────────────────────────────────

    /** Aplica o tema Light no arranque da aplicação. */
    public static void aplicar() {
        aplicarFlatLaf(false);
    }

    /** Alterna entre Dark e Light mode e notifica todos os listeners. */
    public static void toggleTheme() {
        darkMode = !darkMode;
        aplicarFlatLaf(darkMode);
        notifyListeners();
    }

    /** @return {@code true} se o Dark mode está activo */
    public static boolean isDark() {
        return darkMode;
    }

    /** Regista um listener chamado sempre que o tema muda. */
    public static void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    // ── Cores dinâmicas (adaptam-se ao modo actual) ───────────────────────────

    public static Color bg()      { return darkMode ? DARK_BG      : LIGHT_BG; }
    public static Color surface() { return darkMode ? DARK_SURFACE  : LIGHT_SURFACE; }
    public static Color card()    { return darkMode ? DARK_CARD     : LIGHT_CARD; }
    public static Color primary() { return darkMode ? DARK_PRIMARY  : LIGHT_PRIMARY; }
    public static Color hover()   { return darkMode ? DARK_HOVER    : LIGHT_HOVER; }
    public static Color text()    { return darkMode ? DARK_TEXT     : LIGHT_TEXT; }
    public static Color subtext() { return darkMode ? DARK_SUBTEXT  : LIGHT_SUBTEXT; }
    public static Color border()  { return darkMode ? DARK_BORDER   : LIGHT_BORDER; }
    public static Color sidebar() { return darkMode ? DARK_SURFACE  : LIGHT_SIDEBAR; }

    // ── Fontes ────────────────────────────────────────────────────────────────

    public static Font fontTitle()    { return new Font("Segoe UI", Font.BOLD, 22); }
    public static Font fontHeading()  { return new Font("Segoe UI", Font.BOLD, 16); }
    public static Font fontBody()     { return new Font("Segoe UI", Font.PLAIN, 13); }
    public static Font fontSmall()    { return new Font("Segoe UI", Font.PLAIN, 11); }
    public static Font fontButton()   { return new Font("Segoe UI", Font.BOLD, 13); }

    // ── Interno ───────────────────────────────────────────────────────────────

    private static void aplicarFlatLaf(boolean dark) {
        try {
            if (dark) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }

            // Arredondamento global
            UIManager.put("Button.arc",          16);
            UIManager.put("Component.arc",       12);
            UIManager.put("TextComponent.arc",   10);
            UIManager.put("ScrollBar.thumbArc",  999);

            // Botão primário
            UIManager.put("Button.focusWidth",   0);

            // Campos de texto — altura mínima confortável
            UIManager.put("TextField.minimumWidth",  160);
            UIManager.put("PasswordField.minimumWidth", 160);

            // Repintar todas as janelas abertas
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
                w.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void notifyListeners() {
        for (Runnable r : changeListeners) {
            SwingUtilities.invokeLater(r);
        }
    }
}