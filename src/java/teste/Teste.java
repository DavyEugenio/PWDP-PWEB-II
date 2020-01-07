package teste;

import DAO.EspacoDAO;
import DAO.HorarioDAO;
import DAO.TipoDAO;
import DAO.UsuarioAbstratoDAO;
import Excecoes.EspacoInvalidoException;
import controle.espacoBean;
import controle.horarioBean;
import controle.tipoBean;
import util.Conversoes;
import entidades.Espaco;
import entidades.Horario;
import entidades.Operador;
import entidades.Servidor;
import entidades.Tipo;
import entidades.UsuarioAbstrato;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Teste {

    public static void main(String[] args)  {
        Espaco e = new Espaco();
        EspacoDAO uadao = new EspacoDAO();
        e.setStatus(false);
        e.setId_tipo(2);
        e.setDescricao("");
        e.setNumero(0);
        e.setId(1);
        try {
            uadao.Salvar(e);
            /*EspacoDAO uadao = new EspacoDAO();
            Espaco e = new Espaco();
            e.setDescricao("teste");
            e.setStatus(true);
            e.setNumero(1);
            e.setId_tipo(4);
            uadao.Salvar(e);*/
            /*horarioBean hb = new horarioBean(new Horario(8, null, null, "0001", 2, "Em espera") ,new Date(2019, 11, 24), 8,19,20,30,30);
            hb.adicionar();
            */ /*
            Date data, int pid, int horaE, int horaS, int minE, int minS
            */
        } catch (SQLException | EspacoInvalidoException ex) {
            Logger.getLogger(Teste.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
        //Inserir
        UsuarioAbstratoDAO uadao = new UsuarioAbstratoDAO();
        UsuarioAbstrato ua = new Servidor("00013","Sandy","senha");
        uadao.salvar(ua);
        //Alterar
        UsuarioAbstratoDAO uadao = new UsuarioAbstratoDAO();
        UsuarioAbstrato ua = new Servidor("00012","Patrick","senha");
        uadao.Aterar(ua);
    
        //Buscar
        UsuarioAbstratoDAO uadao = new UsuarioAbstratoDAO();
        UsuarioAbstrato ua = null;
        ua = uadao.buscar("00011");
        String tipo = ua.getClass()+"";
        System.out.println("Tipo: " + tipo.substring(16, tipo.length()));
        System.out.println("Nome: "+ua.getNome());
        System.out.println("Matricula: "+ua.getMatricula());
        System.out.println("Senha: "+ua.getSenha());
    
        //Listar
        UsuarioAbstratoDAO uadao = new UsuarioAbstratoDAO();
        List uaa = uadao.listar();
        for (Iterator i = uaa.iterator(); i.hasNext();) {
            UsuarioAbstrato ua = (UsuarioAbstrato) i.next();
            String tipo = ua.getClass()+"";
            System.out.println("Tipo: " + tipo.substring(16, tipo.length()));
            System.out.println("Nome: " + ua.getNome());
            System.out.println("Matricula: " + ua.getMatricula());
            System.out.println("Senha: " + ua.getSenha());
        }
        //Excluir
        UsuarioAbstratoDAO uadao = new UsuarioAbstratoDAO();
        uadao.excluir("00014");
     */
}
