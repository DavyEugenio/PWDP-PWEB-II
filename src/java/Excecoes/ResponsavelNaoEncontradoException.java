package Excecoes;

public class ResponsavelNaoEncontradoException extends Exception {

    public ResponsavelNaoEncontradoException() {
        super("Responsável não encontrado!");
    }
}