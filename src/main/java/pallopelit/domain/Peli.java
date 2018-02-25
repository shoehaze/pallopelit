package pallopelit.domain;

import pallopelit.domain.Pelaaja;
import java.util.ArrayList;
import java.util.List;

public class Peli {
    private Integer id;
    private String nimi;
    private Boolean fin;
    private String voittaja;
    private ArrayList<Pelaaja> pelaajat;
    
    public Peli(Integer id, String nimi) {
        this.id = id;
        this.nimi = nimi;
        this.fin = false;
        this.voittaja = null;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public String getNimi() {
        return this.nimi;
    }
    
    public void setFin() {
        this.fin=true;
    }
    
    public Boolean getFin() {
        return this.fin;
    }
    
    public ArrayList<Pelaaja> getPelaajat() {
        return this.pelaajat;
    }
    
    public void setPelaajat(ArrayList<Pelaaja> pelaajat) {
        this.pelaajat=pelaajat;
    }
    
    public void setVoittaja(String voittaja) {
        this.voittaja=voittaja;
    }
    
    public String getVoittaja() {
        return this.voittaja;
    }
    
}
