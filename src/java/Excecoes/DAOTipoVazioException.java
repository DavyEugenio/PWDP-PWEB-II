package Excecoes;

public class DAOTipoVazioException extends Exception {

    public DAOTipoVazioException() {
        super("Não há tipos registrados no sistema!");
    }
}
