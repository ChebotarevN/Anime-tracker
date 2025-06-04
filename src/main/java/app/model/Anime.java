package app.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Класс, представляющий аниме-запись с информацией о просмотре.
 * Содержит основные данные об аниме: название, статус просмотра,
 * количество серий и ссылку на изображение.
 */
public class Anime {
    private int id;
    private ImageView picture;
    private String urlPicture;
    private String title;
    private Status status;
    private int maxEpisode;
    private int currectEpisode;

    /**
     * Конструктор для создания объекта аниме.
     *
     * @param id уникальный идентификатор
     * @param URLpicture URL-адрес изображения обложки
     * @param title название аниме
     * @param status статус просмотра
     * @param maxEpisode общее количество серий
     * @param currectEpisode количество просмотренных серий
     * @throws Exception если параметры недопустимы
     */
    public Anime(int id, String URLpicture, String title, Status status, int maxEpisode, int currectEpisode) throws Exception {
        this.id = id;
        this.title = title;
        setUrlPicture(URLpicture);
        this.status = status;
        setMaxEpisode(maxEpisode);
        setCurrectEpisode(currectEpisode);
    }

    /**
     * Возвращает идентификатор аниме.
     *
     * @return уникальный идентификатор
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор аниме.
     *
     * @param id новый идентификатор
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает название аниме.
     *
     * @return название аниме
     */
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает название аниме.
     *
     * @param title новое название
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Устанавливает изображение обложки по URL.
     *
     * @param urlPicture URL-адрес изображения
     */
    private void setPicture(String urlPicture) {
        try {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(200);
            imageView.setFitHeight(250);
            imageView.setImage(new Image(urlPicture));
            this.picture = imageView;
        } catch (Exception e) {
            this.urlPicture = null;
            picture = null;
        }
    }

    /**
     * Возвращает изображение обложки.
     *
     * @return ImageView с обложкой аниме
     */
    public ImageView getPicture() {
        return picture;
    }

    /**
     * Возвращает URL-адрес обложки.
     *
     * @return строка с URL
     */
    public String getUrlPicture() {
        return urlPicture;
    }

    /**
     * Устанавливает URL-адрес обложки.
     *
     * @param urlPicture новый URL-адрес
     */
    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
        setPicture(urlPicture);
    }

    /**
     * Возвращает текущий статус просмотра.
     *
     * @return статус просмотра
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Устанавливает статус просмотра.
     *
     * @param status новый статус
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Возвращает количество просмотренных серий.
     *
     * @return число просмотренных серий
     */
    public int getCurrectEpisode() {
        return currectEpisode;
    }

    /**
     * Устанавливает количество просмотренных серий.
     *
     * @param currectEpisode новое количество просмотренных серий
     * @throws Exception если количество превышает общее число серий
     */
    public void setCurrectEpisode(int currectEpisode) throws Exception {
        if (currectEpisode > maxEpisode) {
            throw new Exception("Текущая серия не может быть больше максимального");
        } else if (currectEpisode == maxEpisode) {
            status = Status.COMPLETED;
        }
        this.currectEpisode = currectEpisode;
    }

    /**
     * Возвращает общее количество серий.
     *
     * @return общее число серий
     */
    public int getMaxEpisode() {
        return maxEpisode;
    }

    /**
     * Устанавливает общее количество серий.
     *
     * @param maxEpisode новое общее количество серий
     */
    public void setMaxEpisode(int maxEpisode) {
        if (currectEpisode != maxEpisode && status == Status.COMPLETED) {
            status = Status.PLAN_TO_WATCH;
        }
        this.maxEpisode = maxEpisode;
    }
}