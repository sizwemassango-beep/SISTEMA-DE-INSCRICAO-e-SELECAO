package controller;

import com.lowagie.text.DocumentException;
import dao.ConexaoBD;
import dao.CursoDao;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import model.Candidato;
import model.Curso;
import model.Inscricao;
import model.QuestionarioCarencia;
import model.StatusInscricao;
import service.InscricaoService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class InscricaoController extends ControllerBase {

    private static final Pattern REGEX_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern REGEX_BI = Pattern.compile("^[0-9]{11,12}[A-Za-z]$");
    private static final Pattern REGEX_TELEFONE = Pattern.compile("^(82|83|84|85|86|87)[0-9]{7}$");

    private final InscricaoService inscricaoService;
    private final CursoDao cursoDao;

    public InscricaoController() {
        super();
        this.inscricaoService = new InscricaoService(this.conexaoBD);
        this.cursoDao = new CursoDao(this.conexaoBD);
    }

    public InscricaoController(ConexaoBD conexaoBD) {
        super(conexaoBD);
        this.inscricaoService = new InscricaoService(this.conexaoBD);
        this.cursoDao = new CursoDao(this.conexaoBD);
    }

    public void submeterInscricao(Candidato candidato, QuestionarioCarencia questionario) throws IllegalArgumentException {
        inscricaoService.processarNovaInscricao(candidato, questionario);
    }

    public void gerarComprovativoPdf(Candidato candidato, File destino) throws IOException, DocumentException {
        inscricaoService.getRelatorioPdfService().exportarComprovativoInscricao(candidato, destino);
    }

    public void gerarComprovativoPdf(Candidato candidato, QuestionarioCarencia carencia, File destino) throws IOException, DocumentException {
        inscricaoService.getRelatorioPdfService().exportarComprovativoInscricao(candidato, carencia, destino);
    }

    public QuestionarioCarencia buscarQuestionarioPorCandidato(Long candidatoId) {
        if (candidatoId == null) return null;
        try {
            dao.QuestionarioCarenciaDao dao = new dao.QuestionarioCarenciaDao(this.conexaoBD);
            return dao.buscarPorCandidato(candidatoId).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Curso> listarCursosDisponiveis() {
        try {
            return cursoDao.listarTabela();
        } catch (Exception e) {
            System.err.println("Erro ao buscar cursos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<Inscricao> buscarInscricaoPorCandidato(Long candidatoId) {
        return inscricaoService.getInscricaoDao().buscarPorCandidatoId(candidatoId);
    }

    public void atualizarStatusInscricao(Long inscricaoId, StatusInscricao status) {
        inscricaoService.getInscricaoDao().atualizarStatus(inscricaoId, status);
    }

    public void validarDocumentosInscricao(Long inscricaoId, boolean validado) {
        inscricaoService.getInscricaoDao().atualizarValidacaoDocumentos(inscricaoId, validado);
    }

    // --- Métodos de Validação ---

    public static boolean isEmailValido(String email) {
        if (email == null) return false;
        return REGEX_EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isTelefoneValido(String telefone) {
        if (telefone == null) return false;
        String limpo = telefone.replaceAll("\\D", "");
        return limpo.length() == 9;
    }

    public static boolean isBiValido(String bi) {
        if (bi == null) return false;
        String limpo = bi.trim();
        return limpo.length() >= 9 && limpo.length() <= 14;
    }

    public static boolean isNomeValido(String nome) {
        if (nome == null) return false;
        String limpo = nome.trim();
        return limpo.length() >= 3 && limpo.contains(" ");
    }

    public static boolean isDataNascimentoValida(LocalDate data) {
        if (data == null) return false;
        LocalDate hoje = LocalDate.now();
        return data.isBefore(hoje.minusYears(14)) && data.isAfter(hoje.minusYears(100));
    }

    public static boolean isSenhaValida(String senha) {
        return senha != null && senha.length() >= 6;
    }

    public static boolean isDistanciaValida(double distanciaKm) {
        return distanciaKm >= 0.0 && distanciaKm <= 2000.0;
    }

    // --- Métodos de Formatação e Filtro de Campos da Interface ---

    public static void aplicarFiltroCelular(TextField textField, int maxDigitos) {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.matches("\\d*") && novoTexto.length() <= maxDigitos) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(filtro));
    }

    public static void aplicarFiltroNumericoDecimal(TextField textField, double maxValor) {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.isEmpty()) {
                return change;
            }
            if (novoTexto.matches("\\d*(\\.\\d*)?")) {
                try {
                    if (!novoTexto.equals(".")) {
                        double val = Double.parseDouble(novoTexto);
                        if (val <= maxValor) {
                            return change;
                        }
                        return null;
                    }
                    return change;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(filtro));
    }

    public static void aplicarFiltroBI(TextField textField, int maxChars) {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.length() <= maxChars) {
                change.setText(change.getText().toUpperCase());
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<>(filtro));
    }

    public static void marcarValidadeCampo(Control campo, boolean valido) {
        if (valido) {
            campo.setStyle("-fx-border-color: -fx-border-color-default;");
        } else {
            campo.setStyle("-fx-border-color: #EF4444; -fx-effect: dropshadow(three-pass-box, rgba(239, 68, 68, 0.25), 4, 0, 0, 0);");
        }
    }
}
