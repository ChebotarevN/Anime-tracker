package app.animelist;

import app.dao.AnimeDAO;
import app.dao.AnimeFabrica;
import app.model.Anime;
import app.model.Status;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

public class Controller {
    @FXML
    private TableView<Anime> animeTable;
    @FXML
    private TableColumn<Anime, Integer> colId;
    @FXML
    private TableColumn<Anime, String> colTitle;
    @FXML
    private TableColumn<Anime, ImageView> colPicture;
    @FXML
    private TableColumn<Anime, Status> colStatus;
    @FXML
    private ComboBox<String> workMode;

    private AnimeDAO animeList;
    private ObservableList<Anime> animeData;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colTitle.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTitle()));
        colPicture.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getPicture()));
        colStatus.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getStatus()));
        workMode.setItems(FXCollections.observableArrayList(AnimeFabrica.FILE));
        workMode.getSelectionModel().selectedIndexProperty().addListener(
                (_, _, _) -> {
                    try {
                        changeWorkMode();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void changeWorkMode() throws Exception {
        animeList = AnimeFabrica.createDAO(workMode.getValue());
        animeData = FXCollections.observableArrayList(animeList.getAllAnimes());
        animeTable.setItems(animeData);
    }

    @FXML
    private void handleAddAnime() {
        // Создание диалогового окна
        Dialog<Anime> dialog = new Dialog<>();
        dialog.setTitle("Добавить Аниме");

        // Кнопки
        ButtonType addButton = new ButtonType("Добавить аниме", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, cancelButton);

        // Поля ввода
        TextField titleField = new TextField();
        titleField.setPromptText("Введите название аниме");
        TextField pictureField = new TextField();
        pictureField.setPromptText("Введите ссылку на фото");
        ComboBox<Status> statusComboBox = new ComboBox<>(FXCollections.observableArrayList(Status.WATCHING,
                Status.COMPLETED, Status.PLAN_TO_WATCH, Status.ON_HOLD, Status.DROPPED));
        statusComboBox.setPromptText("Выберите статус аниме");

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(10);
        grid.add(new Label("Название:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Ссылка на фото:"), 0, 1);
        grid.add(pictureField, 1, 1);
        grid.add(new Label("Статус:"), 0, 2);
        grid.add(statusComboBox, 1, 2);
        dialog.getDialogPane().setContent(grid);

        // Обработка результата
        dialog.setResultConverter(buttonType -> {
            try {
                if (buttonType == addButton) {
                    if (Objects.equals(titleField.getText(), ""))
                        throw new Exception("Пустая строка в названии аниме");
                    return new Anime(
                            0,
                            pictureField.getText(),
                            titleField.getText(),
                            statusComboBox.getValue()
                    );
                }
            } catch (NullPointerException e) {
                showAlert("Ошибка ввода", "Выберите значение в поле статус");
            } catch (Exception e) {
                showAlert("Ошибка ввода", e.getMessage());
            }
            return null;
        });
        dialog.showAndWait().ifPresent(anime -> {
            if (anime != null) {
                animeList.addAnime(anime);
                animeData.setAll(animeList.getAllAnimes());
            }
        });
    }

    @FXML
    private void handleChangeAnime() {
        Anime selected = animeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка изменения", "Выберите аниме для изменения!");
            return;
        }
        // Создание диалогового окна
        Dialog<Anime> dialog = new Dialog<>();
        dialog.setTitle("Изменить аниме");

        // Кнопки
        ButtonType addButton = new ButtonType("Изменить", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, cancelButton);

        // Поля ввода
        TextField titleField = new TextField();
        titleField.setText(selected.getTitle());
        TextField pictureField = new TextField();
        pictureField.setText(selected.getUrlPicture());
        ComboBox<Status> statusComboBox = new ComboBox<>(FXCollections.observableArrayList(Status.WATCHING,
                Status.COMPLETED, Status.PLAN_TO_WATCH, Status.ON_HOLD, Status.DROPPED));
        statusComboBox.setValue(selected.getStatus());

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(10);
        grid.add(new Label("Название:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Ссылка на фото:"), 0, 1);
        grid.add(pictureField, 1, 1);
        grid.add(new Label("Статус:"), 0, 2);
        grid.add(statusComboBox, 1, 2);
        dialog.getDialogPane().setContent(grid);

        // Обработка результата
        dialog.setResultConverter(buttonType -> {
            try {
                if (buttonType == addButton) {
                    if (Objects.equals(titleField.getText(), ""))
                        throw new Exception("Пустая строка в названии аниме");
                    Anime change = new Anime(selected.getId(),
                            pictureField.getText(),
                            titleField.getText(),
                            statusComboBox.getValue());
                    animeList.updateAnime(change);
                    animeData.setAll(animeList.getAllAnimes());
                    return new Anime(
                            0,
                            pictureField.getText(),
                            titleField.getText(),
                            statusComboBox.getValue()
                    );
                }
            } catch (NullPointerException e) {
                showAlert("Ошибка ввода", "Выберите значение в поле статус");
            } catch (Exception e) {
                showAlert("Ошибка ввода", e.getMessage());
            }
            return null;
        });
        dialog.showAndWait();
        animeTable.refresh();
    }

    @FXML
    private void handleDeleteAnime() {
        Anime selected = animeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            animeList.deleteAnime(selected.getId());
            animeData.remove(selected);
        } else {
            showAlert("Ошибка удаления", "Выберите аниме для удаления!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static File selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Текстовый файл (.txt)", ".txt"));
        return fileChooser.showOpenDialog(null);
    }
}