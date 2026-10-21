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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;

/**

 * Representa a interface visual relacionada a TelaDashboardCandidatoFX.

 */

public class TelaDashboardCandidatoFX extends BorderPane {

    private final Candidato candidato;
    private final InscricaoController controller;

    private Inscricao inscricaoAtual;
    private QuestionarioCarencia carenciaAtual;

    private Label lblStatusBadge;
    private Label lblStatusDescricao;
    private VBox boxConteudoDinamico;

    private static final String PASTA_DOCS = System.getProperty("user.home") + File.separator + "SIS_Documentos";

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
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
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

        VBox cardHero = criarCardHeroStatus();
        VBox cardProgresso = criarCardProgresso();

        HBox boxDuasColunas = new HBox(20);
        boxDuasColunas.setAlignment(Pos.TOP_LEFT);

        VBox cardDadosPessoais = criarCardDadosPessoais();
        HBox.setHgrow(cardDadosPessoais, Priority.ALWAYS);

        VBox cardSocioeconomico = criarCardSocioeconomico();
        HBox.setHgrow(cardSocioeconomico, Priority.ALWAYS);

        boxDuasColunas.getChildren().addAll(cardDadosPessoais, cardSocioeconomico);

        VBox cardDocumentos = criarCardDocumentos();
        VBox cardAcoes = criarCardAcoes();

        boxConteudoDinamico.getChildren().addAll(cardHero, cardProgresso, boxDuasColunas, cardDocumentos, cardAcoes);
    }

    private VBox criarCardHeroStatus() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card-clean");
        card.setStyle("-fx-background-color: linear-gradient(to right, #061E38, #0B3A68); -fx-background-radius: 18px; -fx-padding: 24px;");

        HBox topo = new HBox(16);
        topo.setAlignment(Pos.CENTER_LEFT);

        VBox titulos = new VBox(4);
        Label lblBoasVindas = new Label("Olá, " + (candidato != null ? candidato.getNomeCompleto() : "Candidato") + "!");
        lblBoasVindas.setStyle("-fx-font-size: 22px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");

        Label lblSub = new Label("Acompanhe o estado da sua candidatura, validação de documentos e os seus dados registados.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: #93C5FD;");
        titulos.getChildren().addAll(lblBoasVindas, lblSub);

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        lblStatusBadge = new Label();
        atualizarBadgeStatus();

        topo.getChildren().addAll(titulos, espaco, lblStatusBadge);

        lblStatusDescricao = new Label();
        lblStatusDescricao.setWrapText(true);
        lblStatusDescricao.setStyle("-fx-font-size: 13px; -fx-text-fill: #F1F5F9; -fx-background-color: rgba(255, 255, 255, 0.12); -fx-padding: 12px 16px; -fx-background-radius: 10px;");
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
                lblStatusBadge.setText("ACTIVE • APROVADO");
                lblStatusBadge.setStyle("-fx-background-color: #2DD4BF; -fx-text-fill: #042F2E; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 7px 16px; -fx-background-radius: 20px;");
            }
            case LISTA_ESPERA -> {
                lblStatusBadge.setText("PAUSE • LISTA DE ESPERA");
                lblStatusBadge.setStyle("-fx-background-color: #0284C7; -fx-text-fill: #FFFFFF; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 7px 16px; -fx-background-radius: 20px;");
            }
            case NAO_APROVADO -> {
                lblStatusBadge.setText("✖ NÃO CLASSIFICADO");
                lblStatusBadge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: #FFFFFF; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 7px 16px; -fx-background-radius: 20px;");
            }
            default -> {
                lblStatusBadge.setText("IN PROGRESS • EM ANÁLISE");
                lblStatusBadge.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: #FFFFFF; -fx-font-weight: 800; -fx-font-size: 12px; -fx-padding: 7px 16px; -fx-background-radius: 20px;");
            }
        }
    }

    private void atualizarDescricaoStatus() {
        StatusInscricao st = inscricaoAtual != null && inscricaoAtual.getStatus() != null
                ? inscricaoAtual.getStatus()
                : StatusInscricao.PENDENTE;

        switch (st) {
            case APROVADO -> lblStatusDescricao.setText("Parabéns! A sua candidatura foi APROVADA. Descarregue o seu comprovativo oficial em PDF abaixo e dirija-se à secretaria académica para efetuar a matrícula.");
            case LISTA_ESPERA -> lblStatusDescricao.setText("A sua candidatura foi avaliada e encontra-se em LISTA DE ESPERA. Em caso de desistências ou novas vagas, a secretaria entrará em contacto pelos meios indicados.");
            case NAO_APROVADO -> {
                String motivo = inscricaoAtual != null && inscricaoAtual.getMotivoRejeicao() != null ? " Motivo: " + inscricaoAtual.getMotivoRejeicao() : "";
                lblStatusDescricao.setText("A sua candidatura não foi classificada no número de vagas disponibilizadas para este ciclo." + motivo);
            }
            default -> lblStatusDescricao.setText("A sua candidatura foi registada com sucesso e está AGUARDANDO HOMOLOGAÇÃO da Secretaria. Submeta os seus documentos abaixo para agilizar o processo.");
        }
    }

    private VBox criarCardProgresso() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        Label lblTit = new Label("Etapas do Processo Seletivo");
        lblTit.getStyleClass().add("card-title");

        HBox esteira = new HBox(12);
        esteira.setAlignment(Pos.CENTER);

        boolean docsSubmetidos = inscricaoAtual != null && inscricaoAtual.isDocumentosSubmetidos();
        boolean docsValidados = inscricaoAtual != null && inscricaoAtual.isDocumentosValidados();
        StatusInscricao st = inscricaoAtual != null ? inscricaoAtual.getStatus() : StatusInscricao.PENDENTE;

        VBox passo1 = criarItemPasso("1. Inscrição", "Submetida com sucesso", true, "✔");
        Separator sep1 = criarSeparadorPasso();
        VBox passo2 = criarItemPasso("2. Documentos",
                docsValidados ? "Validados pela Secretaria" : (docsSubmetidos ? "Submetidos – Em Verificação" : "Aguardando Submissão"),
                docsSubmetidos, docsValidados ? "✔" : (docsSubmetidos ? "⏳" : "○"));
        Separator sep2 = criarSeparadorPasso();
        VBox passo3 = criarItemPasso("3. Carência Social",
                carenciaAtual != null ? carenciaAtual.calcularPontuacao() + " pts (" + carenciaAtual.classificar().getDescricao() + ")" : "Aguardando cálculo",
                carenciaAtual != null, carenciaAtual != null ? "✔" : "○");
        Separator sep3 = criarSeparadorPasso();
        VBox passo4 = criarItemPasso("4. Homologação",
                st != StatusInscricao.PENDENTE ? st.getDescricao() : "Em Análise",
                st != StatusInscricao.PENDENTE, st == StatusInscricao.APROVADO ? "✔" : (st == StatusInscricao.PENDENTE ? "⏳" : "•"));

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

        if (inscricaoAtual != null && inscricaoAtual.getCursoNome() != null && !inscricaoAtual.getCursoNome().isEmpty()) {
            adicionarLinhaInfo(grid, linha, "Curso Pretendido:", inscricaoAtual.getCursoNome());
        }

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
            Label lblSemQ = new Label("Nenhum questionário socioeconómico preenchido para este registo.");
            lblSemQ.setStyle("-fx-text-fill: -fx-text-secondary; -fx-font-style: italic;");
            grid.add(lblSemQ, 0, 0, 2, 1);
        }

        card.getChildren().addAll(lblTit, new Separator(), grid);
        return card;
    }

    private VBox criarCardDocumentos() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        Label lblTit = new Label("Documentos de Identificação e Habilitações");
        lblTit.getStyleClass().add("card-title");

        boolean jaSubmetidos = inscricaoAtual != null && inscricaoAtual.isDocumentosSubmetidos();

        if (jaSubmetidos) {
            GridPane gridDocs = new GridPane();
            gridDocs.setHgap(16);
            gridDocs.setVgap(10);

            adicionarLinhaInfo(gridDocs, 0, "Doc. Identificação (BI):",
                    inscricaoAtual.getDocIdentificacao() != null ? "✔ " + Paths.get(inscricaoAtual.getDocIdentificacao()).getFileName() : "—");
            adicionarLinhaInfo(gridDocs, 1, "Certificado 10ª Classe:",
                    inscricaoAtual.getDocCertificado10() != null ? "✔ " + Paths.get(inscricaoAtual.getDocCertificado10()).getFileName() : "—");
            adicionarLinhaInfo(gridDocs, 2,
                    (inscricaoAtual.getTipoComprovativo12() != null ? inscricaoAtual.getTipoComprovativo12() : "Doc. 12ª Classe") + ":",
                    inscricaoAtual.getDocCertificado12OuRecibo() != null ? "✔ " + Paths.get(inscricaoAtual.getDocCertificado12OuRecibo()).getFileName() : "—");

            boolean validados = inscricaoAtual.isDocumentosValidados();
            Label lblEstado = new Label(validados ? "✔ Documentos verificados pela Secretaria" : "⏳ Documentos em análise pela Secretaria");
            lblEstado.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (validados ? "#15803D" : "#D97706") + "; -fx-font-size: 13px;");

            VBox boxInfo = new VBox(6, gridDocs, lblEstado);

            Button btnResubmeter = new Button("🔄 Substituir Documentos");
            btnResubmeter.getStyleClass().add("button-secondary");
            btnResubmeter.setOnAction(e -> mostrarFormularioDocumentos(card));

            card.getChildren().addAll(lblTit, new Separator(), boxInfo, btnResubmeter);
        } else {
            Label lblInstrucoes = new Label(
                    "Para avançar no processo seletivo, submeta os seguintes documentos digitalizados (PDF, JPG ou PNG):\n"
                    + "  1. Bilhete de Identidade (frente e verso)\n"
                    + "  2. Certificado de conclusão da 10ª classe\n"
                    + "  3. Certificado da 12ª classe OU Recibo de solicitação do certificado"
            );
            lblInstrucoes.setStyle("-fx-text-fill: -fx-text-secondary; -fx-font-size: 13px;");
            lblInstrucoes.setWrapText(true);

            Button btnSubmeter = new Button("📎 Submeter Documentos Agora");
            btnSubmeter.getStyleClass().add("button-primary");
            btnSubmeter.setOnAction(e -> mostrarFormularioDocumentos(card));

            card.getChildren().addAll(lblTit, new Separator(), lblInstrucoes, btnSubmeter);
        }

        return card;
    }

    private void mostrarFormularioDocumentos(VBox cardDocumentos) {
        if (inscricaoAtual == null) {
            mostrarAlertaErro("Não foi possível identificar a inscrição.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Submeter Documentos de Candidatura");
        dialog.setHeaderText("Anexe os ficheiros digitalizados solicitados");
        dialog.getDialogPane().setMinWidth(560);

        VBox conteudo = new VBox(16);
        conteudo.setPadding(new Insets(16));

        Label[] lblDocBI = {new Label("Nenhum ficheiro seleccionado")};
        Label[] lblDoc10 = {new Label("Nenhum ficheiro seleccionado")};
        Label[] lblDoc12 = {new Label("Nenhum ficheiro seleccionado")};
        File[] fBI = {null};
        File[] f10 = {null};
        File[] f12 = {null};

        conteudo.getChildren().addAll(
                criarLinhaUpload("BI (frente e verso) *", lblDocBI, fBI),
                criarLinhaUpload("Certificado 10ª Classe *", lblDoc10, f10)
        );

        ToggleGroup grpTipo = new ToggleGroup();
        RadioButton rbCert = new RadioButton("Certificado de Conclusão");
        RadioButton rbRecibo = new RadioButton("Recibo de Solicitação");
        rbCert.setToggleGroup(grpTipo);
        rbRecibo.setToggleGroup(grpTipo);
        rbCert.setSelected(true);
        rbCert.setStyle("-fx-text-fill: -fx-text-primary;");
        rbRecibo.setStyle("-fx-text-fill: -fx-text-primary;");

        HBox boxTipo = new HBox(16, rbCert, rbRecibo);
        boxTipo.setAlignment(Pos.CENTER_LEFT);

        VBox boxDoc12 = new VBox(6);
        Label lblTipoCert = new Label("Comprovativo da 12ª Classe *");
        lblTipoCert.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-primary;");
        boxDoc12.getChildren().addAll(lblTipoCert, boxTipo, criarLinhaUpload("Ficheiro", lblDoc12, f12));
        conteudo.getChildren().add(boxDoc12);

        TextArea txtMotivacao = new TextArea();
        txtMotivacao.setPromptText("Carta de motivação (opcional) — máximo 500 caracteres");
        txtMotivacao.setMaxHeight(100);
        txtMotivacao.setPrefRowCount(4);
        txtMotivacao.setWrapText(true);
        txtMotivacao.textProperty().addListener((obs, old, novo) -> {
            if (novo.length() > 500) txtMotivacao.setText(old);
        });

        VBox boxMotivacao = new VBox(6);
        Label lblMot = new Label("Carta de Motivação (opcional)");
        lblMot.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-primary;");
        boxMotivacao.getChildren().addAll(lblMot, txtMotivacao);
        conteudo.getChildren().add(boxMotivacao);

        dialog.getDialogPane().setContent(conteudo);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btnOk.setText("Submeter Documentos");
        btnOk.getStyleClass().add("button-primary");

        dialog.showAndWait().ifPresent(tipo -> {
            if (tipo != ButtonType.OK) return;

            if (fBI[0] == null || f10[0] == null || f12[0] == null) {
                mostrarAlertaErro("Todos os documentos obrigatórios (*) devem ser seleccionados.");
                return;
            }

            try {
                Files.createDirectories(Paths.get(PASTA_DOCS));
                String prefixo = "cand_" + candidato.getId() + "_";
                String pathBI = copiarFicheiro(fBI[0], prefixo + "bi");
                String path10 = copiarFicheiro(f10[0], prefixo + "cert10");
                String path12 = copiarFicheiro(f12[0], prefixo + (rbCert.isSelected() ? "cert12" : "recibo12"));
                String tipoDoc12 = rbCert.isSelected() ? "Certificado de Conclusão" : "Recibo de Solicitação";

                controller.submeterDocumentos(
                        inscricaoAtual.getId(), pathBI, path10, path12, tipoDoc12,
                        txtMotivacao.getText().trim().isEmpty() ? null : txtMotivacao.getText().trim()
                );

                carregarDadosBanco();
                renderizarCards();

                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Documentos Submetidos");
                ok.setHeaderText("Sucesso!");
                ok.setContentText("Os seus documentos foram submetidos e estão a aguardar verificação da Secretaria.");
                ok.showAndWait();

            } catch (Exception ex) {
                mostrarAlertaErro("Erro ao submeter documentos: " + ex.getMessage());
            }
        });
    }

    private HBox criarLinhaUpload(String rotulo, Label[] lblNome, File[] ficheiro) {
        Label lbl = new Label(rotulo);
        lbl.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-primary; -fx-min-width: 180px;");

        lblNome[0].setStyle("-fx-text-fill: -fx-text-secondary; -fx-font-size: 12px;");

        Button btnSelecionar = new Button("📁 Seleccionar");
        btnSelecionar.getStyleClass().add("button-secondary");
        btnSelecionar.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Seleccionar Documento");
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Documentos (PDF, Imagem)", "*.pdf", "*.jpg", "*.jpeg", "*.png"),
                    new FileChooser.ExtensionFilter("Todos os ficheiros", "*.*")
            );
            File f = fc.showOpenDialog(Navegador.getPalcoPrincipal());
            if (f != null) {
                ficheiro[0] = f;
                lblNome[0].setText("✔ " + f.getName());
                lblNome[0].setStyle("-fx-text-fill: #15803D; -fx-font-size: 12px;");
            }
        });

        HBox box = new HBox(12, lbl, btnSelecionar, lblNome[0]);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private String copiarFicheiro(File origem, String nomeSemExtensao) throws IOException {
        String nomeOriginal = origem.getName();
        String ext = nomeOriginal.contains(".") ? nomeOriginal.substring(nomeOriginal.lastIndexOf('.')) : "";
        Path destino = Paths.get(PASTA_DOCS, nomeSemExtensao + ext);
        try (InputStream in = Files.newInputStream(origem.toPath());
             OutputStream out = Files.newOutputStream(destino)) {
            in.transferTo(out);
        }
        return destino.toString();
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
            alert.setContentText("As informações da sua candidatura foram recarregadas com sucesso.");
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

    private void adicionarLinhaInfo(GridPane grid, int linha, String rotulo, String valor) {
        Label lblR = new Label(rotulo);
        lblR.setStyle("-fx-font-weight: 600; -fx-font-size: 12px; -fx-text-fill: -fx-text-secondary;");

        Label lblV = new Label(valor != null && !valor.isEmpty() ? valor : "-");
        lblV.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-primary;");
        lblV.setWrapText(true);

        grid.add(lblR, 0, linha);
        grid.add(lblV, 1, linha);
    }

    private void exportarComprovativoComFileChooser() {
        if (candidato == null) {
            mostrarAlertaErro("Não foi possível identificar o candidato.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Escolha onde salvar o Comprovativo de Inscrição");
        fileChooser.setInitialFileName("Comprovativo_" + (candidato.getNumeroBI() != null ? candidato.getNumeroBI() : "Inscricao") + ".pdf");
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
