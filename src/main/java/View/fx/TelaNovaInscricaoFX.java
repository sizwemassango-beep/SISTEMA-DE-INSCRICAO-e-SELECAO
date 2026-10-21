package View.fx;

import controller.InscricaoController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import model.Candidato;
import model.Curso;
import model.QuestionarioCarencia;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**

 * Representa a interface visual relacionada a TelaNovaInscricaoFX.

 */

public class TelaNovaInscricaoFX extends BorderPane {

    private final InscricaoController controller;

    private TextField txtNome;
    private TextField txtBI;
    private DatePicker dpNascimento;
    private TextField txtTelefone;
    private ComboBox<String> cbProvincia;
    private TextField txtDistrito;
    private TextField txtMorada;

    private TextField txtEmail;
    private PasswordField txtSenha;
    private PasswordField txtConfirmarSenha;
    private TextField txtSenhaVisivel;
    private TextField txtConfirmarSenhaVisivel;

    private ComboBox<Curso> cbCurso;

    private ComboBox<QuestionarioCarencia.FaixaRendimento> cbRendimento;
    private ComboBox<QuestionarioCarencia.SituacaoLaboral> cbLaboral;
    private TextField txtDistancia;
    private CheckBox chkOrfao;
    private CheckBox chkApoio;

    private Label lblMensagem;

    public TelaNovaInscricaoFX() {
        this.controller = new InscricaoController();
        construirInterface();
        configurarAtalhos();
    }

    private void construirInterface() {
        setTop(criarCabecalho());

        ScrollPane scroll = new ScrollPane(criarFormulario());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        setCenter(scroll);

        setBottom(criarBarraBotoes());
    }

    public void alternarVisibilidadeSenhas() {
        boolean mostrar = (txtSenhaVisivel != null && !txtSenhaVisivel.isVisible());
        if (txtSenha != null && txtSenhaVisivel != null) {
            txtSenha.setVisible(!mostrar);
            txtSenha.setManaged(!mostrar);
            txtSenhaVisivel.setVisible(mostrar);
            txtSenhaVisivel.setManaged(mostrar);
        }
        if (txtConfirmarSenha != null && txtConfirmarSenhaVisivel != null) {
            txtConfirmarSenha.setVisible(!mostrar);
            txtConfirmarSenha.setManaged(!mostrar);
            txtConfirmarSenhaVisivel.setVisible(mostrar);
            txtConfirmarSenhaVisivel.setManaged(mostrar);
        }
        ComponentesVisuais.mostrarPopupToastGlobal(
                mostrar ? "Campos de senha visíveis (Ctrl+O)" : "Campos de senha ocultos (Ctrl+O)",
                ComponentesVisuais.TipoMensagem.INFO
        );
    }

    private void configurarAtalhos() {
        this.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, ev -> {
            if (ev.isControlDown() && (ev.getCode() == KeyCode.ENTER || ev.getCode() == KeyCode.S)) {
                processarSubmissao();
                ev.consume();
            } else if (ev.isControlDown() && ev.getCode() == KeyCode.O) {
                alternarVisibilidadeSenhas();
                ev.consume();
            } else if (ev.isControlDown() && ev.getCode() == KeyCode.P) {
                if (txtNome != null) txtNome.requestFocus();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F1) {
                mostrarAjudaAtalhos();
                ev.consume();
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                Navegador.irParaTelaInicial();
                ev.consume();
            } else if (ev.isAltDown()) {
                if (ev.getCode() == KeyCode.N && txtNome != null) { txtNome.requestFocus(); ev.consume(); }
                else if (ev.getCode() == KeyCode.B && txtBI != null) { txtBI.requestFocus(); ev.consume(); }
                else if (ev.getCode() == KeyCode.C && txtTelefone != null) { txtTelefone.requestFocus(); ev.consume(); }
                else if (ev.getCode() == KeyCode.E && txtEmail != null) { txtEmail.requestFocus(); ev.consume(); }
            }
        });

        sceneProperty().addListener((obs, old, scene) -> {
            if (scene == null) return;
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN), this::processarSubmissao);
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), this::processarSubmissao);
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), this::alternarVisibilidadeSenhas);
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), () -> { if (txtNome != null) txtNome.requestFocus(); });
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), Navegador::irParaTelaInicial);
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.F1), this::mostrarAjudaAtalhos);
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.ALT_DOWN), () -> { if (txtNome != null) txtNome.requestFocus(); });
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.B, KeyCombination.ALT_DOWN), () -> { if (txtBI != null) txtBI.requestFocus(); });
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN), () -> { if (txtTelefone != null) txtTelefone.requestFocus(); });
            scene.getAccelerators().put(new KeyCodeCombination(KeyCode.E, KeyCombination.ALT_DOWN), () -> { if (txtEmail != null) txtEmail.requestFocus(); });
        });
    }

    private HBox criarCabecalho() {
        HBox cabecalho = new HBox(16);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(16, 36, 16, 36));
        cabecalho.setStyle("-fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-width: 0 0 1 0;");

        StackPane logoMini = ComponentesVisuais.criarLogoCompacto(42);

        VBox titulos = new VBox(2);
        Label lblTitulo = new Label("Formulário de Inscrição • Ocean S.A.");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Preencha todos os campos obrigatórios (*) para concorrer a uma vaga.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");

        titulos.getChildren().addAll(lblTitulo, lblSub);

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        Button btnTema = new Button(Navegador.isModoEscuro() ? "☀ Claro" : "🌙 Escuro");
        btnTema.getStyleClass().add("button-secondary");
        btnTema.setOnAction(e -> {
            Navegador.alternarTema();
            btnTema.setText(Navegador.isModoEscuro() ? "☀ Claro" : "🌙 Escuro");
        });

        cabecalho.getChildren().addAll(logoMini, titulos, esp, btnTema);
        return cabecalho;
    }

    private VBox criarFormulario() {
        VBox form = new VBox(24);
        form.setPadding(new Insets(32, 48, 32, 48));
        form.setMaxWidth(900);
        form.setAlignment(Pos.TOP_CENTER);

        lblMensagem = new Label();
        lblMensagem.setVisible(false);
        lblMensagem.setWrapText(true);
        lblMensagem.setMaxWidth(Double.MAX_VALUE);

        VBox cardDados = criarCardDadosPessoais();
        VBox cardConta = criarCardConta();
        VBox cardCurso = criarCardCurso();
        VBox cardSocio = criarCardSocioeconomico();

        form.getChildren().addAll(lblMensagem, cardDados, cardConta, cardCurso, cardSocio);
        return form;
    }

    private VBox criarCardDadosPessoais() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        HBox tituloBox = new HBox(8);
        tituloBox.setAlignment(Pos.CENTER_LEFT);
        Label lblSecao = new Label("1. Dados Pessoais de Identificação");
        lblSecao.getStyleClass().add("card-title");
        Button btnAjuda = criarBotaoAjuda(
                "Dados Pessoais",
                "Informe os seus dados exactamente como constam no Bilhete de Identidade (BI).\n\n"
                + "• Nome Completo: obrigatório, mínimo 2 palavras.\n"
                + "• Número do BI: máx. 13 caracteres, termina em letra (ex: 110100234567A).\n"
                + "• Data de Nascimento: candidatos com menos de 14 anos não são aceites.\n"
                + "• Telefone: 9 dígitos, começa por 82, 83, 84, 85, 86 ou 87."
        );
        tituloBox.getChildren().addAll(lblSecao, btnAjuda);

        GridPane gridDados = new GridPane();
        gridDados.setHgap(16);
        gridDados.setVgap(14);

        txtNome = new TextField();
        txtNome.setPromptText("Nome completo conforme o BI");
        txtNome.setId("txtNome");
        adicionarCampoGrid(gridDados, "Nome Completo *", txtNome, 0, 0, 2);
        txtNome.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                boolean ok = InscricaoController.isNomeValido(txtNome.getText());
                InscricaoController.marcarValidadeCampo(txtNome, ok);
                if (!ok && txtNome.getText() != null && !txtNome.getText().trim().isEmpty()) {
                    ComponentesVisuais.mostrarPopupToast(txtNome, "Nome inválido: deve conter nome e apelido completos.", ComponentesVisuais.TipoMensagem.AVISO);
                }
            }
        });

        txtBI = new TextField();
        txtBI.setPromptText("Ex: 110100234567A");
        InscricaoController.aplicarFiltroBI(txtBI, 13);
        adicionarCampoGrid(gridDados, "Número do BI *", txtBI, 0, 1, 1);
        txtBI.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                boolean ok = InscricaoController.isBiValido(txtBI.getText());
                InscricaoController.marcarValidadeCampo(txtBI, ok);
                if (!ok && txtBI.getText() != null && !txtBI.getText().trim().isEmpty()) {
                    ComponentesVisuais.mostrarPopupToast(txtBI, "BI inválido: deve conter de 9 a 14 caracteres alfanuméricos.", ComponentesVisuais.TipoMensagem.AVISO);
                }
            }
        });

        dpNascimento = new DatePicker();
        dpNascimento.setPromptText("Selecione a data");
        dpNascimento.setMaxWidth(Double.MAX_VALUE);
        aplicarEstiloDatePicker(dpNascimento);
        adicionarCampoGrid(gridDados, "Data de Nascimento *", dpNascimento, 1, 1, 1);
        dpNascimento.valueProperty().addListener((obs, old, data) -> {
            if (data != null && !InscricaoController.isDataNascimentoValida(data)) {
                ComponentesVisuais.mostrarPopupToast(dpNascimento, "Data inválida: o candidato deve ter entre 14 e 100 anos.", ComponentesVisuais.TipoMensagem.AVISO);
            }
        });

        txtTelefone = new TextField();
        txtTelefone.setPromptText("Ex: 841234567 (9 dígitos)");
        txtTelefone.setId("txtTelefone");
        InscricaoController.aplicarFiltroCelular(txtTelefone, 9);
        adicionarCampoGrid(gridDados, "Telefone / Celular (9 dígitos) *", txtTelefone, 0, 2, 1);
        txtTelefone.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                boolean ok = InscricaoController.isTelefoneValido(txtTelefone.getText());
                InscricaoController.marcarValidadeCampo(txtTelefone, ok);
                if (!ok && txtTelefone.getText() != null && !txtTelefone.getText().trim().isEmpty()) {
                    ComponentesVisuais.mostrarPopupToast(txtTelefone, "Celular inválido: deve conter exatamente 9 dígitos.", ComponentesVisuais.TipoMensagem.AVISO);
                }
            }
        });

        cbProvincia = new ComboBox<>(FXCollections.observableArrayList(
                "Maputo Cidade", "Maputo Província", "Gaza", "Inhambane", "Sofala",
                "Manica", "Tete", "Zambézia", "Nampula", "Cabo Delgado", "Niassa"
        ));
        cbProvincia.setPromptText("Selecione a Província");
        cbProvincia.setMaxWidth(Double.MAX_VALUE);
        aplicarEstiloComboBox(cbProvincia);
        adicionarCampoGrid(gridDados, "Província de Residência *", cbProvincia, 1, 2, 1);

        txtDistrito = new TextField();
        txtDistrito.setPromptText("Ex: KaMpfumo / Chókwè");
        adicionarCampoGrid(gridDados, "Distrito", txtDistrito, 0, 3, 1);

        txtMorada = new TextField();
        txtMorada.setPromptText("Bairro, Avenida, Rua, Número");
        adicionarCampoGrid(gridDados, "Morada / Endereço", txtMorada, 1, 3, 1);

        card.getChildren().addAll(tituloBox, new Separator(), gridDados);
        return card;
    }

    private VBox criarCardConta() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        HBox tituloBox = new HBox(8);
        tituloBox.setAlignment(Pos.CENTER_LEFT);
        Label lblSecao = new Label("2. Conta de Acesso ao Portal");
        lblSecao.getStyleClass().add("card-title");
        Button btnAjuda = criarBotaoAjuda(
                "Conta de Acesso",
                "Crie as credenciais que usará para entrar no portal e acompanhar a sua candidatura.\n\n"
                + "• Email: endereço válido, será o seu login.\n"
                + "• Senha: mínimo 6 caracteres. Clique no olho 👁 para ver a senha digitada.\n"
                + "• Confirmar Senha: repita a senha para evitar erros de digitação."
        );
        tituloBox.getChildren().addAll(lblSecao, btnAjuda);

        GridPane gridConta = new GridPane();
        gridConta.setHgap(16);
        gridConta.setVgap(14);

        txtEmail = new TextField();
        txtEmail.setPromptText("seuemail@exemplo.com");
        txtEmail.setId("txtEmail");
        adicionarCampoGrid(gridConta, "Email de Registo *", txtEmail, 0, 0, 2);
        txtEmail.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                boolean ok = InscricaoController.isEmailValido(txtEmail.getText());
                InscricaoController.marcarValidadeCampo(txtEmail, ok);
                if (!ok && txtEmail.getText() != null && !txtEmail.getText().trim().isEmpty()) {
                    ComponentesVisuais.mostrarPopupToast(txtEmail, "Email inválido: insira um formato válido (ex: seuemail@dominio.com).", ComponentesVisuais.TipoMensagem.AVISO);
                }
            }
        });

        HBox boxSenha = criarCampoSenhaComVisibilidade("Mínimo de 6 caracteres");
        txtSenha = (PasswordField) boxSenha.lookup("#pf");
        txtSenhaVisivel = (TextField) boxSenha.lookup("#tf");
        txtSenha.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                String val = obterSenhaAtual(txtSenha, txtSenhaVisivel);
                if (val != null && !val.isEmpty() && !InscricaoController.isSenhaValida(val)) {
                    ComponentesVisuais.mostrarPopupToast(boxSenha, "Senha muito curta: deve ter no mínimo 6 caracteres.", ComponentesVisuais.TipoMensagem.AVISO);
                }
            }
        });
        txtSenhaVisivel.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                String val = obterSenhaAtual(txtSenha, txtSenhaVisivel);
                if (val != null && !val.isEmpty() && !InscricaoController.isSenhaValida(val)) {
                    ComponentesVisuais.mostrarPopupToast(boxSenha, "Senha muito curta: deve ter no mínimo 6 caracteres.", ComponentesVisuais.TipoMensagem.AVISO);
                }
            }
        });
        adicionarHBoxGrid(gridConta, "Senha de Acesso *", boxSenha, 0, 1, 1);

        HBox boxConfirmar = criarCampoSenhaComVisibilidade("Repita a sua senha");
        txtConfirmarSenha = (PasswordField) boxConfirmar.lookup("#pf");
        txtConfirmarSenhaVisivel = (TextField) boxConfirmar.lookup("#tf");
        txtConfirmarSenha.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                String s1 = obterSenhaAtual(txtSenha, txtSenhaVisivel);
                String s2 = obterSenhaAtual(txtConfirmarSenha, txtConfirmarSenhaVisivel);
                if (s2 != null && !s2.isEmpty() && !s2.equals(s1)) {
                    ComponentesVisuais.mostrarPopupToast(boxConfirmar, "A confirmação de senha não coincide com a senha digitada.", ComponentesVisuais.TipoMensagem.ERRO);
                }
            }
        });
        txtConfirmarSenhaVisivel.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                String s1 = obterSenhaAtual(txtSenha, txtSenhaVisivel);
                String s2 = obterSenhaAtual(txtConfirmarSenha, txtConfirmarSenhaVisivel);
                if (s2 != null && !s2.isEmpty() && !s2.equals(s1)) {
                    ComponentesVisuais.mostrarPopupToast(boxConfirmar, "A confirmação de senha não coincide com a senha digitada.", ComponentesVisuais.TipoMensagem.ERRO);
                }
            }
        });
        adicionarHBoxGrid(gridConta, "Confirmar Senha *", boxConfirmar, 1, 1, 1);

        card.getChildren().addAll(tituloBox, new Separator(), gridConta);
        return card;
    }

    private VBox criarCardCurso() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        HBox tituloBox = new HBox(8);
        tituloBox.setAlignment(Pos.CENTER_LEFT);
        Label lblSecao = new Label("3. Opção de Curso Preparatório");
        lblSecao.getStyleClass().add("card-title");
        Button btnAjuda = criarBotaoAjuda(
                "Curso Pretendido",
                "Seleccione o curso preparatório a que deseja candidatar-se.\n\n"
                + "• A escolha é vinculativa para este processo.\n"
                + "• O número entre parênteses indica as vagas disponíveis.\n"
                + "• Apenas pode candidatar-se a um curso por processo seletivo."
        );
        tituloBox.getChildren().addAll(lblSecao, btnAjuda);

        List<Curso> cursosBD = controller.listarCursosDisponiveis();
        cbCurso = new ComboBox<>(FXCollections.observableArrayList(cursosBD));
        cbCurso.setMaxWidth(Double.MAX_VALUE);
        cbCurso.setPromptText(cursosBD.isEmpty() ? "Nenhum curso registado no momento" : "Selecione o curso pretendido");
        cbCurso.setConverter(new StringConverter<>() {
            @Override
            public String toString(Curso c) {
                if (c == null) return "";
                return c.getNome() + " (" + c.getNumeroVagas() + " vagas)";
            }

            @Override
            public Curso fromString(String string) {
                return null;
            }
        });
        aplicarEstiloComboBox(cbCurso);

        VBox boxCurso = new VBox(6);
        Label lblCurso = new Label("Curso Desejado *");
        lblCurso.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-primary;");
        boxCurso.getChildren().addAll(lblCurso, cbCurso);

        card.getChildren().addAll(tituloBox, new Separator(), boxCurso);
        return card;
    }

    private VBox criarCardSocioeconomico() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        HBox tituloBox = new HBox(8);
        tituloBox.setAlignment(Pos.CENTER_LEFT);
        Label lblSecao = new Label("4. Questionário Socioeconómico (Critério de Carência)");
        lblSecao.getStyleClass().add("card-title");
        Button btnAjudaGeral = criarBotaoAjuda(
                "Questionário de Carência Social",
                "Este questionário determina a sua pontuação no critério de carência socioeconómica.\n\n"
                + "A pontuação máxima é 100 pontos e influencia directamente a sua classificação "
                + "para vagas reservadas (regime de bolsa social).\n\n"
                + "Candidatos com 70+ pontos: Carência ALTA → Elegíveis para vagas reservadas.\n"
                + "40 a 69 pontos: Carência MÉDIA → Elegíveis para vagas reservadas.\n"
                + "Abaixo de 40: Carência BAIXA → Concorrem em ampla concorrência.\n\n"
                + "Responda com honestidade: dados falsos implicam desqualificação."
        );
        tituloBox.getChildren().addAll(lblSecao, btnAjudaGeral);

        GridPane gridSocio = new GridPane();
        gridSocio.setHgap(16);
        gridSocio.setVgap(14);

        HBox boxRendimento = new HBox(6);
        boxRendimento.setAlignment(Pos.CENTER_LEFT);
        cbRendimento = new ComboBox<>(FXCollections.observableArrayList(QuestionarioCarencia.FaixaRendimento.values()));
        cbRendimento.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cbRendimento, Priority.ALWAYS);
        cbRendimento.setValue(QuestionarioCarencia.FaixaRendimento.ATE_5000);
        aplicarEstiloComboBox(cbRendimento);
        cbRendimento.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(cbRendimento, "Rendimento: Menor rendimento gera até 40 pontos na candidatura para vagas reservadas.", ComponentesVisuais.TipoMensagem.INFO, true);
        });
        Button btnAjudaRendimento = criarBotaoAjuda(
                "Rendimento Familiar Mensal",
                "Informe o rendimento mensal total do seu agregado familiar (todos que vivem na mesma casa).\n\n"
                + "• Até 5.000 MT → 40 pontos\n"
                + "• 5.001 a 10.000 MT → 32 pontos\n"
                + "• 10.001 a 20.000 MT → 22 pontos\n"
                + "• 20.001 a 40.000 MT → 10 pontos\n"
                + "• Acima de 40.000 MT → 0 pontos"
        );
        boxRendimento.getChildren().addAll(cbRendimento, btnAjudaRendimento);
        adicionarHBoxGrid(gridSocio, "Rendimento Familiar Mensal", boxRendimento, 0, 0, 1);

        HBox boxLaboral = new HBox(6);
        boxLaboral.setAlignment(Pos.CENTER_LEFT);
        cbLaboral = new ComboBox<>(FXCollections.observableArrayList(QuestionarioCarencia.SituacaoLaboral.values()));
        cbLaboral.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cbLaboral, Priority.ALWAYS);
        cbLaboral.setValue(QuestionarioCarencia.SituacaoLaboral.DESEMPREGADO);
        aplicarEstiloComboBox(cbLaboral);
        cbLaboral.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(cbLaboral, "Situação Laboral: Desemprego ou estudo concede até 20 pontos de prioridade na classificação.", ComponentesVisuais.TipoMensagem.INFO, true);
        });
        Button btnAjudaLaboral = criarBotaoAjuda(
                "Situação Laboral",
                "Informe a situação profissional actual do candidato (não do agregado familiar).\n\n"
                + "• Desempregado → 20 pontos\n"
                + "• Trabalho informal → 14 pontos\n"
                + "• Trabalho temporário → 10 pontos\n"
                + "• Empregado → 3 pontos\n"
                + "• Estudante sem rendimento → 15 pontos"
        );
        boxLaboral.getChildren().addAll(cbLaboral, btnAjudaLaboral);
        adicionarHBoxGrid(gridSocio, "Situação Laboral", boxLaboral, 1, 0, 1);

        HBox boxDistancia = new HBox(6);
        boxDistancia.setAlignment(Pos.CENTER_LEFT);
        txtDistancia = new TextField("5.0");
        txtDistancia.setPromptText("Distância em quilómetros");
        HBox.setHgrow(txtDistancia, Priority.ALWAYS);
        InscricaoController.aplicarFiltroNumericoDecimal(txtDistancia, 2000.0);
        txtDistancia.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(txtDistancia, "Distância: Candidatos que residem a mais de 100 km recebem até 15 pontos adicionais.", ComponentesVisuais.TipoMensagem.INFO, true);
        });
        Button btnAjudaDistancia = criarBotaoAjuda(
                "Distância até à Instituição",
                "Informe a distância em quilómetros entre a sua residência e a instituição.\n\n"
                + "• ≥ 100 km → 15 pontos\n"
                + "• 50 a 99 km → 11 pontos\n"
                + "• 20 a 49 km → 7 pontos\n"
                + "• 5 a 19 km → 3 pontos\n"
                + "• Menos de 5 km → 0 pontos"
        );
        boxDistancia.getChildren().addAll(txtDistancia, btnAjudaDistancia);
        adicionarHBoxGrid(gridSocio, "Distância até à Instituição (km)", boxDistancia, 0, 1, 1);

        VBox boxChecks = new VBox(10);
        boxChecks.setPadding(new Insets(20, 0, 0, 0));
        chkOrfao = new CheckBox("Sou órfão ou chefe de família  (+15 pontos)");
        chkOrfao.setStyle("-fx-text-fill: -fx-text-primary;");
        chkOrfao.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(chkOrfao, "Órfão / Chefe de Família: Bonificação de 15 pontos adicionais no critério social.", ComponentesVisuais.TipoMensagem.INFO, true);
        });
        Tooltip ttOrfao = new Tooltip(
                "Marque se não tem pais vivos ou se é a única pessoa que provê renda ao agregado familiar.\n"
                + "Adiciona 15 pontos à sua pontuação de carência."
        );
        ttOrfao.setWrapText(true);
        ttOrfao.setMaxWidth(350);
        Tooltip.install(chkOrfao, ttOrfao);

        chkApoio = new CheckBox("Recebo apoio financeiro / bolsa social");
        chkApoio.setStyle("-fx-text-fill: -fx-text-primary;");
        chkApoio.setOnMouseEntered(e -> {
            ComponentesVisuais.mostrarPopupToast(chkApoio, "Apoio Financeiro: Quem NÃO possui auxílio financeiro recebe 10 pontos de carência.", ComponentesVisuais.TipoMensagem.INFO, true);
        });
        Tooltip ttApoio = new Tooltip(
                "Marque se já recebe alguma forma de apoio financeiro governamental ou bolsa social.\n"
                + "Atenção: receber apoio REDUZ a pontuação de carência (0 pontos extras).\n"
                + "Não receber apoio adiciona 10 pontos."
        );
        ttApoio.setWrapText(true);
        ttApoio.setMaxWidth(350);
        Tooltip.install(chkApoio, ttApoio);

        boxChecks.getChildren().addAll(chkOrfao, chkApoio);
        gridSocio.add(boxChecks, 1, 1);

        card.getChildren().addAll(tituloBox, new Separator(), gridSocio);
        return card;
    }

    private HBox criarCampoSenhaComVisibilidade(String placeholder) {
        PasswordField pf = new PasswordField();
        pf.setId("pf");
        pf.setPromptText(placeholder);
        pf.setMaxWidth(Double.MAX_VALUE);

        TextField tf = new TextField();
        tf.setId("tf");
        tf.setPromptText(placeholder);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setVisible(false);
        tf.setManaged(false);

        pf.textProperty().bindBidirectional(tf.textProperty());

        Button btnVer = new Button("👁");
        btnVer.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 6px 8px;");
        btnVer.setTooltip(new Tooltip("Mostrar / Ocultar senha"));
        btnVer.setOnAction(e -> {
            boolean mostrar = !tf.isVisible();
            tf.setVisible(mostrar);
            tf.setManaged(mostrar);
            pf.setVisible(!mostrar);
            pf.setManaged(!mostrar);
        });

        HBox box = new HBox(4, pf, tf, btnVer);
        HBox.setHgrow(pf, Priority.ALWAYS);
        HBox.setHgrow(tf, Priority.ALWAYS);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Button criarBotaoAjuda(String titulo, String mensagem) {
        Button btn = new Button("?");
        btn.setStyle(
                "-fx-background-color: #2563EB; -fx-text-fill: #FFFFFF; -fx-font-size: 11px; "
                + "-fx-font-weight: bold; -fx-background-radius: 50%; -fx-min-width: 20px; "
                + "-fx-min-height: 20px; -fx-max-width: 20px; -fx-max-height: 20px; -fx-cursor: hand;"
        );
        btn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajuda — " + titulo);
            alert.setHeaderText(titulo);
            alert.setContentText(mensagem);
            alert.showAndWait();
        });
        return btn;
    }

    private void aplicarEstiloComboBox(ComboBox<?> cb) {
        cb.setStyle(
                "-fx-background-color: -fx-surface-color; "
                + "-fx-border-color: -fx-border-color-default; "
                + "-fx-border-radius: 8px; -fx-background-radius: 8px; "
                + "-fx-text-fill: -fx-text-primary;"
        );
        cb.getStyleClass().add("combo-box-tema");
    }

    private void aplicarEstiloDatePicker(DatePicker dp) {
        dp.setStyle(
                "-fx-background-color: -fx-surface-color; "
                + "-fx-border-color: -fx-border-color-default; "
                + "-fx-border-radius: 8px; -fx-background-radius: 8px;"
        );
    }

    private void adicionarCampoGrid(GridPane grid, String rotulo, javafx.scene.control.Control campo, int col, int row, int colSpan) {
        VBox box = new VBox(6);
        Label lbl = new Label(rotulo);
        lbl.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-primary;");
        box.getChildren().addAll(lbl, campo);
        grid.add(box, col, row, colSpan, 1);
        GridPane.setHgrow(box, Priority.ALWAYS);
    }

    private void adicionarHBoxGrid(GridPane grid, String rotulo, HBox campo, int col, int row, int colSpan) {
        VBox box = new VBox(6);
        Label lbl = new Label(rotulo);
        lbl.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-primary;");
        box.getChildren().addAll(lbl, campo);
        grid.add(box, col, row, colSpan, 1);
        GridPane.setHgrow(box, Priority.ALWAYS);
    }

    private HBox criarBarraBotoes() {
        HBox barra = new HBox(16);
        barra.setAlignment(Pos.CENTER_RIGHT);
        barra.setPadding(new Insets(16, 36, 16, 36));
        barra.setStyle("-fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-width: 1 0 0 0;");

        Label lblAtalhos = new Label("Ctrl+Enter: Submeter  |  Ctrl+O: Ver Senhas  |  Ctrl+P: Início  |  F1: Ajuda  |  Esc: Cancelar");
        lblAtalhos.setStyle("-fx-font-size: 11px; -fx-text-fill: -fx-text-secondary;");
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        Button btnVoltar = new Button("Cancelar e Voltar");
        btnVoltar.getStyleClass().add("button-secondary");
        btnVoltar.setOnAction(e -> Navegador.irParaTelaInicial());

        Button btnSubmeter = new Button("Submeter Inscrição →");
        btnSubmeter.getStyleClass().add("button-primary");
        btnSubmeter.setOnAction(e -> processarSubmissao());

        barra.getChildren().addAll(lblAtalhos, esp, btnVoltar, btnSubmeter);
        return barra;
    }

    private void processarSubmissao() {
        try {
            String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
            String bi = txtBI.getText() != null ? txtBI.getText().trim() : "";
            LocalDate dataNasc = dpNascimento.getValue();
            String telefone = txtTelefone.getText() != null ? txtTelefone.getText().trim() : "";
            String provincia = cbProvincia.getValue();
            String distrito = txtDistrito.getText() != null ? txtDistrito.getText().trim() : "";
            String morada = txtMorada.getText() != null ? txtMorada.getText().trim() : "";

            String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
            String senha = obterSenhaAtual(txtSenha, txtSenhaVisivel);
            String confirmarSenha = obterSenhaAtual(txtConfirmarSenha, txtConfirmarSenhaVisivel);

            boolean valido = true;

            if (!InscricaoController.isNomeValido(nome)) {
                InscricaoController.marcarValidadeCampo(txtNome, false);
                valido = false;
            } else {
                InscricaoController.marcarValidadeCampo(txtNome, true);
            }

            if (!InscricaoController.isBiValido(bi)) {
                InscricaoController.marcarValidadeCampo(txtBI, false);
                valido = false;
            } else {
                InscricaoController.marcarValidadeCampo(txtBI, true);
            }

            if (dataNasc == null) {
                mostrarAviso("A data de nascimento é obrigatória.");
                return;
            }
            if (!InscricaoController.isDataNascimentoValida(dataNasc)) {
                mostrarAviso("Data de nascimento inválida. O candidato deve ter entre 14 e 100 anos.");
                return;
            }

            if (!InscricaoController.isTelefoneValido(telefone)) {
                InscricaoController.marcarValidadeCampo(txtTelefone, false);
                valido = false;
            } else {
                InscricaoController.marcarValidadeCampo(txtTelefone, true);
            }

            if (provincia == null || provincia.isEmpty()) {
                mostrarAviso("Selecione a província de residência.");
                return;
            }

            if (!InscricaoController.isEmailValido(email)) {
                InscricaoController.marcarValidadeCampo(txtEmail, false);
                valido = false;
            } else {
                InscricaoController.marcarValidadeCampo(txtEmail, true);
            }

            if (!InscricaoController.isSenhaValida(senha)) {
                mostrarAviso("A senha deve ter pelo menos 6 caracteres.");
                return;
            }

            if (!senha.equals(confirmarSenha)) {
                mostrarAviso("A senha e a confirmação de senha não coincidem.");
                return;
            }

            if (!valido) {
                mostrarAviso("Corrija os campos assinalados a vermelho antes de continuar.");
                return;
            }

            double distancia = 0.0;
            try {
                if (txtDistancia.getText() != null && !txtDistancia.getText().trim().isEmpty()) {
                    distancia = Double.parseDouble(txtDistancia.getText().trim());
                }
            } catch (NumberFormatException ignored) {}

            Candidato candidato = new Candidato(nome, email, senha);
            candidato.setNumeroBI(bi);
            candidato.setDataDeNascimento(dataNasc);
            candidato.setTelefone(telefone);
            candidato.setProvincia(provincia);
            candidato.setDistrito(distrito);
            candidato.setMorada(morada);
            candidato.setDataInscricao(LocalDate.now());

            QuestionarioCarencia questionario = new QuestionarioCarencia(
                    cbRendimento.getValue() != null ? cbRendimento.getValue() : QuestionarioCarencia.FaixaRendimento.ATE_5000,
                    cbLaboral.getValue() != null ? cbLaboral.getValue() : QuestionarioCarencia.SituacaoLaboral.DESEMPREGADO,
                    distancia,
                    chkOrfao.isSelected(),
                    chkApoio.isSelected()
            );

            Curso cursoSelecionado = cbCurso.getValue();
            controller.submeterInscricao(candidato, questionario, cursoSelecionado);

            exibirModalSucesso(candidato);

        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAviso("Erro ao submeter inscrição: " + ex.getMessage());
        }
    }

    private String obterSenhaAtual(PasswordField pf, TextField tf) {
        if (tf != null && tf.isVisible()) {
            return tf.getText();
        }
        return pf != null ? pf.getText() : "";
    }

    private void exibirModalSucesso(Candidato candidato) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Inscrição Concluída com Sucesso");
        alert.setHeaderText("Candidatura Submetida com Sucesso!");
        alert.setContentText(
                "O candidato foi registado com sucesso no sistema.\n"
                + "Pode agora aceder ao portal para submeter os documentos necessários.\n\n"
                + "Deseja gerar e exportar o Comprovativo Oficial em PDF agora?"
        );

        ButtonType btnPdf = new ButtonType("📄 Gerar PDF Agora");
        ButtonType btnPortal = new ButtonType("Ir ao Portal do Candidato", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnPdf, btnPortal);

        alert.showAndWait().ifPresent(resposta -> {
            if (resposta == btnPdf) {
                try {
                    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
                    fileChooser.setTitle("Salvar Comprovativo de Inscrição");
                    fileChooser.setInitialFileName("Comprovativo_" + (candidato.getNumeroBI() != null ? candidato.getNumeroBI() : "Inscricao") + ".pdf");
                    fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Documentos PDF (*.pdf)", "*.pdf"));
                    File destino = fileChooser.showSaveDialog(getScene() != null ? getScene().getWindow() : null);

                    if (destino != null) {
                        controller.gerarComprovativoPdf(candidato, destino);

                        Alert sucessoPdf = new Alert(Alert.AlertType.INFORMATION);
                        sucessoPdf.setTitle("Comprovativo PDF");
                        sucessoPdf.setHeaderText("PDF Criado com Sucesso!");
                        sucessoPdf.setContentText("Arquivo salvo com sucesso em:\n" + destino.getAbsolutePath());
                        sucessoPdf.showAndWait();
                    }
                } catch (Exception e) {
                    Alert erro = new Alert(Alert.AlertType.ERROR);
                    erro.setTitle("Erro");
                    erro.setContentText("Não foi possível gerar o PDF: " + e.getMessage());
                    erro.showAndWait();
                }
            }
            Navegador.irParaDashboardCandidato(candidato);
        });
    }

    private void mostrarAviso(String mensagem) {
        lblMensagem.setText("⚠ " + mensagem);
        lblMensagem.setStyle("-fx-text-fill: #991B1B; -fx-background-color: #FEE2E2; -fx-padding: 10px 14px; -fx-background-radius: 8px; -fx-font-weight: bold;");
        lblMensagem.setVisible(true);
        ComponentesVisuais.mostrarPopupToast(lblMensagem, mensagem, ComponentesVisuais.TipoMensagem.AVISO);
    }

    private void mostrarAjudaAtalhos() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Guia de Atalhos de Teclado — Nova Inscrição");
        alert.setHeaderText("Atalhos de Teclado Disponíveis");
        alert.setContentText(
                "Ctrl + Enter ou Ctrl + S  →  Submeter inscrição\n"
                + "Ctrl + O                 →  Tornar campos de senha visíveis / ocultos\n"
                + "Ctrl + P                 →  Focar campo inicial (Nome)\n"
                + "Alt + N                  →  Focar campo Nome\n"
                + "Alt + B                  →  Focar campo BI\n"
                + "Alt + C                  →  Focar campo Celular/Contacto\n"
                + "Alt + E                  →  Focar campo Email\n"
                + "Ctrl + T ou F2           →  Alternar tema claro/escuro\n"
                + "Esc                      →  Cancelar e voltar\n"
                + "F1                       →  Mostrar este guia\n\n"
                + "Botão ? em cada secção   →  Explicação detalhada dos campos"
        );
        alert.showAndWait();
    }
}
