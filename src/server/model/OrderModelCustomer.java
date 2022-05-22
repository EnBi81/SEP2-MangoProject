package server.model;

import transferobjects.OrderItem;

import java.sql.SQLException;
import java.util.ArrayList;

public interface OrderModelCustomer
{
  void cancelOrder(String username) throws SQLException;
  ArrayList<OrderItem> getUncollectedOrder(String object) throws SQLException;
}