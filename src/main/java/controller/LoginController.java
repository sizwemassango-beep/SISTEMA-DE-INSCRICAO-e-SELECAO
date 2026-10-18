package controller;

import dao.ConexaoBD;
import model.Administrador;
import service.AutenticacaoService;
import service.AutenticacaoService.ResultadoLogin;

import java.util.List;

public class LoginController extends ControllerBase {

    private final AutenticacaoService autenticacaoService;

    public LoginController() {
        super();
        this.autenticacaoService = new AutenticacaoService(this.conexaoBD);
    }

    public LoginController(ConexaoBD conexaoBD) {
        super(conexaoBD);
        this.autenticacaoService = new AutenticacaoService(this.conexaoBD);
    }

    public ResultadoLogin autenticar(String email, String senha) {
        return autenticacaoService.identificarEAutenticar(email, senha);
    }

    public boolean autenticarSecretaria(String email, String senha) {
        ResultadoLogin r = autenticar(email, senha);
        return r.isSucesso() && r.getPerfil() == service.AutenticacaoService.TipoPerfil.ADMINISTRADOR;
    }

    public java.util.Optional<model.Candidato> autenticarCandidato(String email, String senha) {
        ResultadoLogin r = autenticar(email, senha);
        if (r.isSucesso() && r.getPerfil() == service.AutenticacaoService.TipoPerfil.CANDIDATO) {
            return java.util.Optional.of((model.Candidato) r.getUsuario());
        }
        return java.util.Optional.empty();
    }

    public void cadastrarUsuarioSecretaria(Administrador administrador) throws IllegalArgumentException {
        autenticacaoService.cadastrarSecretaria(administrador);
    }

    public List<Administrador> listarUsuariosSecretaria() {
        return autenticacaoService.listarAdministradores();
    }
}
