package dao;

import java.util.List;

public interface Tabela <T> {
    void  criar(T t);
    void salvar(T t);
    void registrarTabela(T t);
    <T> void buscarId(Long id);
    List<T> listarTabela();
    void atualizar(T object);
    void eleminar(T object);

}
