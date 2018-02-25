package pallopelit.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pallopelit.database.Database;
import pallopelit.domain.Pelaaja;
import pallopelit.domain.PelaajaPeli;
import pallopelit.domain.Peli;


public class PelaajaPeliDao implements Dao<PelaajaPeli, Integer> {

    private Database database;

    public PelaajaPeliDao(Database database) {
        this.database = database;
    }

    @Override
    public PelaajaPeli findOne(Integer key) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM PelaajaPeli WHERE id=?");
            stmt.setInt(1,key);
            
            ResultSet rs = stmt.executeQuery();
        
            boolean hasOne = rs.next();
            if (!hasOne) {
                return null;
            }

            PelaajaPeli ent = new PelaajaPeli(rs.getInt("id"), 
                rs.getInt("pelaaja_id"),rs.getInt("peli_id"));
  
            stmt.close();
            rs.close();
            conn.close();

            return ent;
        }
    }

    @Override
    public ArrayList<PelaajaPeli> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PelaajaPeli saveOrUpdate(PelaajaPeli object) throws SQLException {
        PelaajaDao pdao = new PelaajaDao(database);
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO PelaajaPeli (pelaaja_id, peli_id) VALUES (?,?)");
            stmt.setInt(1, object.getPelaajaId());
            stmt.setInt(2, object.getPeliId());
            stmt.executeUpdate();
            
            return findOne(pdao.findOne(object.getPelaajaId()).getId());
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public ArrayList<PelaajaPeli> findByPelaaja(Pelaaja object) throws SQLException{
        ArrayList<PelaajaPeli> plp = new ArrayList<>();
        
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM PelaajaPeli WHERE pelaaja_id = ?");
            stmt.setInt(1, object.getId());
            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) 
            {
              PelaajaPeli ent = new PelaajaPeli(rs.getInt("id"), 
                      rs.getInt("pelaaja_id"), rs.getInt("peli_id"));
              plp.add(ent);
            }

            stmt.close();
            rs.close();
            conn.close();

            return plp;
        }
    }
    public ArrayList<PelaajaPeli> findByPeli(Peli object) throws SQLException{
        ArrayList<PelaajaPeli> plp = new ArrayList<>();
        
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM PelaajaPeli WHERE peli_id = ?");
            stmt.setInt(1, object.getId());
            
            ResultSet rs = stmt.executeQuery();
        
            while(rs.next()) 
            {
              PelaajaPeli ent = new PelaajaPeli(rs.getInt("id"), 
                      rs.getInt("pelaaja_id"), rs.getInt("peli_id"));
              plp.add(ent);
            }

            stmt.close();
            rs.close();
            conn.close();

            return plp;
        }
    }
}
