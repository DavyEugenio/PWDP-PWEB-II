package Excecoes;

public class UsuarioNaoEncontradoException extends Exception {

    public UsuarioNaoEncontradoException() {
        super("Nenhum usuário encontrado!");
    }
}