package app.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Anime {
    private int id;
    private ImageView picture;
    private String urlPicture;
    private String title;
    private Status status;

    public Anime(int id, String URLpicture, String title, Status status) {
        this.id = id;
        this.title = title;
        setUrlPicture(URLpicture);
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void setPicture(String urlPicture) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(250);
        imageView.setImage(new Image(urlPicture));
        this.picture = imageView;
    }

    public ImageView getPicture() {
        return picture;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
        setPicture(urlPicture);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
