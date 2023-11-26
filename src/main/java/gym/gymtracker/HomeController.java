package gym.gymtracker;

import gym.gymtracker.db.DB;
import gym.gymtracker.utils.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;


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
    private BarChart<?, ?> chartDays;

    @FXML
    private BarChart<?, ?> chartLevel;

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
    private Label txtDate;

    @FXML
    private Label txtTotalHeavy;

    @FXML
    private TextField txtExercise;

    @FXML
    private TextField txtHeavy;

    @FXML
    private TextField txtMaxRep;

    @FXML
    private TextField txtMinRep;

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
                heavy = new HeavysData(rs.getString("exercise"), rs.getDouble("heavy"), rs.getInt("MaxRep"), rs.getInt("MinRep"), rs.getInt("UserId"));

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

        if ((num - 1) < - 1) {
            return;
        }

        txtExercise.setText(String.valueOf(heavys.getExercise()));
        Double heavyValue = heavys.getHeavy();
        txtHeavy.setText(String.valueOf(heavyValue));
        txtMaxRep.setText(Integer.toString(heavys.getMaxRep()));
        txtMinRep.setText(Integer.toString(heavys.getMinRep()));
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
        addHeavyShowList();
    }
}



