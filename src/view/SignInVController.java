/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.logging.Logger;
import exceptions.InvalidPasswordValueException;
import exceptions.InvalidUserException;
import exceptions.InvalidUserValueException;
import exceptions.TimeOutException;
import exceptions.ConnectionErrorException;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author Sendoa
 */
public class SignInVController {

    private Stage stage;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private Line usernameLine;
    @FXML
    private Line passwordLine;
    @FXML
    private Button buttonSignIn;
    @FXML
    private Button buttonSignUp;
    @FXML
    private ToggleButton buttonShowHide;
    @FXML
    private ImageView imageViewButton;
    @FXML
    private Label labelInvalidUser;
    @FXML
    private Label labelInvalidPassword;
    @FXML
    private ImageView userIcon;
    @FXML
    private ImageView passwordIcon;
    private static final Logger LOGGER = Logger.getLogger("SignInVController.class");

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initStage(Parent root) {
        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setTitle("SignIn");
        stage.setResizable(false);

        // USERNAME TEXT FIELD //
        textFieldUsername.setOnKeyTyped(this::textChanged);
        textFieldUsername.focusedProperty().addListener(this::focusedPropertyChanged);

        // PASSWORD FIELD //
        passwordField.setOnKeyReleased(this::handleKeyReleased);
        passwordField.setOnKeyTyped(this::textChanged);
        passwordField.focusedProperty().addListener(this::focusedPropertyChanged);

        // PASSWORD TEXT FIELD //
        textFieldPassword.focusedProperty().addListener(this::focusedPropertyChanged);
        textFieldPassword.setOnKeyTyped(this::textChanged);
        textFieldPassword.setOnKeyReleased(this::handleKeyReleased);

        // BUTTONS //
        buttonSignIn.setOnAction(this::handleSignIn);
        buttonShowHide.setOnAction(this::handleShowHide);
        buttonSignUp.setOnAction(this::handleSignUp);

        stage.show();
        LOGGER.info("SingIn window initialized");
    }

    /**
     * Comprueba que el texto introducido sea inferior a 25 caracteres. Si llega
     * al máximo permitido no deja introducir más caracteres y sustrae y enseña
     * los primeros 25
     *
     * @param event un evento tipo KEY_TYPED para cuando se escribe un caracter
     */
    private void textChanged(KeyEvent event) {
        if (((TextField) event.getSource()).getText().length() >= 25) {
            event.consume();
            ((TextField) event.getSource()).setText(((TextField) event.getSource()).getText().substring(0, 25));
        }
    }

    /**
     * Abre la ventana Sign Up y cierra la Sign in.
     *
     * @param event un evento tipo ActionEvent.ACTION para cuendo el boton es
     * pulsado
     */
    private void handleSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/SignUpView.fxml"));
            Parent root = (Parent) loader.load();

            SignUpVController controller = ((SignUpVController) loader.getController());

            controller.setStage(stage);

            controller.initStage(root);
        } catch (IOException ex) {
            Logger.getLogger(SignInVController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo para iniciar sesion
     *
     * @param event un evento tipo ActionEvent.ACTION para cuendo el boton es
     * pulsado
     */
    private void handleSignIn(ActionEvent event) {
        buttonSignIn.requestFocus();
        // Comprueba que los campos están informados y que el usuario y la contraseña son válidos 
        // (cumplen los requisitos especificados en sus propios eventos)
        // Si los datos se validan correctamente, se ejecuta el método doSignIn().
        focusedPropertyChanged(null, true, false);
    }

    /**
     * Comprueba en qué estado (presionado/no presionado) está la contraseña.
     *
     * @param event un evento tipo ActionEvent.ACTION para cuendo el boton es
     * pulsado
     */
    private void handleShowHide(ActionEvent event) {
        if (buttonShowHide.isSelected()) {
            // Si está presionado se muestra un textField y a imagen de imageShowHide es hideIcon.
            imageViewButton.setImage(new Image(getClass().getResourceAsStream("/resources/iconEye2.png")));
            passwordField.setVisible(false);
            textFieldPassword.setVisible(true);
        } else {
            // Si no está presionado se muestra un passwordField y la imagen de imageShowHide es showIcon.
            imageViewButton.setImage(new Image(getClass().getResourceAsStream("/resources/iconEye.png")));
            passwordField.setVisible(true);
            textFieldPassword.setVisible(false);
        }
    }

    /**
     * Copia el texto de un campo al otro
     *
     * @param event un evento tipo KEY_RELEASED el usuario deja de presionar la
     * tecla en cuestion
     */
    private void handleKeyReleased(KeyEvent event) {
        if (passwordField.isVisible()) {
            // Cuando se escribe un carácter en el passwordField se copia en el textFieldPassword.
            textFieldPassword.setText(passwordField.getText());
        } else if (textFieldPassword.isVisible()) {
            // Cuando se escribe un carácter en el textFieldPassword se copia en el passwordField.
            passwordField.setText(textFieldPassword.getText());
        }
    }

    /**
     * Comprueba el cambio de foco
     *
     * @param observable
     * @param oldValue
     * @param newValue
     */
    private void focusedPropertyChanged(ObservableValue observable, Boolean oldValue, Boolean newValue) {
        if (oldValue) {
            if (!textFieldUsername.isFocused()) {
                try {
                    if (textFieldUsername.getText().isEmpty()) {
                        throw new InvalidUserValueException("Enter a username");
                    }
                    // Si el campo no está vacío comprobar que la contraseña tiene al menos 8 caracteres y que no hay espacios.
                    // En caso de que no tenga 8 caracteres o contenga espacios en blanco cambiar el color de imagePassword y linePassword a rojo.
                    if (textFieldUsername.getText().contains(" ")) {
                        throw new InvalidUserValueException("Username can't contain an empty space");
                    }
                    userIcon.setImage(new Image(getClass().getResourceAsStream("/resources/iconUser.png")));
                    usernameLine.setStroke(Color.GRAY);
                    labelInvalidUser.setText("");
                } catch (InvalidUserValueException ex) {
                    userIcon.setImage(new Image(getClass().getResourceAsStream("/resources/iconUserIncorrect.png")));
                    usernameLine.setStroke(Color.RED);
                    LOGGER.info(ex.getMessage());
                    labelInvalidUser.setText(ex.getMessage());
                }
            }
            if (!passwordField.isFocused() && !textFieldPassword.isFocused()) {
                try {
                    if (passwordField.getText().isEmpty() || textFieldPassword.getText().isEmpty()) {
                        throw new InvalidPasswordValueException("Enter a password");
                    }
                    // Si el campo no está vacío comprobar que la contraseña tiene al menos 8 caracteres y que no hay espacios.
                    // En caso de que no tenga 8 caracteres o contenga espacios en blanco cambiar el color de imagePassword y linePassword a rojo.
                    if (passwordField.getText().contains(" ") || textFieldPassword.getText().contains(" ") || passwordField.getText().length() < 8 || textFieldPassword.getText().length() < 8) {
                        throw new InvalidPasswordValueException("Password must be at least 8 characters long and must not contain blank spaces");
                    }
                    passwordIcon.setImage(new Image(getClass().getResourceAsStream("/resources/iconPassword.png")));
                    passwordLine.setStroke(Color.GRAY);
                    labelInvalidPassword.setText("");
                } catch (InvalidPasswordValueException ex) {
                    passwordIcon.setImage(new Image(getClass().getResourceAsStream("/resources/iconPasswordRedIncorrect.png")));
                    passwordLine.setStroke(Color.RED);
                    LOGGER.info(ex.getMessage());
                    labelInvalidPassword.setText(ex.getMessage());
                }
            }
        }
    }
}