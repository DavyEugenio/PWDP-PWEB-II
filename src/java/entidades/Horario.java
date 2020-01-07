package entidades;

import java.time.LocalDateTime;

public class Horario {

    private int id;
    private LocalDateTime entrada;
    private LocalDateTime saida;
    private String matricula_responsavel;
    private int id_espaco;
    private String status;

    public Horario() {
    }

    public Horario(int id, LocalDateTime entrada, LocalDateTime saida, String matricula_responsavel, int id_espaco, String status) {
        this.id = id;
        this.entrada = entrada;
        this.saida = saida;
        this.matricula_responsavel = matricula_responsavel;
        this.id_espaco = id_espaco;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public void setSaida(LocalDateTime saida) {
        this.saida = saida;
    }

    public String getMatricula_responsavel() {
        return matricula_responsavel;
    }

    public void setMatricula_responsavel(String matricula_responsavel) {
        this.matricula_responsavel = matricula_responsavel;
    }

    public int getId_espaco() {
        return id_espaco;
    }

    public void setId_espaco(int id_espaco) {
        this.id_espaco = id_espaco;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
