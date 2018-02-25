package pallopelit.dao;

import pallopelit.database.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import pallopelit.domain.Pelaaja;
import pallopelit.domain.Peli;

public class PeliDao implements Dao<Peli, Integer> {

    private Database database;

    public PeliDao(Database database) {
         this.database = database;
    }

    @Override
    public Peli findOne(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pelit WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Peli ent = new Peli(rs.getInt("id"),rs.getString("nimi"));
        ent.setVoittaja(rs.getString("voittaja"));
  
        stmt.close();
        rs.close();
        conn.close();

        return ent;
    }

    @Override
    public ArrayList<Peli> findAll() throws SQLException {
	Connection conn = database.getConnection();
        ArrayList<Peli> pelit = new ArrayList<>();
        
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pelit");

        ResultSet rs = stmt.executeQuery();
        
        while(rs.next()) 
        {
          Peli ent = new Peli(rs.getInt("id"),rs.getString("nimi")); 
          pelit.add(ent);
        }
        
        stmt.close();
        rs.close();
        conn.close();

        return pelit;
    }
    
    public ArrayList<Peli> findAllIncompleted() throws SQLException {
	Connection conn = database.getConnection();
        ArrayList<Peli> pelit = new ArrayList<>();
        
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pelit WHERE fin=?");
        stmt.setBoolean(1,false);
        ResultSet rs = stmt.executeQuery();
        
        while(rs.next()) 
        {
          Peli ent = new Peli(rs.getInt("id"),rs.getString("nimi")); 
          pelit.add(ent);
        }
        
        stmt.close();
        rs.close();
        conn.close();

        return pelit;
    }

    @Override
    public Peli saveOrUpdate(Peli object) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Pelit (nimi,fin,voittaja) VALUES (?,?,?)");
            stmt.setString(1, object.getNimi());
            stmt.setBoolean(2, false);
            stmt.setString(3, "none");
            stmt.executeUpdate();
            stmt.close();
            conn.close();            
            return findByNimi(object.getNimi());
        }
    }
    
    public void update(Peli object) throws SQLException { 
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Pelit SET fin=?, voittaja=? WHERE id=?");
            stmt.setBoolean(1, true);
            stmt.setString(2, object.getVoittaja());
            stmt.setInt(3, object.getId());
            stmt.executeUpdate();
            stmt.close();
            conn.close();            
        }   
    }
    
    @Override
    public void delete(Integer key) throws SQLException {
        // not implemented
    }

    public Peli findByNimi(String nimi) throws SQLException {
        ArrayList<Peli> pelit = new ArrayList<>();
        
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Pelit WHERE nimi = ?");
            stmt.setString(1, nimi);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }
            
            return new Peli(rs.getInt("id"),rs.getString("nimi"));
        }
    }
}


