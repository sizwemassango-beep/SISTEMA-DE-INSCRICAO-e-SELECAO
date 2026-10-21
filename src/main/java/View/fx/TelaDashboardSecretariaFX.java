package View.fx;

import controller.InscricaoController;
import controller.PainelController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import model.Candidato;
import model.Curso;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


/**


 * Representa a interface visual relacionada a TelaDashboardSecretariaFX.


 */


public class TelaDashboardSecretariaFX extends BorderPane {

    private final PainelController controller;
    private final InscricaoController inscricaoController;

    private StackPane areaConteudo;
    private ObservableList<Candidato> listaCandidatosObservable;
    private TableView<Candidato> tabelaCandidatos;
    private TextField txtBusca;

    private Button btnMenuPainel;
    private Button btnMenuCandidatos;
    private Button btnMenuCursos;
    private Button btnMenuUsuarios;
    private Button btnMenuPdf;
    private final controller.LoginController loginController;

    public TelaDashboardSecretariaFX() {
        this.controller = new PainelController();
        this.inscricaoController = new InscricaoController();
        this.loginController = new controller.LoginController();
        this.listaCandidatosObservable = FXCollections.observableArrayList();
        construirInterface();
        carregarDados();
        configurarAtalhos();
    }

    public void focarPesquisa() {
        mostrarGestaoCandidatos();
        if (txtBusca != null) {
            javafx.application.Platform.runLater(() -> {
                txtBusca.requestFocus();
                txtBusca.selectAll();
            });
        }
    }

    private void configurarAtalhos() {
        this.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, ev -> {
            if (ev.isControlDown() && ev.getCode() == javafx.scene.input.KeyCode.P) {
                focarPesquisa();
                ev.consume();
            }
        });
    }

    private void construirInterface() {
        setLeft(criarSidebar());

        areaConteudo = new StackPane();
        areaConteudo.setPadding(new Insets(24));
        setCenter(areaConteudo);

        mostrarVisaoGeral();
    }

    private VBox criarSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        HBox boxMarca = new HBox(12);
        boxMarca.setAlignment(Pos.CENTER_LEFT);
        boxMarca.setPadding(new Insets(6, 10, 24, 10));

        StackPane logoSidebar = ComponentesVisuais.criarLogoCompacto(38);

        VBox titulosSidebar = new VBox(2);
        Label lblMarca = new Label("sis.ocean");
        lblMarca.setStyle("-fx-font-size: 19px; -fx-font-weight: 800; -fx-text-fill: #FFFFFF;");

        Label lblPerfil = new Label("PAINEL ACADÉMICO");
        lblPerfil.setStyle("-fx-font-size: 9.5px; -fx-font-weight: 700; -fx-text-fill: #2DD4BF; -fx-letter-spacing: 0.5px;");
        titulosSidebar.getChildren().addAll(lblMarca, lblPerfil);

        boxMarca.getChildren().addAll(logoSidebar, titulosSidebar);

        Label lblSecGeral = new Label("GESTÃO");
        lblSecGeral.getStyleClass().add("sidebar-section-header");

        btnMenuPainel = criarBotaoMenu("📊 Visão Geral", true, this::mostrarVisaoGeral);
        btnMenuCandidatos = criarBotaoMenu("👥 Candidatos", false, this::mostrarGestaoCandidatos);
        btnMenuCursos = criarBotaoMenu("📚 Cursos & Vagas", false, this::mostrarGestaoCursos);

        Label lblSecAdmin = new Label("ADMINISTRAÇÃO");
        lblSecAdmin.getStyleClass().add("sidebar-section-header");

        btnMenuUsuarios = criarBotaoMenu("🛡 Utilizadores", false, this::mostrarGestaoSecretaria);
        btnMenuPdf = criarBotaoMenu("📄 Relatórios PDF", false, this::mostrarModuloPdf);

        Region espacador = new Region();
        VBox.setVgrow(espacador, Priority.ALWAYS);

        Label lblSecConfig = new Label("PREFERÊNCIAS");
        lblSecConfig.getStyleClass().add("sidebar-section-header");

        Button btnTema = new Button(Navegador.isModoEscuro() ? "☀ Modo Claro" : "🌙 Modo Escuro");
        btnTema.getStyleClass().add("sidebar-button");
        btnTema.setMaxWidth(Double.MAX_VALUE);
        btnTema.setOnAction(e -> {
            Navegador.alternarTema();
            btnTema.setText(Navegador.isModoEscuro() ? "☀ Modo Claro" : "🌙 Modo Escuro");
        });

        Button btnTerminarSessao = new Button("🚪 Terminar Sessão");
        btnTerminarSessao.getStyleClass().add("sidebar-button");
        btnTerminarSessao.setMaxWidth(Double.MAX_VALUE);
        btnTerminarSessao.setOnAction(e -> Navegador.irParaTelaInicial());

        sidebar.getChildren().addAll(
                boxMarca,
                lblSecGeral,
                btnMenuPainel,
                btnMenuCandidatos,
                btnMenuCursos,
                lblSecAdmin,
                btnMenuUsuarios,
                btnMenuPdf,
                espacador,
                lblSecConfig,
                btnTema,
                btnTerminarSessao
        );

        return sidebar;
    }

    private Button criarBotaoMenu(String texto, boolean ativoInicial, Runnable acao) {
        Button btn = new Button(texto);
        btn.getStyleClass().add(ativoInicial ? "sidebar-button-active" : "sidebar-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            desmarcarBotoesMenu();
            btn.getStyleClass().remove("sidebar-button");
            if (!btn.getStyleClass().contains("sidebar-button-active")) {
                btn.getStyleClass().add("sidebar-button-active");
            }
            acao.run();
        });
        return btn;
    }

    private void desmarcarBotoesMenu() {
        for (Button b : new Button[]{btnMenuPainel, btnMenuCandidatos, btnMenuCursos, btnMenuUsuarios, btnMenuPdf}) {
            if (b != null) {
                b.getStyleClass().remove("sidebar-button-active");
                if (!b.getStyleClass().contains("sidebar-button")) {
                    b.getStyleClass().add("sidebar-button");
                }
            }
        }
    }

    private void carregarDados() {
        List<Candidato> candidatos = controller.listarCandidatos();
        listaCandidatosObservable.setAll(candidatos);
    }

    private void mostrarVisaoGeral() {
        VBox box = new VBox(22);
        box.setFillWidth(true);

        // Header Superior com estética do Mockup (Título + Ações em Pill)
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titulos = new VBox(2);
        Label lblTitulo = new Label("Balance information");
        lblTitulo.setStyle("-fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Painel analítico e distribuição em tempo real das candidaturas");
        lblSub.setStyle("-fx-font-size: 12.5px; -fx-text-fill: -fx-text-secondary;");
        titulos.getChildren().addAll(lblTitulo, lblSub);

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        Button btnAtualizar = new Button("+ Atualizar Dados");
        btnAtualizar.getStyleClass().add("badge-teal-action");
        btnAtualizar.setOnAction(e -> {
            carregarDados();
            mostrarVisaoGeral();
        });

        Button btnExportarGeral = new Button("📥 Relatório PDF");
        btnExportarGeral.getStyleClass().add("button-secondary");
        btnExportarGeral.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-background-radius: 8px; -fx-padding: 7px 14px;");
        btnExportarGeral.setOnAction(e -> exportarPdfGeral());

        header.getChildren().addAll(titulos, esp, btnAtualizar, btnExportarGeral);

        // Grid com 2 colunas principais (Estilo Layout do Mockup: Coluna Esquerda com Cards de Métricas/Métodos, Coluna Direita com Gráficos e Transações Recentes)
        HBox layoutColunas = new HBox(22);
        layoutColunas.setAlignment(Pos.TOP_LEFT);

        // COLUNA ESQUERDA (Cards de Resumo e Ações)
        VBox colunaEsquerda = new VBox(16);
        colunaEsquerda.setPrefWidth(310);
        colunaEsquerda.setMinWidth(290);

        int totalCandidatos = controller.obterTotalCandidatos();
        int totalCursos = controller.obterTotalCursos();
        int totalVagas = controller.obterTotalVagas();
        double concorrencia = controller.obterConcorrenciaGeral();

        // Card 1: Candidatos
        VBox cardCand = criarCardMetodoMockup(
                "CANDIDATURAS",
                "👥 Total de Inscritos",
                String.valueOf(totalCandidatos),
                "Candidatos registados",
                "Ver Candidatos →",
                this::mostrarGestaoCandidatos
        );

        // Card 2: Cursos
        VBox cardCur = criarCardMetodoMockup(
                "CURSOS ATIVOS",
                "📚 Cursos em Oferta",
                String.valueOf(totalCursos),
                "Cursos preparatórios",
                "Gerir Cursos →",
                this::mostrarGestaoCursos
        );

        // Card 3: Vagas e Concorrência
        VBox cardVagas = criarCardMetodoMockup(
                "VAGAS & DEMANDA",
                "🎯 Vagas Disponíveis",
                String.valueOf(totalVagas),
                String.format("%.2f cand/vaga", concorrencia),
                "Emitir Balanço →",
                this::exportarPdfGeral
        );

        colunaEsquerda.getChildren().addAll(cardCand, cardCur, cardVagas);

        // COLUNA DIREITA (Gráfico de Barras + Donut de Estatísticas + Lista de Transações/Inscrições Recentes)
        VBox colunaDireita = new VBox(18);
        HBox.setHgrow(colunaDireita, Priority.ALWAYS);

        // Linha com 2 Gráficos (Daily Overview + Balance Statistic Donut)
        HBox linhaGraficos = new HBox(18);

        // 1. BarChart: Candidatos por Província
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Província");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Inscritos");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Distribuição de Candidatos por Província");
        barChart.setLegendVisible(false);
        barChart.getStyleClass().add("card-clean");
        barChart.setPrefHeight(290);
        HBox.setHgrow(barChart, Priority.ALWAYS);

        XYChart.Series<String, Number> serieProv = new XYChart.Series<>();
        Map<String, Integer> mapaProv = controller.obterCandidatosPorProvincia();

        if (mapaProv.isEmpty()) {
            serieProv.getData().add(new XYChart.Data<>("Sem dados", 0));
        } else {
            for (Map.Entry<String, Integer> entry : mapaProv.entrySet()) {
                serieProv.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }
        barChart.getData().add(serieProv);

        // 2. PieChart Moderno (Estilo Balance Statistic Donut da Imagem)
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        List<Curso> cursos = controller.listarCursos();
        if (cursos.isEmpty()) {
            pieData.add(new PieChart.Data("Nenhum curso", 1));
        } else {
            for (Curso c : cursos) {
                pieData.add(new PieChart.Data(c.getNome(), c.getNumeroVagas()));
            }
        }

        PieChart pieChart = new PieChart(pieData);
        pieChart.setTitle("Proporção de Vagas por Curso");
        pieChart.getStyleClass().add("card-clean");
        pieChart.setPrefWidth(330);
        pieChart.setPrefHeight(290);

        linhaGraficos.getChildren().addAll(barChart, pieChart);

        // Tabela/Lista de Inscrições Recentes (Estilo Card "Transactions" do Mockup)
        VBox cardTransacoes = criarTabelaInscricoesRecentes();
        VBox.setVgrow(cardTransacoes, Priority.ALWAYS);

        colunaDireita.getChildren().addAll(linhaGraficos, cardTransacoes);

        layoutColunas.getChildren().addAll(colunaEsquerda, colunaDireita);
        box.getChildren().addAll(header, layoutColunas);

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        areaConteudo.getChildren().setAll(scroll);
    }

    private VBox criarCardMetodoMockup(String tag, String titulo, String valor, String subtitulo, String textoBotao, Runnable acao) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card-clean");

        HBox topo = new HBox(8);
        topo.setAlignment(Pos.CENTER_LEFT);

        Label lblTag = new Label(tag);
        lblTag.setStyle("-fx-font-size: 11px; -fx-font-weight: 700; -fx-text-fill: -fx-text-secondary;");

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        topo.getChildren().addAll(lblTag, esp);

        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: -fx-text-primary;");

        Button btnAcao = new Button(textoBotao);
        btnAcao.getStyleClass().add("button-soft-blue");
        btnAcao.setMaxWidth(Double.MAX_VALUE);
        btnAcao.setOnAction(e -> acao.run());

        HBox rodape = new HBox();
        rodape.setAlignment(Pos.CENTER_LEFT);

        Label lblSub = new Label(subtitulo);
        lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: -fx-text-secondary;");

        Region espRodape = new Region();
        HBox.setHgrow(espRodape, Priority.ALWAYS);

        Label lblVal = new Label(valor);
        lblVal.setStyle("-fx-font-size: 15px; -fx-font-weight: 800; -fx-text-fill: -fx-text-primary;");

        rodape.getChildren().addAll(lblSub, espRodape, lblVal);

        card.getChildren().addAll(topo, lblTit, btnAcao, rodape);
        return card;
    }

    private VBox criarTabelaInscricoesRecentes() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card-clean");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTit = new Label("Candidaturas Registadas");
        lblTit.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: -fx-text-primary;");

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        Label lblFiltro = new Label(listaCandidatosObservable.size() + " Registos Totais");
        lblFiltro.setStyle("-fx-font-size: 11.5px; -fx-font-weight: 700; -fx-text-fill: -fx-accent-color; -fx-background-color: rgba(37,99,235,0.1); -fx-padding: 4px 10px; -fx-background-radius: 12px;");

        Button btnVerTodos = new Button("Gerir Candidatos (Ctrl+P) →");
        btnVerTodos.setStyle("-fx-background-color: transparent; -fx-text-fill: -fx-accent-color; -fx-font-size: 12px; -fx-font-weight: 600; -fx-cursor: hand;");
        btnVerTodos.setOnAction(e -> focarPesquisa());

        header.getChildren().addAll(lblTit, esp, lblFiltro, btnVerTodos);

        VBox lista = new VBox(8);

        if (listaCandidatosObservable.isEmpty()) {
            Label lblVazio = new Label("Nenhuma candidatura registada no momento.");
            lblVazio.setStyle("-fx-text-fill: -fx-text-secondary; -fx-font-size: 13px; -fx-padding: 18px;");
            lista.getChildren().add(lblVazio);
        } else {
            for (Candidato c : listaCandidatosObservable) {
                HBox linha = new HBox(12);
                linha.setAlignment(Pos.CENTER_LEFT);
                linha.setStyle("-fx-padding: 10px 14px; -fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-width: 0 0 1 0; -fx-border-radius: 6px;");

                VBox colNome = new VBox(2);
                Label lblNome = new Label(c.getNomeCompleto());
                lblNome.setStyle("-fx-font-weight: 700; -fx-font-size: 13px; -fx-text-fill: -fx-text-primary;");
                Label lblEmail = new Label(c.getEmail() != null ? c.getEmail() : "Sem email");
                lblEmail.setStyle("-fx-font-size: 11px; -fx-text-fill: -fx-text-secondary;");
                colNome.getChildren().addAll(lblNome, lblEmail);
                colNome.setPrefWidth(240);

                Label lblBi = new Label("BI: " + (c.getNumeroBI() != null ? c.getNumeroBI() : "-"));
                lblBi.setStyle("-fx-font-size: 12px; -fx-text-fill: -fx-text-secondary;");
                lblBi.setPrefWidth(150);

                Label lblProv = new Label(c.getProvincia() != null ? c.getProvincia() : "-");
                lblProv.setStyle("-fx-font-size: 12px; -fx-text-fill: -fx-text-secondary;");
                lblProv.setPrefWidth(120);

                Region espLinha = new Region();
                HBox.setHgrow(espLinha, Priority.ALWAYS);

                // Obter estado real do candidato
                String textoStatus = "Pendente";
                String classeStatus = "badge-status-pendente";

                var optIns = inscricaoController.buscarInscricaoPorCandidato(c.getId());
                if (optIns.isPresent()) {
                    model.StatusInscricao st = optIns.get().getStatus();
                    if (st == model.StatusInscricao.APROVADO) {
                        textoStatus = "Aceite";
                        classeStatus = "badge-status-aceite";
                    } else if (st == model.StatusInscricao.NAO_APROVADO) {
                        textoStatus = "Recusado";
                        classeStatus = "badge-status-recusado";
                    } else if (st == model.StatusInscricao.LISTA_ESPERA) {
                        textoStatus = "Lista Espera";
                        classeStatus = "badge-status-pendente";
                    } else {
                        textoStatus = "Pendente";
                        classeStatus = "badge-status-pendente";
                    }
                }

                Label lblStatus = new Label(textoStatus);
                lblStatus.getStyleClass().add(classeStatus);

                linha.getChildren().addAll(colNome, lblBi, lblProv, espLinha, lblStatus);
                lista.getChildren().add(linha);
            }
        }

        ScrollPane scrollLista = new ScrollPane(lista);
        scrollLista.setFitToWidth(true);
        scrollLista.setMaxHeight(360);
        scrollLista.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        card.getChildren().addAll(header, scrollLista);
        return card;
    }

    private VBox criarCardKpi(String rotulo, String valor, String corDestaque) {
        VBox card = new VBox(6);
        card.getStyleClass().add("kpi-card");

        Label lblRotulo = new Label(rotulo);
        lblRotulo.getStyleClass().add("kpi-title");

        Label lblValor = new Label(valor);
        lblValor.getStyleClass().add("kpi-value");
        lblValor.setStyle("-fx-text-fill: " + corDestaque + ";");

        card.getChildren().addAll(lblRotulo, lblValor);
        return card;
    }

    private void mostrarGestaoCandidatos() {
        VBox box = new VBox(18);
        box.setFillWidth(true);

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titulos = new VBox(4);
        Label lblTitulo = new Label("Base de Dados de Candidatos");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Consulte, filtre, aprove e exporte os registos de candidaturas");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");
        titulos.getChildren().addAll(lblTitulo, lblSub);

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        txtBusca = new TextField();
        txtBusca.setPromptText("🔍 Pesquisar por Nome ou BI... (Ctrl+P)");
        txtBusca.setPrefWidth(300);

        Button btnExportarPdf = new Button("📥 Exportar Lista (PDF)");
        btnExportarPdf.getStyleClass().add("button-pdf");
        btnExportarPdf.setOnAction(e -> exportarPdfGeral());

        Button btnComprovativo = new Button("📄 Comprovativo do Selecionado");
        btnComprovativo.getStyleClass().add("button-primary");
        btnComprovativo.setOnAction(e -> exportarComprovativoSelecionado());

        header.getChildren().addAll(titulos, esp, txtBusca, btnExportarPdf, btnComprovativo);

        tabelaCandidatos = new TableView<>();
        tabelaCandidatos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelaCandidatos.setPlaceholder(new Label("Nenhum candidato registado na base de dados."));

        TableColumn<Candidato, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colId.setMaxWidth(60);

        TableColumn<Candidato, String> colNome = new TableColumn<>("Nome Completo");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomeCompleto()));

        TableColumn<Candidato, String> colBI = new TableColumn<>("Número de BI");
        colBI.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNumeroBI()));

        TableColumn<Candidato, String> colProvincia = new TableColumn<>("Província");
        colProvincia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProvincia()));

        TableColumn<Candidato, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefone()));

        TableColumn<Candidato, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));

        tabelaCandidatos.getColumns().addAll(colId, colNome, colBI, colProvincia, colTelefone, colEmail);

        FilteredList<Candidato> dadosFiltrados = new FilteredList<>(listaCandidatosObservable, p -> true);
        txtBusca.textProperty().addListener((obs, antigo, novo) -> {
            dadosFiltrados.setPredicate(candidato -> {
                if (novo == null || novo.trim().isEmpty()) return true;
                String filtro = novo.toLowerCase();
                boolean bateNome = candidato.getNomeCompleto() != null && candidato.getNomeCompleto().toLowerCase().contains(filtro);
                boolean bateBI = candidato.getNumeroBI() != null && candidato.getNumeroBI().toLowerCase().contains(filtro);
                return bateNome || bateBI;
            });
        });

        tabelaCandidatos.setItems(dadosFiltrados);
        VBox.setVgrow(tabelaCandidatos, Priority.ALWAYS);

        VBox painelAcoes = criarPainelAprovacao();

        box.getChildren().addAll(header, tabelaCandidatos, painelAcoes);
        areaConteudo.getChildren().setAll(box);
    }

    private VBox criarPainelAprovacao() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");

        Label lblTit = new Label("Acções sobre o Candidato Seleccionado");
        lblTit.getStyleClass().add("card-title");

        HBox boxBotoes = new HBox(12);
        boxBotoes.setAlignment(Pos.CENTER_LEFT);

        Button btnAprovar = new Button("✔ Aprovar Candidato");
        btnAprovar.setStyle("-fx-background-color: #10B981; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 18px; -fx-cursor: hand;");

        Button btnListaEspera = new Button("📋 Lista de Espera");
        btnListaEspera.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 18px; -fx-cursor: hand;");

        Button btnRejeitar = new Button("✖ Rejeitar Candidato");
        btnRejeitar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 18px; -fx-cursor: hand;");

        Button btnVerDocs = new Button("📁 Ver Documentos Submetidos");
        btnVerDocs.getStyleClass().add("button-secondary");

        Button btnValidarDocs = new Button("✔ Validar Documentos");
        btnValidarDocs.setStyle("-fx-background-color: #059669; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 10px 18px; -fx-cursor: hand;");

        btnAprovar.setOnAction(e -> {
            Candidato c = tabelaCandidatos.getSelectionModel().getSelectedItem();
            if (c == null) { mostrarAviso("Seleccione um candidato na tabela."); return; }
            inscricaoController.buscarInscricaoPorCandidato(c.getId()).ifPresentOrElse(
                    ins -> {
                        inscricaoController.aprovarCandidato(ins.getId());
                        mostrarSucesso("Candidato " + c.getNomeCompleto() + " APROVADO com sucesso.");
                        carregarDados();
                    },
                    () -> mostrarAviso("Inscrição não encontrada para este candidato.")
            );
        });

        btnListaEspera.setOnAction(e -> {
            Candidato c = tabelaCandidatos.getSelectionModel().getSelectedItem();
            if (c == null) { mostrarAviso("Seleccione um candidato na tabela."); return; }
            inscricaoController.buscarInscricaoPorCandidato(c.getId()).ifPresentOrElse(
                    ins -> {
                        inscricaoController.colocarListaEspera(ins.getId());
                        mostrarSucesso("Candidato colocado em Lista de Espera.");
                        carregarDados();
                    },
                    () -> mostrarAviso("Inscrição não encontrada.")
            );
        });

        btnRejeitar.setOnAction(e -> {
            Candidato c = tabelaCandidatos.getSelectionModel().getSelectedItem();
            if (c == null) { mostrarAviso("Seleccione um candidato na tabela."); return; }
            TextInputDialog dlg = new TextInputDialog();
            dlg.setTitle("Rejeitar Candidato");
            dlg.setHeaderText("Motivo da Rejeição (opcional)");
            dlg.setContentText("Motivo:");
            dlg.showAndWait().ifPresent(motivo ->
                inscricaoController.buscarInscricaoPorCandidato(c.getId()).ifPresentOrElse(
                        ins -> {
                            inscricaoController.rejeitarCandidato(ins.getId(), motivo.trim().isEmpty() ? null : motivo.trim());
                            mostrarSucesso("Candidato " + c.getNomeCompleto() + " rejeitado.");
                            carregarDados();
                        },
                        () -> mostrarAviso("Inscrição não encontrada.")
                )
            );
        });

        btnVerDocs.setOnAction(e -> {
            Candidato c = tabelaCandidatos.getSelectionModel().getSelectedItem();
            if (c == null) { mostrarAviso("Seleccione um candidato na tabela."); return; }
            inscricaoController.buscarInscricaoPorCandidato(c.getId()).ifPresentOrElse(
                    this::mostrarDialogDocumentos,
                    () -> mostrarAviso("Inscrição não encontrada.")
            );
        });

        btnValidarDocs.setOnAction(e -> {
            Candidato c = tabelaCandidatos.getSelectionModel().getSelectedItem();
            if (c == null) { mostrarAviso("Seleccione um candidato na tabela."); return; }
            inscricaoController.buscarInscricaoPorCandidato(c.getId()).ifPresentOrElse(
                    ins -> {
                        inscricaoController.validarDocumentosInscricao(ins.getId(), true);
                        mostrarSucesso("Documentos do candidato validados com sucesso.");
                        carregarDados();
                    },
                    () -> mostrarAviso("Inscrição não encontrada.")
            );
        });

        boxBotoes.getChildren().addAll(btnAprovar, btnListaEspera, btnRejeitar, btnVerDocs, btnValidarDocs);
        card.getChildren().addAll(lblTit, new Separator(), boxBotoes);
        return card;
    }

    private void mostrarDialogDocumentos(model.Inscricao inscricao) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Documentos Submetidos");
        dialog.setHeaderText("Candidato: " + (inscricao.getCandidato() != null ? inscricao.getCandidato().getNomeCompleto() : "—"));
        dialog.getDialogPane().setMinWidth(500);

        VBox conteudo = new VBox(12);
        conteudo.setPadding(new Insets(12));

        adicionarLinhaDocumento(conteudo, "Documento de Identificação (BI):", inscricao.getDocIdentificacao());
        adicionarLinhaDocumento(conteudo, "Certificado 10ª Classe:", inscricao.getDocCertificado10());
        adicionarLinhaDocumento(conteudo,
                (inscricao.getTipoComprovativo12() != null ? inscricao.getTipoComprovativo12() : "Doc. 12ª Classe") + ":",
                inscricao.getDocCertificado12OuRecibo());

        if (inscricao.getCartaMotivacao() != null && !inscricao.getCartaMotivacao().isEmpty()) {
            Label lblTitMot = new Label("Carta de Motivação:");
            lblTitMot.setStyle("-fx-font-weight: 600; -fx-text-fill: -fx-text-primary;");
            TextArea txtMot = new TextArea(inscricao.getCartaMotivacao());
            txtMot.setEditable(false);
            txtMot.setWrapText(true);
            txtMot.setPrefRowCount(4);
            conteudo.getChildren().addAll(new Separator(), lblTitMot, txtMot);
        }

        Label lblStatus = new Label("Documentos validados: " + (inscricao.isDocumentosValidados() ? "Sim ✔" : "Não ✖"));
        lblStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (inscricao.isDocumentosValidados() ? "#15803D" : "#D97706") + ";");
        conteudo.getChildren().add(lblStatus);

        dialog.getDialogPane().setContent(conteudo);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void adicionarLinhaDocumento(VBox container, String rotulo, String caminho) {
        HBox linha = new HBox(12);
        linha.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(rotulo);
        lbl.setStyle("-fx-font-weight: 600; -fx-min-width: 210px; -fx-text-fill: -fx-text-primary;");

        if (caminho == null || caminho.isEmpty()) {
            Label lblVazio = new Label("— Não submetido");
            lblVazio.setStyle("-fx-text-fill: -fx-text-secondary; -fx-font-style: italic;");
            linha.getChildren().addAll(lbl, lblVazio);
        } else {
            java.io.File ficheiro = new java.io.File(caminho);
            Label lblNome = new Label(ficheiro.getName());
            lblNome.setStyle("-fx-text-fill: -fx-text-primary;");

            Button btnAbrir = new Button("📂 Abrir");
            btnAbrir.getStyleClass().add("button-secondary");
            btnAbrir.setOnAction(e -> {
                try {
                    java.awt.Desktop.getDesktop().open(ficheiro);
                } catch (Exception ex) {
                    mostrarAviso("Não foi possível abrir o ficheiro: " + ex.getMessage());
                }
            });

            linha.getChildren().addAll(lbl, lblNome, btnAbrir);
        }
        container.getChildren().add(linha);
    }

    private void mostrarAviso(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Aviso");
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarSucesso(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Sucesso");
        a.setContentText(msg);
        a.showAndWait();
    }


    private void mostrarGestaoCursos() {
        VBox box = new VBox(20);
        box.setFillWidth(true);

        Label lblTitulo = new Label("Gestão de Cursos e Vagas");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        HBox layoutCursos = new HBox(20);

        // Tabela de cursos à esquerda
        TableView<Curso> tabelaCursos = new TableView<>();
        tabelaCursos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelaCursos.setPlaceholder(new Label("Nenhum curso cadastrado ainda."));

        TableColumn<Curso, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colId.setMaxWidth(50);

        TableColumn<Curso, String> colNome = new TableColumn<>("Nome do Curso");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));

        TableColumn<Curso, String> colVagas = new TableColumn<>("Total Vagas");
        colVagas.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getNumeroVagas())));

        TableColumn<Curso, String> colMerito = new TableColumn<>("% Mérito");
        colMerito.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.0f%%", c.getValue().getPercentualMerito())));

        TableColumn<Curso, String> colNec = new TableColumn<>("% Necessidade");
        colNec.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.0f%%", c.getValue().getPercentualNecessidade())));

        tabelaCursos.getColumns().addAll(colId, colNome, colVagas, colMerito, colNec);
        tabelaCursos.setItems(FXCollections.observableArrayList(controller.listarCursos()));
        HBox.setHgrow(tabelaCursos, Priority.ALWAYS);

        // Formulário de Cadastro Rápido de Curso à direita
        VBox formCurso = new VBox(12);
        formCurso.getStyleClass().add("card");
        formCurso.setPrefWidth(340);

        Label lblNovoCurso = new Label("Cadastrar Novo Curso");
        lblNovoCurso.getStyleClass().add("card-title");

        TextField txtNomeCurso = new TextField();
        txtNomeCurso.setPromptText("Nome do curso (ex: Medicina)");

        TextField txtDescCurso = new TextField();
        txtDescCurso.setPromptText("Breve descrição");

        TextField txtVagas = new TextField();
        txtVagas.setPromptText("Número de vagas (ex: 50)");

        TextField txtMerito = new TextField("60");
        txtMerito.setPromptText("% Vagas Mérito (ex: 60)");

        TextField txtNec = new TextField("40");
        txtNec.setPromptText("% Vagas Necessidade (ex: 40)");

        Button btnSalvarCurso = new Button("Cadastrar Curso");
        btnSalvarCurso.getStyleClass().add("button-primary");
        btnSalvarCurso.setMaxWidth(Double.MAX_VALUE);
        btnSalvarCurso.setOnAction(e -> {
            try {
                String nome = txtNomeCurso.getText();
                String desc = txtDescCurso.getText();
                int vagas = Integer.parseInt(txtVagas.getText().trim());
                double merito = Double.parseDouble(txtMerito.getText().trim());
                double nec = Double.parseDouble(txtNec.getText().trim());

                if (nome == null || nome.trim().isEmpty()) {
                    throw new IllegalArgumentException("Nome do curso é obrigatório.");
                }

                Curso c = new Curso();
                c.setNome(nome.trim());
                c.setDescricao(desc != null ? desc.trim() : "");
                c.setNumeroVagas(vagas);
                c.setPercentualMerito(merito);
                c.setPercentualNecessidade(nec);
                c.setDataInicio(LocalDate.now());
                c.setDataFim(LocalDate.now().plusMonths(3));

                controller.getDashboardService().registrarCurso(c);

                tabelaCursos.setItems(FXCollections.observableArrayList(controller.listarCursos()));
                txtNomeCurso.clear();
                txtDescCurso.clear();
                txtVagas.clear();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setContentText("Curso cadastrado com sucesso!");
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Erro ao salvar curso: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        formCurso.getChildren().addAll(
                lblNovoCurso,
                new Label("Nome do Curso:"), txtNomeCurso,
                new Label("Descrição:"), txtDescCurso,
                new Label("Vagas:"), txtVagas,
                new Label("% Mérito:"), txtMerito,
                new Label("% Necessidade:"), txtNec,
                btnSalvarCurso
        );

        layoutCursos.getChildren().addAll(tabelaCursos, formCurso);
        box.getChildren().addAll(lblTitulo, layoutCursos);
        areaConteudo.getChildren().setAll(box);
    }

    private void mostrarModuloPdf() {
        VBox box = new VBox(24);
        box.setAlignment(Pos.TOP_LEFT);

        Label lblTitulo = new Label("Central de Exportação de Relatórios em PDF");
        lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Gere documentos oficiais com formatação institucional e carimbos de validação.");
        lblSub.setStyle("-fx-font-size: 14px; -fx-text-fill: -fx-text-secondary;");

        HBox gridCartoes = new HBox(24);

        // Cartão 1: Relatório Geral
        VBox card1 = new VBox(14);
        card1.getStyleClass().add("card");
        card1.setPrefWidth(380);

        Label lblCard1Tit = new Label("Relatório Completo de Inscritos");
        lblCard1Tit.getStyleClass().add("card-title");

        Label lblCard1Desc = new Label("Exporta a lista nominal de todos os candidatos inscritos com ID, BI, Província, Contactos e totais gerais consolidados.");
        lblCard1Desc.getStyleClass().add("card-subtitle");
        lblCard1Desc.setWrapText(true);

        Button btnExportar1 = new Button("📄 Exportar Lista Completa em PDF");
        btnExportar1.getStyleClass().add("button-pdf");
        btnExportar1.setOnAction(e -> exportarPdfGeral());

        card1.getChildren().addAll(lblCard1Tit, lblCard1Desc, new Separator(), btnExportar1);

        // Cartão 2: Comprovativo Individual
        VBox card2 = new VBox(14);
        card2.getStyleClass().add("card");
        card2.setPrefWidth(380);

        Label lblCard2Tit = new Label("Comprovativo Individual de Candidatura");
        lblCard2Tit.getStyleClass().add("card-title");

        Label lblCard2Desc = new Label("Gera o comprovativo oficial com número de inscrição e campos para carimbo da secretaria.");
        lblCard2Desc.getStyleClass().add("card-subtitle");
        lblCard2Desc.setWrapText(true);

        Button btnExportar2 = new Button("📄 Ir para Seleção de Candidatos");
        btnExportar2.getStyleClass().add("button-primary");
        btnExportar2.setOnAction(e -> mostrarGestaoCandidatos());

        card2.getChildren().addAll(lblCard2Tit, lblCard2Desc, new Separator(), btnExportar2);

        gridCartoes.getChildren().addAll(card1, card2);
        box.getChildren().addAll(lblTitulo, lblSub, gridCartoes);

        areaConteudo.getChildren().setAll(box);
    }

    private void exportarPdfGeral() {
        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Salvar Relatório de Candidatos");
            chooser.setInitialFileName("Relatorio_Candidatos_SIS.pdf");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos PDF (*.pdf)", "*.pdf"));

            File destino = chooser.showSaveDialog(Navegador.getPalcoPrincipal());
            if (destino != null) {
                controller.exportarRelatorioGeralCandidatosPdf(destino);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Relatório Gerado");
                alert.setHeaderText("PDF Exportado com Sucesso!");
                alert.setContentText("Arquivo disponível em:\n" + destino.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao exportar PDF: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void exportarComprovativoSelecionado() {
        if (tabelaCandidatos == null) return;
        Candidato selecionado = tabelaCandidatos.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setContentText("Por favor, selecione um candidato na tabela para emitir o comprovativo.");
            alert.showAndWait();
            return;
        }

        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Salvar Comprovativo de Inscrição");
            chooser.setInitialFileName("Comprovativo_" + selecionado.getNumeroBI() + ".pdf");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos PDF (*.pdf)", "*.pdf"));

            File destino = chooser.showSaveDialog(Navegador.getPalcoPrincipal());
            if (destino != null) {
                controller.exportarComprovativoCandidatoPdf(selecionado, destino);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Comprovativo Gerado");
                alert.setHeaderText("PDF Exportado com Sucesso!");
                alert.setContentText("Comprovativo salvo em:\n" + destino.getAbsolutePath());
                alert.showAndWait();
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setContentText("Erro ao exportar comprovativo: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void mostrarGestaoSecretaria() {
        VBox box = new VBox(20);
        box.setFillWidth(true);

        Label lblTitulo = new Label("Gestão de Utilizadores da Secretaria");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Somente administradores autorizados podem criar e gerir novos acessos para a equipa da secretaria.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");

        HBox layoutGeral = new HBox(20);

        // Tabela à esquerda
        TableView<model.Administrador> tabelaAdmins = new TableView<>();
        tabelaAdmins.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelaAdmins.setPlaceholder(new Label("Nenhum utilizador registado."));

        TableColumn<model.Administrador, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(a -> new SimpleStringProperty(String.valueOf(a.getValue().getId())));
        colId.setMaxWidth(50);

        TableColumn<model.Administrador, String> colNome = new TableColumn<>("Nome Completo");
        colNome.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().getNomeCompleto()));

        TableColumn<model.Administrador, String> colEmail = new TableColumn<>("Email de Acesso");
        colEmail.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().getEmail()));

        TableColumn<model.Administrador, String> colCargo = new TableColumn<>("Cargo");
        colCargo.setCellValueFactory(a -> new SimpleStringProperty(a.getValue().getCargo()));

        tabelaAdmins.getColumns().addAll(colId, colNome, colEmail, colCargo);
        tabelaAdmins.setItems(FXCollections.observableArrayList(loginController.listarUsuariosSecretaria()));
        HBox.setHgrow(tabelaAdmins, Priority.ALWAYS);

        // Formulário à direita
        VBox formAdmin = new VBox(12);
        formAdmin.getStyleClass().add("card");
        formAdmin.setPrefWidth(350);

        Label lblNovoTit = new Label("Novo Acesso de Secretaria");
        lblNovoTit.getStyleClass().add("card-title");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome completo");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email institucional");

        PasswordField txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha de acesso");

        TextField txtSenhaVisivel = new TextField();
        txtSenhaVisivel.setPromptText("Senha de acesso");
        txtSenhaVisivel.setVisible(false);
        txtSenhaVisivel.setManaged(false);
        txtSenha.textProperty().bindBidirectional(txtSenhaVisivel.textProperty());

        Button btnOlho = new Button("👁");
        btnOlho.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px; -fx-padding: 2px 6px;");
        btnOlho.setOnAction(ev -> {
            boolean mostrar = !txtSenhaVisivel.isVisible();
            txtSenha.setVisible(!mostrar);
            txtSenha.setManaged(!mostrar);
            txtSenhaVisivel.setVisible(mostrar);
            txtSenhaVisivel.setManaged(mostrar);
        });

        HBox boxSenhaAdmin = new HBox(4, txtSenha, txtSenhaVisivel, btnOlho);
        boxSenhaAdmin.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        HBox.setHgrow(txtSenha, Priority.ALWAYS);
        HBox.setHgrow(txtSenhaVisivel, Priority.ALWAYS);
        txtSenha.setMaxWidth(Double.MAX_VALUE);
        txtSenhaVisivel.setMaxWidth(Double.MAX_VALUE);

        TextField txtCargo = new TextField();
        txtCargo.setPromptText("Cargo (ex: Secretaria Geral)");

        Button btnCriar = new Button("Cadastrar Utilizador");
        btnCriar.getStyleClass().add("button-primary");
        btnCriar.setMaxWidth(Double.MAX_VALUE);

        btnCriar.setOnAction(e -> {
            try {
                String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
                String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
                String senha = txtSenha.isVisible() ? txtSenha.getText() : txtSenhaVisivel.getText();
                String cargo = txtCargo.getText() != null ? txtCargo.getText().trim() : "Secretaria";

                if (nome.isEmpty() || email.isEmpty() || senha == null || senha.isEmpty()) {
                    throw new IllegalArgumentException("Preencha todos os campos obrigatórios.");
                }

                model.Administrador novo = new model.Administrador(nome, email, senha, cargo);
                loginController.cadastrarUsuarioSecretaria(novo);

                tabelaAdmins.setItems(FXCollections.observableArrayList(loginController.listarUsuariosSecretaria()));
                txtNome.clear();
                txtEmail.clear();
                txtSenha.clear();
                txtCargo.clear();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setContentText("Utilizador de secretaria cadastrado com sucesso!");
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setContentText("Não foi possível cadastrar: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        formAdmin.getChildren().addAll(
                lblNovoTit,
                new Label("Nome Completo:"), txtNome,
                new Label("Email de Login:"), txtEmail,
                new Label("Senha:"), boxSenhaAdmin,
                new Label("Cargo:"), txtCargo,
                btnCriar
        );

        layoutGeral.getChildren().addAll(tabelaAdmins, formAdmin);
        box.getChildren().addAll(lblTitulo, lblSub, layoutGeral);
        areaConteudo.getChildren().setAll(box);
    }
}
