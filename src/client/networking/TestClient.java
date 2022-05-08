package client.networking;

import shared.UserType;
import transferobjects.LoginRequest;
import transferobjects.MenuItem;
import transferobjects.User;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

public class TestClient implements Client
{
  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public TestClient() throws IOException {
    new Thread(() -> {
      try {
        Thread.sleep(7000);
        support.firePropertyChange(Client.LOGGED_IN_RECEIVED, null, new User("test-user", UserType.EMPLOYEE, "first-test-name", "last-test-name"));
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }

  @Override public void login(LoginRequest request) {

  }

  @Override public void register(LoginRequest request) {

  }

  @Override public void addItem(MenuItem menuItem) {

  }

  @Override public void addListener(String event, PropertyChangeListener listener) {
    support.addPropertyChangeListener(event, listener);
  }

  @Override public void addListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }
}