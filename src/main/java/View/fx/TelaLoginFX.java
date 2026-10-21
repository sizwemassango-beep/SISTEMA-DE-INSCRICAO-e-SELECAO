package View.fx;

import controller.LoginController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Candidato;
import service.RelatorioPdfService;

import java.io.File;
import java.util.Optional;

/**

 * Representa a interface visual relacionada a TelaLoginFX.

 */

public class TelaLoginFX extends BorderPane {

    private final LoginController loginController;
    private boolean perfilSecretaria;

    private TextField txtEmail;
    private PasswordField txtSenha;
    private Label lblMensagem;
    private ToggleButton btnAbaSecretaria;
    private ToggleButton btnAbaCandidato;

    public TelaLoginFX(boolean perfilSecretariaInicial) {
        this.loginController = new LoginController();
        this.perfilSecretaria = perfilSecretariaInicial;
        construirInterface();
    }

    private void construirInterface() {
        setCenter(criarPainelPrincipal());
    }

    private HBox criarPainelPrincipal() {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        // Painel Lateral Esquerdo (Banner institucional moderno)
        VBox banner = new VBox(20);
        banner.setPrefWidth(420);
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setPadding(new Insets(48));
        banner.setStyle("-fx-background-color: linear-gradient(to bottom right, #1E3A8A, #1D4ED8);");

        Label lblBannerTag = new Label("PORTAL ACADÉMICO");
        lblBannerTag.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #93C5FD; -fx-background-color: rgba(255,255,255,0.1); -fx-padding: 4px 8px; -fx-background-radius: 4px;");

        Label lblBannerTitulo = new Label("Bem-vindo de volta ao SIS");
        lblBannerTitulo.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: white;");
        lblBannerTitulo.setWrapText(true);

        Label lblBannerTexto = new Label("Aceda com as suas credenciais para gerir o processo seletivo ou consultar o estado atual da sua candidatura e notas.");
        lblBannerTexto.setStyle("-fx-font-size: 14px; -fx-text-fill: #BFDBFE;");
        lblBannerTexto.setWrapText(true);

        VBox boxDestaques = new VBox(12);
        boxDestaques.getChildren().addAll(
                criarItemDestaque("✓ Acompanhe o estado da sua candidatura em tempo real"),
                criarItemDestaque("✓ Emissão de comprovativos em PDF com carimbo digital"),
                criarItemDestaque("✓ Segurança e transparência nos critérios de seleção")
        );

        banner.getChildren().addAll(lblBannerTag, lblBannerTitulo, lblBannerTexto, boxDestaques);

        // Painel Direito (Formulário de Acesso)
        VBox formularioBox = new VBox(20);
        formularioBox.setAlignment(Pos.CENTER);
        formularioBox.setPadding(new Insets(48));
        formularioBox.setPrefWidth(480);
        HBox.setHgrow(formularioBox, Priority.ALWAYS);

        VBox formCard = new VBox(18);
        formCard.getStyleClass().add("card");
        formCard.setMaxWidth(420);
        formCard.setAlignment(Pos.TOP_LEFT);

        Label lblFormTitulo = new Label("Iniciar Sessão");
        lblFormTitulo.getStyleClass().add("card-title");
        lblFormTitulo.setStyle("-fx-font-size: 22px;");

        Label lblFormSub = new Label("Selecione o seu perfil e introduza as suas credenciais.");
        lblFormSub.getStyleClass().add("card-subtitle");

        // Alternador de Perfil (Secretaria / Candidato)
        HBox seletorPerfil = new HBox(8);
        ToggleGroup grupoPerfil = new ToggleGroup();

        btnAbaSecretaria = new ToggleButton("Secretaria / Admin");
        btnAbaSecretaria.setToggleGroup(grupoPerfil);
        btnAbaSecretaria.setSelected(perfilSecretaria);
        btnAbaSecretaria.getStyleClass().add("button-secondary");

        btnAbaCandidato = new ToggleButton("Candidato");
        btnAbaCandidato.setToggleGroup(grupoPerfil);
        btnAbaCandidato.setSelected(!perfilSecretaria);
        btnAbaCandidato.getStyleClass().add("button-secondary");

        btnAbaSecretaria.setOnAction(e -> {
            perfilSecretaria = true;
            limparMensagem();
        });

        btnAbaCandidato.setOnAction(e -> {
            perfilSecretaria = false;
            limparMensagem();
        });

        seletorPerfil.getChildren().addAll(btnAbaSecretaria, btnAbaCandidato);

        VBox boxEmail = new VBox(6);
        Label lblEmail = new Label("Email");
        lblEmail.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-primary;");
        txtEmail = new TextField();
        txtEmail.setPromptText("exemplo@dominio.com");
        boxEmail.getChildren().addAll(lblEmail, txtEmail);

        VBox boxSenha = new VBox(6);
        Label lblSenha = new Label("Palavra-passe / Senha");
        lblSenha.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-primary;");

        txtSenha = new PasswordField();
        txtSenha.setPromptText("Introduza a sua senha");
        TextField txtSenhaVisivel = new TextField();
        txtSenhaVisivel.setPromptText("Introduza a sua senha");
        txtSenhaVisivel.setVisible(false);
        txtSenhaVisivel.setManaged(false);
        txtSenha.textProperty().bindBidirectional(txtSenhaVisivel.textProperty());
        Button btnVerSenha = new Button("👁");
        btnVerSenha.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 6px 8px;");
        btnVerSenha.setTooltip(new Tooltip("Mostrar / Ocultar senha"));
        btnVerSenha.setOnAction(ev -> {
            boolean mostrar = !txtSenhaVisivel.isVisible();
            txtSenhaVisivel.setVisible(mostrar);
            txtSenhaVisivel.setManaged(mostrar);
            txtSenha.setVisible(!mostrar);
            txtSenha.setManaged(!mostrar);
        });
        HBox boxCampoSenha = new HBox(4, txtSenha, txtSenhaVisivel, btnVerSenha);
        HBox.setHgrow(txtSenha, Priority.ALWAYS);
        HBox.setHgrow(txtSenhaVisivel, Priority.ALWAYS);
        boxCampoSenha.setAlignment(Pos.CENTER_LEFT);

        txtSenha.setOnAction(e -> executarLogin());
        txtSenhaVisivel.setOnAction(e -> executarLogin());

        boxSenha.getChildren().addAll(lblSenha, boxCampoSenha);

        lblMensagem = new Label();
        lblMensagem.setWrapText(true);
        lblMensagem.setVisible(false);

        Button btnEntrar = new Button("Entrar no Sistema");
        btnEntrar.getStyleClass().add("button-primary");
        btnEntrar.setMaxWidth(Double.MAX_VALUE);
        btnEntrar.setOnAction(e -> executarLogin());

        txtEmail.setOnAction(e -> txtSenha.requestFocus());

        Button btnNovoCadastro = new Button("Não tem conta? Criar Conta de Candidato →");
        btnNovoCadastro.setStyle("-fx-background-color: transparent; -fx-text-fill: -fx-accent-color; -fx-font-weight: 600; -fx-cursor: hand; -fx-font-size: 13px;");
        btnNovoCadastro.setMaxWidth(Double.MAX_VALUE);
        btnNovoCadastro.setOnAction(e -> Navegador.irParaNovaInscricao());

        Button btnVoltar = new Button("← Voltar ao Início");
        btnVoltar.getStyleClass().add("button-secondary");
        btnVoltar.setMaxWidth(Double.MAX_VALUE);
        btnVoltar.setOnAction(e -> Navegador.irParaTelaInicial());

        formCard.getChildren().addAll(lblFormTitulo, lblFormSub, seletorPerfil, boxEmail, boxSenha, lblMensagem, btnEntrar, btnNovoCadastro, btnVoltar);
        formularioBox.getChildren().add(formCard);

        container.getChildren().addAll(banner, formularioBox);
        return container;
    }

    private Label criarItemDestaque(String texto) {
        Label item = new Label(texto);
        item.setStyle("-fx-text-fill: #E0E7FF; -fx-font-size: 13px;");
        return item;
    }

    private void executarLogin() {
        String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
        String senha = txtSenha.getText() != null ? txtSenha.getText() : "";

        if (email.isEmpty() || senha.isEmpty()) {
            mostrarMensagem("Por favor, preencha o email e a senha.", false);
            return;
        }

        if (perfilSecretaria) {
            boolean sucesso = loginController.autenticarSecretaria(email, senha);
            if (sucesso) {
                mostrarMensagem("Login efetuado com sucesso! Redirecionando...", true);
                Navegador.irParaDashboardSecretaria();
            } else {
                mostrarMensagem("Credenciais de secretaria inválidas. Verifique o email e senha.", false);
            }
        } else {
            Optional<Candidato> optCandidato = loginController.autenticarCandidato(email, senha);
            if (optCandidato.isPresent()) {
                Navegador.irParaDashboardCandidato(optCandidato.get());
            } else {
                mostrarMensagem("Candidato não encontrado com este email e senha.", false);
            }
        }
    }

    private void exibirAreaCandidato(Candidato candidato) {
        VBox boxDetalhes = new VBox(20);
        boxDetalhes.setAlignment(Pos.CENTER);
        boxDetalhes.setPadding(new Insets(30));

        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setMaxWidth(550);

        Label lblTitulo = new Label("Painel do Candidato");
        lblTitulo.getStyleClass().add("card-title");
        lblTitulo.setStyle("-fx-font-size: 20px;");

        Label lblSub = new Label("Bem-vindo(a), " + candidato.getNomeCompleto());
        lblSub.getStyleClass().add("card-subtitle");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.add(new Label("Nº de Inscrição:"), 0, 0);
        grid.add(new Label(String.valueOf(candidato.getId())), 1, 0);

        grid.add(new Label("Número de BI:"), 0, 1);
        grid.add(new Label(candidato.getNumeroBI()), 1, 1);

        grid.add(new Label("Email:"), 0, 2);
        grid.add(new Label(candidato.getEmail()), 1, 2);

        grid.add(new Label("Telefone:"), 0, 3);
        grid.add(new Label(candidato.getTelefone() != null ? candidato.getTelefone() : "-"), 1, 3);

        grid.add(new Label("Província:"), 0, 4);
        grid.add(new Label(candidato.getProvincia() != null ? candidato.getProvincia() : "-"), 1, 4);

        Button btnPdf = new Button("📄 Exportar Comprovativo em PDF");
        btnPdf.getStyleClass().add("button-pdf");
        btnPdf.setOnAction(e -> {
            try {
                File arquivo = new File("Comprovativo_" + candidato.getNumeroBI() + ".pdf");
                new RelatorioPdfService().exportarComprovativoInscricao(candidato, arquivo);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Comprovativo Gerado");
                alert.setHeaderText("PDF Exportado com Sucesso!");
                alert.setContentText("Arquivo salvo em:\n" + arquivo.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro ao gerar PDF: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        Button btnSair = new Button("Terminar Sessão");
        btnSair.getStyleClass().add("button-secondary");
        btnSair.setOnAction(e -> Navegador.irParaTelaInicial());

        HBox acoes = new HBox(12, btnPdf, btnSair);

        card.getChildren().addAll(lblTitulo, lblSub, new Separator(), grid, new Separator(), acoes);
        boxDetalhes.getChildren().add(card);

        setCenter(boxDetalhes);
    }

    private void mostrarMensagem(String texto, boolean sucesso) {
        lblMensagem.setText(texto);
        lblMensagem.setVisible(true);
        if (sucesso) {
            lblMensagem.setStyle("-fx-text-fill: #15803D; -fx-font-weight: bold; -fx-background-color: #DCFCE7; -fx-padding: 8px 12px; -fx-background-radius: 6px;");
            ComponentesVisuais.mostrarPopupToast(lblMensagem, texto, ComponentesVisuais.TipoMensagem.SUCESSO);
        } else {
            lblMensagem.setStyle("-fx-text-fill: #B91C1C; -fx-font-weight: bold; -fx-background-color: #FEE2E2; -fx-padding: 8px 12px; -fx-background-radius: 6px;");
            ComponentesVisuais.mostrarPopupToast(lblMensagem, texto, ComponentesVisuais.TipoMensagem.ERRO);
        }
    }

    private void limparMensagem() {
        lblMensagem.setText("");
        lblMensagem.setVisible(false);
    }
}
