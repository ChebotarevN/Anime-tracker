package app.service;

import app.model.Anime;

import java.util.ArrayList;
import java.util.List;

public class Synchronization {
    public void sync(AnimeService source1, AnimeService source2, AnimeService source3) throws Exception {
        List<Anime> combinedList = new ArrayList<>(source1.getAllAnimes());

        for (Anime anime : source2.getAllAnimes()) {
            if (combinedList.stream().noneMatch(a -> a.getTitle().equals(anime.getTitle()))) {
                combinedList.add(anime);
            }
        }

        for (Anime anime : source3.getAllAnimes()) {
            if (combinedList.stream().noneMatch(a -> a.getTitle().equals(anime.getTitle()))) {
                combinedList.add(anime);
            }
        }

        for (Anime anime : combinedList) {
            if (source1.getAllAnimes().stream().noneMatch(a -> a.getTitle().equals(anime.getTitle()))) {
                source1.addAnime(anime);
            }
            if (source2.getAllAnimes().stream().noneMatch(a -> a.getTitle().equals(anime.getTitle()))) {
                source2.addAnime(anime);
            }
            if (source3.getAllAnimes().stream().noneMatch(a -> a.getTitle().equals(anime.getTitle()))) {
                source3.addAnime(anime);
            }
        }
    }
}