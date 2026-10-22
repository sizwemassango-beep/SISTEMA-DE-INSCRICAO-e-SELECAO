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
import model.Inscricao;
import model.QuestionarioCarencia;
import model.StatusInscricao;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**

 * Representa a interface visual relacionada a TelaDashboardSecretariaFX.

 */

public class TelaDashboardSecretariaFX extends BorderPane {

    private final PainelController controller;
    private final InscricaoController inscricaoController;
    private final controller.LoginController loginController;

    private StackPane areaConteudo;
    private ObservableList<Candidato> listaCandidatosObservable;
    private TableView<Candidato> tabelaCandidatos;
    private TextField txtBusca;

    private Button btnMenuPainel;
    private Button btnMenuCandidatos;
    private Button btnMenuCursos;
    private final model.Administrador usuarioLogado;
    private Button btnMenuListaEspera;
    private Button btnMenuAdmitidos;
    private Button btnMenuNaoAdmitidos;
    private Button btnMenuUsuarios;
    private Button btnMenuPdf;

    public TelaDashboardSecretariaFX() {
        this(Navegador.getAdministradorLogado());
    }

    public TelaDashboardSecretariaFX(model.Administrador usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
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

        String perfilTexto = "PAINEL ACADÉMICO";
        if (usuarioLogado != null) {
            String cargoNome = usuarioLogado.getCargo() != null ? usuarioLogado.getCargo() : (usuarioLogado.isAdministrador() ? "Administrador" : "Secretaria");
            perfilTexto = usuarioLogado.isAdministrador() ? "🛡 " + cargoNome.toUpperCase() : "📋 " + cargoNome.toUpperCase();
        }
        Label lblPerfil = new Label(perfilTexto);
        lblPerfil.setStyle("-fx-font-size: 9.5px; -fx-font-weight: 700; -fx-text-fill: #2DD4BF; -fx-letter-spacing: 0.5px;");
        titulosSidebar.getChildren().addAll(lblMarca, lblPerfil);

        if (usuarioLogado != null && usuarioLogado.getNomeCompleto() != null) {
            Label lblUsuarioNome = new Label(usuarioLogado.getNomeCompleto());
            lblUsuarioNome.setStyle("-fx-font-size: 10px; -fx-text-fill: #CBD5E1; -fx-font-weight: 600;");
            titulosSidebar.getChildren().add(lblUsuarioNome);
        }

        boxMarca.getChildren().addAll(logoSidebar, titulosSidebar);

        Label lblSecGeral = new Label("GESTÃO");
        lblSecGeral.getStyleClass().add("sidebar-section-header");

        btnMenuPainel = criarBotaoMenu("📊 Visão Geral", true, this::mostrarVisaoGeral);
        btnMenuCandidatos = criarBotaoMenu("👥 Candidatos", false, this::mostrarGestaoCandidatos);
        btnMenuCursos = criarBotaoMenu("📚 Cursos & Vagas", false, this::mostrarGestaoCursos);

        Label lblSecSelecao = new Label("PROCESSO SELETIVO");
        lblSecSelecao.getStyleClass().add("sidebar-section-header");

        btnMenuListaEspera = criarBotaoMenu("⏳ Lista de Espera", false, this::mostrarListaEspera);
        btnMenuAdmitidos = criarBotaoMenu("🎓 Admitidos por Carência", false, this::mostrarAdmitidosCarencia);
        btnMenuNaoAdmitidos = criarBotaoMenu("❌ Não Admitidos", false, this::mostrarNaoAdmitidos);

        Label lblSecAdmin = new Label("ADMINISTRAÇÃO");
        lblSecAdmin.getStyleClass().add("sidebar-section-header");

        boolean podeGerirUsuarios = usuarioLogado == null || usuarioLogado.isAdministrador();
        if (podeGerirUsuarios) {
            btnMenuUsuarios = criarBotaoMenu("🛡 Utilizadores", false, this::mostrarGestaoSecretaria);
        }
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
                lblSecSelecao,
                btnMenuListaEspera,
                btnMenuAdmitidos,
                btnMenuNaoAdmitidos,
                lblSecAdmin
        );

        if (podeGerirUsuarios && btnMenuUsuarios != null) {
            sidebar.getChildren().add(btnMenuUsuarios);
        }

        sidebar.getChildren().addAll(
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
        for (Button b : new Button[]{btnMenuPainel, btnMenuCandidatos, btnMenuCursos, btnMenuListaEspera, btnMenuAdmitidos, btnMenuUsuarios, btnMenuPdf}) {
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

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titulos = new VBox(2);
        Label lblTitulo = new Label("Balanço e Visão Geral");
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

        HBox layoutColunas = new HBox(22);
        layoutColunas.setAlignment(Pos.TOP_LEFT);

        VBox colunaEsquerda = new VBox(16);
        colunaEsquerda.setPrefWidth(310);
        colunaEsquerda.setMinWidth(290);

        int totalCandidatos = controller.obterTotalCandidatos();
        int totalCursos = controller.obterTotalCursos();
        int totalVagas = controller.obterTotalVagas();
        double concorrencia = controller.obterConcorrenciaGeral();

        VBox cardCand = criarCardMetodoMockup(
                "CANDIDATURAS",
                "👥 Total de Inscritos",
                String.valueOf(totalCandidatos),
                "Candidatos registados",
                "Ver Candidatos →",
                this::mostrarGestaoCandidatos
        );

        VBox cardCur = criarCardMetodoMockup(
                "CURSOS ATIVOS",
                "📚 Cursos em Oferta",
                String.valueOf(totalCursos),
                "Cursos preparatórios",
                "Gerir Cursos →",
                this::mostrarGestaoCursos
        );

        VBox cardVagas = criarCardMetodoMockup(
                "VAGAS & DEMANDA",
                "🎯 Vagas Disponíveis",
                String.valueOf(totalVagas),
                String.format("%.2f cand/vaga", concorrencia),
                "Emitir Balanço →",
                this::exportarPdfGeral
        );

        colunaEsquerda.getChildren().addAll(cardCand, cardCur, cardVagas);

        VBox colunaDireita = new VBox(18);
        HBox.setHgrow(colunaDireita, Priority.ALWAYS);

        HBox linhaGraficos = new HBox(18);

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

                String textoStatus = "Pendente";
                String classeStatus = "badge-status-pendente";

                var optIns = inscricaoController.buscarInscricaoPorCandidato(c.getId());
                if (optIns.isPresent()) {
                    model.StatusInscricao st = optIns.get().getStatus();
                    if (st == model.StatusInscricao.APROVADO) {
                        textoStatus = "Aprovado";
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

    private void mostrarGestaoCandidatos() {
        VBox box = new VBox(18);
        box.setFillWidth(true);

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titulos = new VBox(4);
        Label lblTitulo = new Label("Base de Dados de Candidatos");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Consulte, filtre e analise os dados de carência para decisão de admissão");
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
        tabelaCandidatos.setPrefHeight(240);

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

        TableColumn<Candidato, String> colStatus = new TableColumn<>("Estado");
        colStatus.setCellValueFactory(c -> {
            var opt = inscricaoController.buscarInscricaoPorCandidato(c.getValue().getId());
            return new SimpleStringProperty(opt.map(i -> i.getStatus().getDescricao()).orElse("Pendente"));
        });

        tabelaCandidatos.getColumns().addAll(colId, colNome, colBI, colProvincia, colTelefone, colEmail, colStatus);

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

        // Caixa completa contendo: Dados do Candidato, Questionário de Carência, Pontos Totais, Requisito do Curso e Botões de Decisão
        VBox caixaDetalhesECarencia = new VBox(16);
        caixaDetalhesECarencia.getStyleClass().add("card");
        atualizarCaixaDetalhesCandidato(caixaDetalhesECarencia, null);

        tabelaCandidatos.getSelectionModel().selectedItemProperty().addListener((obs, oldV, novoCand) -> {
            atualizarCaixaDetalhesCandidato(caixaDetalhesECarencia, novoCand);
        });

        box.getChildren().addAll(header, tabelaCandidatos, caixaDetalhesECarencia);

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        areaConteudo.getChildren().setAll(scroll);
    }

    private void atualizarCaixaDetalhesCandidato(VBox container, Candidato candidato) {
        container.getChildren().clear();

        if (candidato == null) {
            Label lblVazio = new Label("ℹ Seleccione um candidato na tabela acima para visualizar os dados completos, o questionário de carência, pontos totais e tomar decisões de admissão.");
            lblVazio.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary; -fx-padding: 24px; -fx-alignment: center;");
            container.getChildren().add(lblVazio);
            return;
        }

        Inscricao inscricao = inscricaoController.buscarInscricaoPorCandidato(candidato.getId()).orElse(null);
        QuestionarioCarencia carencia = inscricaoController.buscarQuestionarioPorCandidato(candidato.getId());
        Curso curso = null;
        if (inscricao != null && inscricao.getCursoId() != null) {
            curso = controller.getDashboardService().getCursoDao().buscarId(inscricao.getCursoId()).orElse(null);
        }

        int pontosCarencia = carencia != null ? carencia.calcularPontuacao() : 0;
        int requisitoCurso = curso != null ? curso.getPontosMinimosCarencia() : 50;
        boolean cumpreCarencia = pontosCarencia >= requisitoCurso;

        // Cabeçalho da Caixa de Informações
        HBox headerCaixa = new HBox(12);
        headerCaixa.setAlignment(Pos.CENTER_LEFT);

        VBox titulosCaixa = new VBox(3);
        Label lblNomeCand = new Label("Ficha de Candidatura: " + candidato.getNomeCompleto() + " (BI: " + candidato.getNumeroBI() + ")");
        lblNomeCand.setStyle("-fx-font-size: 16px; -fx-font-weight: 800; -fx-text-fill: -fx-text-primary;");

        Label lblCursoInfo = new Label("Curso: " + (inscricao != null && inscricao.getCursoNome() != null ? inscricao.getCursoNome() : "Não selecionado")
                + " • Requisito de Carência do Curso: " + requisitoCurso + " pontos mínimos");
        lblCursoInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: -fx-text-secondary;");
        titulosCaixa.getChildren().addAll(lblNomeCand, lblCursoInfo);

        Region espHeader = new Region();
        HBox.setHgrow(espHeader, Priority.ALWAYS);

        StatusInscricao st = inscricao != null ? inscricao.getStatus() : StatusInscricao.PENDENTE;
        Label badgeStatus = new Label(st.getDescricao().toUpperCase());
        if (st == StatusInscricao.APROVADO) {
            badgeStatus.setStyle("-fx-background-color: #10B981; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5px 12px; -fx-background-radius: 12px;");
        } else if (st == StatusInscricao.NAO_APROVADO) {
            badgeStatus.setStyle("-fx-background-color: #EF4444; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5px 12px; -fx-background-radius: 12px;");
        } else if (st == StatusInscricao.LISTA_ESPERA) {
            badgeStatus.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5px 12px; -fx-background-radius: 12px;");
        } else {
            badgeStatus.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 5px 12px; -fx-background-radius: 12px;");
        }

        headerCaixa.getChildren().addAll(titulosCaixa, espHeader, badgeStatus);

        // Se o candidato tiver sido recusado, exibir o motivo da não admissão com destaque
        VBox boxMotivoNaoAdmissao = new VBox(4);
        if (st == StatusInscricao.NAO_APROVADO) {
            boxMotivoNaoAdmissao.setStyle("-fx-background-color: #FEF2F2; -fx-border-color: #EF4444; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10px 14px;");
            Label lblMotivoTit = new Label("✖ Motivo da Não Admissão Registado:");
            lblMotivoTit.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #991B1B;");
            String mot = (inscricao != null && inscricao.getMotivoRejeicao() != null && !inscricao.getMotivoRejeicao().trim().isEmpty())
                    ? inscricao.getMotivoRejeicao()
                    : "Candidatura não classificada.";
            Label lblMotivoTxt = new Label(mot);
            lblMotivoTxt.setStyle("-fx-font-size: 13px; -fx-text-fill: #7F1D1D; -fx-font-weight: 600;");
            lblMotivoTxt.setWrapText(true);
            boxMotivoNaoAdmissao.getChildren().addAll(lblMotivoTit, lblMotivoTxt);
        }

        // Grid em 3 colunas com todos os dados:
        // Coluna 1: Dados Pessoais & 12ª Classe
        // Coluna 2: Questionário de Carência & Pontos Totais
        // Coluna 3: Documentos Submetidos & Carta de Motivação
        HBox gridColunas = new HBox(18);
        gridColunas.setAlignment(Pos.TOP_LEFT);

        // 1. Dados Pessoais & Habilitações
        VBox col1 = new VBox(8);
        col1.setStyle("-fx-background-color: rgba(148, 163, 184, 0.06); -fx-padding: 12px; -fx-background-radius: 10px;");
        HBox.setHgrow(col1, Priority.ALWAYS);

        Label titCol1 = new Label("👤 Dados Pessoais & 12ª Classe");
        titCol1.setStyle("-fx-font-weight: 700; -fx-font-size: 12.5px; -fx-text-fill: -fx-text-primary;");

        GridPane grid1 = new GridPane();
        grid1.setHgap(10);
        grid1.setVgap(6);

        String dataNasc = "-";
        if (candidato.getDataDeNascimento() != null) {
            int idade = Period.between(candidato.getDataDeNascimento(), LocalDate.now()).getYears();
            dataNasc = candidato.getDataDeNascimento() + " (" + idade + " anos)";
        }

        double nota12Val = (inscricao != null && inscricao.getNota12() > 0) ? inscricao.getNota12() : (inscricao != null ? inscricao.getNota() : 0.0);
        String nota12Txt = nota12Val > 0 ? String.format("%.1f / 20.0 valores", nota12Val) : "—";

        adicionarLinhaGrid(grid1, 0, "BI (Moçambique):", candidato.getNumeroBI());
        adicionarLinhaGrid(grid1, 1, "Nascimento / Idade:", dataNasc);
        adicionarLinhaGrid(grid1, 2, "Telefone:", candidato.getTelefone());
        adicionarLinhaGrid(grid1, 3, "Email:", candidato.getEmail());
        adicionarLinhaGrid(grid1, 4, "Província:", candidato.getProvincia());
        adicionarLinhaGrid(grid1, 5, "Distrito / Morada:", (candidato.getDistrito() != null ? candidato.getDistrito() : "") + " " + (candidato.getMorada() != null ? candidato.getMorada() : ""));

        Label lblNotaDestaque = new Label(nota12Txt);
        lblNotaDestaque.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: #1E3A8A;");
        grid1.add(new Label("Nota da 12ª Classe:"), 0, 6);
        grid1.add(lblNotaDestaque, 1, 6);

        col1.getChildren().addAll(titCol1, new Separator(), grid1);

        // 2. Questionário de Carência & Pontos Totais
        VBox col2 = new VBox(8);
        col2.setStyle("-fx-background-color: rgba(148, 163, 184, 0.06); -fx-padding: 12px; -fx-background-radius: 10px;");
        HBox.setHgrow(col2, Priority.ALWAYS);

        Label titCol2 = new Label("📊 Questionário de Carência");
        titCol2.setStyle("-fx-font-weight: 700; -fx-font-size: 12.5px; -fx-text-fill: -fx-text-primary;");

        GridPane grid2 = new GridPane();
        grid2.setHgap(10);
        grid2.setVgap(6);

        if (carencia != null) {
            adicionarLinhaGrid(grid2, 0, "Rendimento:", carencia.getRendimentoFamiliar() != null ? carencia.getRendimentoFamiliar().getDescricao() : "-");
            adicionarLinhaGrid(grid2, 1, "Situação Laboral:", carencia.getSituacaoLaboral() != null ? carencia.getSituacaoLaboral().getDescricao() : "-");
            adicionarLinhaGrid(grid2, 2, "Distância:", carencia.getDistanciaKm() + " km");
            adicionarLinhaGrid(grid2, 3, "Órfão / Chefe Família:", carencia.isOrfaoOuChefeFamilia() ? "Sim (+15 pts)" : "Não");
            adicionarLinhaGrid(grid2, 4, "Apoio Governamental:", carencia.isRecebeApoioFinanceiro() ? "Sim" : "Não");

            Label lblPontos = new Label(pontosCarencia + " / 100 pontos (" + carencia.classificar().getDescricao() + ")");
            lblPontos.setStyle("-fx-font-weight: 800; -fx-font-size: 13px; -fx-text-fill: " + (cumpreCarencia ? "#15803D" : "#B91C1C") + ";");
            grid2.add(new Label("Pontos de Carência:"), 0, 5);
            grid2.add(lblPontos, 1, 5);

            Label lblReq = new Label(requisitoCurso + " pontos mínimos");
            lblReq.setStyle("-fx-font-weight: 700; -fx-text-fill: -fx-text-primary;");
            grid2.add(new Label("Requisito do Curso:"), 0, 6);
            grid2.add(lblReq, 1, 6);

            Label badgeCarencia = new Label(cumpreCarencia ? "✔ CUMPRE O REQUISITO" : "✖ ABAIXO DO REQUISITO");
            badgeCarencia.setStyle(
                    "-fx-font-weight: 800; -fx-font-size: 10px; -fx-padding: 3px 8px; -fx-background-radius: 6px; "
                    + (cumpreCarencia ? "-fx-background-color: #DCFCE7; -fx-text-fill: #166534;" : "-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;")
            );
            grid2.add(new Label("Avaliação Carência:"), 0, 7);
            grid2.add(badgeCarencia, 1, 7);

        } else {
            Label lblSemQ = new Label("Nenhum questionário respondido.");
            lblSemQ.setStyle("-fx-text-fill: -fx-text-secondary; -fx-font-style: italic;");
            grid2.add(lblSemQ, 0, 0, 2, 1);
        }

        col2.getChildren().addAll(titCol2, new Separator(), grid2);

        // 3. Documentos Submetidos & Carta de Motivação
        VBox col3 = new VBox(8);
        col3.setStyle("-fx-background-color: rgba(148, 163, 184, 0.06); -fx-padding: 12px; -fx-background-radius: 10px;");
        HBox.setHgrow(col3, Priority.ALWAYS);

        Label titCol3 = new Label("📎 Documentos & Motivação");
        titCol3.setStyle("-fx-font-weight: 700; -fx-font-size: 12.5px; -fx-text-fill: -fx-text-primary;");

        GridPane grid3 = new GridPane();
        grid3.setHgap(10);
        grid3.setVgap(6);

        adicionarLinhaGrid(grid3, 0, "Doc. Identificação (BI):", (inscricao != null && inscricao.getDocIdentificacao() != null) ? "✔ Anexado" : "— Pendente");
        adicionarLinhaGrid(grid3, 1, "Certificado 10ª:", (inscricao != null && inscricao.getDocCertificado10() != null) ? "✔ Anexado" : "— Pendente");
        adicionarLinhaGrid(grid3, 2, "Comprovativo 12ª:", (inscricao != null && inscricao.getDocCertificado12OuRecibo() != null) ? "✔ Anexado" : "— Pendente");

        String txtAtestado = (inscricao != null && inscricao.getDocAtestadoPobreza() != null) ? "✔ Anexado" : "— Não apresentado (caso tenha)";
        adicionarLinhaGrid(grid3, 3, "Atestado de Pobreza:", txtAtestado);

        String txtCarta = (inscricao != null && inscricao.getCartaMotivacao() != null && !inscricao.getCartaMotivacao().trim().isEmpty())
                ? "✔ Redigida (" + inscricao.getCartaMotivacao().length() + " caracteres)"
                : "— Não redigida";
        adicionarLinhaGrid(grid3, 4, "Carta de Motivação:", txtCarta);

        boolean docsValidados = inscricao != null && inscricao.isDocumentosValidados();
        Label lblValidados = new Label(docsValidados ? "✔ Documentos Validados" : "⏳ Em Verificação");
        lblValidados.setStyle("-fx-font-weight: 700; -fx-text-fill: " + (docsValidados ? "#15803D" : "#D97706") + ";");
        grid3.add(new Label("Estado dos Documentos:"), 0, 5);
        grid3.add(lblValidados, 1, 5);

        col3.getChildren().addAll(titCol3, new Separator(), grid3);

        gridColunas.getChildren().addAll(col1, col2, col3);

        // Obtenção das cotas do curso e notas para apoio à decisão da secretaria
        Curso cursoInsc = inscricao != null ? inscricao.getCurso() : null;
        if (cursoInsc == null && inscricao != null && inscricao.getCursoId() != null) {
            cursoInsc = controller.listarCursos().stream()
                    .filter(c -> c.getId().equals(inscricao.getCursoId()))
                    .findFirst().orElse(null);
        }

        int totalVagas = cursoInsc != null ? cursoInsc.getNumeroVagas() : 0;
        int vagasMerito = cursoInsc != null ? cursoInsc.getVagasMerito() : 0;
        int vagasCarencia = cursoInsc != null ? cursoInsc.getVagasNecessidade() : 0;
        int ocupadasMerito = (cursoInsc != null && cursoInsc.getId() != null)
                ? inscricaoController.contarAprovadosPorCursoETipo(cursoInsc.getId(), "MERITO") : 0;
        int ocupadasCarencia = (cursoInsc != null && cursoInsc.getId() != null)
                ? inscricaoController.contarAprovadosPorCursoETipo(cursoInsc.getId(), "CARENCIA") : 0;
        double nota12MinCurso = (cursoInsc != null && cursoInsc.getId() != null)
                ? inscricaoController.buscarNota12MaisBaixaPorCurso(cursoInsc.getId()) : 0.0;
        double nota12Candidato = inscricao != null ? inscricao.getNota12() : 0.0;

        HBox boxQuotas = new HBox(16);
        boxQuotas.setStyle("-fx-background-color: rgba(59, 130, 246, 0.08); -fx-padding: 10px 14px; -fx-background-radius: 8px; -fx-border-color: #93C5FD; -fx-border-radius: 8px;");
        boxQuotas.setAlignment(Pos.CENTER_LEFT);

        Label lblQMerito = new Label(String.format("🏆 Vagas Mérito: %d / %d preenchidas", ocupadasMerito, vagasMerito));
        lblQMerito.setStyle("-fx-font-weight: bold; -fx-font-size: 11.5px; -fx-text-fill: " + (ocupadasMerito >= vagasMerito ? "#EF4444" : "#1D4ED8") + ";");

        Label lblQCarencia = new Label(String.format("🤝 Vagas Carência: %d / %d preenchidas", ocupadasCarencia, vagasCarencia));
        lblQCarencia.setStyle("-fx-font-weight: bold; -fx-font-size: 11.5px; -fx-text-fill: " + (ocupadasCarencia >= vagasCarencia ? "#EF4444" : "#15803D") + ";");

        Label lblNotaMin = new Label(String.format("📊 Nota 12ª mais baixa no curso: %.1f val (Candidato: %.1f val)", nota12MinCurso, nota12Candidato));
        lblNotaMin.setStyle("-fx-font-weight: bold; -fx-font-size: 11.5px; -fx-text-fill: #334155;");

        boxQuotas.getChildren().addAll(lblQMerito, new Separator(javafx.geometry.Orientation.VERTICAL), lblQCarencia, new Separator(javafx.geometry.Orientation.VERTICAL), lblNotaMin);

        // BOX DE BOTÕES DE APROVAÇÃO E RECUSA DENTRO DA CAIXA DE INFORMAÇÕES
        HBox boxBotoes = new HBox(10);
        boxBotoes.setAlignment(Pos.CENTER_LEFT);
        boxBotoes.setPadding(new Insets(10, 0, 0, 0));

        Button btnAprovarMerito = new Button(String.format("🏆 Aprovar por Mérito (%d/%d)", ocupadasMerito, vagasMerito));
        btnAprovarMerito.setStyle("-fx-background-color: #2563EB; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px 14px; -fx-cursor: hand;");

        Button btnAprovarCarencia = new Button(String.format("🤝 Aprovar por Carência (%d/%d)", ocupadasCarencia, vagasCarencia));
        btnAprovarCarencia.setStyle("-fx-background-color: #059669; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px 14px; -fx-cursor: hand;");

        Button btnListaEspera = new Button("📋 Lista de Espera");
        btnListaEspera.setStyle("-fx-background-color: #475569; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px 14px; -fx-cursor: hand;");

        Button btnRejeitar = new Button("✖ Recusar com Motivo");
        btnRejeitar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px 14px; -fx-cursor: hand;");

        Button btnVerDocs = new Button("📁 Ver Documentos & Carta");
        btnVerDocs.getStyleClass().add("button-secondary");

        Button btnValidarDocs = new Button("✔ Validar Documentos");
        btnValidarDocs.setStyle("-fx-background-color: #0D9488; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 9px 14px; -fx-cursor: hand;");

        final int fVagasMerito = vagasMerito;
        final int fOcupadasMerito = ocupadasMerito;
        final int fVagasCarencia = vagasCarencia;
        final int fOcupadasCarencia = ocupadasCarencia;

        btnAprovarMerito.setOnAction(e -> {
            if (inscricao == null) {
                mostrarAviso("Inscrição não encontrada para este candidato.");
                return;
            }
            if (fOcupadasMerito >= fVagasMerito && fVagasMerito > 0) {
                mostrarAviso(String.format("🚫 Limite de vagas por Mérito atingido!\nO curso dispõe de %d vaga(s) por Mérito e todas as %d já foram preenchidas.\nNão é possível aprovar mais por esta modalidade.", fVagasMerito, fOcupadasMerito));
                return;
            }
            inscricaoController.aprovarCandidato(inscricao.getId(), "MERITO");
            mostrarSucesso("Candidato " + candidato.getNomeCompleto() + " APROVADO POR MÉRITO!");
            carregarDados();
            mostrarGestaoCandidatos();
        });

        btnAprovarCarencia.setOnAction(e -> {
            if (inscricao == null) {
                mostrarAviso("Inscrição não encontrada para este candidato.");
                return;
            }
            if (fOcupadasCarencia >= fVagasCarencia && fVagasCarencia > 0) {
                mostrarAviso(String.format("🚫 Limite de vagas por Carência atingido!\nO curso dispõe de %d vaga(s) por Carência e todas as %d já foram preenchidas.\nNão é possível aprovar mais por esta modalidade.", fVagasCarencia, fOcupadasCarencia));
                return;
            }
            if (!cumpreCarencia) {
                Alert alertaCarencia = new Alert(Alert.AlertType.CONFIRMATION);
                alertaCarencia.setTitle("Aviso de Requisito de Carência");
                alertaCarencia.setHeaderText("Atenção: Candidato com Carência Inferior ao Requisito!");
                alertaCarencia.setContentText("A pontuação de carência deste candidato (" + pontosCarencia + " pts) é menor do que o requisito definido para o curso (" + requisitoCurso + " pts).\n\nDeseja aprovar por carência excepcionalmente?");
                Optional<ButtonType> res = alertaCarencia.showAndWait();
                if (res.isEmpty() || res.get() != ButtonType.OK) {
                    return;
                }
            }
            inscricaoController.aprovarCandidato(inscricao.getId(), "CARENCIA");
            mostrarSucesso("Candidato " + candidato.getNomeCompleto() + " APROVADO POR CARÊNCIA!");
            carregarDados();
            mostrarGestaoCandidatos();
        });

        btnListaEspera.setOnAction(e -> {
            if (inscricao == null) {
                mostrarAviso("Inscrição não encontrada.");
                return;
            }
            inscricaoController.colocarListaEspera(inscricao.getId());
            mostrarSucesso("Candidato " + candidato.getNomeCompleto() + " colocado na Lista de Espera.");
            carregarDados();
            mostrarGestaoCandidatos();
        });

        btnRejeitar.setOnAction(e -> {
            if (inscricao == null) {
                mostrarAviso("Inscrição não encontrada.");
                return;
            }
            TextInputDialog dlg = new TextInputDialog();
            dlg.setTitle("Recusa de Candidatura");
            dlg.setHeaderText("Registar Motivo da Não Admissão");
            dlg.setContentText("Motivo da não admissão (obrigatório):");
            dlg.getDialogPane().setMinWidth(460);

            Optional<String> resMotivo = dlg.showAndWait();
            if (resMotivo.isPresent()) {
                String motivo = resMotivo.get().trim();
                if (motivo.isEmpty()) {
                    mostrarAviso("O motivo da não admissão é estritamente obrigatório para recusar o candidato.");
                    return;
                }
                inscricaoController.rejeitarCandidato(inscricao.getId(), motivo);
                mostrarSucesso("Candidatura de " + candidato.getNomeCompleto() + " não admitida. Motivo registado: \"" + motivo + "\"");
                carregarDados();
                mostrarGestaoCandidatos();
            }
        });

        btnVerDocs.setOnAction(e -> {
            if (inscricao != null) {
                mostrarDialogDocumentos(inscricao);
            }
        });

        btnValidarDocs.setOnAction(e -> {
            if (inscricao == null) {
                mostrarAviso("Inscrição não encontrada.");
                return;
            }
            inscricaoController.validarDocumentosInscricao(inscricao.getId(), true);
            mostrarSucesso("Documentos validados com sucesso!");
            carregarDados();
            mostrarGestaoCandidatos();
        });

        boxBotoes.getChildren().addAll(btnAprovarMerito, btnAprovarCarencia, btnListaEspera, btnRejeitar, btnVerDocs, btnValidarDocs);

        container.getChildren().addAll(headerCaixa);
        if (st == StatusInscricao.NAO_APROVADO) {
            container.getChildren().add(boxMotivoNaoAdmissao);
        }
        container.getChildren().addAll(new Separator(), gridColunas, boxQuotas, new Separator(), boxBotoes);
    }

    private void adicionarLinhaGrid(GridPane grid, int linha, String rotulo, String valor) {
        Label lblR = new Label(rotulo);
        lblR.setStyle("-fx-font-size: 11.5px; -fx-text-fill: -fx-text-secondary; -fx-font-weight: 600;");

        Label lblV = new Label(valor != null && !valor.isEmpty() ? valor : "-");
        lblV.setStyle("-fx-font-size: 12px; -fx-text-fill: -fx-text-primary;");
        lblV.setWrapText(true);

        grid.add(lblR, 0, linha);
        grid.add(lblV, 1, linha);
    }

    private void mostrarDialogDocumentos(model.Inscricao inscricao) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Documentos Submetidos & Carta de Motivação");
        dialog.setHeaderText("Candidato: " + (inscricao.getCandidato() != null ? inscricao.getCandidato().getNomeCompleto() : "—"));
        dialog.getDialogPane().setMinWidth(550);

        VBox conteudo = new VBox(12);
        conteudo.setPadding(new Insets(12));

        adicionarLinhaDocumento(conteudo, "Documento de Identificação (BI):", inscricao.getDocIdentificacao());
        adicionarLinhaDocumento(conteudo, "Certificado 10ª Classe:", inscricao.getDocCertificado10());
        adicionarLinhaDocumento(conteudo,
                (inscricao.getTipoComprovativo12() != null ? inscricao.getTipoComprovativo12() : "Doc. 12ª Classe") + ":",
                inscricao.getDocCertificado12OuRecibo());
        adicionarLinhaDocumento(conteudo, "Atestado de Pobreza (caso tenha):", inscricao.getDocAtestadoPobreza());

        if (inscricao.getCartaMotivacao() != null && !inscricao.getCartaMotivacao().isEmpty()) {
            Label lblTitMot = new Label("Carta de Motivação (Obrigatória):");
            lblTitMot.setStyle("-fx-font-weight: 700; -fx-text-fill: -fx-text-primary;");
            TextArea txtMot = new TextArea(inscricao.getCartaMotivacao());
            txtMot.setEditable(false);
            txtMot.setWrapText(true);
            txtMot.setPrefRowCount(5);
            conteudo.getChildren().addAll(new Separator(), lblTitMot, txtMot);
        } else {
            Label lblSemMot = new Label("Carta de Motivação: Não apresentada.");
            lblSemMot.setStyle("-fx-text-fill: #EF4444; -fx-font-style: italic;");
            conteudo.getChildren().add(lblSemMot);
        }

        Label lblStatus = new Label("Documentos validados pela secretaria: " + (inscricao.isDocumentosValidados() ? "Sim ✔" : "Não ✖"));
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
        lbl.setStyle("-fx-font-weight: 600; -fx-min-width: 230px; -fx-text-fill: -fx-text-primary;");

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

    // =========================================================================
    // 3. GESTÃO DE CURSOS & VAGAS (COM DEFINIÇÃO DE REQUISITO DE CARÊNCIA)
    // =========================================================================
    private void mostrarGestaoCursos() {
        VBox box = new VBox(20);
        box.setFillWidth(true);

        Label lblTitulo = new Label("Gestão de Cursos, Vagas & Requisitos de Carência");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Defina as vagas de cada curso e o requisito mínimo de pontos de carência social para aprovação.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");

        HBox layoutCursos = new HBox(20);

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

        TableColumn<Curso, String> colMinCarencia = new TableColumn<>("Min. Pontos Carência");
        colMinCarencia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPontosMinimosCarencia() + " pts"));

        TableColumn<Curso, String> colNota12Min = new TableColumn<>("Nota 12ª Mínima");
        colNota12Min.setCellValueFactory(c -> {
            double min = inscricaoController.buscarNota12MaisBaixaPorCurso(c.getValue().getId());
            return new SimpleStringProperty(min > 0 ? String.format(Locale.US, "%.1f val", min) : "—");
        });

        tabelaCursos.getColumns().addAll(colId, colNome, colVagas, colMerito, colNec, colMinCarencia, colNota12Min);
        tabelaCursos.setItems(FXCollections.observableArrayList(controller.listarCursos()));
        HBox.setHgrow(tabelaCursos, Priority.ALWAYS);

        // Formulário de Cadastro / Atualização de Curso à direita
        VBox formCurso = new VBox(12);
        formCurso.getStyleClass().add("card");
        formCurso.setPrefWidth(360);

        Label lblNovoCurso = new Label("Cadastrar / Atualizar Curso");
        lblNovoCurso.getStyleClass().add("card-title");

        TextField txtNomeCurso = new TextField();
        txtNomeCurso.setPromptText("Nome do curso (ex: Direito)");

        TextField txtDescCurso = new TextField();
        txtDescCurso.setPromptText("Breve descrição");

        TextField txtVagas = new TextField();
        txtVagas.setPromptText("Número de vagas (ex: 50)");

        TextField txtMerito = new TextField("60");
        txtMerito.setPromptText("% Vagas Mérito (ex: 60)");

        TextField txtNec = new TextField("40");
        txtNec.setPromptText("% Vagas Necessidade (ex: 40)");

        TextField txtMinCarencia = new TextField("70");
        txtMinCarencia.setPromptText("Requisito de pontos de carência (ex: 70)");

        Long[] cursoEditandoId = {null};

        tabelaCursos.getSelectionModel().selectedItemProperty().addListener((obs, oldC, c) -> {
            if (c != null) {
                cursoEditandoId[0] = c.getId();
                txtNomeCurso.setText(c.getNome());
                txtDescCurso.setText(c.getDescricao());
                txtVagas.setText(String.valueOf(c.getNumeroVagas()));
                txtMerito.setText(String.format(Locale.US, "%.0f", c.getPercentualMerito()));
                txtNec.setText(String.format(Locale.US, "%.0f", c.getPercentualNecessidade()));
                txtMinCarencia.setText(String.valueOf(c.getPontosMinimosCarencia()));
                lblNovoCurso.setText("Atualizar Curso: " + c.getNome());
            }
        });

        Button btnSalvarCurso = new Button("Salvar Curso & Requisitos");
        btnSalvarCurso.getStyleClass().add("button-primary");
        btnSalvarCurso.setMaxWidth(Double.MAX_VALUE);

        Button btnLimparForm = new Button("Novo Curso");
        btnLimparForm.getStyleClass().add("button-secondary");
        btnLimparForm.setMaxWidth(Double.MAX_VALUE);
        btnLimparForm.setOnAction(e -> {
            cursoEditandoId[0] = null;
            lblNovoCurso.setText("Cadastrar Novo Curso");
            txtNomeCurso.clear();
            txtDescCurso.clear();
            txtVagas.clear();
            txtMerito.setText("60");
            txtNec.setText("40");
            txtMinCarencia.setText("70");
            tabelaCursos.getSelectionModel().clearSelection();
        });

        btnSalvarCurso.setOnAction(e -> {
            try {
                String nome = txtNomeCurso.getText();
                String desc = txtDescCurso.getText();
                int vagas = Integer.parseInt(txtVagas.getText().trim());
                double merito = Double.parseDouble(txtMerito.getText().trim());
                double nec = Double.parseDouble(txtNec.getText().trim());
                int minCar = Integer.parseInt(txtMinCarencia.getText().trim());

                if (nome == null || nome.trim().isEmpty()) {
                    throw new IllegalArgumentException("Nome do curso é obrigatório.");
                }

                Curso c = new Curso();
                c.setId(cursoEditandoId[0]);
                c.setNome(nome.trim());
                c.setDescricao(desc != null ? desc.trim() : "");
                c.setNumeroVagas(vagas);
                c.setPercentualMerito(merito);
                c.setPercentualNecessidade(nec);
                c.setPontosMinimosCarencia(minCar);
                c.setDataInicio(LocalDate.now());
                c.setDataFim(LocalDate.now().plusMonths(3));

                controller.getDashboardService().registrarCurso(c);

                tabelaCursos.setItems(FXCollections.observableArrayList(controller.listarCursos()));
                btnLimparForm.fire();

                mostrarSucesso("Curso " + c.getNome() + " salvo com sucesso! Requisito de carência: " + minCar + " pts.");
            } catch (Exception ex) {
                mostrarAviso("Erro ao salvar curso: " + ex.getMessage());
            }
        });

        formCurso.getChildren().addAll(
                lblNovoCurso,
                new Label("Nome do Curso:"), txtNomeCurso,
                new Label("Descrição:"), txtDescCurso,
                new Label("Vagas Totais:"), txtVagas,
                new Label("% Mérito:"), txtMerito,
                new Label("% Necessidade:"), txtNec,
                new Label("Requisito Mínimo de Pontos de Carência (ex: 70):"), txtMinCarencia,
                btnSalvarCurso,
                btnLimparForm
        );

        layoutCursos.getChildren().addAll(tabelaCursos, formCurso);
        box.getChildren().addAll(lblTitulo, lblSub, layoutCursos);
        areaConteudo.getChildren().setAll(box);
    }

    // =========================================================================
    // 4. LISTA DE ESPERA (COM GRÁFICO DE MÉDIAS E PONTOS DE CARÊNCIA & ORDENAÇÃO)
    // =========================================================================
    public static class ItemCandidaturaView {
        public Candidato candidato;
        public Inscricao inscricao;
        public QuestionarioCarencia carencia;
        public Curso curso;

        public ItemCandidaturaView(Candidato candidato, Inscricao inscricao, QuestionarioCarencia carencia, Curso curso) {
            this.candidato = candidato;
            this.inscricao = inscricao;
            this.carencia = carencia;
            this.curso = curso;
        }

        public String getNome() { return candidato != null ? candidato.getNomeCompleto() : ""; }
        public String getBi() { return candidato != null ? candidato.getNumeroBI() : ""; }
        public String getCursoNome() { return inscricao != null && inscricao.getCursoNome() != null ? inscricao.getCursoNome() : (curso != null ? curso.getNome() : "-"); }
        public int getPontosCarencia() { return carencia != null ? carencia.calcularPontuacao() : 0; }
        public int getRequisitoCurso() { return curso != null ? curso.getPontosMinimosCarencia() : 50; }
        public double getMedia12() { return inscricao != null && inscricao.getNota12() > 0 ? inscricao.getNota12() : (inscricao != null ? inscricao.getNota() : 0.0); }
        public double getDistancia() { return carencia != null ? carencia.getDistanciaKm() : 0.0; }
        public String getProvincia() { return candidato != null && candidato.getProvincia() != null ? candidato.getProvincia() : "-"; }
        public boolean isTemAtestadoPobreza() { return inscricao != null && inscricao.getDocAtestadoPobreza() != null && !inscricao.getDocAtestadoPobreza().isEmpty(); }
        public String getTipoAdmissao() { return inscricao != null && inscricao.getTipoAdmissao() != null ? inscricao.getTipoAdmissao() : ""; }
        public LocalDate getDataInscricao() { return inscricao != null ? inscricao.getDataInscricao() : LocalDate.now(); }
    }

    private List<ItemCandidaturaView> carregarItensPorStatus(StatusInscricao status) {
        List<Inscricao> inscricoes = inscricaoController.listarTodasInscricoes();
        List<ItemCandidaturaView> resultado = new ArrayList<>();

        for (Inscricao ins : inscricoes) {
            if (ins.getStatus() == status) {
                Candidato cand = ins.getCandidato();
                if (cand == null && ins.getCandidato() != null && ins.getCandidato().getId() != null) {
                    cand = controller.getDashboardService().getCandidatoDao().buscarId(ins.getCandidato().getId()).orElse(null);
                }
                QuestionarioCarencia q = cand != null ? inscricaoController.buscarQuestionarioPorCandidato(cand.getId()) : null;
                Curso cur = ins.getCursoId() != null ? controller.getDashboardService().getCursoDao().buscarId(ins.getCursoId()).orElse(null) : null;
                resultado.add(new ItemCandidaturaView(cand, ins, q, cur));
            }
        }
        return resultado;
    }

    private void mostrarListaEspera() {
        VBox box = new VBox(18);
        box.setFillWidth(true);

        Label lblTitulo = new Label("⏳ Lista de Espera de Candidatos");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Candidatos aguardando vagas. Organize por pontos de carência, médias da 12ª, distância e critérios acadêmicos.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");

        List<ItemCandidaturaView> itensEspera = carregarItensPorStatus(StatusInscricao.LISTA_ESPERA);
        ObservableList<ItemCandidaturaView> masterData = FXCollections.observableArrayList(itensEspera);

        // GRÁFICO DE MÉDIAS E PONTOS DE CARÊNCIA
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Candidato");
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Pontuação");

        BarChart<String, Number> chartEspera = new BarChart<>(xAxis, yAxis);
        chartEspera.setTitle("Comparativo: Pontos de Carência (0-100) vs Média 12ª Classe (escala x5)");
        chartEspera.setPrefHeight(270);
        chartEspera.getStyleClass().add("card-clean");

        XYChart.Series<String, Number> serieCarencia = new XYChart.Series<>();
        serieCarencia.setName("Pontos de Carência (0 a 100)");

        XYChart.Series<String, Number> serieMedia12 = new XYChart.Series<>();
        serieMedia12.setName("Média 12ª Classe (Normalizada x5)");

        for (int i = 0; i < Math.min(masterData.size(), 12); i++) {
            ItemCandidaturaView item = masterData.get(i);
            String labelNome = item.getNome().length() > 14 ? item.getNome().substring(0, 12) + ".." : item.getNome();
            serieCarencia.getData().add(new XYChart.Data<>(labelNome, item.getPontosCarencia()));
            serieMedia12.getData().add(new XYChart.Data<>(labelNome, item.getMedia12() * 5.0)); // normalizada para escala 100
        }
        chartEspera.getData().addAll(serieCarencia, serieMedia12);

        // BARRA DE FILTROS & ORDENAÇÃO
        HBox barFiltros = new HBox(12);
        barFiltros.setAlignment(Pos.CENTER_LEFT);

        Label lblOrd = new Label("Organizar por:");
        lblOrd.setStyle("-fx-font-weight: 700; -fx-text-fill: -fx-text-primary;");

        ComboBox<String> cbOrdenacao = new ComboBox<>(FXCollections.observableArrayList(
                "Pontos de Carência (Maior → Menor)",
                "Média 12ª Classe (Maior → Menor)",
                "Distância (Maior → Menor)",
                "Distância (Menor → Maior)",
                "Nome (A-Z)",
                "Data de Inscrição"
        ));
        cbOrdenacao.setValue("Pontos de Carência (Maior → Menor)");

        ComboBox<String> cbFiltroCurso = new ComboBox<>();
        List<String> nomesCursos = new ArrayList<>();
        nomesCursos.add("Todos os Cursos");
        for (Curso c : controller.listarCursos()) nomesCursos.add(c.getNome());
        cbFiltroCurso.setItems(FXCollections.observableArrayList(nomesCursos));
        cbFiltroCurso.setValue("Todos os Cursos");

        TextField txtPesquisa = new TextField();
        txtPesquisa.setPromptText("🔍 Buscar por Nome ou BI...");
        txtPesquisa.setPrefWidth(220);

        Region espFiltros = new Region();
        HBox.setHgrow(espFiltros, Priority.ALWAYS);

        Button btnPromover = new Button("✔ Promover para Aprovado");
        btnPromover.setStyle("-fx-background-color: #10B981; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 14px; -fx-cursor: hand;");

        Button btnRejeitar = new Button("✖ Recusar com Motivo");
        btnRejeitar.setStyle("-fx-background-color: #EF4444; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 14px; -fx-cursor: hand;");

        barFiltros.getChildren().addAll(lblOrd, cbOrdenacao, cbFiltroCurso, txtPesquisa, espFiltros, btnPromover, btnRejeitar);

        // TABELA DE LISTA DE ESPERA
        TableView<ItemCandidaturaView> tabelaEspera = new TableView<>();
        tabelaEspera.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelaEspera.setPlaceholder(new Label("Nenhum candidato em lista de espera no momento."));

        TableColumn<ItemCandidaturaView, String> colNome = new TableColumn<>("Nome Completo");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));

        TableColumn<ItemCandidaturaView, String> colBi = new TableColumn<>("BI");
        colBi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBi()));

        TableColumn<ItemCandidaturaView, String> colCurso = new TableColumn<>("Curso");
        colCurso.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCursoNome()));

        TableColumn<ItemCandidaturaView, String> colPontos = new TableColumn<>("Pontos Carência");
        colPontos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPontosCarencia() + " pts (Min: " + c.getValue().getRequisitoCurso() + ")"));

        TableColumn<ItemCandidaturaView, String> colMedia = new TableColumn<>("Média 12ª");
        colMedia.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.US, "%.1f v.", c.getValue().getMedia12())));

        TableColumn<ItemCandidaturaView, String> colDist = new TableColumn<>("Distância");
        colDist.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.US, "%.1f km", c.getValue().getDistancia())));

        TableColumn<ItemCandidaturaView, String> colAtestado = new TableColumn<>("Atestado Pobreza");
        colAtestado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isTemAtestadoPobreza() ? "✔ Sim" : "— Não"));

        TableColumn<ItemCandidaturaView, String> colProv = new TableColumn<>("Província");
        colProv.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProvincia()));

        tabelaEspera.getColumns().addAll(colNome, colBi, colCurso, colPontos, colMedia, colDist, colAtestado, colProv);

        // Função de aplicação de filtros e ordenação
        Runnable aplicarFiltrosEOrdem = () -> {
            String filtroTexto = txtPesquisa.getText() != null ? txtPesquisa.getText().toLowerCase().trim() : "";
            String cursoSel = cbFiltroCurso.getValue();

            List<ItemCandidaturaView> filtrados = masterData.stream().filter(item -> {
                boolean bateTexto = filtroTexto.isEmpty()
                        || item.getNome().toLowerCase().contains(filtroTexto)
                        || item.getBi().toLowerCase().contains(filtroTexto);
                boolean bateCurso = cursoSel == null || cursoSel.equals("Todos os Cursos")
                        || item.getCursoNome().equalsIgnoreCase(cursoSel);
                return bateTexto && bateCurso;
            }).collect(Collectors.toList());

            String ordem = cbOrdenacao.getValue();
            if ("Pontos de Carência (Maior → Menor)".equals(ordem)) {
                filtrados.sort(Comparator.comparingInt(ItemCandidaturaView::getPontosCarencia).reversed());
            } else if ("Média 12ª Classe (Maior → Menor)".equals(ordem)) {
                filtrados.sort(Comparator.comparingDouble(ItemCandidaturaView::getMedia12).reversed());
            } else if ("Distância (Maior → Menor)".equals(ordem)) {
                filtrados.sort(Comparator.comparingDouble(ItemCandidaturaView::getDistancia).reversed());
            } else if ("Distância (Menor → Maior)".equals(ordem)) {
                filtrados.sort(Comparator.comparingDouble(ItemCandidaturaView::getDistancia));
            } else if ("Nome (A-Z)".equals(ordem)) {
                filtrados.sort(Comparator.comparing(ItemCandidaturaView::getNome, String.CASE_INSENSITIVE_ORDER));
            } else if ("Data de Inscrição".equals(ordem)) {
                filtrados.sort(Comparator.comparing(ItemCandidaturaView::getDataInscricao));
            }

            tabelaEspera.setItems(FXCollections.observableArrayList(filtrados));
        };

        cbOrdenacao.valueProperty().addListener(e -> aplicarFiltrosEOrdem.run());
        cbFiltroCurso.valueProperty().addListener(e -> aplicarFiltrosEOrdem.run());
        txtPesquisa.textProperty().addListener(e -> aplicarFiltrosEOrdem.run());
        aplicarFiltrosEOrdem.run();

        btnPromover.setOnAction(e -> {
            ItemCandidaturaView sel = tabelaEspera.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAviso("Seleccione um candidato na tabela de lista de espera.");
                return;
            }
            inscricaoController.aprovarCandidato(sel.inscricao.getId());
            mostrarSucesso("Candidato " + sel.getNome() + " PROMOVIDO para Aprovado com sucesso!");
            carregarDados();
            mostrarListaEspera();
        });

        btnRejeitar.setOnAction(e -> {
            ItemCandidaturaView sel = tabelaEspera.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAviso("Seleccione um candidato na tabela.");
                return;
            }
            TextInputDialog dlg = new TextInputDialog();
            dlg.setTitle("Recusa de Candidatura");
            dlg.setHeaderText("Registar Motivo da Não Admissão");
            dlg.setContentText("Motivo da não admissão (obrigatório):");
            dlg.getDialogPane().setMinWidth(460);

            Optional<String> res = dlg.showAndWait();
            if (res.isPresent()) {
                String motivo = res.get().trim();
                if (motivo.isEmpty()) {
                    mostrarAviso("O motivo da não admissão é obrigatório para rejeitar o candidato.");
                    return;
                }
                inscricaoController.rejeitarCandidato(sel.inscricao.getId(), motivo);
                mostrarSucesso("Candidatura recusada. Motivo registado: \"" + motivo + "\"");
                carregarDados();
                mostrarListaEspera();
            }
        });

        box.getChildren().addAll(lblTitulo, lblSub, chartEspera, barFiltros, tabelaEspera);

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        areaConteudo.getChildren().setAll(scroll);
    }

    // =========================================================================
    // 5. ADMITIDOS POR CARÊNCIA (COM GRÁFICO DE MÉDIA DE CARÊNCIA & FILTROS)
    // =========================================================================
    private void mostrarAdmitidosCarencia() {
        VBox box = new VBox(18);
        box.setFillWidth(true);

        Label lblTitulo = new Label("🎓 Candidatos Admitidos por Carência Socioeconómica");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Lista de candidatos admitidos que cumprem os requisitos de carência social, com análise gráfica de médias.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");

        List<ItemCandidaturaView> itensAdmitidos = carregarItensPorStatus(StatusInscricao.APROVADO);
        ObservableList<ItemCandidaturaView> masterData = FXCollections.observableArrayList(itensAdmitidos);

        // GRÁFICO DE MÉDIA DE PONTOS DE CARÊNCIA POR CURSO
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Curso");
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Média de Pontos de Carência");

        BarChart<String, Number> chartCarencia = new BarChart<>(xAxis, yAxis);
        chartCarencia.setTitle("Média de Pontos de Carência dos Admitidos por Curso");
        chartCarencia.setPrefHeight(270);
        chartCarencia.getStyleClass().add("card-clean");
        chartCarencia.setLegendVisible(false);

        XYChart.Series<String, Number> serieMediaCarencia = new XYChart.Series<>();

        Map<String, List<ItemCandidaturaView>> porCurso = masterData.stream()
                .collect(Collectors.groupingBy(ItemCandidaturaView::getCursoNome));

        if (porCurso.isEmpty()) {
            serieMediaCarencia.getData().add(new XYChart.Data<>("Sem admitidos", 0));
        } else {
            for (Map.Entry<String, List<ItemCandidaturaView>> entry : porCurso.entrySet()) {
                double media = entry.getValue().stream().mapToInt(ItemCandidaturaView::getPontosCarencia).average().orElse(0.0);
                serieMediaCarencia.getData().add(new XYChart.Data<>(entry.getKey(), media));
            }
        }
        chartCarencia.getData().add(serieMediaCarencia);

        // BARRA DE FILTROS
        HBox barFiltros = new HBox(12);
        barFiltros.setAlignment(Pos.CENTER_LEFT);

        Label lblFiltros = new Label("Filtrar por:");
        lblFiltros.setStyle("-fx-font-weight: 700; -fx-text-fill: -fx-text-primary;");

        ComboBox<String> cbFiltroCurso = new ComboBox<>();
        List<String> nomesCursos = new ArrayList<>();
        nomesCursos.add("Todos os Cursos");
        for (Curso c : controller.listarCursos()) nomesCursos.add(c.getNome());
        cbFiltroCurso.setItems(FXCollections.observableArrayList(nomesCursos));
        cbFiltroCurso.setValue("Todos os Cursos");

        ComboBox<String> cbFiltroNivel = new ComboBox<>(FXCollections.observableArrayList(
                "Todos os Níveis de Carência",
                "Carência Alta (≥ 70 pontos)",
                "Carência Média (40 a 69 pontos)",
                "Com Atestado de Pobreza"
        ));
        cbFiltroNivel.setValue("Todos os Níveis de Carência");

        ComboBox<String> cbFiltroProvincia = new ComboBox<>(FXCollections.observableArrayList(
                "Todas as Províncias", "Maputo Cidade", "Maputo Província", "Gaza", "Inhambane",
                "Sofala", "Manica", "Tete", "Zambézia", "Nampula", "Cabo Delgado", "Niassa"
        ));
        cbFiltroProvincia.setValue("Todas as Províncias");

        TextField txtBusca = new TextField();
        txtBusca.setPromptText("🔍 Buscar por Nome ou BI...");
        txtBusca.setPrefWidth(220);

        Region espFiltros = new Region();
        HBox.setHgrow(espFiltros, Priority.ALWAYS);

        Button btnExportar = new Button("📄 Exportar Comprovativo");
        btnExportar.getStyleClass().add("button-primary");

        barFiltros.getChildren().addAll(lblFiltros, cbFiltroCurso, cbFiltroNivel, cbFiltroProvincia, txtBusca, espFiltros, btnExportar);

        // TABELA DE ADMITIDOS POR CARÊNCIA
        TableView<ItemCandidaturaView> tabelaAdmitidos = new TableView<>();
        tabelaAdmitidos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelaAdmitidos.setPlaceholder(new Label("Nenhum candidato admitido registrado ainda."));

        TableColumn<ItemCandidaturaView, String> colNome = new TableColumn<>("Nome Completo");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));

        TableColumn<ItemCandidaturaView, String> colBi = new TableColumn<>("BI");
        colBi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBi()));

        TableColumn<ItemCandidaturaView, String> colCurso = new TableColumn<>("Curso");
        colCurso.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCursoNome()));

        TableColumn<ItemCandidaturaView, String> colPontos = new TableColumn<>("Pontos Carência");
        colPontos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPontosCarencia() + " pts (Min: " + c.getValue().getRequisitoCurso() + ")"));

        TableColumn<ItemCandidaturaView, String> colMedia = new TableColumn<>("Média 12ª");
        colMedia.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.US, "%.1f v.", c.getValue().getMedia12())));

        TableColumn<ItemCandidaturaView, String> colDist = new TableColumn<>("Distância");
        colDist.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.US, "%.1f km", c.getValue().getDistancia())));

        TableColumn<ItemCandidaturaView, String> colAtestado = new TableColumn<>("Atestado Pobreza");
        colAtestado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isTemAtestadoPobreza() ? "✔ Sim" : "— Não"));

        TableColumn<ItemCandidaturaView, String> colTipoAdmissao = new TableColumn<>("Modalidade");
        colTipoAdmissao.setCellValueFactory(c -> {
            String tipo = c.getValue().getTipoAdmissao();
            if ("MERITO".equalsIgnoreCase(tipo)) return new SimpleStringProperty("🏆 Mérito");
            if ("CARENCIA".equalsIgnoreCase(tipo)) return new SimpleStringProperty("🤝 Carência");
            return new SimpleStringProperty("Aprovado");
        });

        TableColumn<ItemCandidaturaView, String> colProv = new TableColumn<>("Província");
        colProv.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProvincia()));

        tabelaAdmitidos.getColumns().addAll(colNome, colBi, colCurso, colTipoAdmissao, colPontos, colMedia, colDist, colAtestado, colProv);

        // Função de filtro dinâmico
        Runnable aplicarFiltros = () -> {
            String filtroTexto = txtBusca.getText() != null ? txtBusca.getText().toLowerCase().trim() : "";
            String cursoSel = cbFiltroCurso.getValue();
            String nivelSel = cbFiltroNivel.getValue();
            String provSel = cbFiltroProvincia.getValue();

            List<ItemCandidaturaView> filtrados = masterData.stream().filter(item -> {
                boolean bateTexto = filtroTexto.isEmpty()
                        || item.getNome().toLowerCase().contains(filtroTexto)
                        || item.getBi().toLowerCase().contains(filtroTexto);
                boolean bateCurso = cursoSel == null || cursoSel.equals("Todos os Cursos")
                        || item.getCursoNome().equalsIgnoreCase(cursoSel);
                boolean bateProv = provSel == null || provSel.equals("Todas as Províncias")
                        || item.getProvincia().equalsIgnoreCase(provSel);

                boolean bateNivel = true;
                if ("Carência Alta (≥ 70 pontos)".equals(nivelSel)) {
                    bateNivel = item.getPontosCarencia() >= 70;
                } else if ("Carência Média (40 a 69 pontos)".equals(nivelSel)) {
                    bateNivel = item.getPontosCarencia() >= 40 && item.getPontosCarencia() < 70;
                } else if ("Com Atestado de Pobreza".equals(nivelSel)) {
                    bateNivel = item.isTemAtestadoPobreza();
                }

                return bateTexto && bateCurso && bateProv && bateNivel;
            }).collect(Collectors.toList());

            tabelaAdmitidos.setItems(FXCollections.observableArrayList(filtrados));
        };

        cbFiltroCurso.valueProperty().addListener(e -> aplicarFiltros.run());
        cbFiltroNivel.valueProperty().addListener(e -> aplicarFiltros.run());
        cbFiltroProvincia.valueProperty().addListener(e -> aplicarFiltros.run());
        txtBusca.textProperty().addListener(e -> aplicarFiltros.run());
        aplicarFiltros.run();

        btnExportar.setOnAction(e -> {
            ItemCandidaturaView sel = tabelaAdmitidos.getSelectionModel().getSelectedItem();
            if (sel == null || sel.candidato == null) {
                mostrarAviso("Seleccione um candidato admitido para gerar o comprovativo.");
                return;
            }
            try {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Salvar Comprovativo de Admissão");
                chooser.setInitialFileName("Admissao_" + sel.getBi() + ".pdf");
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos PDF (*.pdf)", "*.pdf"));

                File destino = chooser.showSaveDialog(Navegador.getPalcoPrincipal());
                if (destino != null) {
                    controller.exportarComprovativoCandidatoPdf(sel.candidato, destino);
                    mostrarSucesso("Comprovativo oficial salvo com sucesso em:\n" + destino.getAbsolutePath());
                }
            } catch (Exception ex) {
                mostrarAviso("Erro ao exportar: " + ex.getMessage());
            }
        });

        box.getChildren().addAll(lblTitulo, lblSub, chartCarencia, barFiltros, tabelaAdmitidos);

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        areaConteudo.getChildren().setAll(scroll);
    }

    // =========================================================================
    // 6. NÃO ADMITIDOS (COM MOTIVO DA NÃO ADMISSÃO & REABERTURA)
    // =========================================================================
    private void mostrarNaoAdmitidos() {
        VBox box = new VBox(18);
        box.setFillWidth(true);

        Label lblTitulo = new Label("❌ Candidatos Não Admitidos / Recusados");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Lista de candidaturas com admissão recusada, acompanhadas do respectivo motivo registrado obrigatoriamente.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");

        List<ItemCandidaturaView> itensNaoAdmitidos = carregarItensPorStatus(StatusInscricao.NAO_APROVADO);

        HBox barFiltros = new HBox(12);
        barFiltros.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> cbFiltroCurso = new ComboBox<>();
        cbFiltroCurso.getItems().add("Todos os Cursos");
        controller.listarCursos().forEach(c -> cbFiltroCurso.getItems().add(c.getNome()));
        cbFiltroCurso.setValue("Todos os Cursos");

        TextField txtPesquisa = new TextField();
        txtPesquisa.setPromptText("Buscar por nome, BI...");
        txtPesquisa.setPrefWidth(240);

        Label lblTotal = new Label("Total: " + itensNaoAdmitidos.size() + " recusas");
        lblTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #EF4444; -fx-background-color: #FEE2E2; -fx-padding: 6px 12px; -fx-background-radius: 6px;");

        Region espFiltros = new Region();
        HBox.setHgrow(espFiltros, Priority.ALWAYS);

        Button btnReabrir = new Button("🔄 Reabrir Candidatura (Pendente)");
        btnReabrir.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8px 14px; -fx-cursor: hand;");

        Button btnVerDocs = new Button("📁 Ver Detalhes / Docs");
        btnVerDocs.getStyleClass().add("button-secondary");

        barFiltros.getChildren().addAll(new Label("Filtrar Curso:"), cbFiltroCurso, txtPesquisa, espFiltros, lblTotal, btnReabrir, btnVerDocs);

        TableView<ItemCandidaturaView> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabela.setPlaceholder(new Label("Nenhum candidato na lista de não admitidos."));

        TableColumn<ItemCandidaturaView, String> colNome = new TableColumn<>("Candidato");
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));

        TableColumn<ItemCandidaturaView, String> colBi = new TableColumn<>("BI");
        colBi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBi()));

        TableColumn<ItemCandidaturaView, String> colCurso = new TableColumn<>("Curso");
        colCurso.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCursoNome()));

        TableColumn<ItemCandidaturaView, String> colNota12 = new TableColumn<>("Nota 12ª");
        colNota12.setCellValueFactory(c -> new SimpleStringProperty(String.format(Locale.US, "%.1f val", c.getValue().getMedia12())));

        TableColumn<ItemCandidaturaView, String> colPontos = new TableColumn<>("Pontos Carência");
        colPontos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPontosCarencia() + " pts"));

        TableColumn<ItemCandidaturaView, String> colMotivo = new TableColumn<>("Motivo da Não Admissão");
        colMotivo.setCellValueFactory(c -> {
            String mot = c.getValue().inscricao != null ? c.getValue().inscricao.getMotivoRejeicao() : null;
            return new SimpleStringProperty(mot != null && !mot.trim().isEmpty() ? mot : "Não especificado");
        });

        TableColumn<ItemCandidaturaView, String> colData = new TableColumn<>("Data Inscrição");
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDataInscricao().toString()));

        tabela.getColumns().addAll(colNome, colBi, colCurso, colNota12, colPontos, colMotivo, colData);

        Runnable aplicarFiltros = () -> {
            String txt = txtPesquisa.getText() != null ? txtPesquisa.getText().toLowerCase().trim() : "";
            String cursoSel = cbFiltroCurso.getValue();

            List<ItemCandidaturaView> filtrados = itensNaoAdmitidos.stream().filter(item -> {
                boolean bateTexto = txt.isEmpty()
                        || item.getNome().toLowerCase().contains(txt)
                        || item.getBi().toLowerCase().contains(txt);
                boolean bateCurso = cursoSel == null || cursoSel.equals("Todos os Cursos")
                        || item.getCursoNome().equalsIgnoreCase(cursoSel);
                return bateTexto && bateCurso;
            }).collect(Collectors.toList());

            tabela.setItems(FXCollections.observableArrayList(filtrados));
            lblTotal.setText("Total: " + filtrados.size() + " recusas");
        };

        cbFiltroCurso.valueProperty().addListener(e -> aplicarFiltros.run());
        txtPesquisa.textProperty().addListener(e -> aplicarFiltros.run());
        aplicarFiltros.run();

        btnReabrir.setOnAction(e -> {
            ItemCandidaturaView sel = tabela.getSelectionModel().getSelectedItem();
            if (sel == null || sel.inscricao == null) {
                mostrarAviso("Seleccione um candidato para reabrir a candidatura.");
                return;
            }
            inscricaoController.reabrirCandidatura(sel.inscricao.getId());
            mostrarSucesso("Candidatura de " + sel.getNome() + " reaberta e movida para PENDENTE!");
            carregarDados();
            mostrarNaoAdmitidos();
        });

        btnVerDocs.setOnAction(e -> {
            ItemCandidaturaView sel = tabela.getSelectionModel().getSelectedItem();
            if (sel != null && sel.inscricao != null) {
                mostrarDialogDocumentos(sel.inscricao);
            } else {
                mostrarAviso("Seleccione um candidato para ver os detalhes.");
            }
        });

        box.getChildren().addAll(lblTitulo, lblSub, barFiltros, tabela);
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
        if (usuarioLogado != null && usuarioLogado.isSecretaria()) {
            VBox boxRestrito = new VBox(16);
            boxRestrito.setAlignment(Pos.CENTER);
            boxRestrito.setPadding(new Insets(40));

            Label lblRestrito = new Label("🔒 Acesso Restrito à Administração");
            lblRestrito.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");

            Label lblDesc = new Label("Membros da secretaria não possuem permissão para criar ou gerir contas de utilizadores.\nEsta funcionalidade é exclusiva do Administrador Geral.");
            lblDesc.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary; -fx-text-alignment: center;");

            boxRestrito.getChildren().addAll(lblRestrito, lblDesc);
            areaConteudo.getChildren().setAll(boxRestrito);
            return;
        }

        VBox box = new VBox(20);
        box.setFillWidth(true);

        Label lblTitulo = new Label("Gestão de Utilizadores da Secretaria");
        lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Somente administradores autorizados podem criar e gerir novos acessos para a equipa da secretaria.");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");

        HBox layoutGeral = new HBox(20);

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

                mostrarSucesso("Utilizador de secretaria cadastrado com sucesso!");
            } catch (Exception ex) {
                mostrarAviso("Não foi possível cadastrar: " + ex.getMessage());
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
