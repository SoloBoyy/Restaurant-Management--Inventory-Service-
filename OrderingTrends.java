import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import java.text.SimpleDateFormat;
import java.text.DateFormat;

class OrderTrend {
    public int item;
    public float trend;

    OrderTrend(int i, float q) {
        item = i;
        trend = q;
    }

    public float getTrend() {
        return this.trend;
    }
}


public class OrderingTrends {
    
    OrderingTrends(Statement stmt, JPanel fr) {

        // panel to hold text input for date selection
        JPanel date_selection_panel = new JPanel();

        // panel to hold table
        JPanel table_panel = new JPanel();

        JPanel bottom_panel = new JPanel();

        // set format of date input

        JTextField start_date_1 = new JTextField("First Window Start (YYYY-MM-DD)");
        JTextField end_date_1 = new JTextField("First Window End (YYYY-MM-DD)");
        JTextField start_date_2 = new JTextField("Second Window Start (YYYY-MM-DD)");
        JTextField end_date_2 = new JTextField("Second Window End (YYYY-MM-DD)");

        start_date_1.setPreferredSize(new Dimension(200, 30));
        end_date_1.setPreferredSize(new Dimension(200, 30));
        start_date_2.setPreferredSize(new Dimension(200, 30));
        end_date_2.setPreferredSize(new Dimension(200, 30));

        // add date ipnut to date selection panel
        date_selection_panel.add(start_date_1);
        date_selection_panel.add(end_date_1);
        date_selection_panel.add(start_date_2);
        date_selection_panel.add(end_date_2);

        // button to refresh dates
        JButton refresh_button = new JButton("Refresh");

        bottom_panel.add(refresh_button);

        // main action
        refresh_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {

            try{
              // compare dates to make sure they don't overlap and the starts are before the ends
              int result1 = start_date_1.getText().compareTo(end_date_1.getText());
              int result2 = start_date_2.getText().compareTo(end_date_2.getText()); 
              int result3 = start_date_1.getText().compareTo(start_date_2.getText());
              int result4 = end_date_1.getText().compareTo(start_date_2.getText());
              int result5 = end_date_1.getText().compareTo(end_date_2.getText());

              if (result1 > 0 ||
                  result2 > 0 ||
                  (result3 < 0 && result4 > 0) ||
                  (result3 > 0 && result5 < 0)){
                JOptionPane.showMessageDialog(null,"Invalid date windows!");
                return; // dont create sql statement
              }
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, e.toString());
            }

                table_panel.removeAll();
                
                // get begin and end dates
                String begin_1 = start_date_1.getText();
                String end_1 = end_date_1.getText();
                String begin_2 = start_date_2.getText();
                String end_2 = end_date_2.getText();

                // fill orders with all possible menu items
                ArrayList<OrderTrend> orders = new ArrayList<OrderTrend>();
                String sqlstmt = "SELECT * FROM menukey";

                try {
                    ResultSet result = stmt.executeQuery(sqlstmt);

                    while (result.next()) {
                        // create order object and add it to orders
                        OrderTrend tmp = new OrderTrend(result.getInt("item"), 0);
                        orders.add(tmp);
                    }
                } catch (Exception err) {
                    //JOptionPane.showMessageDialog(null, err.toString());
                }

                // fill menu item orders with a total trend between date range
                String sqlstmt1 = "SELECT total FROM orders WHERE date BETWEEN " + "'" + begin_1 + "'" + " AND " + "'" + end_1 + "'";
                String sqlstmt2 = "SELECT total FROM orders WHERE date BETWEEN " + "'" + begin_2 + "'" + " AND " + "'" + end_2 + "'";

                try {
                    ResultSet result1 = stmt.executeQuery(sqlstmt1);                    
                    // get revenue total for both time windows
                    float revenue1 = 0;
                    while (result1.next()) {
                        revenue1 += result1.getFloat("total");
                    }
                    
                    ResultSet result2 = stmt.executeQuery(sqlstmt2);
                    
                    float revenue2 = 0;
                    while(result2.next()) {
                        revenue2 += result2.getFloat("total");
                    }

                    // loop though all menu items
                    for (OrderTrend o : orders) {

                        String sqlstmt_item1 = "SELECT total FROM orders WHERE item = '" + Integer.toString(o.item) + "' AND date BETWEEN " + "'" + begin_1 + "'" + " AND " + "'" + end_1 + "'";
                        String sqlstmt_item2 = "SELECT total FROM orders WHERE item = '" + Integer.toString(o.item) + "' AND date BETWEEN " + "'" + begin_2 + "'" + " AND " + "'" + end_2 + "'";
                        
                        ResultSet item_revenue1 = stmt.executeQuery(sqlstmt_item1);

                        // get partial revenue for the item for each window
                        float window1_revenue = 0;
                        while (item_revenue1.next()) {
                            window1_revenue += item_revenue1.getFloat("total");
                        }
                        
                        ResultSet item_revenue2 = stmt.executeQuery(sqlstmt_item2);

                        float window2_revenue = 0;
                        while (item_revenue2.next()) {
                            window2_revenue += item_revenue2.getFloat("total");
                        }

                        // find difference in % of share of revenue from each window
                        float revenue_share1 = 100 * (window1_revenue / revenue1);
                        float revenue_share2 = 100 * (window2_revenue / revenue2);

                        float order_trend = revenue_share2 - revenue_share1;

                        o.trend = order_trend;
                    }
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(null, err.toString());
                }


                //Sort list (descending)
                Collections.sort(orders, (o1, o2) -> Float.compare(o2.getTrend(), o1.getTrend()));

                // orders.sort((o2, o1)
                //       -> o1.getTrend().compareTo(o2.getTrend()));
                
                // create jtable with sku total decrements
                
                String[] headers = {"Item", "Trend", "Popularity Rating"};

                // 2D list to contain each row
                List<List<Object>> data = new ArrayList<List<Object>>();
                int count = 0;
                for(OrderTrend o: orders)
                {
                    count+=1;
                     // list to contain values in row
                     List<Object> row = new ArrayList<Object>();
                     row.add(o.item);
                     row.add(o.trend);
                     row.add(count);
                     
                     data.add(row);
                   // System.out.println("Item: " +o.item);
                   // System.out.println("Quantity: " +o.trend);
                }

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
