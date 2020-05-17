package Excecoes;

public class DAOUsuarioVazioException extends Exception {

    public DAOUsuarioVazioException() {
        super("Não há usuários registrados no sistema!");
    }
}
