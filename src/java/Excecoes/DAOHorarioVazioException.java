package Excecoes;

public class DAOHorarioVazioException extends Exception {

    public DAOHorarioVazioException() {
        super("Não há reservas registradas no sistema!");
    }
}
