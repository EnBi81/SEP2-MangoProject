package client.networking;

import transferobjects.*;
import util.PropertyChangeSubject;

import java.rmi.Remote;
import java.util.ArrayList;

// An interface that holds two variables that will be called in the socketClient for firing events
// It will handle the login and the register action
// It takes a loginRequest object as a parameter for both register and login
public interface Client extends PropertyChangeSubject
{
  String ERROR_RECEIVED = "ErrorReceived";
  String LOGGED_IN_RECEIVED = "LogInReceived";
  String PENDING_EMPLOYEES_RECEIVED = "PendingEmployeesReceived";
  String MENU_ITEMS_RECEIVED = "MenuItemsReceived";

  /**
   * The method is used to sent to the Server the LoginRequest object
   *    when a person loggs in it is called
   * @param request the LoginRequest to be sent
   */
  void login(LoginRequest request);
  void register(LoginRequest request);
  void addItem(MenuItem menuItem);
  void sendRequest(Request request);
  void addItemsToDailyMenu(DailyMenuItem dailyMenuItem);
  void acceptEmployee(User user);
  void declineEmployee(User user);
}
