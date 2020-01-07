package util;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Conversoes {
    public final SimpleDateFormat formatoDT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public final SimpleDateFormat formatoT = new SimpleDateFormat("HH:mm:ss");
    public final SimpleDateFormat formatoD = new SimpleDateFormat("yyyy-MM-dd");
    public final DateTimeFormatter formatoLDT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    public final DateTimeFormatter formatoLD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final DateTimeFormatter formatoLT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Conversoes() {
    }
    
    public LocalDate conversorD(Date data){
        return LocalDate.parse(formatoD.format(data), formatoLD);
    }
    
    public LocalTime conversorT(Date hora){
        return LocalTime.parse(formatoT.format(hora), formatoLT);
    }
    
    public LocalDateTime conversorDT(Date data, Date hora){
        return LocalDateTime.of(conversorD(data), conversorT(hora));
    }
    
    public LocalDateTime conversorDT2(Date dateTime){
        return LocalDateTime.of(conversorD(dateTime), conversorT(dateTime));
    }
    
    public Date conversorLD(LocalDate data) throws ParseException{
        return this.formatoDT.parse(data.format(this.formatoLD));
    }
    
    public Date conversorLT(LocalTime hora) throws ParseException{
        return this.formatoDT.parse(hora.format(this.formatoLT));
    }
    
    public Date conversorLDT(LocalDateTime dt) throws ParseException{
        return this.formatoDT.parse(dt.format(this.formatoLDT));
    }
}
