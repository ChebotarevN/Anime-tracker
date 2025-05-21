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
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

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
    private TableColumn<Anime, Integer> colMaxEp;
    @FXML
    private TableColumn<Anime, Integer> colCurEp;
    @FXML
    private ComboBox<String> workMode;
    @FXML
    private Label filePath;
    @FXML
    private VBox fileBox;
    @FXML
    private ComboBox<String> statusList;

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
        colMaxEp.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getMaxEpisode()).asObject());
        colCurEp.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCurrectEpisode()).asObject());
        workMode.setItems(FXCollections.observableArrayList(AnimeFabrica.FILE, AnimeFabrica.API));
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
        statusList.setItems(FXCollections.observableArrayList("Все статусы", Status.WATCHING.toString(),
                Status.COMPLETED.toString(), Status.PLAN_TO_WATCH.toString(), Status.ON_HOLD.toString(), Status.DROPPED.toString()));
        statusList.setValue("Все статусы");
        statusList.getSelectionModel().selectedIndexProperty().addListener(
                (_, _, _) -> {
                    updateTable();
                });
    }

    private void updateTable() {
        List<Anime> list = animeList.getAllAnimes();
        switch (statusList.getValue()) {
            case "Смотрю":
                list = list.stream()
                        .filter(anime -> anime.getStatus() == Status.WATCHING)
                        .collect(Collectors.toList());
                animeData = FXCollections.observableArrayList(list);
                break;
            case "Просмотрено":
                list = list.stream()
                        .filter(anime -> anime.getStatus() == Status.COMPLETED)
                        .collect(Collectors.toList());
                animeData = FXCollections.observableArrayList(list);
                break;
            case "Отложено":
                list = list.stream()
                        .filter(anime -> anime.getStatus() == Status.ON_HOLD)
                        .collect(Collectors.toList());
                animeData = FXCollections.observableArrayList(list);
                break;
            case "Брошено":
                list = list.stream()
                        .filter(anime -> anime.getStatus() == Status.DROPPED)
                        .collect(Collectors.toList());
                animeData = FXCollections.observableArrayList(list);
                break;
            case "Запланировано":
                list = list.stream()
                        .filter(anime -> anime.getStatus() == Status.PLAN_TO_WATCH)
                        .collect(Collectors.toList());
                animeData = FXCollections.observableArrayList(list);
                break;
            default:
                animeData = FXCollections.observableArrayList(animeList.getAllAnimes());
                break;
        }
        animeTable.setItems(animeData);
    }

    public void changeWorkMode() throws Exception {
        if (Objects.equals(workMode.getValue(), AnimeFabrica.FILE)) {
            File f = new File("src/main/resources/setting.txt");
            if (f.exists()) {
                Scanner in = new Scanner(f);
                if (in.hasNextLine()) {
                    String path = in.nextLine();
                    filePath.setText(path);
                }
            } else {
                File newFile = Controller.selectFile();
                f.createNewFile();
                FileWriter writer = new FileWriter(f);
                writer.write(newFile.getPath());
                writer.close();
            }
            fileBox.setVisible(true);
        } else {
            fileBox.setVisible(false);
        }
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
        TextField maxEpisode = new TextField();
        maxEpisode.setPromptText("Введите количество серий в аниме");
        TextField currentEpisode = new TextField();
        currentEpisode.setPromptText("Введите номер серии, которую вы посмотрели");

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(10);
        grid.add(new Label("Название:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Ссылка на фото:"), 0, 1);
        grid.add(pictureField, 1, 1);
        grid.add(new Label("Статус:"), 0, 2);
        grid.add(statusComboBox, 1, 2);
        grid.add(new Label("Количество серий в аниме:"), 0, 3);
        grid.add(maxEpisode, 1, 3);
        grid.add(new Label("Просмотренных серий:"), 0, 4);
        grid.add(currentEpisode, 1, 4);
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
                            statusComboBox.getValue(),
                            Integer.parseInt(maxEpisode.getText()),
                            Integer.parseInt(currentEpisode.getText())
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
                try {
                    animeList.addAnime(anime);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                updateTable();
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
        TextField maxEpisode = new TextField();
        maxEpisode.setText(String.valueOf(selected.getMaxEpisode()));
        TextField currentEpisode = new TextField();
        currentEpisode.setText(String.valueOf(selected.getCurrectEpisode()));

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(10);
        grid.add(new Label("Название:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Ссылка на фото:"), 0, 1);
        grid.add(pictureField, 1, 1);
        grid.add(new Label("Статус:"), 0, 2);
        grid.add(statusComboBox, 1, 2);
        grid.add(new Label("Количество серий в аниме:"), 0, 3);
        grid.add(maxEpisode, 1, 3);
        grid.add(new Label("Просмотренных серий:"), 0, 4);
        grid.add(currentEpisode, 1, 4);
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
                            statusComboBox.getValue(),
                            Integer.parseInt(maxEpisode.getText()),
                            Integer.parseInt(currentEpisode.getText()));
                    animeList.updateAnime(change);
                    updateTable();
                    return new Anime(
                            0,
                            pictureField.getText(),
                            titleField.getText(),
                            statusComboBox.getValue(),
                            Integer.parseInt(maxEpisode.getText()),
                            Integer.parseInt(currentEpisode.getText())
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
    private void handleDeleteAnime() throws IOException {
        Anime selected = animeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            animeList.deleteAnime(selected.getId());
            animeData.remove(selected);
        } else {
            showAlert("Ошибка удаления", "Выберите аниме для удаления!");
        }
    }

    private static void showAlert(String title, String message) {
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