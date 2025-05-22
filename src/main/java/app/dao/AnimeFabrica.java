package app.dao;

import app.dao.impl.APIAnimeDAO;
import app.dao.impl.DBAnimeDAO;
import app.dao.impl.FileAnimeDAO;

import java.io.File;
import java.util.Scanner;

public class AnimeFabrica {
    public static String BD = "База данных";
    public static String FILE = "Файл";
    public static String API = "MyAnimeList";

    public static AnimeDAO createDAO(String type) throws Exception {
        if (type.equalsIgnoreCase(BD)) {
            return new DBAnimeDAO();
        } else if (type.equalsIgnoreCase(FILE)) {
                File f = new File("src/main/resources/setting.txt");
                Scanner in = new Scanner(f);
                return new FileAnimeDAO(new File(in.nextLine()));
        } else if (type.equalsIgnoreCase(API)) {
            return new APIAnimeDAO();
        } else {
            throw new IllegalArgumentException("Invalid datasource type!");
        }
    }
}
