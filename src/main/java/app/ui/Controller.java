package app.ui;

import app.dao.AnimeFabrica;
import app.model.Anime;
import app.model.Status;
import app.service.AnimeService;
import app.service.Synchronization;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Controller {
    @FXML private TableView<Anime> animeTable;
    @FXML private TableColumn<Anime, Integer> colId;
    @FXML private TableColumn<Anime, String> colTitle;
    @FXML private TableColumn<Anime, ImageView> colPicture;
    @FXML private TableColumn<Anime, Status> colStatus;
    @FXML private TableColumn<Anime, Integer> colMaxEp;
    @FXML private TableColumn<Anime, Integer> colCurEp;
    @FXML private ComboBox<String> workMode;
    @FXML private Label filePath;
    @FXML private VBox fileBox;
    @FXML private ComboBox<String> statusList;

    private AnimeService animeService;
    private ObservableList<Anime> animeData;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupWorkModeComboBox();
        setupStatusFilter();
    }

    private void setupTableColumns() {
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
    }

    private void setupWorkModeComboBox() {
        workMode.setItems(FXCollections.observableArrayList(
                AnimeFabrica.FILE,
                AnimeFabrica.API,
                AnimeFabrica.BD
        ));
        workMode.getSelectionModel().selectedItemProperty().addListener(
                (_, _, _) -> {
                    try {
                        changeWorkMode();
                    } catch (Exception e) {
                        showAlert("Ошибка", e.getMessage());
                    }
                });
    }

    private void setupStatusFilter() {
        statusList.setItems(FXCollections.observableArrayList(
                "Все статусы",
                Status.WATCHING.toString(),
                Status.COMPLETED.toString(),
                Status.PLAN_TO_WATCH.toString(),
                Status.ON_HOLD.toString(),
                Status.DROPPED.toString()
        ));
        statusList.setValue("Все статусы");
        statusList.getSelectionModel().selectedItemProperty().addListener(
                (_, _, _) -> updateTable());
    }

    public void changeWorkMode() throws Exception {
        if (Objects.equals(workMode.getValue(), AnimeFabrica.FILE)) {
            File f = new File("src/main/resources/setting.txt");
            if (f.exists()) {
                Scanner in = new Scanner(f);
                if (in.hasNextLine()) {
                    String path = in.nextLine();
                    filePath.setText(path);
                } else {
                    changeFilePath();
                }
                animeService = new AnimeService(AnimeFabrica.createDAO(AnimeFabrica.FILE));
            } else {
                changeFilePath();
                animeService = new AnimeService(AnimeFabrica.createDAO(AnimeFabrica.FILE));
            }
            fileBox.setVisible(true);
        } else {
            fileBox.setVisible(false);
            if (Objects.equals(workMode.getValue(), AnimeFabrica.BD)) {
                animeService = new AnimeService(AnimeFabrica.createDAO(AnimeFabrica.BD));
            } else {
                animeService = new AnimeService(AnimeFabrica.createDAO(AnimeFabrica.API));
            }

        }

        updateTable();
    }

    public void changeFilePath() {
        try {
            FileWriter fileWriter = new FileWriter("src/main/resources/setting.txt");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Открыть файл");
            FileChooser.ExtensionFilter extFilter = new
                    FileChooser.ExtensionFilter("Текстовый файл (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File f = fileChooser.showOpenDialog(null);
            fileWriter.write(f.getAbsolutePath());
            fileWriter.close();
            updateFilePathLabel();
        } catch (Exception e) {
            filePath.setText("Файл не удалось выбрать");
        }
    }

    private void updateFilePathLabel() {
        try {
            Scanner scanner = new Scanner(new File("src/main/resources/setting.txt"));
            if (scanner.hasNextLine()) {
                filePath.setText(scanner.nextLine());
            }
            scanner.close();
        } catch (Exception e) {
            changeFilePath();
        }
    }

    private void updateTable() {
        if (animeService == null) return;

        List<Anime> list;
        if ("Все статусы".equals(statusList.getValue())) {
            list = animeService.getAllAnimes();
        } else {
            Status status = Status.getEnum(statusList.getValue());
            list = animeService.getAnimesByStatus(status);
        }

        animeData = FXCollections.observableArrayList(list);
        animeTable.setItems(animeData);
    }

    @FXML
    private void handleAddAnime() {
        Dialog<Anime> dialog = createAnimeDialog("Добавить аниме", null);
        dialog.showAndWait().ifPresent(anime -> {
            try {
                animeService.addAnime(anime);
                updateTable();
            } catch (Exception e) {
                showAlert("Ошибка", e.getMessage());
            }
        });
    }

    @FXML
    private void handleChangeAnime() {
        Anime selected = animeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите аниме для изменения!");
            return;
        }

        Dialog<Anime> dialog = createAnimeDialog("Изменить аниме", selected);
        dialog.showAndWait().ifPresent(anime -> {
            try {
                anime.setId(selected.getId());
                animeService.updateAnime(anime);
                updateTable();
            } catch (Exception e) {
                showAlert("Ошибка", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDeleteAnime() {
        Anime selected = animeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                animeService.deleteAnime(selected.getId());
                updateTable();
            } catch (Exception e) {
                showAlert("Ошибка", e.getMessage());
            }
        } else {
            showAlert("Ошибка", "Выберите аниме для удаления!");
        }
    }

    @FXML
    private void synchron() {
        try {
            AnimeService fileService = new AnimeService(AnimeFabrica.createDAO(AnimeFabrica.FILE));
            AnimeService dbService = new AnimeService(AnimeFabrica.createDAO(AnimeFabrica.BD));
            AnimeService apiService = new AnimeService(AnimeFabrica.createDAO(AnimeFabrica.API));
            new Synchronization().sync(fileService, dbService, apiService);
            String currentMode = workMode.getValue();
            animeService = new AnimeService(AnimeFabrica.createDAO(currentMode));
            updateTable();
            showAlert("Успех", "Синхронизация завершена успешно");
        } catch (Exception e) {
            showAlert("Ошибка синхронизации", e.getMessage());
        }
    }

    private Dialog<Anime> createAnimeDialog(String title, Anime existingAnime) {
        Dialog<Anime> dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType confirmButton = new ButtonType(
                existingAnime == null ? "Добавить" : "Сохранить",
                ButtonBar.ButtonData.OK_DONE
        );
        ButtonType cancelButton = new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField();
        titleField.setPromptText("Название");
        TextField pictureField = new TextField();
        pictureField.setPromptText("URL изображения");
        ComboBox<Status> statusCombo = new ComboBox<>(
                FXCollections.observableArrayList(Status.values())
        );
        statusCombo.setPromptText("Статус");
        TextField maxEpField = new TextField();
        maxEpField.setPromptText("Всего серий");
        TextField curEpField = new TextField();
        curEpField.setPromptText("Просмотрено серий");

        if (existingAnime != null) {
            titleField.setText(existingAnime.getTitle());
            pictureField.setText(existingAnime.getUrlPicture());
            statusCombo.setValue(existingAnime.getStatus());
            maxEpField.setText(String.valueOf(existingAnime.getMaxEpisode()));
            curEpField.setText(String.valueOf(existingAnime.getCurrectEpisode()));
        }

        grid.add(new Label("Название:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Изображение:"), 0, 1);
        grid.add(pictureField, 1, 1);
        grid.add(new Label("Статус:"), 0, 2);
        grid.add(statusCombo, 1, 2);
        grid.add(new Label("Всего серий:"), 0, 3);
        grid.add(maxEpField, 1, 3);
        grid.add(new Label("Просмотрено:"), 0, 4);
        grid.add(curEpField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == confirmButton) {
                try {
                    return new Anime(
                            0,
                            pictureField.getText(),
                            titleField.getText(),
                            statusCombo.getValue(),
                            Integer.parseInt(maxEpField.getText()),
                            Integer.parseInt(curEpField.getText())
                    );
                } catch (Exception e) {
                    showAlert("Ошибка ввода", "Проверьте правильность введенных данных");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}