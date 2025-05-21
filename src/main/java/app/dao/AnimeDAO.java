package app.dao;

import app.model.Anime;

import java.io.IOException;
import java.util.List;

public interface AnimeDAO {
    List<Anime> getAllAnimes();
    Anime getAnimeById(int id);
    void addAnime(Anime anime) throws Exception;
    void updateAnime(Anime anime) throws IOException;
    void deleteAnime(int id) throws IOException;
}