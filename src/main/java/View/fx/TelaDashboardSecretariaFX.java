package View.fx;

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

public class TelaDashboardSecretariaFX extends BorderPane {

    private final PainelController controller;

    private StackPane areaConteudo;
    private ObservableList<Candidato> listaCandidatosObservable;
    private TableView<Candidato> tabelaCandidatos;

    private Button btnMenuPainel;
    private Button btnMenuCandidatos;
    private Button btnMenuCursos;
    private Button btnMenuUsuarios;
    private Button btnMenuPdf;
    private final controller.LoginController loginController;

    public TelaDashboardSecretariaFX() {
        this.controller = new PainelController();
        this.loginController = new controller.LoginController();
        this.listaCandidatosObservable = FXCollections.observableArrayList();
        construirInterface();
        carregarDados();
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
        sidebar.setPrefWidth(250);

        HBox boxMarca = new HBox(12);
        boxMarca.setAlignment(Pos.CENTER_LEFT);
        boxMarca.setPadding(new Insets(0, 0, 20, 0));

        StackPane logoSidebar = ComponentesVisuais.criarLogoCompacto(44);

        VBox titulosSidebar = new VBox(2);
        Label lblMarca = new Label("Ocean S.A.");
        lblMarca.getStyleClass().add("sidebar-title");

        Label lblPerfil = new Label("Secretaria Geral");
        lblPerfil.getStyleClass().add("sidebar-subtitle");
        titulosSidebar.getChildren().addAll(lblMarca, lblPerfil);

        boxMarca.getChildren().addAll(logoSidebar, titulosSidebar);

        btnMenuPainel = criarBotaoMenu("📊 Visão Geral", true, this::mostrarVisaoGeral);
        btnMenuCandidatos = criarBotaoMenu("👥 Candidatos", false, this::mostrarGestaoCandidatos);
        btnMenuCursos = criarBotaoMenu("📚 Cursos & Vagas", false, this::mostrarGestaoCursos);
        btnMenuUsuarios = criarBotaoMenu("🛡 Utilizadores Secretaria", false, this::mostrarGestaoSecretaria);
        btnMenuPdf = criarBotaoMenu("📄 Exportar PDF", false, this::mostrarModuloPdf);

        Region espacador = new Region();
        VBox.setVgrow(espacador, Priority.ALWAYS);

        Button btnTema = new Button(Navegador.isModoEscuro() ? "☀ Tema Claro" : "🌙 Tema Escuro");
        btnTema.getStyleClass().add("sidebar-button");
        btnTema.setMaxWidth(Double.MAX_VALUE);
        btnTema.setOnAction(e -> {
            Navegador.alternarTema();
            btnTema.setText(Navegador.isModoEscuro() ? "☀ Tema Claro" : "🌙 Tema Escuro");
        });

        Button btnTerminarSessao = new Button("🚪 Terminar Sessão");
        btnTerminarSessao.getStyleClass().add("sidebar-button");
        btnTerminarSessao.setMaxWidth(Double.MAX_VALUE);
        btnTerminarSessao.setOnAction(e -> Navegador.irParaTelaInicial());

        sidebar.getChildren().addAll(
                boxMarca,
                btnMenuPainel,
                btnMenuCandidatos,
                btnMenuCursos,
                btnMenuUsuarios,
                btnMenuPdf,
                espacador,
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
        VBox box = new VBox(24);
        box.setFillWidth(true);

        // Header da Seção
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titulos = new VBox(4);
        Label lblTitulo = new Label("Painel de Controlo da Secretaria");
        lblTitulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblSub = new Label("Estatísticas consolidadas e métricas do processo de seleção");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");
        titulos.getChildren().addAll(lblTitulo, lblSub);

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        Button btnAtualizar = new Button("🔄 Atualizar Indicadores");
        btnAtualizar.getStyleClass().add("button-secondary");
        btnAtualizar.setOnAction(e -> {
            carregarDados();
            mostrarVisaoGeral();
        });

        Button btnExportarGeral = new Button("📥 Baixar Relatório Geral PDF");
        btnExportarGeral.getStyleClass().add("button-pdf");
        btnExportarGeral.setOnAction(e -> exportarPdfGeral());

        header.getChildren().addAll(titulos, esp, btnAtualizar, btnExportarGeral);

        // KPIs Reais (sem dados inventados)
        int totalCandidatos = controller.obterTotalCandidatos();
        int totalCursos = controller.obterTotalCursos();
        int totalVagas = controller.obterTotalVagas();
        double concorrencia = controller.obterConcorrenciaGeral();

        HBox gridKpis = new HBox(20);
        gridKpis.setAlignment(Pos.CENTER);

        VBox kpi1 = criarCardKpi("Candidatos Inscritos", String.valueOf(totalCandidatos), "#2563EB");
        VBox kpi2 = criarCardKpi("Cursos Ativos", String.valueOf(totalCursos), "#059669");
        VBox kpi3 = criarCardKpi("Vagas Disponíveis", String.valueOf(totalVagas), "#D97706");
        VBox kpi4 = criarCardKpi("Concorrência Geral", String.format("%.2f cand/vaga", concorrencia), "#7C3AED");

        HBox.setHgrow(kpi1, Priority.ALWAYS);
        HBox.setHgrow(kpi2, Priority.ALWAYS);
        HBox.setHgrow(kpi3, Priority.ALWAYS);
        HBox.setHgrow(kpi4, Priority.ALWAYS);

        gridKpis.getChildren().addAll(kpi1, kpi2, kpi3, kpi4);

        // Seção de Gráficos Reais
        HBox painelGraficos = new HBox(20);
        painelGraficos.setAlignment(Pos.CENTER);

        // 1. BarChart: Candidatos por Província
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Província");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Número de Candidatos");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Distribuição Real de Candidatos por Província");
        barChart.setLegendVisible(false);
        barChart.getStyleClass().add("card");
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

        // 2. PieChart: Cursos Cadastrados e Vagas
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        List<Curso> cursos = controller.listarCursos();
        if (cursos.isEmpty()) {
            pieData.add(new PieChart.Data("Nenhum curso na base", 1));
        } else {
            for (Curso c : cursos) {
                pieData.add(new PieChart.Data(c.getNome() + " (" + c.getNumeroVagas() + ")", c.getNumeroVagas()));
            }
        }

        PieChart pieChart = new PieChart(pieData);
        pieChart.setTitle("Distribuição de Vagas por Curso");
        pieChart.getStyleClass().add("card");
        pieChart.setPrefWidth(420);

        painelGraficos.getChildren().addAll(barChart, pieChart);

        box.getChildren().addAll(header, gridKpis, painelGraficos);

        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        areaConteudo.getChildren().setAll(scroll);
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

        Label lblSub = new Label("Consulte, filtre e exporte os registos de candidaturas");
        lblSub.setStyle("-fx-font-size: 13px; -fx-text-fill: -fx-text-secondary;");
        titulos.getChildren().addAll(lblTitulo, lblSub);

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        TextField txtBusca = new TextField();
        txtBusca.setPromptText("🔍 Pesquisar por Nome ou BI...");
        txtBusca.setPrefWidth(280);

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

        box.getChildren().addAll(header, tabelaCandidatos);
        areaConteudo.getChildren().setAll(box);
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

        TextField txtCargo = new TextField();
        txtCargo.setPromptText("Cargo (ex: Secretaria Geral)");

        Button btnCriar = new Button("Cadastrar Utilizador");
        btnCriar.getStyleClass().add("button-primary");
        btnCriar.setMaxWidth(Double.MAX_VALUE);

        btnCriar.setOnAction(e -> {
            try {
                String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
                String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
                String senha = txtSenha.getText();
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
                new Label("Senha:"), txtSenha,
                new Label("Cargo:"), txtCargo,
                btnCriar
        );

        layoutGeral.getChildren().addAll(tabelaAdmins, formAdmin);
        box.getChildren().addAll(lblTitulo, lblSub, layoutGeral);
        areaConteudo.getChildren().setAll(box);
    }
}
