package Excecoes;

public class DAOEspacoVazioException extends Exception {

    public DAOEspacoVazioException() {
        super("Não há espaços registrados no sistema!");
    }
}
