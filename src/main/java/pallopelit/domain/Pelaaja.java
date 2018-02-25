package pallopelit.domain;

import java.util.ArrayList;
import java.util.List;

public class Pelaaja {
    private Integer id;
    private String nimi;
    private Integer voitot;
    private Integer pelit;
    
    public Pelaaja(Integer id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public String getNimi() {
        return this.nimi;
    }
    
    public Integer getVoitot() {
        return this.voitot;
    }
    
    public Integer getPelit() {
        return this.pelit;
    }
    
    public void setVoitot(Integer a) {
        this.voitot = a;
    }
    
    public void setPelit(Integer a) {
        this.pelit = a;
    }
    
}
