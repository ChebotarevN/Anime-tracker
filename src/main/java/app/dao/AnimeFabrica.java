package app.dao;

import app.animelist.Controller;
import app.dao.impl.FileAnimeDAO;

public class AnimeFabrica {
    public static String BD = "База данных";
    public static String FILE = "Файл";
    public static String API = "MyAnimeList";

    public static AnimeDAO createDAO(String type) throws Exception {
        if (type.equalsIgnoreCase(BD)) {
            //return new DBAnimeDAO();
        } else if (type.equalsIgnoreCase(FILE)) {
            return new FileAnimeDAO(Controller.selectFile());
        } else if (type.equalsIgnoreCase(API)) {
            //return new AnimeListDAO();
        } else {
            throw new IllegalArgumentException("Invalid datasource type!");
        }
        return null;
    }
}
