package pallopelit.dao;


import pallopelit.dao.PeliDao;
import pallopelit.dao.Dao;
import pallopelit.database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import pallopelit.domain.Pelaaja;

public class PelaajaDao implements Dao<Pelaaja, Integer> {

    private Database database;

    public PelaajaDao(Database database) {
         this.database = database;
    }

    @Override
    public Pelaaja findOne(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pelaajat WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Pelaaja ent = new Pelaaja(rs.getInt("id"), rs.getString("nimi"));            
        ent.setVoitot(rs.getInt("score"));
        ent.setPelit(rs.getInt("pelit"));
  
        stmt.close();
        rs.close();
        conn.close();

        return ent;
    }

    @Override
    public ArrayList<Pelaaja> findAll() throws SQLException {
	Connection conn = database.getConnection();
        ArrayList<Pelaaja> pelaajat = new ArrayList<>();
        
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pelaajat");

        ResultSet rs = stmt.executeQuery();
        
        while(rs.next()) 
        {
          Pelaaja ent = new Pelaaja(rs.getInt("id"), rs.getString("nimi"));
          ent.setVoitot(rs.getInt("score"));
          ent.setPelit(rs.getInt("pelit"));
          pelaajat.add(ent);
        }
        
        stmt.close();
        rs.close();
        conn.close();

        return pelaajat;
    }

    @Override
    public Pelaaja saveOrUpdate(Pelaaja object) throws SQLException {
        Pelaaja byName = findByName(object.getNimi());

        if (byName != null) {
            return byName;
        } 

        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Pelaajat (nimi,score,pelit) VALUES (?,?,?)");
            stmt.setString(1, object.getNimi());
            stmt.setInt(2, 0);
            stmt.setInt(3, 0);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        }

        return findByName(object.getNimi());
    }

    public Pelaaja findByName(String nimi) throws SQLException {
        PeliDao peliDao = new PeliDao(database);
        
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pelaajat WHERE nimi = ?");
            stmt.setString(1, nimi);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }

            Pelaaja pelaaja = new Pelaaja(rs.getInt("id"), rs.getString("nimi"));
            pelaaja.setVoitot(rs.getInt("score"));
            pelaaja.setPelit(rs.getInt("pelit"));
            return pelaaja;
        }
    }
    
    public Pelaaja update(Pelaaja object) throws SQLException {
        Pelaaja byName = findByName(object.getNimi());
        
        if (!Objects.equals(byName.getVoitot(), object.getVoitot())) {
            try (Connection conn = database.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Pelaajat SET score=? WHERE nimi=?");
                stmt.setInt(1, object.getVoitot());
                stmt.setString(2, object.getNimi());
                stmt.executeUpdate();
                stmt.close();
                conn.close();                    
            }
        }

        if (!Objects.equals(byName.getPelit(), object.getPelit())) {
            try (Connection conn = database.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Pelaajat SET pelit=? WHERE nimi=?");
                stmt.setInt(1, object.getPelit());
                stmt.setString(2, object.getNimi());
                stmt.executeUpdate();
                stmt.close();
                conn.close();
            }
        }
        
        return findByName(object.getNimi());
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

