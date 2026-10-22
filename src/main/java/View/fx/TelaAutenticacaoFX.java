package View.fx;

import controller.InscricaoController;
import controller.LoginController;
import dao.ConexaoBD;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Administrador;
import model.Candidato;
import model.Curso;
import model.QuestionarioCarencia;
import service.AutenticacaoService.ResultadoLogin;
import service.DatabaseInitializer;
import service.RelatorioPdfService;

import java.io.File;
import java.time.LocalDate;

/**

 * Representa a interface visual relacionada a TelaAutenticacaoFX.

 */

public class TelaAutenticacaoFX extends BorderPane {

    private final LoginController loginController;
    private final InscricaoController inscricaoController;
    private final DatabaseInitializer dbInit;

    private VBox boxFormularioDinamico;
    private Button btnAbaLogin;
    private Button btnAbaCadastro;
    private Label lblMensagemFeedback;
    private Runnable alternadorSenhaAtual;
    private javafx.scene.Node campoFocoPrincipal;

    public TelaAutenticacaoFX() {
        this.loginController = new LoginController();
        this.inscricaoController = new InscricaoController();
        this.dbInit = new DatabaseInitializer(new ConexaoBD());
        construirInterface();
        configurarAtalhos();
    }

    public void alternarVisibilidadeSenha() {
        if (alternadorSenhaAtual != null) {
            alternadorSenhaAtual.run();
        }
    }

    private void configurarAtalhos() {
        this.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, ev -> {
            if (ev.isControlDown() && ev.getCode() == javafx.scene.input.KeyCode.TAB) {
                if (btnAbaLogin.getStyleClass().contains("tab-seletor-ativa")) {
                    ativarAbaCadastro();
                } else {
                    ativarAbaLogin();
                }
                ev.consume();
            } else if (ev.isControlDown() && ev.getCode() == javafx.scene.input.KeyCode.O) {
                alternarVisibilidadeSenha();
                ev.consume();
            } else if (ev.isControlDown() && ev.getCode() == javafx.scene.input.KeyCode.P) {
                if (campoFocoPrincipal != null) {
                    campoFocoPrincipal.requestFocus();
                }
                ev.consume();
            }
        });
    }

    private void construirInterface() {
        setTop(criarBarraSuperior());
        setCenter(criarPainelPrincipal());
    }

    private HBox criarBarraSuperior() {
        HBox barra = new HBox(16);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(12, 28, 12, 28));
        barra.setStyle("-fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-width: 0 0 1 0;");

        StackPane logoMini = ComponentesVisuais.criarLogoCompacto(36);

        VBox titulos = new VBox(1);
        Label lblInst = new Label("Centro de Formação Ocean S.A.");
        lblInst.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");
        Label lblSis = new Label("Sistema de Inscrição e Seleção • 2026");
        lblSis.setStyle("-fx-font-size: 11px; -fx-text-fill: -fx-text-secondary;");
        titulos.getChildren().addAll(lblInst, lblSis);

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        boolean conectado = dbInit.testarConexao();
        Label lblStatusDb = new Label(conectado ? "● Base de Dados Ativa" : "○ Erro de Conexão BD");
        lblStatusDb.getStyleClass().add(conectado ? "badge-status-online" : "badge-status-offline");

        Button btnTema = new Button(Navegador.isModoEscuro() ? "☀ Claro" : "🌙 Escuro");
        btnTema.getStyleClass().add("button-secondary");
        btnTema.setOnAction(e -> {
            Navegador.alternarTema();
            btnTema.setText(Navegador.isModoEscuro() ? "☀ Claro" : "🌙 Escuro");
        });

        barra.getChildren().addAll(logoMini, titulos, espacador, lblStatusDb, btnTema);
        return barra;
    }

    private HBox criarPainelPrincipal() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        VBox banner = new VBox(24);
        banner.setPrefWidth(440);
        banner.setAlignment(Pos.CENTER);
        banner.setPadding(new Insets(40));
        banner.setStyle("-fx-background-color: linear-gradient(to bottom right, #0F172A, #1E3A8A);");

        StackPane logoGrande = ComponentesVisuais.criarLogo(170, 170);

        Label lblTitulo = new Label("Centro de Formação\nOcean S.A.");
        lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF; -fx-text-alignment: center;");

        Label lblSub = new Label("Portal Unificado de Acesso Académico e Inscrições para o Ano Letivo 2026.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #93C5FD; -fx-text-alignment: center;");
        lblSub.setWrapText(true);

        VBox boxBeneficios = new VBox(10);
        boxBeneficios.setAlignment(Pos.CENTER_LEFT);
        boxBeneficios.setPadding(new Insets(10, 0, 0, 10));
        boxBeneficios.getChildren().addAll(
                criarItemBanner("✓ Acesso direto via base de dados para Alunos e Secretaria"),
                criarItemBanner("✓ Inscrições online com emissão instantânea de PDF"),
                criarItemBanner("✓ Gestão transparente de vagas e critérios de carência")
        );

        banner.getChildren().addAll(logoGrande, lblTitulo, lblSub, boxBeneficios);

        VBox areaDireita = new VBox(16);
        areaDireita.setAlignment(Pos.CENTER);
        areaDireita.setPadding(new Insets(30, 48, 30, 48));
        HBox.setHgrow(areaDireita, Priority.ALWAYS);

        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setPrefWidth(550);
        card.setMaxWidth(580);
        card.setAlignment(Pos.TOP_LEFT);

        HBox abas = new HBox(12);
        btnAbaLogin = new Button("Iniciar Sessão");
        btnAbaLogin.getStyleClass().addAll("tab-seletor", "tab-seletor-ativa");

        btnAbaCadastro = new Button("Criar Conta (Candidato)");
        btnAbaCadastro.getStyleClass().add("tab-seletor");

        btnAbaLogin.setOnAction(e -> ativarAbaLogin());
        btnAbaCadastro.setOnAction(e -> ativarAbaCadastro());

        abas.getChildren().addAll(btnAbaLogin, btnAbaCadastro);

        lblMensagemFeedback = new Label();
        lblMensagemFeedback.setVisible(false);
        lblMensagemFeedback.setWrapText(true);
        lblMensagemFeedback.setMaxWidth(Double.MAX_VALUE);

        boxFormularioDinamico = new VBox(14);
        boxFormularioDinamico.setAlignment(Pos.TOP_LEFT);
        boxFormularioDinamico.setPadding(new Insets(2, 6, 6, 2));

        ScrollPane scrollForm = new ScrollPane(boxFormularioDinamico);
        scrollForm.setFitToWidth(true);
        scrollForm.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        scrollForm.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollForm.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollForm.setPrefHeight(490);
        scrollForm.setMaxHeight(550);

        card.getChildren().addAll(abas, lblMensagemFeedback, scrollForm);
        areaDireita.getChildren().add(card);

        container.getChildren().addAll(banner, areaDireita);

        ativarAbaLogin();

        return container;
    }

    private Label criarItemBanner(String texto) {
        Label item = new Label(texto);
        item.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 13px;");
        return item;
    }

    private void ativarAbaLogin() {
        btnAbaLogin.getStyleClass().remove("tab-seletor-ativa");
        btnAbaCadastro.getStyleClass().remove("tab-seletor-ativa");
        btnAbaLogin.getStyleClass().add("tab-seletor-ativa");
        limparMensagem();

        boxFormularioDinamico.getChildren().clear();

        Label lblSub = new Label("Introduza o seu email e senha. O sistema determinará automaticamente se o seu perfil é de Secretaria ou de Candidato.");
        lblSub.getStyleClass().add("card-subtitle");
        lblSub.setWrapText(true);

        VBox boxEmail = new VBox(6);
        Label lblEmail = new Label("Email de Acesso");
        lblEmail.setStyle("-fx-font-weight: 600; -fx-font-size: 12px;");
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("ex: utilizador@dominio.com");
        boxEmail.getChildren().addAll(lblEmail, txtEmail);

        VBox boxSenha = new VBox(6);
        Label lblSenha = new Label("Senha");
        lblSenha.setStyle("-fx-font-weight: 600; -fx-font-size: 12px;");
        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Sua palavra-passe");
        txtSenha.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 8px 10px;");

        TextField txtSenhaVisivel = new TextField();
        txtSenhaVisivel.setPromptText("Sua palavra-passe");
        txtSenhaVisivel.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 8px 10px;");
        txtSenhaVisivel.setVisible(false);
        txtSenhaVisivel.setManaged(false);
        txtSenha.textProperty().bindBidirectional(txtSenhaVisivel.textProperty());

        Button btnOlho = new Button("👁");
        btnOlho.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2px 8px; -fx-text-fill: -fx-text-secondary;");
        btnOlho.setOnAction(ev -> {
            boolean mostrar = !txtSenhaVisivel.isVisible();
            txtSenha.setVisible(!mostrar);
            txtSenha.setManaged(!mostrar);
            txtSenhaVisivel.setVisible(mostrar);
            txtSenhaVisivel.setManaged(mostrar);
            btnOlho.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2px 8px; -fx-text-fill: " + (mostrar ? "#2563EB;" : "-fx-text-secondary;"));
        });

        HBox boxInputSenha = new HBox(4, txtSenha, txtSenhaVisivel, btnOlho);
        boxInputSenha.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        boxInputSenha.setStyle("-fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        HBox.setHgrow(txtSenha, Priority.ALWAYS);
        HBox.setHgrow(txtSenhaVisivel, Priority.ALWAYS);
        txtSenha.setMaxWidth(Double.MAX_VALUE);
        txtSenhaVisivel.setMaxWidth(Double.MAX_VALUE);

        boxSenha.getChildren().addAll(lblSenha, boxInputSenha);

        campoFocoPrincipal = txtEmail;
        alternadorSenhaAtual = () -> btnOlho.fire();

        Button btnEntrar = new Button("Aceder ao Sistema →");
        btnEntrar.getStyleClass().add("button-primary");
        btnEntrar.setMaxWidth(Double.MAX_VALUE);

        btnEntrar.setOnAction(e -> {
            String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
            String senha = txtSenha.isVisible() ? txtSenha.getText() : txtSenhaVisivel.getText();

            if (email.isEmpty() || senha.isEmpty()) {
                mostrarAviso("Por favor, preencha o seu email e senha de acesso.", false);
                return;
            }

            ResultadoLogin resultado = loginController.autenticar(email, senha);
            if (!resultado.isSucesso()) {
                mostrarAviso("Credenciais inválidas. Verifique o email e a senha digitados.", false);
                return;
            }

            if (resultado.getPerfil() == service.AutenticacaoService.TipoPerfil.ADMINISTRADOR) {
                mostrarAviso("Acesso administrativo confirmado! A redirecionar...", true);
                model.Administrador admin = (model.Administrador) resultado.getUsuario();
                Navegador.irParaDashboardSecretaria(admin);
            } else if (resultado.getPerfil() == service.AutenticacaoService.TipoPerfil.CANDIDATO) {
                Candidato c = (Candidato) resultado.getUsuario();
                Navegador.irParaDashboardCandidato(c);
            }
        });

        txtSenha.setOnAction(e -> btnEntrar.fire());
        txtEmail.setOnAction(e -> txtSenha.requestFocus());

        boxFormularioDinamico.getChildren().addAll(lblSub, boxEmail, boxSenha, btnEntrar);
    }

    private void ativarAbaCadastro() {
        btnAbaLogin.getStyleClass().remove("tab-seletor-ativa");
        btnAbaCadastro.getStyleClass().remove("tab-seletor-ativa");
        btnAbaCadastro.getStyleClass().add("tab-seletor-ativa");
        limparMensagem();

        boxFormularioDinamico.getChildren().clear();

        Label lblSub = new Label("Criar nova conta de Candidato. Preencha todos os campos abaixo para registar a sua candidatura.");
        lblSub.getStyleClass().add("card-subtitle");
        lblSub.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome completo conforme o BI");
        adicionarCampoGrid(grid, "Nome Completo *", txtNome, 0, 0, 2);

        TextField txtBI = new TextField();
        txtBI.setPromptText("Ex: 11.01.00234567-A");
        Label lblBIFeedback = new Label();
        lblBIFeedback.setVisible(false);
        lblBIFeedback.setManaged(false);
        InscricaoController.aplicarMascaraEValidacaoBI(txtBI, lblBIFeedback);
        VBox boxBICompleto = new VBox(2);
        Label lblBITitulo = new Label("Número do BI (Formato: XX.XX.XXXXXXXX-X) *");
        lblBITitulo.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");
        boxBICompleto.getChildren().addAll(lblBITitulo, txtBI, lblBIFeedback);
        grid.add(boxBICompleto, 0, 1);

        DatePicker dpNasc = new DatePicker(LocalDate.of(2004, 1, 1));
        dpNasc.setMaxWidth(Double.MAX_VALUE);
        adicionarCampoGrid(grid, "Data de Nascimento *", dpNasc, 1, 1, 1);

        TextField txtTelefone = new TextField();
        txtTelefone.setPromptText("Ex: 841234567 (9 dígitos)");
        InscricaoController.aplicarFiltroCelular(txtTelefone, 9);
        adicionarCampoGrid(grid, "Telefone / Celular (9 dígitos) *", txtTelefone, 0, 2, 1);

        ComboBox<String> cbProvincia = new ComboBox<>(FXCollections.observableArrayList(
                "Maputo Cidade", "Maputo Província", "Gaza", "Inhambane", "Sofala",
                "Manica", "Tete", "Zambézia", "Nampula", "Cabo Delgado", "Niassa"
        ));
        cbProvincia.setValue("Maputo Cidade");
        cbProvincia.setMaxWidth(Double.MAX_VALUE);
        adicionarCampoGrid(grid, "Província *", cbProvincia, 1, 2, 1);

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("seuemail@exemplo.com");
        adicionarCampoGrid(grid, "Email de Acesso *", txtEmail, 0, 3, 2);

        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Mínimo 6 caracteres");
        txtSenha.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 8px 10px;");
        TextField txtSenhaVis = new TextField();
        txtSenhaVis.setPromptText("Mínimo 6 caracteres");
        txtSenhaVis.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 8px 10px;");
        txtSenhaVis.setVisible(false);
        txtSenhaVis.setManaged(false);
        txtSenha.textProperty().bindBidirectional(txtSenhaVis.textProperty());

        Button btnOlhoSenha = new Button("👁");
        btnOlhoSenha.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 4px 8px; -fx-text-fill: -fx-text-secondary;");
        btnOlhoSenha.setOnAction(ev -> {
            boolean m = !txtSenhaVis.isVisible();
            txtSenha.setVisible(!m); txtSenha.setManaged(!m);
            txtSenhaVis.setVisible(m); txtSenhaVis.setManaged(m);
            btnOlhoSenha.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 4px 8px; -fx-text-fill: " + (m ? "#2563EB;" : "-fx-text-secondary;"));
        });

        HBox boxSenhaRow = new HBox(2, txtSenha, txtSenhaVis, btnOlhoSenha);
        boxSenhaRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        boxSenhaRow.setStyle("-fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        HBox.setHgrow(txtSenha, Priority.ALWAYS);
        HBox.setHgrow(txtSenhaVis, Priority.ALWAYS);
        adicionarCampoGrid(grid, "Senha *", boxSenhaRow, 0, 4, 1);

        PasswordField txtConfirmar = new PasswordField();
        txtConfirmar.setPromptText("Repita a senha");
        txtConfirmar.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 8px 10px;");
        TextField txtConfirmarVis = new TextField();
        txtConfirmarVis.setPromptText("Repita a senha");
        txtConfirmarVis.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 8px 10px;");
        txtConfirmarVis.setVisible(false);
        txtConfirmarVis.setManaged(false);
        txtConfirmar.textProperty().bindBidirectional(txtConfirmarVis.textProperty());

        Button btnOlhoConf = new Button("👁");
        btnOlhoConf.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 4px 8px; -fx-text-fill: -fx-text-secondary;");
        btnOlhoConf.setOnAction(ev -> {
            boolean m = !txtConfirmarVis.isVisible();
            txtConfirmar.setVisible(!m); txtConfirmar.setManaged(!m);
            txtConfirmarVis.setVisible(m); txtConfirmarVis.setManaged(m);
            btnOlhoConf.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 4px 8px; -fx-text-fill: " + (m ? "#2563EB;" : "-fx-text-secondary;"));
        });

        HBox boxConfRow = new HBox(2, txtConfirmar, txtConfirmarVis, btnOlhoConf);
        boxConfRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        boxConfRow.setStyle("-fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-radius: 8px; -fx-background-radius: 8px;");
        HBox.setHgrow(txtConfirmar, Priority.ALWAYS);
        HBox.setHgrow(txtConfirmarVis, Priority.ALWAYS);
        adicionarCampoGrid(grid, "Confirmar Senha *", boxConfRow, 1, 4, 1);

        campoFocoPrincipal = txtNome;
        alternadorSenhaAtual = () -> {
            btnOlhoSenha.fire();
            btnOlhoConf.fire();
        };

        // Novo campo: Nota da 12ª Classe
        TextField txtNota12 = new TextField();
        txtNota12.setPromptText("Ex: 14.5 (escala de 0 a 20 valores)");
        InscricaoController.aplicarFiltroNumericoDecimal(txtNota12, 20.0);
        adicionarCampoGrid(grid, "Nota da 12ª Classe (0 a 20) *", txtNota12, 0, 5, 2);

        ComboBox<Curso> cbCurso = new ComboBox<>(FXCollections.observableArrayList(inscricaoController.listarCursosDisponiveis()));
        cbCurso.setMaxWidth(Double.MAX_VALUE);
        cbCurso.setPromptText("Selecione o Curso Pretendido");
        cbCurso.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Curso c) {
                if (c == null) return "";
                return c.getNome() + " (" + c.getNumeroVagas() + " vagas • Min. Carência: " + c.getPontosMinimosCarencia() + " pts)";
            }
            @Override
            public Curso fromString(String string) { return null; }
        });
        if (!cbCurso.getItems().isEmpty()) {
            cbCurso.setValue(cbCurso.getItems().get(0));
        }
        adicionarCampoGrid(grid, "Curso Pretendido *", cbCurso, 0, 6, 2);

        // --- Seção Questionário de Carência Socioeconómica ---
        Label lblSecaoQuest = new Label("Questionário de Carência Socioeconómica");
        lblSecaoQuest.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: -fx-primary-color; -fx-padding: 8px 0 0 0;");

        GridPane gridQuest = new GridPane();
        gridQuest.setHgap(12);
        gridQuest.setVgap(10);

        ComboBox<QuestionarioCarencia.FaixaRendimento> cbRendimento = new ComboBox<>(FXCollections.observableArrayList(QuestionarioCarencia.FaixaRendimento.values()));
        cbRendimento.setValue(QuestionarioCarencia.FaixaRendimento.ATE_5000);
        cbRendimento.setMaxWidth(Double.MAX_VALUE);
        cbRendimento.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(cbRendimento, "Rendimento: Menor renda dá até 40 pontos na candidatura para vagas reservadas.", ComponentesVisuais.TipoMensagem.INFO);
        });
        adicionarCampoGrid(gridQuest, "1. Rendimento Familiar Mensal *", cbRendimento, 0, 0, 1);

        ComboBox<QuestionarioCarencia.SituacaoLaboral> cbLaboral = new ComboBox<>(FXCollections.observableArrayList(QuestionarioCarencia.SituacaoLaboral.values()));
        cbLaboral.setValue(QuestionarioCarencia.SituacaoLaboral.DESEMPREGADO);
        cbLaboral.setMaxWidth(Double.MAX_VALUE);
        cbLaboral.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(cbLaboral, "Situação Laboral: Desempregados ou sem renda recebem até 20 pontos de prioridade.", ComponentesVisuais.TipoMensagem.INFO, true);
        });
        adicionarCampoGrid(gridQuest, "2. Situação Laboral do Agregado *", cbLaboral, 1, 0, 1);

        TextField txtDistancia = new TextField("5.0");
        txtDistancia.setPromptText("Distância em km (0 a 2000)");
        InscricaoController.aplicarFiltroNumericoDecimal(txtDistancia, 2000.0);
        txtDistancia.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(txtDistancia, "Distância: Quem mora a mais de 100 km recebe até 15 pontos adicionais.", ComponentesVisuais.TipoMensagem.INFO, true);
        });
        adicionarCampoGrid(gridQuest, "3. Distância até à Instituição (km) *", txtDistancia, 0, 1, 1);

        VBox boxChecks = new VBox(8);
        boxChecks.setPadding(new Insets(18, 0, 0, 0));
        CheckBox chkOrfao = new CheckBox("4. É órfão ou chefe de família (+15 pts)");
        chkOrfao.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(chkOrfao, "Órfão / Chefe de Família: Bonificação de 15 pontos adicionais no critério social.", ComponentesVisuais.TipoMensagem.INFO, true);
        });
        CheckBox chkApoio = new CheckBox("5. Recebe apoio financeiro governamental/familiar");
        chkApoio.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(chkApoio, "Apoio Financeiro: Quem NÃO possui auxílio financeiro recebe 10 pontos de carência.", ComponentesVisuais.TipoMensagem.INFO, true);
        });
        boxChecks.getChildren().addAll(chkOrfao, chkApoio);
        gridQuest.add(boxChecks, 1, 1);

        Label lblPrevia = new Label();
        lblPrevia.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1D4ED8; -fx-background-color: #EFF6FF; -fx-padding: 8px 12px; -fx-background-radius: 6px;");

        Label lblValidacaoAoVivo = new Label();
        lblValidacaoAoVivo.setWrapText(true);
        lblValidacaoAoVivo.setMaxWidth(Double.MAX_VALUE);
        lblValidacaoAoVivo.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 6px 10px; -fx-background-radius: 6px;");

        Button btnCadastrar = new Button("Concluir Inscrição de Candidato");
        btnCadastrar.getStyleClass().add("button-primary");
        btnCadastrar.setMaxWidth(Double.MAX_VALUE);
        btnCadastrar.setDisable(true);

        Runnable validarFormularioEmTempoReal = () -> {
            String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
            String bi = txtBI.getText() != null ? txtBI.getText().trim() : "";
            String telefone = txtTelefone.getText() != null ? txtTelefone.getText().trim() : "";
            String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
            String senha = txtSenha.getText() != null ? txtSenha.getText() : "";
            String confirmar = txtConfirmar.getText() != null ? txtConfirmar.getText() : "";
            String nota12Str = txtNota12.getText() != null ? txtNota12.getText().trim() : "";
            LocalDate dataNasc = dpNasc.getValue();

            boolean nomeOk = InscricaoController.isNomeValido(nome);
            boolean biOk = InscricaoController.isBiValido(bi);
            boolean telOk = InscricaoController.isTelefoneValido(telefone);
            boolean emailOk = InscricaoController.isEmailValido(email);
            boolean senhaOk = InscricaoController.isSenhaValida(senha);
            boolean confirmarOk = !senha.isEmpty() && senha.equals(confirmar);
            boolean dataOk = InscricaoController.isDataNascimentoValida(dataNasc);
            boolean nota12Ok = InscricaoController.isNota12Valida(nota12Str);

            InscricaoController.marcarValidadeCampo(txtNome, nome.isEmpty() || nomeOk);
            InscricaoController.marcarValidadeCampo(txtBI, bi.isEmpty() || biOk);
            InscricaoController.marcarValidadeCampo(txtTelefone, telefone.isEmpty() || telOk);
            InscricaoController.marcarValidadeCampo(txtEmail, email.isEmpty() || emailOk);
            InscricaoController.marcarValidadeCampo(txtSenha, senha.isEmpty() || senhaOk);
            InscricaoController.marcarValidadeCampo(txtConfirmar, confirmar.isEmpty() || confirmarOk);
            InscricaoController.marcarValidadeCampo(txtNota12, nota12Str.isEmpty() || nota12Ok);

            if (nome.isEmpty()) {
                lblValidacaoAoVivo.setText("ℹ Preencha o nome completo (nome e apelido).");
                lblValidacaoAoVivo.setStyle("-fx-text-fill: #475569; -fx-background-color: #F1F5F9;");
                btnCadastrar.setDisable(true);
                return;
            }
            if (!nomeOk) {
                lblValidacaoAoVivo.setText("⚠ Digite o nome completo com pelo menos nome e apelido.");
                lblValidacaoAoVivo.setStyle("-fx-text-fill: #B91C1C; -fx-background-color: #FEE2E2;");
                btnCadastrar.setDisable(true);
                return;
            }
            if (!biOk) {
                lblValidacaoAoVivo.setText("⚠ BI moçambicano inválido: deve ter 13 caracteres (2 província, 2 distrito, 8 sequência e 1 letra maiúscula, ex: 110100234567A).");
                lblValidacaoAoVivo.setStyle("-fx-text-fill: #B91C1C; -fx-background-color: #FEE2E2;");
                btnCadastrar.setDisable(true);
                return;
            }
            if (!dataOk) {
                lblValidacaoAoVivo.setText("⚠ Data de nascimento inválida (o candidato deve ter mais de 14 anos).");
                lblValidacaoAoVivo.setStyle("-fx-text-fill: #B91C1C; -fx-background-color: #FEE2E2;");
                btnCadastrar.setDisable(true);
                return;
            }
            if (!telOk) {
                lblValidacaoAoVivo.setText("⚠ Celular incompleto: deve conter exatamente 9 dígitos (ex: 841234567).");
                lblValidacaoAoVivo.setStyle("-fx-text-fill: #B91C1C; -fx-background-color: #FEE2E2;");
                btnCadastrar.setDisable(true);
                return;
            }
            if (!nota12Ok) {
                lblValidacaoAoVivo.setText("⚠ Digite a nota média da 12ª classe (escala de 0 a 20 valores).");
                lblValidacaoAoVivo.setStyle("-fx-text-fill: #B91C1C; -fx-background-color: #FEE2E2;");
                btnCadastrar.setDisable(true);
                return;
            }
            if (!emailOk) {
                lblValidacaoAoVivo.setText("⚠ Digite um endereço de email válido (ex: seuemail@dominio.com).");
                lblValidacaoAoVivo.setStyle("-fx-text-fill: #B91C1C; -fx-background-color: #FEE2E2;");
                btnCadastrar.setDisable(true);
                return;
            }
            if (!senhaOk) {
                lblValidacaoAoVivo.setText("⚠ A senha de acesso deve ter pelo menos 6 caracteres.");
                lblValidacaoAoVivo.setStyle("-fx-text-fill: #B91C1C; -fx-background-color: #FEE2E2;");
                btnCadastrar.setDisable(true);
                return;
            }
            if (!confirmarOk) {
                lblValidacaoAoVivo.setText("⚠ A confirmação de senha não coincide com a senha digitada.");
                lblValidacaoAoVivo.setStyle("-fx-text-fill: #B91C1C; -fx-background-color: #FEE2E2;");
                btnCadastrar.setDisable(true);
                return;
            }

            lblValidacaoAoVivo.setText("✓ Dados validados com sucesso! Pode avançar com a submissão.");
            lblValidacaoAoVivo.setStyle("-fx-text-fill: #15803D; -fx-background-color: #DCFCE7;");
            btnCadastrar.setDisable(false);
        };

        txtNome.textProperty().addListener((o, a, b) -> validarFormularioEmTempoReal.run());
        txtBI.textProperty().addListener((o, a, b) -> validarFormularioEmTempoReal.run());
        txtTelefone.textProperty().addListener((o, a, b) -> validarFormularioEmTempoReal.run());
        txtEmail.textProperty().addListener((o, a, b) -> validarFormularioEmTempoReal.run());
        txtSenha.textProperty().addListener((o, a, b) -> validarFormularioEmTempoReal.run());
        txtConfirmar.textProperty().addListener((o, a, b) -> validarFormularioEmTempoReal.run());
        txtNota12.textProperty().addListener((o, a, b) -> validarFormularioEmTempoReal.run());
        dpNasc.valueProperty().addListener((o, a, b) -> validarFormularioEmTempoReal.run());

        Runnable atualizarPrevia = () -> {
            try {
                double dist = 0.0;
                try {
                    dist = Double.parseDouble(txtDistancia.getText().trim());
                } catch (Exception ignored) {}

                QuestionarioCarencia temp = new QuestionarioCarencia(
                        cbRendimento.getValue(),
                        cbLaboral.getValue(),
                        dist,
                        chkOrfao.isSelected(),
                        chkApoio.isSelected()
                );
                int pontos = temp.calcularPontuacao();
                String nivel = temp.classificar().getDescricao();
                boolean reservada = temp.elegivelParaVagaReservada();
                lblPrevia.setText("Pontuação Estimada: " + pontos + "/100 • " + nivel + (reservada ? " (Elegível p/ Vagas Reservadas)" : ""));
            } catch (Exception ignored) {}
        };

        cbRendimento.valueProperty().addListener((obs, a, b) -> atualizarPrevia.run());
        cbLaboral.valueProperty().addListener((obs, a, b) -> atualizarPrevia.run());
        chkOrfao.selectedProperty().addListener((obs, a, b) -> atualizarPrevia.run());
        chkApoio.selectedProperty().addListener((obs, a, b) -> atualizarPrevia.run());
        txtDistancia.textProperty().addListener((obs, a, b) -> {
            atualizarPrevia.run();
            validarFormularioEmTempoReal.run();
        });
        atualizarPrevia.run();
        validarFormularioEmTempoReal.run();

        btnCadastrar.setOnAction(e -> {
            try {
                String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
                String bi = txtBI.getText() != null ? txtBI.getText().trim() : "";
                String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
                String senha = txtSenha.getText();
                String confirmar = txtConfirmar.getText();
                String nota12Str = txtNota12.getText() != null ? txtNota12.getText().trim() : "";

                if (nome.isEmpty() || bi.isEmpty() || email.isEmpty() || senha == null || senha.isEmpty() || nota12Str.isEmpty()) {
                    mostrarAviso("Preencha todos os campos obrigatórios (*).", false);
                    return;
                }

                if (!senha.equals(confirmar)) {
                    mostrarAviso("A senha e a confirmação de senha não coincidem.", false);
                    return;
                }

                double distanciaKm = 0.0;
                try {
                    distanciaKm = Double.parseDouble(txtDistancia.getText().trim());
                    if (distanciaKm < 0 || distanciaKm > QuestionarioCarencia.DISTANCIA_MAXIMA_KM) {
                        mostrarAviso("A distância deve ser entre 0 e 2000 km.", false);
                        return;
                    }
                } catch (NumberFormatException nfe) {
                    mostrarAviso("Por favor, insira um número válido para a distância em km.", false);
                    return;
                }

                double nota12 = Double.parseDouble(nota12Str.replace(',', '.'));
                String biNormalizado = InscricaoController.normalizarBI(bi);

                Candidato c = new Candidato(nome, email, senha);
                c.setNumeroBI(biNormalizado);
                c.setDataDeNascimento(dpNasc.getValue());
                c.setTelefone(txtTelefone.getText());
                c.setProvincia(cbProvincia.getValue());
                c.setDataInscricao(LocalDate.now());

                QuestionarioCarencia q = new QuestionarioCarencia(
                        cbRendimento.getValue(),
                        cbLaboral.getValue(),
                        distanciaKm,
                        chkOrfao.isSelected(),
                        chkApoio.isSelected()
                );

                inscricaoController.submeterInscricao(c, q, cbCurso.getValue(), nota12);

                mostrarAviso("Candidatura submetida e conta criada com sucesso!", true);
                Navegador.irParaDashboardCandidato(c);

            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAviso("Erro na inscrição: " + ex.getMessage(), false);
            }
        });

        boxFormularioDinamico.getChildren().addAll(
                lblSub,
                grid,
                new Separator(),
                lblSecaoQuest,
                gridQuest,
                lblPrevia,
                lblValidacaoAoVivo,
                btnCadastrar
        );
    }

    private void adicionarCampoGrid(GridPane grid, String rotulo, Control campo, int col, int row, int colSpan) {
        adicionarCampoGrid(grid, rotulo, (javafx.scene.Node) campo, col, row, colSpan);
    }

    private void adicionarCampoGrid(GridPane grid, String rotulo, javafx.scene.Node campo, int col, int row, int colSpan) {
        VBox box = new VBox(4);
        Label lbl = new Label(rotulo);
        lbl.setStyle("-fx-font-weight: 600; -fx-font-size: 11px;");
        box.getChildren().addAll(lbl, campo);
        grid.add(box, col, row, colSpan, 1);
        GridPane.setHgrow(box, Priority.ALWAYS);
    }

    private void mostrarAviso(String texto, boolean sucesso) {
        lblMensagemFeedback.setText(texto);
        lblMensagemFeedback.setVisible(true);
        if (sucesso) {
            lblMensagemFeedback.setStyle("-fx-text-fill: #15803D !important; -fx-background-color: #DCFCE7; -fx-padding: 8px 12px; -fx-background-radius: 6px; -fx-font-weight: bold;");
            ComponentesVisuais.mostrarPopupToast(lblMensagemFeedback, texto, ComponentesVisuais.TipoMensagem.SUCESSO);
        } else {
            lblMensagemFeedback.setStyle("-fx-text-fill: #B91C1C !important; -fx-background-color: #FEE2E2; -fx-padding: 8px 12px; -fx-background-radius: 6px; -fx-font-weight: bold;");
            ComponentesVisuais.mostrarPopupToast(lblMensagemFeedback, texto, ComponentesVisuais.TipoMensagem.ERRO);
        }
    }

    private void limparMensagem() {
        lblMensagemFeedback.setText("");
        lblMensagemFeedback.setVisible(false);
    }
}
