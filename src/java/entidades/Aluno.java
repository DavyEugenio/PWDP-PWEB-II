package entidades;

public class Aluno extends UsuarioAbstrato {

    public Aluno() {
    }

    public Aluno(String matricula, String nome, String senha) {
        super(matricula, nome, senha);
    }
}
