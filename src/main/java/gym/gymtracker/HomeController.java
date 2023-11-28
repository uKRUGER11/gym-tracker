package gym.gymtracker;

import gym.gymtracker.db.DB;
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
import java.sql.Statement;
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



