package entidades;

public class Espaco {

    private int id;
    private String descricao;
    private int numero;
    private boolean status;
    private int id_tipo;

    public Espaco() {
    }
    
    public Espaco(int id, String descricao, int numero, boolean status, int id_tipo) {
        this.id = id;
        this.descricao = descricao;
        this.numero = numero;
        this.status = status;
        this.id_tipo = id_tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getId_tipo() {
        return id_tipo;
    }

    public void setId_tipo(int id_tipo) {
        this.id_tipo = id_tipo;
    }
    
    public String getStatus(){
        if(this.status == true){
            return "Disponivel";
        } else {
            return "Indisponivel";
        }
    }
}
