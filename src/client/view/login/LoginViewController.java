package client.view.login;

import client.core.ViewHandler;
import client.core.ViewModelFactory;
import client.view.ViewController;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import shared.Log;

/**
 * The class which is responsible for the functionality of
 * the graphical user interface.
 * Implements ViewController interface.
 * @author Agata
 * @version 1
 */

public class LoginViewController implements ViewController
{
  public TextField username;
  public PasswordField password;
  public Label errorLabel;

  private ViewHandler viewHandler;
  private LoginViewModel viewModel;

  /**
   * Override interface's method.
   * Initial base data.
   * @param viewHandler get instance of the ViewHandler class.
   * @param viewModelFactory class needed to get access to LoginViewModel class.
   */

  @Override public void init(ViewHandler viewHandler, ViewModelFactory viewModelFactory)
  {
    this.viewHandler = viewHandler;
    this.viewModel = viewModelFactory.getLoginViewModel();

    errorLabel.textProperty().bindBidirectional(viewModel.getError());

    refresh();
  }

  /**
   * Refreshing the username, password and error label to be empty
   */
  public void refresh() {
    username.clear();
    password.clear();
    errorLabel.setText("");
  }

  /**
   * LogIn method to get particular user object.(When logg in button pressed)
   */
  public void onLogIn()
  {
    Log.log("LoginViewController logg in button pressed");
    viewModel.login(username.getText(), password.getText());

    refresh();
  }

  /**
   * Opens a RegisterView
   */
  public void onRegister()
  {
    Log.log("LoginViewController register button pressed");
    errorLabel.setText("");
    viewHandler.openRegisterView();
    refresh();
  }
}
