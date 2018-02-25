package pallopelit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import pallopelit.domain.Peli;
import pallopelit.dao.PelaajaDao;
import pallopelit.dao.PeliDao;
import pallopelit.database.Database;
import java.util.HashMap;
import java.util.Objects;
import pallopelit.dao.PelaajaPeliDao;
import pallopelit.domain.Pelaaja;
import pallopelit.domain.PelaajaPeli;
import spark.ModelAndView;
import spark.Spark;;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {
    
    // Pitää kirjaa siitä mikä peli on parhaillaan käynnissä
    public static Integer ongoing = 0;

    public static void main(String[] args) throws Exception {
        
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        
        // joitakin tarpeellisia luokkia
        Database db = new Database("jdbc:sqlite:pallopelit.db");
        PelaajaDao pelaajaDao = new PelaajaDao(db);
        PelaajaPeliDao pelaajaPeliDao = new PelaajaPeliDao(db);
        PeliDao peliDao = new PeliDao(db);
         
        // kotisivu
        Spark.get("/index", (req, res) -> {
            HashMap map = new HashMap<>();
            
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        // tilastosivu
        Spark.get("/tilastot", (req, res) -> {
            HashMap map = new HashMap<>();
            ArrayList<Pelaaja> pelaajat = pelaajaDao.findAll();
            
            Collections.sort(pelaajat, new Comparator<Pelaaja>() {
                @Override
                public int compare(Pelaaja z1, Pelaaja z2) {
                    if (z1.getVoitot() > z2.getVoitot())
                        return -1;
                    if (z1.getVoitot() < z2.getVoitot())
                        return 1;
                    return 0;
                }
            });
            map.put("pelaajat",pelaajat);
            return new ModelAndView(map, "tilastot");
        }, new ThymeleafTemplateEngine());
        
        // henkilökohtaiset sivut
        Spark.get("/pelaaja/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Pelaaja pelaaja = pelaajaDao.findOne(Integer.parseInt(req.params(":id")));
            ArrayList<PelaajaPeli> plp = pelaajaPeliDao.findByPelaaja(pelaaja);
            ArrayList<Peli> pelit = new ArrayList<>();
            
            for (PelaajaPeli plpe : plp) {
                Peli peli = peliDao.findOne(plpe.getPeliId());
                pelit.add(peli);
            }
            
            for (Peli peli : pelit) {
                ArrayList<PelaajaPeli> pelaajapeli = pelaajaPeliDao.findByPeli(peli);
                ArrayList<Pelaaja> pelaajat = new ArrayList<>();
            
                for (PelaajaPeli pelp : pelaajapeli) {
                    if (Objects.equals(peli.getId(), pelp.getPeliId())) {
                        pelaajat.add(pelaajaDao.findOne(pelp.getPelaajaId()));
                    }
                }
                peli.setPelaajat(pelaajat);
            }
            map.put("pelaaja",pelaaja);
            map.put("pelit", pelit);
            return new ModelAndView(map, "pelaaja");
        }, new ThymeleafTemplateEngine());
        
        // pelisivu
        Spark.get("/peli", (req, res) -> {
            HashMap map = new HashMap<>();

            ArrayList<Peli> pelit = peliDao.findAllIncompleted();
            
            for (Peli peli : pelit) {
                ArrayList<PelaajaPeli> plp = pelaajaPeliDao.findByPeli(peli);
                ArrayList<Pelaaja> pelaajat = new ArrayList<>();
            
                for (PelaajaPeli pelp : plp) {
                    pelaajat.add(pelaajaDao.findOne(pelp.getPelaajaId()));
                }
                peli.setPelaajat(pelaajat);
            }
            
            map.put("pelit",pelit);
            return new ModelAndView(map, "peli");
        }, new ThymeleafTemplateEngine());
        
        // pelikohtainen sivu
        Spark.get("/pelaa/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Peli peli = peliDao.findOne(Integer.parseInt(req.params(":id")));
            ArrayList<PelaajaPeli> plp = pelaajaPeliDao.findByPeli(peli);
            ArrayList<Pelaaja> pelaajat = new ArrayList<>();
            
            for (PelaajaPeli pelp : plp) {
                Pelaaja pelaaja = pelaajaDao.findOne(pelp.getPelaajaId());
                pelaaja.setPelit(pelaaja.getPelit()+1);
                pelaajaDao.update(pelaaja);
                pelaajat.add(pelaaja);
            }
            
            ongoing = peli.getId();
            
            map.put("pelaajat", pelaajat);
            
            return new ModelAndView(map, "pelaa");
        }, new ThymeleafTemplateEngine());
        
        // voitto-event
        Spark.post("/voitto", (req, res) -> { 
            Pelaaja voittaja = pelaajaDao.findByName(req.queryParams("pelaaja_id"));
            Peli peli = peliDao.findOne(ongoing);
            
            if (peli.getFin() == false) {
                voittaja.setVoitot(voittaja.getVoitot()+1);
                pelaajaDao.update(voittaja);
                peli.setFin();
                peli.setVoittaja(voittaja.getNimi());
                peliDao.update(peli);
            }
            
            res.redirect("/index");
            return "";
        });

        // pelin lisääminen
        Spark.post("/peli", (req, res) -> {
            HashMap map = new HashMap<>();
            ArrayList<Pelaaja> pelaajat = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            
            cal.setTime(now);
            String name = "" + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH)
                    + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY)
                    + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

            Pelaaja pelaaja1 = new Pelaaja(-1,req.queryParams("pelaaja1"));
            Pelaaja pelaaja2 = new Pelaaja(-1,req.queryParams("pelaaja2"));
            Pelaaja pelaaja3 = new Pelaaja(-1,req.queryParams("pelaaja3"));
            Pelaaja pelaaja4 = new Pelaaja(-1,req.queryParams("pelaaja4"));
            
            pelaajat.add(pelaajaDao.saveOrUpdate(pelaaja1));
            pelaajat.add(pelaajaDao.saveOrUpdate(pelaaja2));
            pelaajat.add(pelaajaDao.saveOrUpdate(pelaaja3));
            pelaajat.add(pelaajaDao.saveOrUpdate(pelaaja4));
            
            Peli peli = new Peli(-1, name);
            peli = peliDao.saveOrUpdate(peli);
            
            for (Pelaaja pelaaja : pelaajat) {
                pelaajaPeliDao.saveOrUpdate(new PelaajaPeli(-1,pelaaja.getId(),peli.getId()));
            }
            
            res.redirect("/peli");
                        
            return "";
        });
    }
}