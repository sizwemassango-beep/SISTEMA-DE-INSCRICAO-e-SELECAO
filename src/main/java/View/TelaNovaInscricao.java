package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tela 3: Nova Inscrição
 * @author 01Capital
 */
public class TelaNovaInscricao extends JPanel {

    private JButton btnVoltar;
    private JButton btnSubmeter;
    private JButton btnToggleTema;

    public TelaNovaInscricao() {
        initComponents();
        postInit();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Tema.bg());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setOpaque(false);
        JLabel lblTitulo = new JLabel("Nova inscrição");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel lblDesc = new JLabel("Preencha os seus dados para se candidatar ao curso preparatório.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(lblTitulo);
        headerText.add(Box.createVerticalStrut(5));
        headerText.add(lblDesc);
        header.add(headerText);

        add(header, BorderLayout.NORTH);

        // Body
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // --- Dados Pessoais ---
        JPanel pnlDados = new JPanel(new GridLayout(3, 4, 15, 15));
        pnlDados.setOpaque(false);
        pnlDados.setBorder(BorderFactory.createTitledBorder("Dados pessoais"));
        
        pnlDados.add(new JLabel("Nome completo"));
        JTextField txtNome = new JTextField();
        pnlDados.add(txtNome);
        
        pnlDados.add(new JLabel("Número do BI"));
        JTextField txtBI = new JTextField();
        pnlDados.add(txtBI);
        
        pnlDados.add(new JLabel("Data de nascimento"));
        JTextField txtData = new JTextField();
        pnlDados.add(txtData);
        
        pnlDados.add(new JLabel("Província"));
        JComboBox<String> cbProvincia = new JComboBox<>(new String[]{"Selecione...", "Maputo", "Gaza", "Inhambane"});
        pnlDados.add(cbProvincia);
        
        pnlDados.add(new JLabel("Distrito"));
        JTextField txtDistrito = new JTextField();
        pnlDados.add(txtDistrito);
        
        pnlDados.add(new JLabel("Número de celular"));
        JTextField txtCelular = new JTextField();
        pnlDados.add(txtCelular);

        // --- Conta de acesso ---
        JPanel pnlConta = new JPanel(new GridLayout(2, 2, 15, 15));
        pnlConta.setOpaque(false);
        pnlConta.setBorder(BorderFactory.createTitledBorder("Conta de acesso"));
        
        pnlConta.add(new JLabel("Email"));
        JTextField txtEmail = new JTextField();
        pnlConta.add(txtEmail);
        
        pnlConta.add(new JLabel("Senha"));
        JPasswordField txtSenha = new JPasswordField();
        pnlConta.add(txtSenha);

        // --- Curso Pretendido ---
        JPanel pnlCurso = new JPanel(new GridLayout(1, 2, 15, 15));
        pnlCurso.setOpaque(false);
        pnlCurso.setBorder(BorderFactory.createTitledBorder("Curso pretendido"));
        
        pnlCurso.add(new JLabel("Curso"));
        JComboBox<String> cbCurso = new JComboBox<>(new String[]{"Preparatório de Medicina (20 vagas)", "Preparatório de Engenharia"});
        pnlCurso.add(cbCurso);

        // --- Questionário ---
        JPanel pnlQuest = new JPanel(new GridLayout(3, 2, 15, 15));
        pnlQuest.setOpaque(false);
        pnlQuest.setBorder(BorderFactory.createTitledBorder("Questionário de carência"));
        
        pnlQuest.add(new JLabel("Rendimento familiar"));
        JComboBox<String> cbRendimento = new JComboBox<>(new String[]{"Até 5.000 MT", "5.000 a 10.000 MT", "Mais de 10.000 MT"});
        pnlQuest.add(cbRendimento);
        
        JCheckBox chkOrfao = new JCheckBox("É órfão ou chefe de família");
        chkOrfao.setOpaque(false);
        pnlQuest.add(chkOrfao);
        
        pnlQuest.add(new JLabel("Situação laboral"));
        JComboBox<String> cbLaboral = new JComboBox<>(new String[]{"Desempregado", "Trabalhador Estudante"});
        pnlQuest.add(cbLaboral);
        
        JCheckBox chkApoio = new JCheckBox("Recebe algum apoio financeiro");
        chkApoio.setOpaque(false);
        pnlQuest.add(chkApoio);
        
        pnlQuest.add(new JLabel("Distância (km)"));
        JTextField txtDistancia = new JTextField();
        pnlQuest.add(txtDistancia);

        centerPanel.add(pnlDados);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(pnlConta);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(pnlCurso);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(pnlQuest);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlBottom.setOpaque(false);
        
        btnVoltar = new JButton("Voltar");
        btnSubmeter = new JButton("Submeter inscrição");
        
        pnlBottom.add(btnVoltar);
        pnlBottom.add(btnSubmeter);
        
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void postInit() {
        btnVoltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSubmeter.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSubmeter.setBackground(Tema.primary());
        btnSubmeter.setForeground(Color.WHITE);
        
        btnSubmeter.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Inscrição submetida com sucesso (Validação a implementar).", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnVoltar.addActionListener(e -> {
            // Volta para tela inicial
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if(topFrame != null){
                topFrame.setContentPane(new TelaInicial());
                topFrame.revalidate();
                topFrame.repaint();
            }
        });
        
        criarBotaoToggle();
        Tema.addChangeListener(this::aplicarTema);
        aplicarTema();
    }

    private void aplicarTema() {
        setBackground(Tema.bg());
        btnSubmeter.setBackground(Tema.primary());
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
    }
}
