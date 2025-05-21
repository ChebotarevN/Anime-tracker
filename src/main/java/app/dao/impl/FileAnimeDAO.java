package app.dao.impl;

import app.dao.AnimeDAO;
import app.model.Status;
import app.model.Anime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileAnimeDAO implements AnimeDAO {
    private final List<Anime> animes;
    private File file;

    public FileAnimeDAO(File file) throws Exception {
        this.file = file;
        animes = new ArrayList<>();
        Scanner in = new Scanner(file);
        while (in.hasNext()) {
            String[] line = in.nextLine().split(",");
            int id = Integer.parseInt(line[0]);
            String picture = line[1];
            String title = line[2];
            Status status = Status.getEnum(line[3]);
            int maxExpisode = Integer.parseInt(line[4]);
            int currentEpisode = Integer.parseInt(line[5]);
            Anime p = new Anime(id, picture, title, status, maxExpisode, currentEpisode);
            animes.add(p);
        }
    }

    @Override
    public List<Anime> getAllAnimes() {
        return animes;
    }

    @Override
    public Anime getAnimeById(int id) {
        return animes.get(id);
    }

    @Override
    public void addAnime(Anime anime) {
        if (animes.isEmpty()) {
            anime.setId(1);
        } else {
            anime.setId(animes.getLast().getId() + 1);
        }
        animes.add(anime);
        writeFile();
    }

    @Override
    public void updateAnime(Anime anime) {
        for (int i = 0; i < animes.size(); i++) {
            if (animes.get(i).getId() == anime.getId()) {
                animes.set(i, anime);
                break;
            }
        }
        writeFile();
    }

    @Override
    public void deleteAnime(int id) {
        for (int i = 0; i < animes.size(); i++) {
            if (animes.get(i).getId() == id) {
                animes.remove(i);
                break;
            }
        }
        writeFile();
    }

    private void writeFile() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (Anime t : animes) {
                fileWriter.append(t.getId() + "," + t.getUrlPicture() + "," + t.getTitle() + "," + t.getStatus() + "," + t.getMaxEpisode() + "," + t.getCurrectEpisode() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
