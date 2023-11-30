package gym.gymtracker;

import gym.gymtracker.db.DB;
import gym.gymtracker.utils.Alerts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MainAppController implements Initializable {
    @FXML
    private Button btnLogin;

    @FXML
    private Button btnSignUp;

    @FXML
    private Button btnForgetPass;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnClose;

    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }

    private Connection connect;
    private PreparedStatement ps;
    private ResultSet rs;

    public void login() {
        String sql = "SELECT * FROM user WHERE Username = ? and Password = ?";

        connect = DB.connectDb();

        try {
            Alerts alert = new Alerts();

            ps = connect.prepareStatement(sql);
            ps.setString(1, txtUsername.getText());
            ps.setString(2, txtPassword.getText());

            rs = ps.executeQuery();

            if (txtUsername.getText().isEmpty() || txtPassword.getText().isEmpty()) {
                alert.errorMessage("Preencha todos os campos.");
            } else {
                if (rs.next()) {
                    int userId = rs.getInt("Id");


                    AppContext.setCurrentUserId(userId);
                    alert.sucessMessage("Login concluído com sucesso!");

                    btnLogin.getScene().getWindow().hide();

                    Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    alert.errorMessage("Usuário e/ou senha incorretos.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // carregar as imagens
    }
}
