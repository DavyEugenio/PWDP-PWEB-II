package Excecoes;

public class OperadorNaoEncontradoException extends Exception {

    public OperadorNaoEncontradoException() {
        super("Nenhum operador encontrado!");
    }
}