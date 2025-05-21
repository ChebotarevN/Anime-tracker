package app.dao.impl;

import app.dao.AnimeDAO;
import app.model.Anime;
import app.model.Status;
import io.github.exortions.dotenv.DotEnv;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBAnimeDAO implements AnimeDAO {
    private final List<Anime> animes;
    private DotEnv dotEnv;

    public DBAnimeDAO() {
        animes = new ArrayList<>();
        try {
            dotEnv = new DotEnv(new File(".env"));
            dotEnv.loadParams();
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:animeList.db");
            Statement stmt = connection.createStatement();
            String query = "";
            query = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                    "id INTEGER PRIMARY KEY," +
                    "title TEXT NOT NULL," +
                    "picture TEXT NOT NULL," +
                    "status TEXT NOT NULL," +
                    "maxepisode INTEGER NOT NULL," +
                    "currentepisode INTEGER NOT NULL);", dotEnv.getParameter("TABLE_NAME"));
            stmt.execute(query);
            ResultSet resSet = stmt.executeQuery("SELECT * FROM " + dotEnv.getParameter("TABLE_NAME"));
            while(resSet.next())
            {
                int id = resSet.getInt("id");
                String title = resSet.getString("title");
                String picture = resSet.getString("picture");
                Status status = Status.getEnum(resSet.getString("status"));
                int maxEpisode = resSet.getInt("maxepisode");
                int currentEpisode = resSet.getInt("currentepisode");
                Anime anime = new Anime(id, picture, title, status, maxEpisode, currentEpisode);
                animes.add(anime);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Anime> getAllAnimes() {
        return animes;
    }

    @Override
    public Anime getAnimeById(int id) {
        for (Anime anime : animes) {
            if (anime.getId() == id) {
                return anime;
            }
        }
        return null;
    }

    @Override
    public void addAnime(Anime anime) {
        if (animes.isEmpty()) {
            anime.setId(1);
        } else {
            anime.setId(animes.getLast().getId() + 1);
        }
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:animeList.db");
            String query = String.format("INSERT INTO %s (" +
                    "id, title, picture, status, maxepisode, currentepisode)" +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');", dotEnv.getParameter("TABLE_NAME"), anime.getId(), anime.getTitle(),
                    anime.getUrlPicture(), anime.getStatus(), anime.getMaxEpisode(), anime.getCurrectEpisode());
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            animes.add(anime);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAnime(Anime anime) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:animeList.db");
            String query = String.format("UPDATE %s SET title = '%s', picture = '%s', status = '%s', " +
                            "maxepisode = '%s', currentepisode = '%s' WHERE ID = %d", dotEnv.getParameter("TABLE_NAME"), anime.getTitle(), anime.getUrlPicture(),
                    anime.getStatus(), anime.getMaxEpisode(), anime.getCurrectEpisode(), anime.getId());
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            connection.close();
            for (int i = 0; i < animes.size(); i++) {
                if (animes.get(i).getId() == anime.getId()) {
                    animes.set(i, anime);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAnime(int id) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:animeList.db");
            String query = String.format("DELETE FROM %s WHERE ID = %d", dotEnv.getParameter("TABLE_NAME"), id);
            Statement stmt = connection.createStatement();
            stmt.execute(query);
            connection.close();
            for (int i = 0; i < animes.size(); i++) {
                if (animes.get(i).getId() == id) {
                    animes.remove(i);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}