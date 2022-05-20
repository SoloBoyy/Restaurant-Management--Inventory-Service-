import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreviousOrders {
    
    PreviousOrders(Statement stmt, JPanel fr) {

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
        JButton refresh_button = new JButton("Refresh Orders");

        bottom_panel.add(refresh_button);

        // main action
        refresh_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // create jtable with all orders
                String[] headers = {"Item", "Quantity", "Total", "Date"};

                // 2D list to contain each row
                List<List<Object>> data = new ArrayList<List<Object>>();

                table_panel.removeAll();
                
                // get begin and end dates
                String begin = dateBegin_text.getText();
                String end = dateEnd_text.getText();

                // fill menu item orders with a total quantity between date range
                String sqlstmt = "SELECT * FROM orders WHERE date BETWEEN " + "'" + begin + "'" + " AND " + "'" + end + "'";

                try {
                    ResultSet result = stmt.executeQuery(sqlstmt);

                    // go through all orders
                    while (result.next()) {

                        List<Object> row = new ArrayList<Object>();

                        row.add(result.getString("item"));
                        row.add(result.getString("quantity"));
                        row.add(result.getString("total"));
                        row.add(result.getString("date"));

                        data.add(row);
                    }

                } catch (Exception err) {
                    //JOptionPane.showMessageDialog(null, err.toString());
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
