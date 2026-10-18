package View.fx;

import controller.InscricaoController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import model.Candidato;
import model.Inscricao;
import model.QuestionarioCarencia;
import model.StatusInscricao;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;

public class TelaDashboardCandidatoFX extends BorderPane {

    private final Candidato candidato;
    private final InscricaoController controller;

    private Inscricao inscricaoAtual;
    private QuestionarioCarencia carenciaAtual;

    private VBox boxStatusContainer;
    private Label lblStatusBadge;
    private Label lblStatusDescricao;
    private VBox boxConteudoDinamico;

    public TelaDashboardCandidatoFX(Candidato candidato) {
        this.candidato = candidato;
        this.controller = new InscricaoController();
        carregarDadosBanco();
        construirInterface();
    }

    private void carregarDadosBanco() {
        if (candidato != null && candidato.getId() != null) {
            this.inscricaoAtual = controller.buscarInscricaoPorCandidato(candidato.getId()).orElse(null);
            this.carenciaAtual = controller.buscarQuestionarioPorCandidato(candidato.getId());
        }
    }

    private void construirInterface() {
        setTop(criarBarraSuperior());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(criarCorpoPrincipal());
        setCenter(scrollPane);
    }

    private HBox criarBarraSuperior() {
        HBox barra = new HBox(16);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(14, 32, 14, 32));
        barra.setStyle("-fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-width: 0 0 1 0;");

        StackPane logoMini = ComponentesVisuais.criarLogoCompacto(40);

        VBox titulos = new VBox(2);
        Label lblInst = new Label("Centro de Formação Ocean S.A.");
        lblInst.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");
        Label lblSis = new Label("Portal do Candidato • Processo Seletivo 2026");
        lblSis.setStyle("-fx-font-size: 11px; -fx-text-fill: -fx-text-secondary;");
        titulos.getChildren().addAll(lblInst, lblSis);

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        Button btnTema = new Button(Navegador.isModoEscuro() ? "☀ Claro" : "🌙 Escuro");
        btnTema.getStyleClass().add("button-secondary");
        btnTema.setOnAction(e -> {
            Navegador.alternarTema();
            btnTema.setText(Navegador.isModoEscuro() ? "☀ Claro" : "🌙 Escuro");
        });

        Button btnSair = new Button("Terminar Sessão");
        btnSair.getStyleClass().add("button-secondary");
        btnSair.setOnAction(e -> Navegador.irParaTelaInicial());

        barra.getChildren().addAll(logoMini, titulos, espacador, btnTema, btnSair);
        return barra;
    }

    private VBox criarCorpoPrincipal() {
        boxConteudoDinamico = new VBox(24);
        boxConteudoDinamico.setPadding(new Insets(32, 48, 48, 48));
        boxConteudoDinamico.setMaxWidth(1100);

        StackPane centralizador = new StackPane(boxConteudoDinamico);
        centralizador.setAlignment(Pos.TOP_CENTER);

        renderizarCards();

        VBox containerGeral = new VBox(centralizador);
        containerGeral.setAlignment(Pos.TOP_CENTER);
        return containerGeral;
    }

    private void renderizarCards() {
        boxConteudoDinamico.getChildren().clear();

        // 1. Cartão de Boas-Vindas e Status Geral
        VBox cardHero = criarCardHeroStatus();

        // 2. Esteira do Processo Seletivo (Progresso Visual)
        VBox cardProgresso = criarCardProgresso();

        // 3. Informações em duas colunas (Dados Pessoais e Carência Socioeconómica)
        HBox boxDuasColunas = new HBox(20);
        boxDuasColunas.setAlignment(Pos.TOP_LEFT);

        VBox cardDadosPessoais = criarCardDadosPessoais();
        HBox.setHgrow(cardDadosPessoais, Priority.ALWAYS);

        VBox cardSocioeconomico = criarCardSocioeconomico();
        HBox.setHgrow(cardSocioeconomico, Priority.ALWAYS);

        boxDuasColunas.getChildren().addAll(cardDadosPessoais, cardSocioeconomico);

        // 4. Cartão de Ações e Exportação em PDF
        VBox cardAcoes = criarCardAcoes();

        boxConteudoDinamico.getChildren().addAll(cardHero, cardProgresso, boxDuasColunas, cardAcoes);
    }

    private VBox criarCardHeroStatus() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setStyle("-fx-background-color: linear-gradient(to right, #0F172A, #1E3A8A); -fx-background-radius: 16px; -fx-padding: 26px;");

        HBox topo = new HBox(16);
        topo.setAlignment(Pos.CENTER_LEFT);

        VBox titulos = new VBox(4);
        Label lblBoasVindas = new Label("Olá, " + (candidato != null ? candidato.getNomeCompleto() : "Candidato") + "!");
        lblBoasVindas.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        Label lblSub = new Label("Acompanhe aqui o estado da sua candidatura, validação de documentos e os seus dados registados.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #93C5FD;");
        titulos.getChildren().addAll(lblBoasVindas, lblSub);

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        lblStatusBadge = new Label();
        atualizarBadgeStatus();

        topo.getChildren().addAll(titulos, espaco, lblStatusBadge);

        lblStatusDescricao = new Label();
        lblStatusDescricao.setWrapText(true);
        lblStatusDescricao.setStyle("-fx-font-size: 13px; -fx-text-fill: #E2E8F0; -fx-background-color: rgba(255, 255, 255, 0.1); -fx-padding: 12px 16px; -fx-background-radius: 8px;");
        atualizarDescricaoStatus();

        card.getChildren().addAll(topo, lblStatusDescricao);
        return card;
    }

    private void atualizarBadgeStatus() {
        StatusInscricao st = inscricaoAtual != null && inscricaoAtual.getStatus() != null
                ? inscricaoAtual.getStatus()
                : StatusInscricao.PENDENTE;

        switch (st) {
            case APROVADO -> {
                lblStatusBadge.setText("✔ APROVADO");
                lblStatusBadge.setStyle("-fx-background-color: #22C55E; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8px 16px; -fx-background-radius: 20px;");
            }
            case LISTA_ESPERA -> {
                lblStatusBadge.setText("📋 LISTA DE ESPERA");
                lblStatusBadge.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8px 16px; -fx-background-radius: 20px;");
            }
            case NAO_APROVADO -> {
                lblStatusBadge.setText("✖ NÃO CLASSIFICADO");
                lblStatusBadge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8px 16px; -fx-background-radius: 20px;");
            }
            default -> {
                lblStatusBadge.setText("⏳ EM ANÁLISE / PENDENTE");
                lblStatusBadge.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8px 16px; -fx-background-radius: 20px;");
            }
        }
    }

    private void atualizarDescricaoStatus() {
        StatusInscricao st = inscricaoAtual != null && inscricaoAtual.getStatus() != null
                ? inscricaoAtual.getStatus()
                : StatusInscricao.PENDENTE;

        switch (st) {
            case APROVADO -> lblStatusDescricao.setText("Parabéns! A sua candidatura foi APROVADA pela comissão de seleção. Descarregue o seu comprovativo oficial abaixo e dirija-se à secretaria académica para efetuar a matrícula.");
            case LISTA_ESPERA -> lblStatusDescricao.setText("A sua candidatura foi avaliada e encontra-se em LISTA DE ESPERA. Em caso de desistências ou novas vagas, a secretaria entrará em contacto pelos meios indicados.");
            case NAO_APROVADO -> lblStatusDescricao.setText("Agradecemos a sua participação. A sua candidatura não foi classificada no número de vagas disponibilizadas para este ciclo.");
            default -> lblStatusDescricao.setText("A sua candidatura foi registada com sucesso na base de dados e está AGUARDANDO HOMOLOGAÇÃO da Secretaria. Fique atento a esta página para novidades.");
        }
    }

    private VBox criarCardProgresso() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        Label lblTit = new Label("Etapas do Processo Seletivo");
        lblTit.getStyleClass().add("card-title");

        HBox esteira = new HBox(12);
        esteira.setAlignment(Pos.CENTER);

        boolean docsValidados = inscricaoAtual != null && inscricaoAtual.isDocumentosValidados();
        StatusInscricao st = inscricaoAtual != null ? inscricaoAtual.getStatus() : StatusInscricao.PENDENTE;

        VBox passo1 = criarItemPasso("1. Inscrição", "Submetida com sucesso", true, "✔");
        Separator sep1 = criarSeparadorPasso();
        VBox passo2 = criarItemPasso("2. Documentos", docsValidados ? "Validados pela Secretaria" : "Em Verificação", docsValidados, docsValidados ? "✔" : "⏳");
        Separator sep2 = criarSeparadorPasso();
        VBox passo3 = criarItemPasso("3. Carência Social", carenciaAtual != null ? carenciaAtual.calcularPontuacao() + " pts (" + carenciaAtual.classificar().getDescricao() + ")" : "Aguardando cálculo", carenciaAtual != null, carenciaAtual != null ? "✔" : "○");
        Separator sep3 = criarSeparadorPasso();
        VBox passo4 = criarItemPasso("4. Homologação", st != StatusInscricao.PENDENTE ? st.getDescricao() : "Em Análise", st != StatusInscricao.PENDENTE, st == StatusInscricao.APROVADO ? "✔" : (st == StatusInscricao.PENDENTE ? "⏳" : "•"));

        HBox.setHgrow(passo1, Priority.ALWAYS);
        HBox.setHgrow(passo2, Priority.ALWAYS);
        HBox.setHgrow(passo3, Priority.ALWAYS);
        HBox.setHgrow(passo4, Priority.ALWAYS);

        esteira.getChildren().addAll(passo1, sep1, passo2, sep2, passo3, sep3, passo4);

        card.getChildren().addAll(lblTit, new Separator(), esteira);
        return card;
    }

    private VBox criarItemPasso(String titulo, String subtitulo, boolean ativo, String icone) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle(ativo
                ? "-fx-background-color: rgba(37, 99, 235, 0.08); -fx-background-radius: 8px; -fx-border-color: #2563EB; -fx-border-radius: 8px;"
                : "-fx-background-color: rgba(148, 163, 184, 0.06); -fx-background-radius: 8px; -fx-border-color: -fx-border-color-default; -fx-border-radius: 8px;");

        Label lblIcone = new Label(icone);
        lblIcone.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + (ativo ? "#2563EB" : "#94A3B8") + ";");

        Label lblT = new Label(titulo);
        lblT.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: -fx-text-primary;");

        Label lblS = new Label(subtitulo);
        lblS.setStyle("-fx-font-size: 11px; -fx-text-fill: -fx-text-secondary; -fx-text-alignment: center;");
        lblS.setWrapText(true);

        box.getChildren().addAll(lblIcone, lblT, lblS);
        return box;
    }

    private Separator criarSeparadorPasso() {
        Separator sep = new Separator();
        sep.setValignment(javafx.geometry.VPos.CENTER);
        sep.setPrefWidth(20);
        return sep;
    }

    private VBox criarCardDadosPessoais() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        Label lblTit = new Label("Dados Pessoais Registados");
        lblTit.getStyleClass().add("card-title");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);

        int linha = 0;
        adicionarLinhaInfo(grid, linha++, "Nome Completo:", candidato != null ? candidato.getNomeCompleto() : "-");
        adicionarLinhaInfo(grid, linha++, "Número de BI:", candidato != null ? candidato.getNumeroBI() : "-");

        String dataNasc = "-";
        if (candidato != null && candidato.getDataDeNascimento() != null) {
            int idade = Period.between(candidato.getDataDeNascimento(), LocalDate.now()).getYears();
            dataNasc = candidato.getDataDeNascimento() + " (" + idade + " anos)";
        }
        adicionarLinhaInfo(grid, linha++, "Data de Nascimento:", dataNasc);
        adicionarLinhaInfo(grid, linha++, "Telefone / Celular:", candidato != null ? candidato.getTelefone() : "-");
        adicionarLinhaInfo(grid, linha++, "Email:", candidato != null ? candidato.getEmail() : "-");
        adicionarLinhaInfo(grid, linha++, "Província:", candidato != null && candidato.getProvincia() != null ? candidato.getProvincia() : "-");
        adicionarLinhaInfo(grid, linha++, "Distrito:", candidato != null && candidato.getDistrito() != null ? candidato.getDistrito() : "-");
        adicionarLinhaInfo(grid, linha++, "Morada:", candidato != null && candidato.getMorada() != null ? candidato.getMorada() : "-");

        card.getChildren().addAll(lblTit, new Separator(), grid);
        return card;
    }

    private VBox criarCardSocioeconomico() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        Label lblTit = new Label("Questionário Socioeconómico (Carência)");
        lblTit.getStyleClass().add("card-title");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(12);

        int linha = 0;
        if (carenciaAtual != null) {
            adicionarLinhaInfo(grid, linha++, "Rendimento Familiar:", carenciaAtual.getRendimentoFamiliar() != null ? carenciaAtual.getRendimentoFamiliar().getDescricao() : "-");
            adicionarLinhaInfo(grid, linha++, "Situação Laboral:", carenciaAtual.getSituacaoLaboral() != null ? carenciaAtual.getSituacaoLaboral().getDescricao() : "-");
            adicionarLinhaInfo(grid, linha++, "Distância até à Instituição:", carenciaAtual.getDistanciaKm() + " km");
            adicionarLinhaInfo(grid, linha++, "Órfão ou Chefe de Família:", carenciaAtual.isOrfaoOuChefeFamilia() ? "Sim (+15 pontos)" : "Não");
            adicionarLinhaInfo(grid, linha++, "Apoio Governamental / Social:", carenciaAtual.isRecebeApoioFinanceiro() ? "Sim" : "Não");

            int pontuacao = carenciaAtual.calcularPontuacao();
            String nivel = carenciaAtual.classificar().getDescricao();
            Label lblPontos = new Label(pontuacao + " / 100 pontos (" + nivel + ")");
            lblPontos.setStyle("-fx-font-weight: bold; -fx-text-fill: #1E3A8A; -fx-font-size: 13px;");
            grid.add(new Label("Pontuação Calculada:"), 0, linha);
            grid.add(lblPontos, 1, linha++);

            boolean elegivel = carenciaAtual.elegivelParaVagaReservada();
            Label lblCotas = new Label(elegivel ? "ELEGÍVEL (Regime de Vagas Reservadas / Bolsa)" : "Ampla Concorrência");
            lblCotas.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (elegivel ? "#15803D" : "#475569") + ";");
            grid.add(new Label("Regime de Admissão:"), 0, linha);
            grid.add(lblCotas, 1, linha);
        } else {
            Label lblSemQuestionario = new Label("Nenhum questionário socioeconómico preenchido para este registo.");
            lblSemQuestionario.setStyle("-fx-text-fill: -fx-text-secondary; -fx-font-style: italic;");
            grid.add(lblSemQuestionario, 0, 0, 2, 1);
        }

        card.getChildren().addAll(lblTit, new Separator(), grid);
        return card;
    }

    private void adicionarLinhaInfo(GridPane grid, int linha, String rotulo, String valor) {
        Label lblR = new Label(rotulo);
        lblR.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-secondary;");

        Label lblV = new Label(valor != null && !valor.isEmpty() ? valor : "-");
        lblV.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-primary;");
        lblV.setWrapText(true);

        grid.add(lblR, 0, linha);
        grid.add(lblV, 1, linha);
    }

    private VBox criarCardAcoes() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        Label lblTit = new Label("Ações e Comprovativo");
        lblTit.getStyleClass().add("card-title");

        Button btnPdf = new Button("📄 Descarregar Comprovativo Oficial em PDF");
        btnPdf.getStyleClass().add("button-pdf");
        btnPdf.setOnAction(e -> exportarComprovativoComFileChooser());

        Button btnAtualizar = new Button("🔄 Atualizar Estado");
        btnAtualizar.getStyleClass().add("button-secondary");
        btnAtualizar.setOnAction(e -> {
            carregarDadosBanco();
            renderizarCards();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Atualização");
            alert.setHeaderText("Dados Atualizados");
            alert.setContentText("As informações da sua candidatura foram recarregadas com sucesso a partir da base de dados.");
            alert.showAndWait();
        });

        HBox boxBotoes = new HBox(12, btnPdf, btnAtualizar);
        boxBotoes.setAlignment(Pos.CENTER_LEFT);

        VBox boxAjuda = new VBox(4);
        boxAjuda.setStyle("-fx-background-color: rgba(148, 163, 184, 0.08); -fx-padding: 12px 16px; -fx-background-radius: 8px;");
        Label lblDuvida = new Label("Precisa de auxílio ou deseja alterar algum dado?");
        lblDuvida.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: -fx-text-primary;");
        Label lblInfo = new Label("Contacte a Secretaria Académica da Ocean S.A. através do email secretaria@ocean.ac.mz ou visite presencialmente os nossos serviços.");
        lblInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: -fx-text-secondary;");
        lblInfo.setWrapText(true);
        boxAjuda.getChildren().addAll(lblDuvida, lblInfo);

        card.getChildren().addAll(lblTit, new Separator(), boxBotoes, boxAjuda);
        return card;
    }

    private void exportarComprovativoComFileChooser() {
        if (candidato == null) {
            mostrarAlertaErro("Não foi possível identificar o candidato.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolha onde salvar o Comprovativo de Inscrição");
        String nomeSugerido = "Comprovativo_" + (candidato.getNumeroBI() != null ? candidato.getNumeroBI() : "Inscricao") + ".pdf";
        fileChooser.setInitialFileName(nomeSugerido);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos PDF (*.pdf)", "*.pdf"));

        File destino = fileChooser.showSaveDialog(getScene() != null ? getScene().getWindow() : null);
        if (destino != null) {
            try {
                controller.gerarComprovativoPdf(candidato, carenciaAtual, destino);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Comprovativo Emitido");
                alert.setHeaderText("PDF Salvo com Sucesso!");
                alert.setContentText("O seu comprovativo oficial foi gerado e salvo em:\n" + destino.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception ex) {
                mostrarAlertaErro("Erro ao gerar o comprovativo em PDF: " + ex.getMessage());
            }
        }
    }

    private void mostrarAlertaErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Operação Não Concluída");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
