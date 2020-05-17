package Excecoes;

public class ResponsavelNaoEncontradoException extends Exception {

    public ResponsavelNaoEncontradoException() {
        super("Nenhum responsável encontrado!");
    }
}