package Excecoes;

public class HorarioNaoEncontradoException extends Exception {

    public HorarioNaoEncontradoException() {
        super("Nenhuma reserva encontrada!");
    }
}