package View.fx;

import dao.ConexaoBD;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import service.DatabaseInitializer;

/**

 * Representa a interface visual relacionada a TelaInicialFX.

 */

public class TelaInicialFX extends BorderPane {

    private final DatabaseInitializer dbInit;

    public TelaInicialFX() {
        this.dbInit = new DatabaseInitializer(new ConexaoBD());
        construirInterface();
    }

    private void construirInterface() {
        setTop(criarBarraSuperior());
        setCenter(criarConteudoCentral());
        setBottom(criarRodape());
    }

    private HBox criarBarraSuperior() {
        HBox barra = new HBox(20);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(16, 32, 16, 32));
        barra.setStyle("-fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-width: 0 0 1 0;");

        StackPane logoBarra = ComponentesVisuais.criarLogoCompacto(40);

        VBox boxMarca = new VBox(2);
        Label lblMarca = new Label("SIS • Admissão");
        lblMarca.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -fx-primary-color;");

        Label lblSubMarca = new Label("Sistema de Inscrição e Seleção de Candidatos");
        lblSubMarca.setStyle("-fx-font-size: 11px; -fx-text-fill: -fx-text-secondary;");
        boxMarca.getChildren().addAll(lblMarca, lblSubMarca);

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        boolean conectado = dbInit.testarConexao();
        Label lblStatusDb = new Label(conectado ? "● Base de Dados Conectada" : "○ Modo Offline / Erro BD");
        lblStatusDb.getStyleClass().add(conectado ? "badge-status-online" : "badge-status-offline");

        Button btnTema = new Button(Navegador.isModoEscuro() ? "☀ Modo Claro" : "🌙 Modo Escuro");
        btnTema.getStyleClass().add("button-secondary");
        btnTema.setOnAction(e -> {
            Navegador.alternarTema();
            btnTema.setText(Navegador.isModoEscuro() ? "☀ Modo Claro" : "🌙 Modo Escuro");
        });

        barra.getChildren().addAll(logoBarra, boxMarca, espacador, lblStatusDb, btnTema);
        return barra;
    }

    private VBox criarConteudoCentral() {
        VBox container = new VBox(36);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40, 48, 40, 48));

        VBox hero = new VBox(8);
        hero.setAlignment(Pos.CENTER);

        Label lblBoasVindas = new Label("Portal Académico de Inscrições");
        lblBoasVindas.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: -fx-text-primary;");

        Label lblDescricao = new Label("Selecione uma das opções abaixo para prosseguir com a sua candidatura ou gestão institucional.");
        lblDescricao.setStyle("-fx-font-size: 14px; -fx-text-fill: -fx-text-secondary;");

        hero.getChildren().addAll(lblBoasVindas, lblDescricao);

        HBox gridCartoes = new HBox(24);
        gridCartoes.setAlignment(Pos.CENTER);

        VBox cardInscricao = criarCardOpcao(
                "Quero me Inscrever",
                "Nova Candidatura",
                "Crie a sua conta e submeta a sua candidatura aos cursos preparatórios disponíveis para o ano letivo.",
                "Iniciar Inscrição →",
                "button-primary",
                () -> Navegador.irParaNovaInscricao()
        );

        VBox cardCandidato = criarCardOpcao(
                "Já Tenho Conta",
                "Área do Candidato",
                "Consulte o estado da sua inscrição, verifique a sua pontuação e emita o comprovativo oficial em PDF.",
                "Acessar Conta →",
                "button-secondary",
                () -> Navegador.irParaLogin(false)
        );

        VBox cardSecretaria = criarCardOpcao(
                "Administração",
                "Área da Secretaria",
                "Gestão completa do processo seletivo, visualização de inscritos, relatórios em tempo real e exportação em PDF.",
                "Entrar no Painel →",
                "button-secondary",
                () -> Navegador.irParaLogin(true)
        );

        gridCartoes.getChildren().addAll(cardInscricao, cardCandidato, cardSecretaria);

        container.getChildren().addAll(hero, gridCartoes);
        return container;
    }

    private VBox criarCardOpcao(String badge, String titulo, String descricao, String textoBotao, String classeBotao, Runnable acao) {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPrefWidth(320);
        card.setMaxWidth(340);
        card.setAlignment(Pos.TOP_LEFT);

        Label lblBadge = new Label(badge.toUpperCase());
        lblBadge.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #2563EB; -fx-background-color: #EFF6FF; -fx-padding: 4px 10px; -fx-background-radius: 6px;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("card-title");
        lblTitulo.setStyle("-fx-font-size: 18px;");

        Label lblDesc = new Label(descricao);
        lblDesc.getStyleClass().add("card-subtitle");
        lblDesc.setWrapText(true);
        lblDesc.setMinHeight(65);

        Region espaco = new Region();
        VBox.setVgrow(espaco, Priority.ALWAYS);

        Button btnAcao = new Button(textoBotao);
        btnAcao.getStyleClass().add(classeBotao);
        btnAcao.setMaxWidth(Double.MAX_VALUE);
        btnAcao.setOnAction(e -> acao.run());

        card.getChildren().addAll(lblBadge, lblTitulo, lblDesc, espaco, btnAcao);
        return card;
    }

    private HBox criarRodape() {
        HBox rodape = new HBox();
        rodape.setAlignment(Pos.CENTER);
        rodape.setPadding(new Insets(16, 24, 16, 24));
        rodape.setStyle("-fx-background-color: -fx-surface-color; -fx-border-color: -fx-border-color-default; -fx-border-width: 1 0 0 0;");

        Label lblCopy = new Label("© 2026 Sistema de Inscrição e Seleção • Todos os direitos reservados.");
        lblCopy.setStyle("-fx-font-size: 12px; -fx-text-fill: -fx-text-secondary;");

        rodape.getChildren().add(lblCopy);
        return rodape;
    }
}
