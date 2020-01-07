package Excecoes;

public class DescricaoInvalidaException extends Exception {

    public DescricaoInvalidaException() {
        super("Descrição inserida inválida!");
    }
}
