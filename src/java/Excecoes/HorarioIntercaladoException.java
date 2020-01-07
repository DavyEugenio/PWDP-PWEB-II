package Excecoes;

public class HorarioIntercaladoException extends Exception {

    public HorarioIntercaladoException() {
        super("Horário inserido se intercala com outro/s já inserido/s!");
    }
}
