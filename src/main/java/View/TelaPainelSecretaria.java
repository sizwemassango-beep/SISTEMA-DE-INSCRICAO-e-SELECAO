package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tela 5: Painel da Secretaria
 * @author AI
 */
public class TelaPainelSecretaria extends JPanel {

    private JButton btnToggleTema;

    public TelaPainelSecretaria() {
        initComponents();
        postInit();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Tema.bg());

        // --- Sidebar (Esquerda) ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(0x1F2937)); // Cor fixa escura ou usa Tema.sidebar()
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        
        JLabel lblTituloSidebar = new JLabel("<html><b>Inscrição e Seleção</b><br><small>Área da secretaria</small></html>");
        lblTituloSidebar.setForeground(Color.WHITE);
        lblTituloSidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebar.add(lblTituloSidebar);
        
        sidebar.add(criarBotaoSidebar("Painel", true));
        sidebar.add(criarBotaoSidebar("Cursos", false));
        sidebar.add(criarBotaoSidebar("Inscrições", false));
        sidebar.add(criarBotaoSidebar("Notas", false));
        sidebar.add(criarBotaoSidebar("Seleção", false));
        
        sidebar.add(Box.createVerticalGlue());
        
        JButton btnTerminarSessao = criarBotaoSidebar("Terminar sessão", false);
        btnTerminarSessao.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if(topFrame != null){
                topFrame.setContentPane(new TelaInicial());
                topFrame.revalidate();
                topFrame.repaint();
            }
        });
        sidebar.add(btnTerminarSessao);
        sidebar.add(Box.createVerticalStrut(20));

        add(sidebar, BorderLayout.WEST);

        // --- Main Content (Centro) ---
        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new BoxLayout(pnlCentro, BoxLayout.Y_AXIS));
        pnlCentro.setOpaque(false);
        pnlCentro.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Título principal
        JLabel lblTitulo = new JLabel("Painel");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblSub = new JLabel("Resumo do processo de admissão de 2026");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Tema.subtext());
        
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlHeader.setOpaque(false);
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.add(lblTitulo);
        pnlHeader.add(Box.createVerticalStrut(5));
        pnlHeader.add(lblSub);
        
        // Cards (KPIs)
        JPanel pnlKpis = new JPanel(new GridLayout(1, 4, 20, 0));
        pnlKpis.setOpaque(false);
        pnlKpis.add(criarKpiCard("Candidatos inscritos", "248"));
        pnlKpis.add(criarKpiCard("Cursos ativos", "3"));
        pnlKpis.add(criarKpiCard("Notas registadas", "231"));
        pnlKpis.add(criarKpiCard("Vagas totais", "70"));
        
        // Estado do processo
        JPanel pnlEstado = new JPanel(new BorderLayout());
        pnlEstado.setOpaque(false);
        pnlEstado.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Tema.border()), "Estado do processo"));
        pnlEstado.setBackground(Tema.card());
        pnlEstado.setOpaque(true);
        
        JPanel pnlStepper = new JPanel(new GridLayout(1, 4));
        pnlStepper.setOpaque(false);
        pnlStepper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlStepper.add(criarStep("1", "Inscrições", true));
        pnlStepper.add(criarStep("2", "Notas", false));
        pnlStepper.add(criarStep("3", "Seleção", false));
        pnlStepper.add(criarStep("4", "Resultados", false));
        pnlEstado.add(pnlStepper, BorderLayout.CENTER);

        // Estatísticas (Gráficos simulados)
        JPanel pnlEstatisticas = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlEstatisticas.setOpaque(false);
        
        pnlEstatisticas.add(criarPainelEstatisticas("Candidatos por província", new String[]{"Maputo Cidade", "Gaza", "Inhambane", "Maputo Prov.", "Sofala", "Outras"}, new int[]{74, 56, 36, 29, 15, 38}, 100));
        pnlEstatisticas.add(criarPainelEstatisticas("Inscrições por curso", new String[]{"Medicina", "Engenharia", "Direito"}, new int[]{112, 83, 53}, 120));

        // Montagem do centro
        pnlCentro.add(pnlHeader);
        pnlCentro.add(Box.createVerticalStrut(30));
        
        pnlKpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        pnlCentro.add(pnlKpis);
        pnlCentro.add(Box.createVerticalStrut(30));
        
        pnlEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        pnlCentro.add(pnlEstado);
        pnlCentro.add(Box.createVerticalStrut(30));
        
        pnlCentro.add(pnlEstatisticas);
        
        add(pnlCentro, BorderLayout.CENTER);
    }

    private JButton criarBotaoSidebar(String texto, boolean ativo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", ativo ? Font.BOLD : Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(ativo ? new Color(0x374151) : new Color(0x1F2937));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!ativo) btn.setBackground(new Color(0x374151));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!ativo) btn.setBackground(new Color(0x1F2937));
            }
        });
        
        return btn;
    }

    private JPanel criarKpiCard(String titulo, String valor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Tema.card());
        card.setBorder(new RoundedBorder(15, Tema.border(), 1));
        
        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblT.setForeground(Tema.subtext());
        lblT.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblV = new JLabel(valor);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblV.setForeground(Tema.primary());
        lblV.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(Box.createVerticalStrut(15));
        card.add(lblT);
        card.add(Box.createVerticalStrut(5));
        card.add(lblV);
        card.add(Box.createVerticalStrut(15));
        
        return card;
    }

    private JPanel criarStep(String num, String desc, boolean ativo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        
        JLabel circle = new JLabel(num, SwingConstants.CENTER);
        circle.setOpaque(true);
        circle.setBackground(ativo ? Tema.primary() : Tema.border());
        circle.setForeground(ativo ? Color.WHITE : Tema.text());
        circle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        circle.setPreferredSize(new Dimension(30, 30));
        circle.setMaximumSize(new Dimension(30, 30));
        circle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel text = new JLabel(desc);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        text.setForeground(Tema.text());
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        p.add(circle);
        p.add(Box.createVerticalStrut(5));
        p.add(text);
        
        return p;
    }

    private JPanel criarPainelEstatisticas(String titulo, String[] labels, int[] valores, int maxVal) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.card());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Tema.border()), titulo));
        panel.setOpaque(true);
        
        JPanel pnlLista = new JPanel(new GridLayout(labels.length, 1, 0, 5));
        pnlLista.setOpaque(false);
        pnlLista.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < labels.length; i++) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            
            JLabel lblNome = new JLabel(labels[i]);
            lblNome.setPreferredSize(new Dimension(100, 20));
            
            JProgressBar bar = new JProgressBar(0, maxVal);
            bar.setValue(valores[i]);
            bar.setStringPainted(false);
            bar.setForeground(new Color(0x1D4ED8)); // Azul
            
            JLabel lblVal = new JLabel(String.valueOf(valores[i]));
            lblVal.setPreferredSize(new Dimension(30, 20));
            
            row.add(lblNome, BorderLayout.WEST);
            row.add(bar, BorderLayout.CENTER);
            row.add(lblVal, BorderLayout.EAST);
            
            pnlLista.add(row);
        }
        panel.add(pnlLista);
        return panel;
    }

    private void postInit() {
        criarBotaoToggle();
        Tema.addChangeListener(this::aplicarTema);
        aplicarTema();
    }

    private void aplicarTema() {
        setBackground(Tema.bg());
        if (btnToggleTema != null) {
            btnToggleTema.setText(Tema.isDark() ? "☀ Light" : "🌙 Dark");
        }
        repaint();
    }
    
    private void criarBotaoToggle() {
        btnToggleTema = new JButton(Tema.isDark() ? "☀ Light" : "🌙 Dark");
        btnToggleTema.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnToggleTema.setFocusPainted(false);
        btnToggleTema.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnToggleTema.setSize(100, 28);
        btnToggleTema.addActionListener(e -> {
            Tema.toggleTheme();
        });
        
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(TelaPainelSecretaria.this);
                if (frame != null) {
                    JLayeredPane lp = frame.getLayeredPane();
                    btnToggleTema.setLocation(frame.getWidth() - 120, 10);
                    lp.add(btnToggleTema, JLayeredPane.PALETTE_LAYER);
                    lp.repaint();
                }
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {}
            public void ancestorMoved(javax.swing.event.AncestorEvent event) {}
        });
    }
}
