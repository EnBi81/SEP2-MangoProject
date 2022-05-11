package client.view.general;

import client.core.ViewHandler;
import client.core.ViewModelFactory;
import javafx.scene.control.TabPane;
import shared.Log;

/**
 * CustomerGUI is part of the strategy pattern, specifically this is the strategy when the user
 * is a customer
 * @author Greg
 * @version 1
 */
public class CustomerGUI extends UserStrategy
{
  /**
   * All the tabs to load when the logged in user is a customer
   */
  private final static String[] tabs = {

  };

  public CustomerGUI(TabPane tabPane, ViewHandler viewHandler, ViewModelFactory viewModelFactory) {
    super(tabPane, viewHandler, viewModelFactory);
  }

  @Override public void loadTabs() {
    Log.log("CustomerGUI customer tabs are loading");
    loadTabs(tabs);
  }
}