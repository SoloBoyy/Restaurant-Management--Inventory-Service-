import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Order {
    public int item;
    public int quanitity;

    Order(int i, int q) {
        item = i;
        quanitity = q;
    }
}

class SKU {
    public String description;
    public String sku;
    public Float amount;

    SKU(String d, String s, Float a) {
        description = d;
        sku = s;
        amount = a;
    }
}

public class InventoryUsage {
    
    ArrayList<SKU> SKUs = new ArrayList<SKU>();

    private boolean isThere(ResultSet rs, String column) {
        try {
            rs.findColumn(column);
            return true;
        } catch (SQLException sqlex){
            // not there
        }
    
        return false;
    }
    
    InventoryUsage(Statement stmt, JPanel fr) {

        // panel to hold text input for date selection
        JPanel date_selection_panel = new JPanel();

        // panel to hold table
        JPanel table_panel = new JPanel();

        JPanel bottom_panel = new JPanel();

        // set format of date input
        JTextField dateBegin_text = new JTextField("Begin Date (YYYY-MM-DD)");
        JTextField dateEnd_text = new JTextField("End Date (YYYY-MM-DD)");

        dateBegin_text.setPreferredSize(new Dimension(200, 30));
        dateEnd_text.setPreferredSize(new Dimension(200, 30));

        // add date ipnut to date selection panel
        date_selection_panel.add(dateBegin_text);
        date_selection_panel.add(dateEnd_text);

        // button to refresh dates
        JButton refresh_button = new JButton("Refresh");

        bottom_panel.add(refresh_button);

        // main action
        refresh_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                table_panel.removeAll();
                
                // get begin and end dates
                String begin = dateBegin_text.getText();
                String end = dateEnd_text.getText();

                // fill orders with all possible menu items
                ArrayList<Order> orders = new ArrayList<Order>();
                String sqlstmt = "SELECT * FROM menukey";

                try {
                    ResultSet result = stmt.executeQuery(sqlstmt);

                    while (result.next()) {
                        // create order object and add it to orders
                        Order tmp = new Order(result.getInt("item"), 0);
                        orders.add(tmp);
                    }
                } catch (Exception err) {
                    //JOptionPane.showMessageDialog(null, err.toString());
                }

                // fill menu item orders with a total quantity between date range
                sqlstmt = "SELECT * FROM orders WHERE date BETWEEN " + "'" + begin + "'" + " AND " + "'" + end + "'";

                try {
                    ResultSet result = stmt.executeQuery(sqlstmt);

                    // go through all orders
                    while (result.next()) {

                        // loop though all menu items
                        for (Order o : orders) {

                            // if item in orders == menu item in list
                            if (o.item == result.getInt("item")) {

                                // add to total quantity
                                o.quanitity += result.getInt("quantity");
                            }
                        }
                    }
                } catch (Exception err) {
                    //JOptionPane.showMessageDialog(null, err.toString());
                }


                // fill SKUs with all skus
                sqlstmt = "SELECT * FROM supply";
                try {
                    ResultSet result = stmt.executeQuery(sqlstmt);

                    while (result.next()) {

                        // create SKU object and add it to SKUs
                        SKU tmp = new SKU(result.getString("description"), result.getString("sku"), 0f);
                        SKUs.add(tmp);

                    }
                } catch (Exception err) {
                    //JOptionPane.showMessageDialog(null, err.toString());
                }

                // cycle thorugh each menu item and get the total decrements
                try {
                    for (Order o : orders) {

                        // get unit conversions for item
                        sqlstmt = "SELECT * FROM units WHERE item = " + "'" + o.item + "'";
                        ResultSet units = stmt.executeQuery(sqlstmt);
                        units.next();

                        // get total decremnt and modify SKU object
                        for (SKU s : SKUs) {

                            // check if sku is in units
                            if (isThere(units, s.sku)) {
                                // get decrement value from units
                                Float decrement = Float.parseFloat(units.getString(s.sku));

                                // add decrement * item quantity to total amount of sku decrement
                                s.amount += decrement * o.quanitity;
                            }
                        }
                    }
                } catch (Exception err) {
                    //JOptionPane.showMessageDialog(null, err.toString());
                }

                // create jtable with sku total decrements

                String[] headers = {"Description", "SKU", "Usage"};

                // 2D list to contain each row
                List<List<Object>> data = new ArrayList<List<Object>>();
        
                // create rows from SKUs
                for (SKU s : SKUs) {

                    // list to contain values in row
                    List<Object> row = new ArrayList<Object>();

                    // add values for each column to row list
                    row.add(s.description);
                    row.add(s.sku);
                    row.add(String.valueOf(s.amount));

                    // add row to all data list
                    data.add(row);
                }

                // clearing SKUs for next refresh
                SKUs.clear();
                
                // convert List[][] to Object[][]
                Object[][] dataArray = data.stream().map(l -> l.stream().toArray(Object[]::new)).toArray(Object[][]::new);
                
                //create table with data list and column headers
                JTable table = new JTable(dataArray, headers) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false to editable
                    return false;
                }
                };

                // create the croll view for the table
                JScrollPane scrollPane = new JScrollPane(table);
                table.setPreferredScrollableViewportSize(new Dimension(1100, 400));

                
                //add the table to the text panel of manager
                table_panel.add(scrollPane);
            }
        });

    // add all panel to main panel
    fr.add(date_selection_panel, BorderLayout.PAGE_START);
    fr.add(table_panel, BorderLayout.CENTER);
    fr.add(bottom_panel, BorderLayout.PAGE_END);


    }
}
