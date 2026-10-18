package View.fx;

import controller.InscricaoController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import model.Candidato;
import model.Curso;
import model.QuestionarioCarencia;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

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
    }

    private void construirInterface() {
        setTop(criarCabecalho());

        ScrollPane scroll = new ScrollPane(criarFormulario());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        setCenter(scroll);

        setBottom(criarBarraBotoes());
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

        Label lblSub = new Label("Preencha todos os campos obrigatórios para concorrer a uma vaga.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");

        titulos.getChildren().addAll(lblTitulo, lblSub);
        cabecalho.getChildren().addAll(logoMini, titulos);
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

        // Seção 1: Dados Pessoais
        VBox cardDados = new VBox(16);
        cardDados.getStyleClass().add("card");
        Label lblSecao1 = new Label("1. Dados Pessoais de Identificação");
        lblSecao1.getStyleClass().add("card-title");

        GridPane gridDados = new GridPane();
        gridDados.setHgap(16);
        gridDados.setVgap(14);

        txtNome = new TextField();
        txtNome.setPromptText("Nome completo conforme o BI");
        adicionarCampoGrid(gridDados, "Nome Completo *", txtNome, 0, 0, 2);

        txtBI = new TextField();
        txtBI.setPromptText("Ex: 110100234567A");
        controller.aplicarFiltroBI(txtBI, 13);
        adicionarCampoGrid(gridDados, "Número do BI *", txtBI, 0, 1, 1);

        dpNascimento = new DatePicker();
        dpNascimento.setPromptText("Selecione a data");
        dpNascimento.setMaxWidth(Double.MAX_VALUE);
        adicionarCampoGrid(gridDados, "Data de Nascimento *", dpNascimento, 1, 1, 1);

        txtTelefone = new TextField();
        txtTelefone.setPromptText("Ex: 841234567 (9 dígitos)");
        controller.aplicarFiltroCelular(txtTelefone, 9);
        adicionarCampoGrid(gridDados, "Telefone / Celular (9 dígitos) *", txtTelefone, 0, 2, 1);

        cbProvincia = new ComboBox<>(FXCollections.observableArrayList(
                "Maputo Cidade", "Maputo Província", "Gaza", "Inhambane", "Sofala",
                "Manica", "Tete", "Zambézia", "Nampula", "Cabo Delgado", "Niassa"
        ));
        cbProvincia.setPromptText("Selecione a Província");
        cbProvincia.setMaxWidth(Double.MAX_VALUE);
        adicionarCampoGrid(gridDados, "Província de Residência *", cbProvincia, 1, 2, 1);

        txtDistrito = new TextField();
        txtDistrito.setPromptText("Ex: KaMpfumo / Chókwè");
        adicionarCampoGrid(gridDados, "Distrito", txtDistrito, 0, 3, 1);

        txtMorada = new TextField();
        txtMorada.setPromptText("Bairro, Avenida, Rua, Número");
        adicionarCampoGrid(gridDados, "Morada / Endereço", txtMorada, 1, 3, 1);

        cardDados.getChildren().addAll(lblSecao1, new Separator(), gridDados);

        // Seção 2: Conta de Acesso
        VBox cardConta = new VBox(16);
        cardConta.getStyleClass().add("card");
        Label lblSecao2 = new Label("2. Conta de Acesso ao Portal");
        lblSecao2.getStyleClass().add("card-title");

        GridPane gridConta = new GridPane();
        gridConta.setHgap(16);
        gridConta.setVgap(14);

        txtEmail = new TextField();
        txtEmail.setPromptText("seuemail@exemplo.com");
        adicionarCampoGrid(gridConta, "Email de Registo *", txtEmail, 0, 0, 2);

        txtSenha = new PasswordField();
        txtSenha.setPromptText("Mínimo de 6 caracteres");
        adicionarCampoGrid(gridConta, "Senha de Acesso *", txtSenha, 0, 1, 1);

        txtConfirmarSenha = new PasswordField();
        txtConfirmarSenha.setPromptText("Repita a sua senha");
        adicionarCampoGrid(gridConta, "Confirmar Senha *", txtConfirmarSenha, 1, 1, 1);

        cardConta.getChildren().addAll(lblSecao2, new Separator(), gridConta);

        // Seção 3: Curso Pretendido
        VBox cardCurso = new VBox(16);
        cardCurso.getStyleClass().add("card");
        Label lblSecao3 = new Label("3. Opção de Curso Preparatório");
        lblSecao3.getStyleClass().add("card-title");

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

        VBox boxCurso = new VBox(6);
        Label lblCurso = new Label("Curso Desejado *");
        lblCurso.setStyle("-fx-font-weight: 600; -fx-font-size: 12px;");
        boxCurso.getChildren().addAll(lblCurso, cbCurso);

        cardCurso.getChildren().addAll(lblSecao3, new Separator(), boxCurso);

        // Seção 4: Questionário Socioeconómico
        VBox cardSocio = new VBox(16);
        cardSocio.getStyleClass().add("card");
        Label lblSecao4 = new Label("4. Questionário Socioeconómico (Critério de Carência)");
        lblSecao4.getStyleClass().add("card-title");

        GridPane gridSocio = new GridPane();
        gridSocio.setHgap(16);
        gridSocio.setVgap(14);

        cbRendimento = new ComboBox<>(FXCollections.observableArrayList(QuestionarioCarencia.FaixaRendimento.values()));
        cbRendimento.setMaxWidth(Double.MAX_VALUE);
        cbRendimento.setValue(QuestionarioCarencia.FaixaRendimento.ATE_5000);
        adicionarCampoGrid(gridSocio, "Rendimento Familiar Mensal", cbRendimento, 0, 0, 1);

        cbLaboral = new ComboBox<>(FXCollections.observableArrayList(QuestionarioCarencia.SituacaoLaboral.values()));
        cbLaboral.setMaxWidth(Double.MAX_VALUE);
        cbLaboral.setValue(QuestionarioCarencia.SituacaoLaboral.DESEMPREGADO);
        adicionarCampoGrid(gridSocio, "Situação Laboral", cbLaboral, 1, 0, 1);

        txtDistancia = new TextField("5.0");
        txtDistancia.setPromptText("Distância em quilómetros");
        controller.aplicarFiltroNumericoDecimal(txtDistancia, 2000.0);
        adicionarCampoGrid(gridSocio, "Distância até à Instituição (km)", txtDistancia, 0, 1, 1);

        VBox boxChecks = new VBox(10);
        boxChecks.setPadding(new Insets(20, 0, 0, 0));
        chkOrfao = new CheckBox("Sou órfão ou chefe de família");
        chkApoio = new CheckBox("Recebo apoio financeiro / bolsa social");
        boxChecks.getChildren().addAll(chkOrfao, chkApoio);
        gridSocio.add(boxChecks, 1, 1);

        cardSocio.getChildren().addAll(lblSecao4, new Separator(), gridSocio);

        form.getChildren().addAll(lblMensagem, cardDados, cardConta, cardCurso, cardSocio);
        return form;
    }

    private void adicionarCampoGrid(GridPane grid, String rotulo, Control campo, int col, int row, int colSpan) {
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

        Button btnVoltar = new Button("Cancelar e Voltar");
        btnVoltar.getStyleClass().add("button-secondary");
        btnVoltar.setOnAction(e -> Navegador.irParaTelaInicial());

        Button btnSubmeter = new Button("Submeter Inscrição →");
        btnSubmeter.getStyleClass().add("button-primary");
        btnSubmeter.setOnAction(e -> processarSubmissao());

        barra.getChildren().addAll(btnVoltar, btnSubmeter);
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
            String senha = txtSenha.getText();
            String confirmarSenha = txtConfirmarSenha.getText();

            if (nome.isEmpty() || bi.isEmpty() || email.isEmpty() || senha == null || senha.isEmpty()) {
                mostrarAviso("Preencha todos os campos obrigatórios (*).");
                return;
            }

            if (dataNasc == null) {
                mostrarAviso("A data de nascimento é obrigatória.");
                return;
            }

            if (provincia == null || provincia.isEmpty()) {
                mostrarAviso("Selecione a província de residência.");
                return;
            }

            if (!senha.equals(confirmarSenha)) {
                mostrarAviso("A senha e a confirmação de senha não coincidem.");
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

            controller.submeterInscricao(candidato, questionario);

            exibirModalSucesso(candidato);

        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        } catch (Exception ex) {
            mostrarAviso("Erro ao submeter inscrição: " + ex.getMessage());
        }
    }

    private void exibirModalSucesso(Candidato candidato) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Inscrição Concluída com Sucesso");
        alert.setHeaderText("Candidatura Submetida com Sucesso!");
        alert.setContentText("O candidato foi registado com sucesso no sistema.\nDeseja gerar e exportar o Comprovativo Oficial em PDF agora?");

        ButtonType btnPdf = new ButtonType("📄 Gerar PDF Agora");
        ButtonType btnInicio = new ButtonType("Voltar ao Início", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnPdf, btnInicio);

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
    }
}
