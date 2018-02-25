package pallopelit.domain;

public class PelaajaPeli {

    private Integer id;
    private Integer pelaajaId;
    private Integer peliId;

    public PelaajaPeli(Integer id, Integer pelaajaId, Integer peliId) {
        this.id = id;
        this.pelaajaId = pelaajaId;
        this.peliId = peliId;
    }

    public Integer getId() {
        return this.id;
    }
  
    public Integer getPeliId() {
        return this.peliId;
    }

    public Integer getPelaajaId() {
        return this.pelaajaId;
    }  
}
