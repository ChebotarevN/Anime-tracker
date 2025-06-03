package app.service;

import app.dao.AnimeDAO;
import app.model.Anime;
import app.model.Status;

import java.util.List;

public class AnimeService {
    private final AnimeDAO animeDAO;

    public AnimeService(AnimeDAO animeDAO) {
        this.animeDAO = animeDAO;
    }

    public List<Anime> getAllAnimes() {
        return animeDAO.getAllAnimes();
    }

    public List<Anime> getAnimesByStatus(Status status) {
        return animeDAO.getAllAnimes().stream()
                .filter(anime -> anime.getStatus() == status)
                .toList();
    }

    public Anime getAnimeById(int id) {
        return animeDAO.getAnimeById(id);
    }

    public void addAnime(Anime anime) throws Exception {
        validateAnime(anime);
        animeDAO.addAnime(anime);
    }

    public void updateAnime(Anime anime) throws Exception {
        validateAnime(anime);
        animeDAO.updateAnime(anime);
    }

    public void deleteAnime(int id) throws Exception {
        animeDAO.deleteAnime(id);
    }

    private void validateAnime(Anime anime) throws Exception {
        if (anime.getTitle() == null || anime.getTitle().isEmpty()) {
            throw new Exception("Название аниме не может быть пустым");
        }
        if (anime.getMaxEpisode() <= 0) {
            throw new Exception("Количество серий должно быть положительным");
        }
        if (anime.getCurrectEpisode() < 0 || anime.getCurrectEpisode() > anime.getMaxEpisode()) {
            throw new Exception("Некорректное количество просмотренных серий");
        }
    }
}