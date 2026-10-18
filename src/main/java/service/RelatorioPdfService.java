package service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import model.Candidato;
import model.Curso;
import model.QuestionarioCarencia;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioPdfService {

    private static final Color COR_PRIMARIA = new Color(30, 58, 138); // Deep Blue
    private static final Color COR_SECUNDARIA = new Color(59, 130, 246); // Blue
    private static final Color COR_CINZA_CLARO = new Color(243, 244, 246);
    private static final Color COR_TEXTO = new Color(31, 41, 55);

    private static final Font FONTE_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COR_PRIMARIA);
    private static final Font FONTE_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(100, 116, 139));
    private static final Font FONTE_CABECALHO_TABELA = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Font FONTE_CELULA = FontFactory.getFont(FontFactory.HELVETICA, 10, COR_TEXTO);
    private static final Font FONTE_CELULA_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COR_TEXTO);
    private static final Font FONTE_RODAPE = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, new Color(156, 163, 175));

    public void exportarListaCandidatos(List<Candidato> candidatos, File arquivoDestino) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(arquivoDestino));

        document.open();

        adicionarCabecalho(document, "RELATÓRIO GERAL DE CANDIDATOS INSCRITOS", "Sistema Integrado de Inscrição e Seleção");

        Paragraph meta = new Paragraph("Total de candidatos listados: " + (candidatos != null ? candidatos.size() : 0), FONTE_CELULA_BOLD);
        meta.setSpacingAfter(15);
        document.add(meta);

        PdfPTable tabela = new PdfPTable(6);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1.0f, 3.5f, 2.2f, 2.2f, 2.2f, 3.5f});

        adicionarCabecalhoColuna(tabela, "ID");
        adicionarCabecalhoColuna(tabela, "Nome Completo");
        adicionarCabecalhoColuna(tabela, "Número BI");
        adicionarCabecalhoColuna(tabela, "Telefone");
        adicionarCabecalhoColuna(tabela, "Província");
        adicionarCabecalhoColuna(tabela, "Email");

        if (candidatos == null || candidatos.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("Nenhum candidato encontrado na base de dados.", FONTE_CELULA));
            emptyCell.setColspan(6);
            emptyCell.setPadding(15);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabela.addCell(emptyCell);
        } else {
            boolean linhaPar = false;
            for (Candidato c : candidatos) {
                Color bg = linhaPar ? COR_CINZA_CLARO : Color.WHITE;
                tabela.addCell(criarCelula(String.valueOf(c.getId() != null ? c.getId() : "-"), bg, Element.ALIGN_CENTER));
                tabela.addCell(criarCelula(c.getNomeCompleto() != null ? c.getNomeCompleto() : "-", bg, Element.ALIGN_LEFT));
                tabela.addCell(criarCelula(c.getNumeroBI() != null ? c.getNumeroBI() : "-", bg, Element.ALIGN_CENTER));
                tabela.addCell(criarCelula(c.getTelefone() != null ? c.getTelefone() : "-", bg, Element.ALIGN_CENTER));
                tabela.addCell(criarCelula(c.getProvincia() != null ? c.getProvincia() : "-", bg, Element.ALIGN_LEFT));
                tabela.addCell(criarCelula(c.getEmail() != null ? c.getEmail() : "-", bg, Element.ALIGN_LEFT));
                linhaPar = !linhaPar;
            }
        }

        document.add(tabela);

        adicionarRodape(document);

        document.close();
    }

    public void exportarComprovativoInscricao(Candidato candidato, File arquivoDestino) throws IOException, DocumentException {
        exportarComprovativoInscricao(candidato, null, arquivoDestino);
    }

    public void exportarComprovativoInscricao(Candidato candidato, QuestionarioCarencia carencia, File arquivoDestino) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 40, 40, 50, 40);
        PdfWriter.getInstance(document, new FileOutputStream(arquivoDestino));

        document.open();

        adicionarCabecalho(document, "COMPROVATIVO OFICIAL DE INSCRIÇÃO", "Processo Seletivo de Admissão • Ocean S.A. 2026");

        PdfPTable infoBox = new PdfPTable(2);
        infoBox.setWidthPercentage(100);
        infoBox.setWidths(new float[]{3.2f, 6.8f});
        infoBox.setSpacingBefore(12);
        infoBox.setSpacingAfter(16);

        adicionarLinhaInfo(infoBox, "Número de Inscrição:", String.valueOf(candidato.getId() != null ? candidato.getId() : "Atribuído"));
        adicionarLinhaInfo(infoBox, "Nome do Candidato:", candidato.getNomeCompleto());
        adicionarLinhaInfo(infoBox, "Documento de Identificação (BI):", candidato.getNumeroBI());
        adicionarLinhaInfo(infoBox, "Data de Nascimento:", candidato.getDataDeNascimento() != null ? candidato.getDataDeNascimento().toString() : "-");
        adicionarLinhaInfo(infoBox, "Email de Contacto:", candidato.getEmail());
        adicionarLinhaInfo(infoBox, "Telefone / Celular:", candidato.getTelefone());
        adicionarLinhaInfo(infoBox, "Província de Residência:", candidato.getProvincia());
        adicionarLinhaInfo(infoBox, "Distrito:", candidato.getDistrito() != null ? candidato.getDistrito() : "-");
        adicionarLinhaInfo(infoBox, "Morada / Bairro:", candidato.getMorada() != null ? candidato.getMorada() : "-");

        document.add(infoBox);

        if (carencia != null && carencia.isValido()) {
            Paragraph titCarencia = new Paragraph("Resultado da Avaliação Socioeconómica (Carência):", FONTE_CELULA_BOLD);
            titCarencia.setSpacingBefore(8);
            titCarencia.setSpacingAfter(6);
            document.add(titCarencia);

            PdfPTable tabelaCarencia = new PdfPTable(2);
            tabelaCarencia.setWidthPercentage(100);
            tabelaCarencia.setWidths(new float[]{3.2f, 6.8f});
            tabelaCarencia.setSpacingAfter(16);

            adicionarLinhaInfo(tabelaCarencia, "Rendimento Familiar:", carencia.getRendimentoFamiliar() != null ? carencia.getRendimentoFamiliar().getDescricao() : "-");
            adicionarLinhaInfo(tabelaCarencia, "Situação Laboral:", carencia.getSituacaoLaboral() != null ? carencia.getSituacaoLaboral().getDescricao() : "-");
            adicionarLinhaInfo(tabelaCarencia, "Distância Declarada:", carencia.getDistanciaKm() + " km");
            adicionarLinhaInfo(tabelaCarencia, "Órfão / Chefe de Família:", carencia.isOrfaoOuChefeFamilia() ? "Sim" : "Não");
            adicionarLinhaInfo(tabelaCarencia, "Pontuação de Carência:", carencia.calcularPontuacao() + " / 100 pontos (" + carencia.classificar().getDescricao() + ")");
            adicionarLinhaInfo(tabelaCarencia, "Elegível p/ Vaga Reservada:", carencia.elegivelParaVagaReservada() ? "SIM" : "NÃO");

            document.add(tabelaCarencia);
        }

        Paragraph avisos = new Paragraph();
        avisos.setSpacingBefore(10);
        avisos.add(new Chunk("Observações Importantes:\n", FONTE_CELULA_BOLD));
        avisos.add(new Chunk("1. Este documento serve como comprovativo formal de submissão dos dados no sistema.\n" +
                "2. O candidato deverá acompanhar as etapas de validação de documentos e publicação de notas.\n" +
                "3. Quaisquer dados inexatos poderão implicar a anulação da candidatura.", FONTE_SUBTITULO));
        document.add(avisos);

        PdfPTable assinaturaTable = new PdfPTable(2);
        assinaturaTable.setWidthPercentage(100);
        assinaturaTable.setSpacingBefore(60);

        PdfPCell cellAssinatura1 = new PdfPCell(new Phrase("_____________________________________\nAssinatura do Candidato", FONTE_CELULA));
        cellAssinatura1.setBorder(Rectangle.NO_BORDER);
        cellAssinatura1.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell cellAssinatura2 = new PdfPCell(new Phrase("_____________________________________\nCarimbo e Validação da Secretaria", FONTE_CELULA));
        cellAssinatura2.setBorder(Rectangle.NO_BORDER);
        cellAssinatura2.setHorizontalAlignment(Element.ALIGN_CENTER);

        assinaturaTable.addCell(cellAssinatura1);
        assinaturaTable.addCell(cellAssinatura2);
        document.add(assinaturaTable);

        adicionarRodape(document);

        document.close();
    }

    private void adicionarCabecalho(Document document, String titulo, String subtitulo) throws DocumentException {
        Paragraph pTitulo = new Paragraph(titulo, FONTE_TITULO);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);

        Paragraph pSub = new Paragraph(subtitulo, FONTE_SUBTITULO);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(15);
        document.add(pSub);

        Paragraph linha = new Paragraph("_______________________________________________________________________________", FONTE_SUBTITULO);
        linha.setAlignment(Element.ALIGN_CENTER);
        linha.setSpacingAfter(20);
        document.add(linha);
    }

    private void adicionarRodape(Document document) throws DocumentException {
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        Paragraph rodape = new Paragraph("\n\nDocumento gerado eletronicamente em " + dataHora + " pelo Sistema de Inscrição e Seleção.", FONTE_RODAPE);
        rodape.setAlignment(Element.ALIGN_CENTER);
        document.add(rodape);
    }

    private void adicionarCabecalhoColuna(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONTE_CABECALHO_TABELA));
        cell.setBackgroundColor(COR_PRIMARIA);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private PdfPCell criarCelula(String text, Color background, int alinhamento) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", FONTE_CELULA));
        cell.setBackgroundColor(background);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alinhamento);
        return cell;
    }

    private void adicionarLinhaInfo(PdfPTable table, String rotulo, String valor) {
        PdfPCell cellRotulo = new PdfPCell(new Phrase(rotulo, FONTE_CELULA_BOLD));
        cellRotulo.setPadding(7);
        cellRotulo.setBackgroundColor(COR_CINZA_CLARO);

        PdfPCell cellValor = new PdfPCell(new Phrase(valor != null ? valor : "-", FONTE_CELULA));
        cellValor.setPadding(7);

        table.addCell(cellRotulo);
        table.addCell(cellValor);
    }
}
