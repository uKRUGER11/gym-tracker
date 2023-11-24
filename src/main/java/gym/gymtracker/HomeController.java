package gym.gymtracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;


public class HomeController implements Initializable {
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private BarChart<?, ?> chartDays;

    @FXML
    private BarChart<?, ?> chartLevel;

    @FXML
    private AnchorPane mainForm;

    @FXML
    private TableColumn<?, ?> tableColumnExercise;

    @FXML
    private TableColumn<?, ?> tableColumnHeavy;

    @FXML
    private TableColumn<?, ?> tableColumnMaxRep;

    @FXML
    private TableColumn<?, ?> tableColumnMinRep;

    @FXML
    private TableView<?> tableViewHeavys;

    @FXML
    private Label txtDate;

    @FXML
    private TextField txtExercise;

    @FXML
    private TextField txtHeavy;

    @FXML
    private TextField txtMaxRep;

    @FXML
    private TextField txtMinRep;

    @FXML
    private Label txtTotalHeavy;

    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}



