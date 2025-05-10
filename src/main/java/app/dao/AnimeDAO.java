package app.dao;

import app.model.Anime;
import java.util.List;

public interface AnimeDAO {
    List<Anime> getAllAnimes();
    Anime getAnimeById(int id);
    void addAnime(Anime anime);
    void updateAnime(Anime anime);
    void deleteAnime(int id);
}