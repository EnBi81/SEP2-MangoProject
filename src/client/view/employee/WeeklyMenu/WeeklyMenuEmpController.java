package client.view.employee.WeeklyMenu;

import client.core.ViewModelFactory;
import client.view.TabController;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import shared.Log;
import transferobjects.MenuItem;
import transferobjects.MenuItemWithQuantity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The class is responsible for the functionality of the graphical user
 * interface that displays the weekly menu for employee
 */

public class WeeklyMenuEmpController implements TabController
{
  public ListView<MenuItemWithQuantity> mondayList;
  public Label mondayDate;
  public ListView<MenuItemWithQuantity> tuesdayList;
  public Label tuesdayDate;
  public ListView<MenuItemWithQuantity> wednesdayList;
  public Label wednesdayDate;
  public ListView<MenuItemWithQuantity> thursdayList;
  public Label thursdayDate;
  public ListView<MenuItemWithQuantity> fridayList;
  public Label fridayDate;
  public Button deleteButton;

  private WeeklyMenuEmpViewModel viewModel;

  private ArrayList<ListView<MenuItemWithQuantity>> listViews;


  /**
   * Initial data that has to be loaded.
   * @param viewModelFactory instance of ViewModelFactory class, where ViewModels are created
   */
  @Override public void init(ViewModelFactory viewModelFactory)
  {
    viewModel = viewModelFactory.getWeeklyMenuEmpViewModel();

    mondayList.setItems(viewModel.getMondayList());
    tuesdayList.setItems(viewModel.getTuesdayList());
    wednesdayList.setItems(viewModel.getWednesdayList());
    thursdayList.setItems(viewModel.getThursdayList());
    fridayList.setItems(viewModel.getFridayList());

    listViews = new ArrayList<>();

    listViews.add(mondayList);
    listViews.add(tuesdayList);
    listViews.add(wednesdayList);
    listViews.add(thursdayList);
    listViews.add(fridayList);

    enableSelectItems();

    mondayDate.textProperty().bind(viewModel.mondayDateProperty());
    tuesdayDate.textProperty().bind(viewModel.tuesdayDateProperty());
    wednesdayDate.textProperty().bind(viewModel.wednesdayDateProperty());
    thursdayDate.textProperty().bind(viewModel.thursdayDateProperty());
    fridayDate.textProperty().bind(viewModel.fridayDateProperty());

    deleteButton.setDisable(true);
  }

  /**
   * Enabling the button for deleting items
   */
  private void enableButton()
  {
    for (ListView<MenuItemWithQuantity> list : listViews)
    {
      if(list.getSelectionModel().getSelectedItems().size() > 0)
      {
        deleteButton.setDisable(false);
        return;
      }
    }

    deleteButton.setDisable(true);
    Log.log("Button to delete items from daily menu is enabled");
  }

  /**
   *
   * @param change
   */
  private void listenList(ListChangeListener.Change<? extends MenuItem> change)
  {
    change.next();
    enableButton();
  }

  @Override public void refresh()
  {
    viewModel.refresh();
  }

  /**
   * When the delete button is pressed a list of menu items with quantity in send tot the viewModel
   */
  public void onDelete()
  {
    ArrayList<MenuItemWithQuantity> listToDelete = new ArrayList<>();

    for (ListView<MenuItemWithQuantity> listView : listViews) {
      ObservableList<MenuItemWithQuantity> items = listView.getSelectionModel().getSelectedItems();
      listToDelete.addAll(items);
      listView.getItems().removeAll(items);
    }

    viewModel.deleteItems(listToDelete);

    Log.log("Delete button has been clicked to delete items from weeklyMenu");
  }

  /**
   *
   * @param evt
   */
  private void multipleSelection(MouseEvent evt)
  {
    Node node = evt.getPickResult().getIntersectedNode();

    // go up from the target node until a list cell is found or it's clear
    // it was not a cell that was clicked
    while (node != null &&
        (node != mondayList
            && node != tuesdayList
            && node != wednesdayList
            && node != thursdayList
            && node != fridayList) && !(node instanceof ListCell)) {
      node = node.getParent();
    }

    // if is part of a cell or the cell,
    // handle event instead of using standard handling
    if (node instanceof ListCell) {
      // prevent further handling
      evt.consume();

      ListCell cell = (ListCell) node;
      ListView lv = cell.getListView();

      // focus the listview
      lv.requestFocus();

      if (!cell.isEmpty()) {
        // handle selection for non-empty cells
        int index = cell.getIndex();
        if (cell.isSelected()) {
          lv.getSelectionModel().clearSelection(index);
        } else {
          lv.getSelectionModel().select(index);
        }
      }
    }
  }

  private void enableSelectItems()
  {
    LocalDate date = LocalDate.now();
    DayOfWeek dayOfWeek = date.getDayOfWeek();
    int day = dayOfWeek.getValue();

    for (int i = 0; i < listViews.size(); i++)
    {
      ListView list = listViews.get(i);
      if (i < day && day >= 6)
      {
        list.setDisable(true);
      }
      else
      {
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        list.addEventFilter(MouseEvent.MOUSE_PRESSED, this::multipleSelection);
        list.getSelectionModel().getSelectedItems().addListener(this::listenList);
      }
    }

  }
}
