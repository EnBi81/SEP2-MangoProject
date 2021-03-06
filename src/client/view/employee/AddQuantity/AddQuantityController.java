package client.view.employee.AddQuantity;

import client.core.ViewModelFactory;
import client.imageHandler.ClientImageLoader;
import client.view.TabController;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import shared.Log;
import transferobjects.MenuItemWithQuantity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Controller for AddQuantityView
 *
 * @author Uafa
 * @version 1
 */
public class AddQuantityController implements TabController
{

  @FXML private VBox menuItemsVBox;
  @FXML private Label dateLabel;

  private AddQuantityViewModel viewModel;

  private Map<String, TextField> textFields = new HashMap<>();
  private Map<String, MenuItemWithQuantity> menuItems = new HashMap<>();

  /**
   * Method that initializes the controller
   *
   * @param viewModelFactory instance of ViewModelFactory class, where ViewModels are created
   */
  @Override public void init(ViewModelFactory viewModelFactory)
  {
    viewModel = viewModelFactory.getAddQuantityViewModel();

    viewModel.dailyMenuItemsList().addListener(this::menuItemListChangeListener);

    LocalDate localDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    String dateText = localDate.getDayOfWeek() + " - " + localDate.format(formatter);
    dateLabel.setText(dateText);

  }

  /**
   * When the Set button in the view is clicked this goes through all the menu
   * items displayed, checks if the user entered a valid quantity value. If a
   * value is not entered, it is automatically assigned to 0. If the entered
   * value is invalid the background color of the text field changes.
   * If all values are valid an Arraylist with MenuItemWithQuantity is sent
   * to the view model.
   */
  @FXML public void onSet()
  {
    Log.log("Set button has been clicked to set quantity");
    ArrayList<MenuItemWithQuantity> menuItemWithQuantities = new ArrayList<>();

    boolean isValidList = true;

    for (String itemName : textFields.keySet())
    {
      TextField field = textFields.get(itemName);
      MenuItemWithQuantity itemQuantity = menuItems.get(itemName);

      boolean isValid = true;
      try
      {

        if (field.getText().isEmpty())
        {
          field.setText("0");
        }
        int quantity = Integer.parseInt(field.getText());

        if (quantity < 0)
        {
          isValid = false;
        }

        itemQuantity.setQuantity(quantity);
        menuItemWithQuantities.add(itemQuantity);
      }
      catch (NumberFormatException e)
      {
        isValid = false;
      }

      if (!isValid)
      {
        field.setBackground(new Background(
            new BackgroundFill(Color.rgb(250, 100, 100), null, null)));
        isValidList = false;
      }
      else
      {
        field.setBackground(new Background(
            new BackgroundFill(Color.rgb(250, 250, 250), null, null)));
      }
    }

    if (isValidList)
    {
      viewModel.addQuantityToItems(menuItemWithQuantities);
    }
    else
    {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error Dialog");
      alert.setHeaderText(null);
      alert.setContentText("Invalid Quantity");

      alert.showAndWait();
    }

  }

  /**
   * Listener for the ObservableList in the ViewModel
   *
   * @param change data of the change event
   */
  private void menuItemListChangeListener(ListChangeListener.Change<? extends MenuItemWithQuantity> change)
  {
    change.next();

    Platform.runLater(() -> {
      if (change.wasAdded())
      {
        addedMenuItem(change.getAddedSubList());
      }
      else if (change.wasRemoved())
      {
        menuItemsVBox.getChildren().clear();
        menuItems.clear();
        textFields.clear();
      }
    });
  }

  /**
   * This method is called when any MenuItemWithQuantity was added to the
   * ObservableList. Creates the GUI boxes for all the added items, and puts
   * them in the right place of the GUI.
   *
   * @param list The list of the added items
   */
  private void addedMenuItem(List<? extends MenuItemWithQuantity> list)
  {
    for (MenuItemWithQuantity menuItem : list)
    {
      try
      {
        HBox hBox = createDailyMenuItemBox(menuItem);
        putIntoHBox(hBox);
      }
      catch (IOException e)
      {
        Log.log(
            "AddQuantityController: Image could not be loaded for menu item " + menuItem.getName());

      }
    }
  }

  /**
   * Puts the menu item pane into the main menu pane.
   * The main menu pane is a vbox, every two menuItemPane is put together into one
   * HBox, so that there is maximum two menuItems in one row.
   * This method is responsible for putting two menuItemPanes into one HBox,
   * and if the last HBox is full (there are 2 menuItemPanes), it creates a
   * new HBox and puts the menuItemPane into the empty box.
   *
   * @param menuItemPane the menu item's pane to put into the HBoxes
   */
  private void putIntoHBox(Pane menuItemPane)
  {
    int vboxChildrenSize = menuItemsVBox.getChildren().size();
    if (vboxChildrenSize == 0)
    {
      menuItemsVBox.getChildren().add(new HBox());
      vboxChildrenSize++;
    }


    HBox lastHBox = (HBox) menuItemsVBox.getChildren().get(vboxChildrenSize - 1);

    if (lastHBox.getChildren().size() >= 2)
    {
      lastHBox = new HBox();
      lastHBox.setPadding(new Insets(50, 0, 0, 0));

      menuItemsVBox.getChildren().add(lastHBox);
    }

    lastHBox.getChildren().add(menuItemPane);
  }

  /**
   * Creates and returns the HBox which includes the information and the image
   * for the MenuItemWithQuantity given in the parameter. Also creates a text
   * field for each item so a quantity value can be entered. Menu items and
   * Text fields are stored in a Hashmap and use the menu item's name as a key.
   * @param menuItemWithQuantity MenuItemWithQuantity object to create the HBox of.
   * @return HBox which includes the information and the image for the MenuItemWithQuantity given in the parameter
   * @throws IOException when there was an error loading the image
   */
  private HBox createDailyMenuItemBox(MenuItemWithQuantity menuItemWithQuantity) throws
      IOException {

    String itemName = menuItemWithQuantity.getName();
    String ingredients = String.join(", ", menuItemWithQuantity.getIngredients());
    String price = menuItemWithQuantity.getPrice() + " DKK";
    String quantity = menuItemWithQuantity.getQuantity() + "";
    String imagePath = menuItemWithQuantity.getImgPath();
    int imgSize = 130;

    menuItems.put(itemName, menuItemWithQuantity);

    Label nameLabel = new Label(itemName){{ // (1)
      setHeight(80);
      setFont(Font.font(null, FontWeight.BOLD, FontPosture.REGULAR, 18));
      setWrapText(true);
    }};
    Tooltip.install(nameLabel, new Tooltip(itemName));
    Label ingredientsLabel = new Label(ingredients){{ // (2)
      setWidth(170);
      setHeight(70);
      setWrapText(true);
    }};
    Tooltip.install(ingredientsLabel, new Tooltip(ingredients));
    Label priceLabel = new Label(price); // (3)

    VBox priceVbox = new VBox(){{ // (4)
      setAlignment(Pos.TOP_RIGHT);
      setPadding(new Insets(30, 0, 0, 0));
      getChildren().add(priceLabel);
    }};

    VBox rightVbox = new VBox(){{ // (9)
      setPadding(new Insets(40, 30, 10, 20));
      setMinWidth(250);
      getChildren().addAll(
          nameLabel,
          ingredientsLabel,
          priceVbox
      );
    }};


    TextField quantityField = new TextField(){{
      setMaxSize(60, 25);
      setPromptText("Quantity");
      setText(quantity);
    }};

    textFields.put(itemName, quantityField);


    HBox quantityBox = new HBox(){{ // (12)
      setHeight(100);
      setAlignment(Pos.CENTER);
      getChildren().addAll(
          quantityField
      );
    }};

    ImageView menuItemImageView = new ImageView() {{ // (13)
      setPickOnBounds(true);
      setPreserveRatio(true);
    }};
    ClientImageLoader.loadImage(imagePath, menuItemImageView, imgSize, imgSize);

    VBox leftVbox = new VBox(){{ // (14)
      getChildren().addAll(
          menuItemImageView,
          quantityBox
      );
    }};

    menuItemImageView.setFitWidth(leftVbox.getWidth() - 10);
    menuItemImageView.setFitHeight(leftVbox.getHeight() - 10);


    HBox wrapper = new HBox(){{ // (15)
      setHeight(256);
      setWidth(355);
      setMinWidth(355);
      setPadding(new Insets(10,10,10,10));
      getChildren().addAll(
          leftVbox,
          rightVbox
      );
    }};

    return wrapper;
  }

  @Override public void refresh()
  {
    viewModel.requestDailyMenuItems();
  }

  /**
   * Converts a given Image into a BufferedImage
   *
   * @param img The Image to be converted
   * @return The converted BufferedImage
   */
  private static BufferedImage toBufferedImage(java.awt.Image img)
  {
    if (img instanceof BufferedImage)
    {
      return (BufferedImage) img;
    }

    // Create a buffered image with transparency
    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    // Draw the image on to the buffered image
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();

    // Return the buffered image
    return bimage;
  }

}