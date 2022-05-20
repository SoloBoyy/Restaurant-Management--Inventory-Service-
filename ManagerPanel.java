import java.sql.*;
import java.util.List;
import java.awt.*;    
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;  
import java.util.ArrayList;

public class ManagerPanel {

    public static void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +1 , width);
            }
            if(width > 300)
                width=300;
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
      
    ManagerPanel(Statement stmt, JFrame frame) throws SQLException
    {
      frame.setPreferredSize(new Dimension(1480, 700));
      // jframe is closed
      WindowListener listener = new WindowAdapter() {
        public void windowClosing(WindowEvent evt) {
           Frame frame = (Frame) evt.getSource();
           System.out.println("Closing = "+frame.getTitle());
        }
      }; 

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.addWindowListener(listener);

      // add panel to hold server and manager
      Container panel = frame.getContentPane();
      JTabbedPane tabbedPane = new JTabbedPane();

      // DESIGN MANAGER PANEL
      JPanel manager = new JPanel();
      manager.setLayout(new BoxLayout(manager, BoxLayout.PAGE_AXIS));

      // top button panel for manager
      JPanel managerTopButtons = new JPanel();

      // panel to show table of db
      JPanel managerText = new JPanel();

      // bottom button panel for manager
      JPanel managerBottomButtons = new JPanel();

      // button to view menu
      JButton menu_button = new JButton("View Menu");

      // event for view menu button
      menu_button.addActionListener(new ActionListener() {
      
        @Override
        public void actionPerformed(ActionEvent a) {
          try{
            
            // remove all existing things in panel
            managerText.removeAll();

            //create the SQL statement
            ResultSet result = stmt.executeQuery("SELECT * FROM menukey");

            //headers for the table
            String[] columns = new String[] {
              "Item", "Name", "Description", "Price"
            };


            // 2D list to contain each row
            List<List<Object>> data = new ArrayList<List<Object>>();
    
            // get SQL response
            while (result.next()) {

              // list to contain values in row
              List<Object> row = new ArrayList<Object>();

              // add values for each column to row list
              row.add(result.getString("item"));
              row.add(result.getString("name"));
              row.add(result.getString("description"));
              row.add(result.getFloat("price"));

              // add row to all data list
              data.add(row);
            }
            
            // convert List[][] to Object[][]
            Object[][] dataArray = data.stream().map(l -> l.stream().toArray(Object[]::new)).toArray(Object[][]::new);
            
            //create table with data list and column headers
            JTable table = new JTable(dataArray, columns) {

              @Override
              public boolean isCellEditable(int row, int column) {
                 //all cells false to editable
                 return false;
              }
            };
            
            // create the croll view for the table
            JScrollPane scrollPane = new JScrollPane(table);
            table.setPreferredScrollableViewportSize(new Dimension(1100, 400));

            // resize each column
            resizeColumnWidth(table);
            
            //add the table to the text panel of manager
            managerText.add(scrollPane);

            // refresh jframe
            SwingUtilities.updateComponentTreeUI(frame);
          } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.toString());
          }
        }       
      });

      // button to view inventory
      JButton inventory_button = new JButton("View Inventory");

      // event for view inventory
      inventory_button.addActionListener(new ActionListener() {
      
        @Override
        public void actionPerformed(ActionEvent a) {
          try{
            
            // remove all existing things in panel
            managerText.removeAll();

            //create the SQL statement
            ResultSet result = stmt.executeQuery("SELECT * FROM supply");

            //headers for the table
            String[] columns = new String[] {
              "Type", "Description", "SKU", "Quanitity", "Delivered", "Sold By", "Delivered By", 
              "Quantity Mult", "Price", "Total Price", "Category", "Invoice", "Detail", "Last Supply Date"
            };


            // 2D list to contain each row
            List<List<Object>> data = new ArrayList<List<Object>>();
    
            // get SQL response
            while (result.next()) {

              // list to contain values in row
              List<Object> row = new ArrayList<Object>();

              // add values for each column to row list
              row.add(result.getString("type"));
              row.add(result.getString("description"));
              row.add(result.getString("sku"));
              row.add(result.getFloat("quantity"));
              row.add(result.getInt("delivered"));
              row.add(result.getString("sold_by"));
              row.add(result.getString("delivered_by"));
              row.add(result.getInt("quantity_mult"));
              row.add(result.getFloat("price"));
              row.add(result.getFloat("tot_price"));
              row.add(result.getString("category"));
              row.add(result.getInt("invoice_line"));
              row.add(result.getString("detail"));
              row.add(result.getDate("date"));

              // add row to all data list
              data.add(row);
            }
            
            // convert List[][] to Object[][]
            Object[][] dataArray = data.stream().map(l -> l.stream().toArray(Object[]::new)).toArray(Object[][]::new);
            
            //create table with data list and column headers
            JTable table = new JTable(dataArray, columns) {

              @Override
              public boolean isCellEditable(int row, int column) {
                 //all cells false to editable
                 return false;
              }
          };
            
            // create the croll view for the table
            JScrollPane scrollPane = new JScrollPane(table);
            table.setPreferredScrollableViewportSize(new Dimension(1100, 400));

            // resize each column
            resizeColumnWidth(table);
            
            //add the table to the text panel of manager
            managerText.add(scrollPane);

            // refresh jframe
            SwingUtilities.updateComponentTreeUI(frame);
          } catch (Exception e){
            JOptionPane.showMessageDialog(null, e.toString());
          }
        }
      });

      // button to add/update menu
      JButton add_menu_button = new JButton("Add/Update Menu Item");

      // action for add/update menu item
      add_menu_button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          JTextField item = new JTextField();
          JTextField name = new JTextField();
          JTextField description = new JTextField();
          JTextField price = new JTextField();
          Object[] input = {
              "Item", item,
              "Name", name,
              "Description", description,
              "Price", price
          };
          
          int option = JOptionPane.showConfirmDialog(null, input, "Add/Update Menu Item", JOptionPane.OK_CANCEL_OPTION);
          if (option == JOptionPane.OK_OPTION) {
            try {
              
              if (item.getText().equals("")) {
                JOptionPane.showMessageDialog(null,"Must enter menu item number!");
                return; // dont create sql statement
              }

              // get current values from database
              String sql_stmt = "SELECT * FROM menukey WHERE item = " + item.getText();
              ResultSet result = stmt.executeQuery(sql_stmt);
              try {
                result.next();
              } catch (Exception error) {
                  System.out.println("new item detected");
                }

              String sql_name = null;
              String sql_description = null;
              String sql_price = null;

              // keep name or update
              if (name.getText().equals("")) {
                sql_name = result.getString("name");
              } else {
                sql_name = name.getText();
              }

              // keep description or update
              if (description.getText().equals("")) {
                sql_description = result.getString("description");
              } else {
                sql_description = description.getText();
              }

              // keep price or update
              if (price.getText().equals("")) {
                sql_price = result.getString("price");
              } else {
                sql_price= price.getText();
              }

              //create the SQL statement
              sql_stmt = "INSERT INTO menukey (item, name, description, price) VALUES ("
                                                        + item.getText() + ", "
                                                        + "'" + sql_name + "'" + ", "
                                                        + "'" + sql_description + "'" + ", "
                                                        + sql_price 
                                                        + ") ON CONFLICT (item) WHERE ((item)::INT = "+ item.getText() + "::INT) "
                                                        + "DO UPDATE SET "
                                                        + "name = " + "'" + sql_name + "'" + ", "
                                                        + "description = " + "'" + sql_description + "'" + ", "
                                                        + "price = " + sql_price;
              
              System.out.println(sql_stmt);

              stmt.executeUpdate(sql_stmt);

              // refresh jframe
              SwingUtilities.updateComponentTreeUI(frame);
            }
            catch (Exception err) {
              JOptionPane.showMessageDialog(null, err.toString());
            }

            System.out.println("Item added/updated");
          } else {

            System.out.println("Item not added/updated");
          }
        }
    });

      // button to remove menu item
      JButton remove_menu_button = new JButton("Remove Menu Item");

      // action to remove menu item
      remove_menu_button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          JTextField item = new JTextField();
          Object[] input = {
              "Item", item
          };
          
          int option = JOptionPane.showConfirmDialog(null, input, "Remove Menu Item", JOptionPane.OK_CANCEL_OPTION);
          if (option == JOptionPane.OK_OPTION) {
            try {
              
              if (item.getText().equals("")) {
                JOptionPane.showMessageDialog(null,"Must enter menu item number!");
                return; // dont create sql statement
              }

              //create the SQL statement
              String sql_stmt = "DELETE FROM menukey WHERE item = " + item.getText();
              
              System.out.println(sql_stmt);

              stmt.executeUpdate(sql_stmt);

              // refresh jframe
              SwingUtilities.updateComponentTreeUI(frame);
            }
            catch (Exception err) {
              JOptionPane.showMessageDialog(null, err.toString());
            }

            System.out.println("Item removed");
          } else {

            System.out.println("Item not removed");
          }
        }
    });

      // button to add/update inventory
      JButton add_inventory_button = new JButton("Add/Update Inventory Item");

      // action for add/update inventory item
      add_inventory_button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          JTextField type = new JTextField();
          JTextField description = new JTextField();
          JTextField sku = new JTextField();
          JTextField quantity = new JTextField();
          JTextField delivered = new JTextField();
          JTextField sold_by = new JTextField();
          JTextField delivered_by = new JTextField();
          JTextField quantity_mult = new JTextField();
          JTextField price = new JTextField();
          JTextField tot_price = new JTextField();
          JTextField category = new JTextField();
          JTextField invoice_line = new JTextField();
          JTextField detail = new JTextField();
          JTextField date = new JTextField();
          Object[] input = {
              "Type", type,
              "Description", description,
              "SKU", sku,
              "Quantity", quantity,
              "Delivered", delivered,
              "Sold By", sold_by,
              "Delivered By", delivered_by,
              "Quantity Multiplier", quantity_mult,
              "Price", price,
              "Total Price", tot_price,
              "Category", category,
              "Invoice Line", invoice_line,
              "Detail", detail,
              "Date (YYYY-MM-DD)", date,

          };
          
          int option = JOptionPane.showConfirmDialog(null, input, "Add/Update Inventory Item", JOptionPane.OK_CANCEL_OPTION);
          if (option == JOptionPane.OK_OPTION) {
            try {
              
              if (sku.getText().equals("")) {
                JOptionPane.showMessageDialog(null,"Must enter invetory SKU number!");
                return; // dont create sql statement
              }

              // get current values from database
              String sql_stmt = "SELECT * FROM supply WHERE sku = '" + sku.getText() + "'";
              ResultSet result = stmt.executeQuery(sql_stmt);
              try {
                result.next();
              } catch (Exception error) {
                System.out.println("new sku detected");
              }

              String sql_type = null;
              String sql_description = null;
              String sql_quantity = null;
              String sql_delivered = null;
              String sql_sold_by = null;
              String sql_delivered_by = null;
              String sql_quantity_mult = null;
              String sql_price = null;
              String sql_tot_price = null;
              String sql_category = null;
              String sql_invoice_line = null;
              String sql_detail = null;
              String sql_date = null;

              // keep type or update
              if (type.getText().equals("")) {
                sql_type = result.getString("type");
              } else {
                sql_type = type.getText();
              }

              // keep description or update
              if (description.getText().equals("")) {
                sql_description = result.getString("description");
              } else {
                sql_description = description.getText();
              }

              // keep quantity or update
              if (quantity.getText().equals("")) {
                sql_quantity = result.getString("quantity");
              } else {
                sql_quantity = quantity.getText();
              }

              // keep delivered or update
              if (delivered.getText().equals("")) {
                sql_delivered = result.getString("delivered");
              } else {
                sql_delivered = delivered.getText();
              }

              // keep sold_by or update
              if (sold_by.getText().equals("")) {
                sql_sold_by = result.getString("sold_by");
              } else {
                sql_sold_by = sold_by.getText();
              }

              // keep delivered_by or update
              if (delivered_by.getText().equals("")) {
                sql_delivered_by = result.getString("delivered_by");
              } else {
                sql_delivered_by = delivered_by.getText();
              }

              // keep quantity_mult or update
              if (quantity_mult.getText().equals("")) {
                sql_quantity_mult = result.getString("quantity_mult");
              } else {
                sql_quantity_mult = quantity_mult.getText();
              }

              // keep price or update
              if (price.getText().equals("")) {
                sql_price = result.getString("price");
              } else {
                sql_price= price.getText();
              }


              // keep tot_price or update
              if (tot_price.getText().equals("")) {
                sql_tot_price = result.getString("tot_price");
              } else {
                sql_tot_price = tot_price.getText();
              }

              // keep category or update
              if (category.getText().equals("")) {
                sql_category = result.getString("category");
              } else {
                sql_category = category.getText();
              }

              // keep invoice_line or update
              if (invoice_line.getText().equals("")) {
                sql_invoice_line = result.getString("invoice_line");
              } else {
                sql_invoice_line = invoice_line.getText();
              }

              // keep detail or update
              if (detail.getText().equals("")) {
                sql_detail = result.getString("detail");
              } else {
                sql_detail = detail.getText();
              }

              // keep date or update
              if (date.getText().equals("")) {
                sql_date = result.getString("date");
              } else {
                sql_date = date.getText();
              }

              //create the SQL statement
              sql_stmt = "INSERT INTO supply (type, description, sku, quantity, delivered, sold_by, delivered_by, quantity_mult, price, tot_price, category, invoice_line, detail, date) VALUES ("
                                                        + "'" + sql_type + "'" + ", "
                                                        + "'" + sql_description + "'" + ", "
                                                        + "'" + sku.getText()+ "'" + ", "
                                                        + sql_quantity + ", "
                                                        + sql_delivered + ", "
                                                        + "'" + sql_sold_by + "'" + ", "
                                                        + "'" + sql_delivered_by + "'" + ", "
                                                        + sql_quantity_mult + ", "
                                                        + sql_price + ", "
                                                        + sql_tot_price + ", "
                                                        + "'" + sql_category + "'" + ", "
                                                        + sql_invoice_line + ", "
                                                        + "'" + sql_detail + "'" + ", "
                                                        + "'" + sql_date + "'"
                                                        + ") ON CONFLICT (sku) WHERE ((sku)::text = '"+ sku.getText() + "'::text) "
                                                        + "DO UPDATE SET "
                                                        + "type = " + "'" + sql_type + "'" + ", "
                                                        + "description = " + "'" + sql_description + "'" + ", "
                                                        + "quantity = " + sql_quantity + ", "
                                                        + "delivered = " + sql_delivered + ", "
                                                        + "sold_by = " + "'" + sql_sold_by + "'" + ", "
                                                        + "delivered_by = " + "'" + sql_delivered_by + "'" + ", "
                                                        + "quantity_mult = " + sql_quantity_mult + ", "
                                                        + "price = " + sql_price + ", "
                                                        + "tot_price = " + sql_tot_price + ", "
                                                        + "category = " + "'" + sql_category + "'" + ", "
                                                        + "invoice_line = " + sql_invoice_line + ", "
                                                        + "detail = " + "'" + sql_detail + "'" + ", "
                                                        + "date = " + "'" + sql_date + "'";
              
              System.out.println(sql_stmt);

              stmt.executeUpdate(sql_stmt);

              // refresh jframe
              SwingUtilities.updateComponentTreeUI(frame);
            }
            catch (Exception err) {
              JOptionPane.showMessageDialog(null, err.toString());
            }

            System.out.println("Inventory item added/updated");
          } else {

            System.out.println("Inventory item not added/updated");
          }
        }
    });

      // button to remove inventory item
      JButton remove_inventory_button = new JButton("Remove Inventory Item");

      // action to remove menu item
      remove_inventory_button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          JTextField sku = new JTextField();
          Object[] input = {
              "SKU", sku
          };
          
          int option = JOptionPane.showConfirmDialog(null, input, "Remove Inventory Item", JOptionPane.OK_CANCEL_OPTION);
          if (option == JOptionPane.OK_OPTION) {
            try {
              
              if (sku.getText().equals("")) {
                JOptionPane.showMessageDialog(null,"Must enter inventory SKU number!");
                return; // dont create sql statement
              }

              //create the SQL statement
              String sql_stmt = "DELETE FROM supply WHERE sku = '" + sku.getText() + "'";
              
              System.out.println(sql_stmt);

              stmt.executeUpdate(sql_stmt);

              // refresh jframe
              SwingUtilities.updateComponentTreeUI(frame);
            }
            catch (Exception err) {
              JOptionPane.showMessageDialog(null, err.toString());
            }

            System.out.println("Inventory item removed");
          } else {

            System.out.println("Inventory item not removed");
          }
        }
    });

      // // button to view order trends
      // JButton order_trends_button = new JButton("Order Trends");

      // // event for the order trends button
      // order_trends_button.addActionListener(new ActionListener() {
      //   @Override
      //   public void actionPerformed(ActionEvent a) {
      //     DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");

      //     JFormattedTextField start_date_1 = new JFormattedTextField(date_format);
      //     JFormattedTextField end_date_1 = new JFormattedTextField(date_format);
      //     JFormattedTextField start_date_2 = new JFormattedTextField(date_format);
      //     JFormattedTextField end_date_2 = new JFormattedTextField(date_format);
      //     Object[] input = {
      //       "Start of first time window (yyyy-MM-dd)", start_date_1,
      //       "End of first time window (yyyy-MM-dd)", end_date_1,
      //       "Start of second time window (yyyy-MM-dd)", start_date_2,
      //       "End of second time window (yyyy-MM-dd)", end_date_2,
      //     };

      //     int dates = JOptionPane.showConfirmDialog(null, input, "Order Trends", JOptionPane.OK_CANCEL_OPTION);

      //     if (dates == JOptionPane.OK_OPTION) {
      //       try{
      //         // compare dates to make sure they don't overlap and the starts are before the ends
      //         int result1 = start_date_1.getText().compareTo(end_date_1.getText());
      //         int result2 = start_date_2.getText().compareTo(end_date_2.getText()); 
      //         int result3 = start_date_1.getText().compareTo(start_date_2.getText());
      //         int result4 = end_date_1.getText().compareTo(start_date_2.getText());
      //         int result5 = end_date_1.getText().compareTo(end_date_2.getText());

      //         if (result1 > 0 ||
      //             result2 > 0 ||
      //             (result3 < 0 && result4 > 0) ||
      //             (result3 > 0 && result5 < 0)){
      //           JOptionPane.showMessageDialog(null,"Invalid date windows!");
      //           return; // dont create sql statement
      //         }

      //         //headers for the table
      //         String[] columns = new String[] {
      //           "Item", "Change"
      //         };
      //         // 2D list containing item and revenue change
      //         List<List<Object>> data = new ArrayList<List<Object>>();

      //         // iterate throught all menu items and find change in revenue
      //         for (int i = 501; i < 520; i++) {

      //           // create seperate SQL statements for each time window
      //           ResultSet result_window1 = stmt.executeQuery("SELECT total FROM orders WHERE item = " + String.valueOf(i) + " AND date BETWEEN '" + start_date_1 + "' AND '" + end_date_1 + "'");
      //           ResultSet result_window2 = stmt.executeQuery("SELECT total FROM orders WHERE item = " + String.valueOf(i) + " AND date BETWEEN '" + start_date_2 + "' AND '" + end_date_2 + "'");
                
      //           // calculate difference in revenue
      //           result_window1.getFloat("total");
      //           result_window2.getFloat("total");
      //         }
      //       } catch (Exception e){
      //         JOptionPane.showMessageDialog(null, e.toString());
      //       } 
      //     } else {

      //       System.out.println("No order trends compared");
      //     }
      //   }  
      // });
    // add buttons to top manager panel
      managerTopButtons.add(menu_button);
      managerTopButtons.add(inventory_button);

      // add buttons to bottom manager panel
      managerBottomButtons.add(add_menu_button);
      managerBottomButtons.add(remove_menu_button);
      managerBottomButtons.add(add_inventory_button);
      managerBottomButtons.add(remove_inventory_button);

      // add top and bottom button and text panel to manager panel
      manager.add(managerTopButtons, BorderLayout.PAGE_START);
      manager.add(managerText, BorderLayout.CENTER);
      manager.add(managerBottomButtons, BorderLayout.PAGE_END);
    
      // add manager panel to tabbed panel
      tabbedPane.addTab("MANAGER", manager);


      // DESIGN INVENTORY USAGE PANEL for manager
      JPanel inventoryUsage = new JPanel();

      // set inventory usage panel to box layout
      inventoryUsage.setLayout(new BoxLayout(inventoryUsage, BoxLayout.PAGE_AXIS));

      // create inventory usage instance
      new InventoryUsage(stmt, inventoryUsage);

      // add inventory usage to tabbed panel
      tabbedPane.addTab("INVENTORY USAGE", inventoryUsage);

      // DESIGN INVENTORY USAGE PANEL for manager
      JPanel orderingPopularity = new JPanel();

      // set inventory usage panel to box layout
      orderingPopularity.setLayout(new BoxLayout(orderingPopularity, BoxLayout.PAGE_AXIS));

      // create inventory usage instance
      new OrderingPopularity(stmt, orderingPopularity);

      // add inventory usage to tabbed panel
      tabbedPane.addTab("ORDERING POPULARITY", orderingPopularity);

      // DESIGN ORDERING TRENDS PANEL for manager
      JPanel orderingTrends = new JPanel();

      // set ordering trends panel to box layout
      orderingTrends.setLayout(new BoxLayout(orderingTrends, BoxLayout.PAGE_AXIS));

      // create ordering trends instance
      new OrderingTrends(stmt, orderingTrends);

      // add ordering trends to tabbed panel
      tabbedPane.addTab("ORDERING TRENDS", orderingTrends);     

      // DESIGN INVENTORY USAGE PANEL for manager
      JPanel inventoryPopularity = new JPanel();

      // set inventory usage panel to box layout
      inventoryPopularity.setLayout(new BoxLayout(inventoryPopularity, BoxLayout.PAGE_AXIS));

      // create inventory usage instance
      new InventoryPopular(stmt, inventoryPopularity);

      // add inventory usage to tabbed panel
      tabbedPane.addTab("INVENTORY POPULARITY", inventoryPopularity);

      // add all tabs to main panel
      panel.add(tabbedPane, BorderLayout.CENTER);

      // show the UI
      frame.pack();
      frame.setVisible(true);
    }
    
} 

    
     
