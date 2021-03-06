package client.model;

import transferobjects.MenuItem;
import transferobjects.MenuItemWithQuantity;
import util.PropertyChangeSubject;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * An interface which is responsible for handling the connection between the
 * ViewModels and the Networking
 *
 * @author Mango
 * @version 1
 */

public interface MenuModel extends PropertyChangeSubject
{
  String ERROR_RECEIVED = "ErrorReceived";
  String MENU_ITEMS_RECEIVED = "MenuItemsReceived";
  String DAILY_MENU_RECEIVED = "DailyMenuReceived";
  String WEEKLY_MENU_RECEIVED = "WeeklyMenuReceived";
  String OPENING_HOURS_RECEIVED = "OpeningHoursReceived";

  /**
   * The method is used to send to the Client String objects, that are required
   * for adding MenuItem
   * @param name the name of menu Item
   * @param ingredients list of all MenuItem's ingredients
   * @param price the price of menu Item
   * @param imgPath the path where the image is stored
   */

  void addItem(String name, ArrayList<String> ingredients, double price,
      String imgPath);

  /**
   * The method is used to send to the Client request for list of all MenuItems
   */

  void requestMenuItems();

  /**
   * The method is used to send to the Client LocalDate and ArrayList objects, that are required
   *  for adding DailyMenu Item
   * @param date the menu's date
   * @param menuItems the list of menu's items
   */

  void addItemsToDailyMenu(LocalDate date,ArrayList<MenuItem> menuItems);

  /**
   * The method is used to send oto the Client request for the DailyMenu on a specific date
   */
  void requestDailyMenu();

  /**
   * The method is used to the client the DailyMenuItemList, where each item list has a quantity
   * @param listOfItemsWithQuantity the list of DailyMenuItems
   */
  void addQuantity(ArrayList<MenuItemWithQuantity> listOfItemsWithQuantity);

  /**
   * The method is used to send request for weekly menu to the Client
   */

  void requestWeeklyMenu();

  /**
   * The method is used to delete Menu items from weekly menu
   * @param listOfMenuItemsToDelete the list of MenuItemsWithQuantity to delete
   */

  void deleteMenuItemFromWeeklyMenu(ArrayList<MenuItemWithQuantity> listOfMenuItemsToDelete);

  /**
   * The method is used to delete Menu items from database
   * @param menuItems the list of MenuItem to delete
   */
  void removeMenuItem(ArrayList<MenuItem> menuItems);

  /**
   *The method sends a request for the opening hours to the CLient
   */
  void requestOpeningHours();
}
