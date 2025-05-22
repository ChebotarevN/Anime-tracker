package app;

import app.dao.AnimeDAO;
import app.model.Anime;

import java.util.List;

public class Synchronization {
    public void sync(AnimeDAO listFILE, AnimeDAO listBD, AnimeDAO listAPI) throws Exception {
        List<Anime> list = listAPI.getAllAnimes();
        for (Anime a: listFILE.getAllAnimes()) {
            if (list.stream().noneMatch(p -> p.getTitle().equals(a.getTitle()))) {
                list.add(a);
            }
        }
        for (Anime a: listBD.getAllAnimes()) {
            if (list.stream().noneMatch(p -> p.getTitle().equals(a.getTitle()))) {
                list.add(a);
            }
        }

        for (Anime a: list) {
            if (listFILE.getAllAnimes().stream().noneMatch(p -> p.getTitle().equals(a.getTitle()))) {
                listFILE.addAnime(a);
            }
            if (listBD.getAllAnimes().stream().noneMatch(p -> p.getTitle().equals(a.getTitle()))) {
                listBD.addAnime(a);
            }
            if (listAPI.getAllAnimes().stream().noneMatch(p -> p.getTitle().equals(a.getTitle()))) {
                listAPI.addAnime(a);
            }
        }
    }
}
