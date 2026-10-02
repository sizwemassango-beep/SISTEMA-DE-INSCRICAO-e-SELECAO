package dao;

import java.util.List;
import java.util.Optional;

public interface Tabela<T> {
    void criar(T t);
    void salvar(T t);
    void registrarTabela(T t);
    Optional<T> buscarId(Long id);
    List<T> listarTabela();
    void atualizar(T t);
    void eleminar(T t);
}