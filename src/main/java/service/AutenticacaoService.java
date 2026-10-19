package service;

import dao.AdministradorDao;
import dao.CandidatoDao;
import dao.ConexaoBD;
import model.Administrador;
import model.Candidato;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AutenticacaoService {

    public enum TipoPerfil {
        ADMINISTRADOR,
        CANDIDATO,
        INVALIDO
    }

    public static class ResultadoLogin {
        private final TipoPerfil perfil;
        private final Object usuario;

        public ResultadoLogin(TipoPerfil perfil, Object usuario) {
            this.perfil = perfil;
            this.usuario = usuario;
        }

        public TipoPerfil getPerfil() {
            return perfil;
        }

        public Object getUsuario() {
            return usuario;
        }

        public boolean isSucesso() {
            return perfil != TipoPerfil.INVALIDO;
        }
    }

    private final AdministradorDao administradorDao;
    private final CandidatoDao candidatoDao;

    public AutenticacaoService(ConexaoBD conexaoBD) {
        this.administradorDao = new AdministradorDao(conexaoBD);
        this.candidatoDao = new CandidatoDao(conexaoBD);
    }

    public ResultadoLogin identificarEAutenticar(String email, String senha) {
        if (email == null || senha == null || email.trim().isEmpty() || senha.trim().isEmpty()) {
            return new ResultadoLogin(TipoPerfil.INVALIDO, null);
        }

        String emailLimpo = email.trim();
        String senhaLimpa = senha;

        try {
            // 1. Verifica se é Administrador / Secretaria
            List<Administrador> admins = administradorDao.listarTabela();
            if (admins.isEmpty()) {
                // Primeiro acesso do sistema para configuração inicial
                if ("admin@sis.ac.mz".equalsIgnoreCase(emailLimpo) && "admin123".equals(senhaLimpa)) {
                    Administrador root = new Administrador("Administrador Geral", "admin@sis.ac.mz", "admin123", "Secretaria Geral");
                    administradorDao.salvar(root);
                    return new ResultadoLogin(TipoPerfil.ADMINISTRADOR, root);
                }
            }

            for (Administrador a : admins) {
                if (emailLimpo.equalsIgnoreCase(a.getEmail()) && senhaLimpa.equals(a.getSenhaHash())) {
                    return new ResultadoLogin(TipoPerfil.ADMINISTRADOR, a);
                }
            }

            // 2. Se não for Administrador, verifica se é Candidato
            Optional<Candidato> optCandidato = candidatoDao.buscarPorEmail(emailLimpo);
            if (optCandidato.isPresent()) {
                Candidato c = optCandidato.get();
                if (senhaLimpa.equals(c.getSenhaHash())) {
                    return new ResultadoLogin(TipoPerfil.CANDIDATO, c);
                }
            }

        } catch (Exception e) {
            System.err.println("Erro na autenticação unificada: " + e.getMessage());
        }

        return new ResultadoLogin(TipoPerfil.INVALIDO, null);
    }

    public void cadastrarSecretaria(Administrador novoAdmin) throws IllegalArgumentException {
        if (novoAdmin.getNomeCompleto() == null || novoAdmin.getNomeCompleto().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome completo é obrigatório.");
        }
        if (novoAdmin.getEmail() == null || novoAdmin.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O email é obrigatório.");
        }
        if (novoAdmin.getSenhaHash() == null || novoAdmin.getSenhaHash().trim().isEmpty()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        if (novoAdmin.getCargo() == null || novoAdmin.getCargo().trim().isEmpty()) {
            novoAdmin.setCargo("Secretaria");
        }

        if (candidatoDao.existeEmail(novoAdmin.getEmail())) {
            throw new IllegalArgumentException("Este email já está cadastrado no sistema.");
        }

        administradorDao.salvar(novoAdmin);
    }

    public List<Administrador> listarAdministradores() {
        try {
            return administradorDao.listarTabela();
        } catch (Exception e) {
            System.err.println("Erro ao listar administradores: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
