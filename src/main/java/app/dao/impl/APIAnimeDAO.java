package app.dao.impl;

import app.dao.AnimeDAO;
import app.model.Anime;
import app.model.Status;
import io.github.exortions.dotenv.DotEnv;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class APIAnimeDAO implements AnimeDAO {
    private List<Anime> animes = new ArrayList<>();
    private String code;
    private String name;

    public APIAnimeDAO() throws Exception {
        DotEnv dotEnv = new DotEnv(new File(".env"));
        dotEnv.loadParams();
        this.code = dotEnv.getParameter("BEARER_TOKEN");
        this.name = dotEnv.getParameter("NICKNAME");
        addAnime(Status.WATCHING, "?status=watching");
        addAnime(Status.COMPLETED, "?status=completed");
        addAnime(Status.PLAN_TO_WATCH, "?status=plan_to_watch");
        addAnime(Status.ON_HOLD, "?status=on_hold");
        addAnime(Status.DROPPED, "?status=dropped");
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
    public void addAnime(Anime anime) throws Exception {
        Anime newAnime = searchAnime(anime.getTitle());
        if (newAnime == null) return;
        URL url = new URL("https://api.myanimelist.net/v2/anime/" + newAnime.getId() + "/my_list_status");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Authorization", "Bearer " + code);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes("status=" + anime.getStatus().name().toLowerCase() + "&num_watched_episodes=" + anime.getCurrectEpisode());
        out.flush();
        out.close();
        con.getInputStream();
        con.disconnect();
        newAnime.setStatus(anime.getStatus());
        animes.add(newAnime);
    }

    @Override
    public void updateAnime(Anime anime) throws IOException {
        if (anime == null) return;
        URL url = new URL("https://api.myanimelist.net/v2/anime/" + anime.getId() + "/my_list_status");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Authorization", "Bearer " + code);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes("status=" + anime.getStatus().name().toLowerCase() + "&num_watched_episodes=" + anime.getCurrectEpisode());
        con.getInputStream();
        con.disconnect();
        for (int i = 0; i < animes.size(); i++) {
            if (animes.get(i).getId() == anime.getId()) {
                animes.set(i, anime);
                break;
            }
        }
    }

    @Override
    public void deleteAnime(int id) throws IOException {
        Anime anime = null;
        for (int i = 0; i < animes.size(); i++) {
            if (animes.get(i).getId() == id) {
                anime = animes.get(i);
                animes.remove(i);
                break;
            }
        }
        if (anime == null) return;
        URL url = new URL("https://api.myanimelist.net/v2/anime/" + anime.getId() + "/my_list_status");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Authorization", "Bearer " + code);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setDoOutput(true);
        final DataOutputStream out = new DataOutputStream(con.getOutputStream());
        con.getInputStream();
        con.disconnect();
    }

    private String readURL(HttpURLConnection con) {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } catch (final Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private void addAnime(Status status, String param) throws Exception {
        final URL url = new URL("https://api.myanimelist.net/v2/users/" + name + "/animelist" + param + "&fields=id,title,main_picture,num_episodes,my_list_status");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + code);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String result = readURL(con);
        con.disconnect();
        if (!result.contains("{\"node\"")) return;
        String s = result.substring(result.indexOf("{\"node\""), result.lastIndexOf("]"));
        while (s.contains("{")) {
            int id = Integer.parseInt(s.substring(s.indexOf("\"id\":") + 5, s.indexOf(",", 1)));
            s = s.substring(s.indexOf(",", 1) + 1);
            String title = s.substring(s.indexOf("\"title\":") + 9, s.indexOf("\","));
            s = s.substring(s.indexOf(",") + 1);
            String URLpicture = s.substring(s.indexOf("\"medium\":") + 10, s.indexOf("\",")).replace("\\", "");
            s = s.substring(s.indexOf("},") + 2);
            int maxEpisode = Integer.parseInt(s.substring(s.indexOf("\"num_episodes\":") + 15, s.indexOf(",")));
            s = s.substring(s.indexOf("\"num_episodes_watched\":"));
            int curentEpisode = Integer.parseInt(s.substring(s.indexOf("\"num_episodes_watched\":") + 23, s.indexOf(",")));
            s = s.substring(s.indexOf("}}}") + 3);
            Anime anime = new Anime(id, URLpicture, title, status, maxEpisode, curentEpisode);
            animes.add(anime);
        }
    }

    private Anime searchAnime(String name) throws Exception {
        final URL url = new URL("https://api.myanimelist.net/v2/anime?q=" + name.replace(" ", "") + "&limit=20");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + code);
        String result = readURL(con);
        con.disconnect();
        String s = result.substring(result.indexOf("{\"node\""), result.lastIndexOf("]"));
        while (s.contains("{")) {
            int id = Integer.parseInt(s.substring(s.indexOf("\"id\":") + 5, s.indexOf(",")));
            s = s.substring(s.indexOf(",") + 1);
            String title = s.substring(s.indexOf("\"title\":") + 9, s.indexOf("\","));
            s = s.substring(s.indexOf(",") + 1);
            String URLpicture = s.substring(s.indexOf("\"medium\":") + 10, s.indexOf("\",")).replace("\\", "");
            s = s.substring(s.indexOf(",") + 1);
            s = s.substring(s.indexOf("{") + 1);
            if (title.equals(name)) {
                Anime anime = new Anime(id, URLpicture, title, Status.COMPLETED, 1, 0);
                return anime;
            }
        }
        return null;
    }
}
