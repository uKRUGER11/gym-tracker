package gym.gymtracker;

import gym.gymtracker.db.DB;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HomeController implements Initializable {
    private double x = 0;
    private double y = 0;



    @FXML
    private Button btnAdd;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnHeavys;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnProgress;

    @FXML
    private Button btnAddProgress;

    @FXML
    private LineChart<?, ?> chartDays;

    @FXML
    private LineChart<Number, Number> chartLevel;

    @FXML
    private AnchorPane formHome;

    @FXML
    private AnchorPane formLoads;

    @FXML
    private AnchorPane formProgress;

    @FXML
    private AnchorPane mainForm;

    @FXML
    private TableColumn<HeavysData, String> tableColumnExercise;

    @FXML
    private TableColumn<HeavysData, Double> tableColumnHeavy;

    @FXML
    private TableColumn<HeavysData, Integer> tableColumnMaxRep;

    @FXML
    private TableColumn<HeavysData, Integer> tableColumnMinRep;

    @FXML
    private TableView<HeavysData> tableViewHeavys;

    @FXML
    private ProgressBar progressBar;

    //@FXML
    //private Button addProgress;

    @FXML
    private Label txtDate;

    @FXML
    private Label percentage;

    @FXML
    private Label txtDate2;

    @FXML
    private Label txtTotalHeavy;

    @FXML
    private Label txtTotalHeavyName;

    @FXML
    private TextField txtExercise;

    @FXML
    private TextField txtHeavy;

    @FXML
    private TextField txtMaxRep;

    @FXML
    private TextField txtMinRep;

    private Statement st;
    private Connection connect;
    private PreparedStatement ps;
    private ResultSet rs;

    public ObservableList<HeavysData> addHeavyList() {
        ObservableList<HeavysData> listHeavys = FXCollections.observableArrayList();
        int currentUserId = AppContext.getCurrentUserId();

        String sql = "SELECT * FROM loads WHERE UserID = ?";

        connect = DB.connectDb();

        try {
            HeavysData heavy;
            ps = connect.prepareStatement(sql);
            ps.setInt(1, currentUserId);
            rs = ps.executeQuery();

            while(rs.next()) {
                heavy = new HeavysData(rs.getInt("Id"), rs.getString("exercise"), rs.getDouble("heavy"), rs.getInt("MaxRep"), rs.getInt("MinRep"), rs.getInt("UserId"));

                if (heavy.getUserId() == rs.getInt("UserId")) {
                    listHeavys.add(heavy);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listHeavys;
    }

    private ObservableList<HeavysData> addHeavyDataList;
    public void addHeavyShowList() {
        addHeavyDataList = addHeavyList();

        tableColumnExercise.setCellValueFactory(new PropertyValueFactory<>("exercise"));
        tableColumnHeavy.setCellValueFactory(new PropertyValueFactory<>("heavy"));
        tableColumnMaxRep.setCellValueFactory(new PropertyValueFactory<>("maxRep"));
        tableColumnMinRep.setCellValueFactory(new PropertyValueFactory<>("minRep"));

        tableViewHeavys.setItems(addHeavyDataList);
    }

    public void addHeavySelect() {
        HeavysData heavys = tableViewHeavys.getSelectionModel().getSelectedItem();
        int num = tableViewHeavys.getSelectionModel().getSelectedIndex();

        if (num < 0) {
            return;
        }

        txtExercise.setText(String.valueOf(heavys.getExercise()));
        Double heavyValue = heavys.getHeavy();
        txtHeavy.setText(String.valueOf(heavyValue));
        txtMaxRep.setText(Integer.toString(heavys.getMaxRep()));
        txtMinRep.setText(Integer.toString(heavys.getMinRep()));
    }


    public void homeDisplayMaxHeavy() {
        String checkMaxHeavy = "SELECT exercise, heavy FROM loads WHERE UserId = ? ORDER BY heavy DESC LIMIT 1";

        connect = DB.connectDb();

        int currentUserId = AppContext.getCurrentUserId();

        try {
            ps = connect.prepareStatement(checkMaxHeavy);
            ps.setInt(1, currentUserId);
            rs = ps.executeQuery();

            if (rs.next()) {
                String exerciseName = rs.getString("exercise");
                double maxHeavy = rs.getDouble("heavy");

                String displayText =  "no " + exerciseName;
                txtTotalHeavy.setText(maxHeavy + "Kg");
                txtTotalHeavyName.setText(displayText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void homeDisplayHeavysLevels() {
        int userId = AppContext.getCurrentUserId();
        chartLevel.getData().clear();

        String levelSql = "SELECT heavy, MinRep, COUNT(heavy) FROM loads WHERE UserId = ? GROUP BY heavy, MinRep";

        connect = DB.connectDb();

        try {
            XYChart.Series<Number, Number> chart = new XYChart.Series<>();
            NumberAxis xAxis = (NumberAxis) chartLevel.getXAxis();
            NumberAxis yAxis = (NumberAxis) chartLevel.getYAxis();
            xAxis.setAutoRanging(false);
            xAxis.setUpperBound(20);
            xAxis.setLowerBound(1);

            ps = connect.prepareStatement(levelSql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            Set<Integer> addedMinReps = new HashSet<>();
            chart.getData().add(new XYChart.Data<>(0, 0));
            while (rs.next()) {
                double heavy = rs.getDouble(1);
                int minRep = rs.getInt(2);
                int count = rs.getInt(3);


                for (int i = 0; i < count; i++) {
                    if (!addedMinReps.contains(minRep)) {
                        chart.getData().add(new XYChart.Data<>(minRep, heavy));
                        addedMinReps.add(minRep);
                    }
                }
            }

            chartLevel.getData().add(chart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void heavysAdd() {
        int currentUserId = AppContext.getCurrentUserId();

        String insertHeavy = "INSERT INTO loads (exercise, heavy, MaxRep, MinRep, UserId) VALUES (?, ?, ?, ?, ?)";

        connect = DB.connectDb();

        try {
            Alert alert;

            if (txtExercise.getText().isEmpty() || txtHeavy.getText().isEmpty() || txtMaxRep.getText().isEmpty() || txtMinRep.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Preencha todos os campos.");
                alert.showAndWait();
            } else {
                String checkHeavy = "SELECT * FROM loads WHERE exercise  = '" + txtExercise.getText() + "'";

                st  = connect.createStatement();
                rs = st.executeQuery(checkHeavy);

                if (rs.next()) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Mensagem de erro");
                    alert.setHeaderText(null);
                    alert.setContentText("O exercício " + txtExercise.getText() + " já existe!");
                    alert.showAndWait();
                } else {
                    ps = connect.prepareStatement(insertHeavy);
                    ps.setString(1, txtExercise.getText());
                    ps.setDouble(2, Double.parseDouble(txtHeavy.getText()));
                    ps.setInt(3, Integer.parseInt(txtMaxRep.getText()));
                    ps.setInt(4, Integer.parseInt(txtMinRep.getText()));
                    ps.setInt(5, currentUserId);

                    ps.executeUpdate();

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Mensagem de informação");
                    alert.setHeaderText(null);
                    alert.setContentText("Exercício adcionado com sucesso!");
                    alert.showAndWait();

                    addHeavyShowList();
                    heavysAddClear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void heavysUpdate() {
        int row = tableViewHeavys.getSelectionModel().getSelectedIndex();
        int currentUserId = AppContext.getCurrentUserId();

        String updateHeavys = "UPDATE loads "
                + "SET exercise = ?, heavy = ?, MaxRep = ?, MinRep = ? "
                + "WHERE UserId = ? AND Id = ?";

        connect = DB.connectDb();

        try {
            Alert alert;

            if (row < 0 || txtExercise.getText().isEmpty() || txtHeavy.getText().isEmpty() || txtMaxRep.getText().isEmpty() || txtMinRep.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Selecione um exercício na tabela e preencha todos os campos.");
                alert.showAndWait();
            } else {
                st  = connect.createStatement();
                ps = connect.prepareStatement(updateHeavys);

                    HeavysData selectedHeavys = tableViewHeavys.getItems().get(row);

                    ps.setString(1, txtExercise.getText());
                    ps.setDouble(2, Double.parseDouble(txtHeavy.getText()));
                    ps.setInt(3, Integer.parseInt(txtMaxRep.getText()));
                    ps.setInt(4, Integer.parseInt(txtMinRep.getText()));
                    ps.setInt(5, currentUserId);
                    ps.setInt(6, selectedHeavys.getHeavyId());

                    ps.executeUpdate();

                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Mensagem de informação");
                    alert.setHeaderText(null);
                    alert.setContentText("Exercício editado com sucesso!");
                    alert.showAndWait();

                    addHeavyShowList();
                    heavysAddClear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void heavysDelete() {
        String deleteHeavys = "DELETE FROM loads WHERE exercise = '" + txtExercise.getText() + "'";

        connect = DB.connectDb();

        try {
            Alert alert;

            if (txtExercise.getText().isEmpty() || txtHeavy.getText().isEmpty() || txtMaxRep.getText().isEmpty() || txtMinRep.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Mensagem de erro");
                alert.setHeaderText(null);
                alert.setContentText("Selecione um exercício na tabela para excluir algo.");
                alert.showAndWait();
            } else {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Mensagem de confirmação");
                alert.setHeaderText(null);
                alert.setContentText("Você tem certeza que deseja excluir esse exercício?");
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get().equals(ButtonType.OK)) {
                    st = connect.createStatement();
                    st.executeUpdate(deleteHeavys);

                    alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Mensagem de confirmação");
                    alert.setHeaderText(null);
                    alert.setContentText("Exercício excluído com sucesso!");
                    alert.showAndWait();

                    addHeavyShowList();
                    heavysAddClear();
                } else {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void heavysAddClear() {
        txtExercise.setText("");
        txtHeavy.setText("");
        txtMaxRep.setText("");
        txtMinRep.setText("");
    }

    BigDecimal progress = new BigDecimal(String.format(Locale.US, "%.2f", 0.0));
    private final int totalDays = 312;
    private LocalDateTime lastClickTime = LocalDateTime.MIN;
    private Preferences prefs = Preferences.userNodeForPackage(HomeController.class);

    public void addProgress() {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration durationSinceLastClick = Duration.between(lastClickTime, currentTime);

        if (durationSinceLastClick.toHours() >= 24 && progress.intValue() < totalDays) {
            progress = progress.add(BigDecimal.ONE);
            updateProgress();
            lastClickTime = currentTime;
            btnAddProgress.setDisable(true);

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                Platform.runLater(() -> btnAddProgress.setDisable(false));
            }, 24, TimeUnit.HOURS);


            prefs.put("progress", progress.toString());
            prefs.put("lastClickTime", lastClickTime.toString());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Você só pode adicionar +1 uma vez a cada 24 horas. Aguarde até o próximo dia para continuar seu progresso.");

            alert.showAndWait();
        }
    }

    private void updateProgress() {
        int per = Math.min(progress.multiply(BigDecimal.valueOf(100)).intValue() / totalDays, 100);

        progressBar.setProgress(per / 100.0);

        percentage.setText(per + "%");

        if (per == 100) {
            Alert alert;
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Mensagem de confirmação");
            alert.setHeaderText(null);
            alert.setContentText("Parabéns por concluir 1 ano de treino, você tem direito a 1 Whey da sua preferência!");
            alert.showAndWait();
        }
    }

    public void logout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Mensagem de confirmação");
            alert.setHeaderText(null);
            alert.setContentText("Você tem certeza que deseja sair?");

            Optional<ButtonType> optional = alert.showAndWait();

            if (optional.get().equals(ButtonType.OK)) {
                btnLogout.getScene().getWindow().hide();

                Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed((MouseEvent event) ->  {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });

                root.setOnMouseDragged((MouseEvent event) ->  {
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);

                    stage.setOpacity(.8);
                });

                root.setOnMouseReleased((MouseEvent event) ->  {
                    stage.setOpacity(1);
                });


                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setScene(scene);
                stage.show();
            } else {
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchForm (ActionEvent event) {
        if (event.getSource() == btnHome) {
            formHome.setVisible(true);
            formLoads.setVisible(false);
            formProgress.setVisible(false);

            btnHome.setStyle("-fx-background-color: linear-gradient(to bottom, #18224a 95%, #f4e022 100%);");
            btnHeavys.setStyle("-fx-background-color: transparent");
            btnProgress.setStyle("-fx-background-color: transparent");
            homeDisplayMaxHeavy();
            homeDisplayHeavysLevels();
        } else if (event.getSource() == btnHeavys) {
            formHome.setVisible(false);
            formLoads.setVisible(true);
            formProgress.setVisible(false);

            btnHeavys.setStyle("-fx-background-color: linear-gradient(to bottom, #18224a 95%, #f4e022 100%);");
            btnHome.setStyle("-fx-background-color: transparent");
            btnProgress.setStyle("-fx-background-color: transparent");

            addHeavyShowList();
        } else if (event.getSource() == btnProgress) {
            formHome.setVisible(true);
            formLoads.setVisible(false);
            formProgress.setVisible(true);

            btnProgress.setStyle("-fx-background-color: linear-gradient(to bottom, #18224a 95%, #f4e022 100%);");
            btnHeavys.setStyle("-fx-background-color: transparent");
            btnHome.setStyle("-fx-background-color: transparent");
        }
    }

    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String formattedDate = AppContext.getFormatedDate();
        txtDate.setText(formattedDate);
        txtDate2.setText(formattedDate);
        progress = new BigDecimal(prefs.get("progress", "0.0"));
        lastClickTime = LocalDateTime.parse(prefs.get("lastClickTime", LocalDateTime.MIN.toString()));
        updateProgress();
        homeDisplayMaxHeavy();
        homeDisplayHeavysLevels();
        addHeavyShowList();
    }
}



