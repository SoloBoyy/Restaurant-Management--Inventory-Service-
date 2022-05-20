import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

  class DEC {
    String sku;
    Float amount;

    DEC(String s, Float a) {
      sku = s;
      amount = a;
    }
  }

  class ServerPanel {

  DefaultTableModel tableModel = new DefaultTableModel();
  DefaultTableModel tableModel1 = new DefaultTableModel();
  List<Double> finTotal = new ArrayList<Double>();
  List<Double> deductor = new ArrayList<Double>();
  final ArrayList<ArrayList<String> > submitOrder = new ArrayList<ArrayList<String> >();
  Double sum = 0.0;

  List<String> prices = new ArrayList<String>();

  private boolean isThere(ResultSet rs, String column) {
    try {
        rs.findColumn(column);
        return true;
    } catch (SQLException sqlex){
        // not there
    }

    return false;
}
    
  ServerPanel(List<String> fill, ResultSet result,Statement stmt, JFrame fr ){

    // setting size of the main jframe
    fr.setPreferredSize(new Dimension(1480, 700));

    // jframe is closed
    WindowListener listener = new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
         Frame frame = (Frame) evt.getSource();
         System.out.println("Closing = "+frame.getTitle());
      }
    }; 

    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    fr.addWindowListener(listener);

    // create a tabbed pane
    Container panel = fr.getContentPane();
    JTabbedPane tabbedPane = new JTabbedPane();
    
    // Panels to organize contet
    JPanel server = new JPanel();
    server.setLayout(new BoxLayout(server, BoxLayout.PAGE_AXIS));

    // add server order to frame
    fr.add(server);

    JPanel top = new JPanel();
    JPanel middle = new JPanel();
    JPanel bottom = new JPanel();

    // add all panela to main frame
    server.add(top, BorderLayout.PAGE_START);
    server.add(middle, BorderLayout.CENTER);
    server.add(bottom, BorderLayout.PAGE_END);

     // list to hold all sql statements
    List<String> sqlstmts= new ArrayList<String>();

    // getting all SKUs from supply
    String sku_stmt = "SELECT sku FROM supply";
    ArrayList<String>  SKUs = new ArrayList<String>();

    try {
      ResultSet sku_result = stmt.executeQuery(sku_stmt);

      while (sku_result.next()) {
          // create order object and add it to orders
          SKUs.add(sku_result.getString("sku"));
      }
    } catch (Exception err) {
        JOptionPane.showMessageDialog(null, err.toString());
    }

    prices = fill;

    // add each button from menukey
    try {
      // get all menus from menukey
      List<Integer> int_menu_item = new ArrayList<Integer>();
      List<String> str_menu_name = new ArrayList<String>();
      List<Float> float_menu_price = new ArrayList<Float>();


      result = stmt.executeQuery("SELECT * FROM menukey");
      while (result.next())
      {
        int_menu_item.add(Integer.parseInt(result.getString("item")));
        str_menu_name.add(result.getString("name"));
        float_menu_price.add(Float.parseFloat(result.getString("price")));
      }

      // create all menu buttons from menukey
      for (int j = 0; j < int_menu_item.size(); j++) {

        final Integer menu_item = int_menu_item.get(j);
        final String menu_name = str_menu_name.get(j);
        final Float menu_price = float_menu_price.get(j);

        // create button
        JButton menu_button = new JButton(menu_name);
        
        // add a the button instance to main frame
        top.add(menu_button);

        // add action listener to button
        menu_button.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent a) {
            try{
               //prompting for how much the customer is ordering
              String count=JOptionPane.showInputDialog(fr,"Enter Order Amount","");

              // display count ordered
              if(count!=null) {
                JOptionPane.showMessageDialog(fr,"You Ordered "+count);
              } else {
                JOptionPane.showMessageDialog(fr,"Error");
              }

              //calculate subtotal based of count input
              double total = menu_price * Integer.parseInt(count); 

              //add subtotal to arraylist
              finTotal.add(total);

              //inserting name, order quantity, and price into Jtable
              tableModel.insertRow(0, new Object[] {menu_name, count, total});
  
              // get the date of order
              DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
              LocalDate localDate = LocalDate.now();
              String date = dtf.format(localDate);
  
              System.out.println(date);
  
              //create the SQL statement
              System.out.println("Check");
              String sqlStatement = String.format("INSERT INTO orders VALUES (%d, %d, %f, '%s')", menu_item, Integer.parseInt(count), total, date);
              sqlstmts.add(sqlStatement);

              //Summing subtotals in arraylist
              sum = 0.0;
              for(int i = 0; i < finTotal.size();i++) {
                sum+=finTotal.get(i);
              }

              //Updating 2nd JTable with total (real time)
              tableModel1.setValueAt(sum,0,0);
  
  
              // decrement quantity from supply
              // get unit conversions for item
              String getUnits = "SELECT * FROM units WHERE item = " + "'" + Integer.toString(menu_item) + "'";
              ResultSet units = stmt.executeQuery(getUnits);
              units.next();
  
              ArrayList<DEC> decrements = new ArrayList<DEC>();
              
              // add unit conversions to decreemnt list
              for (String sku : SKUs) {
                if (isThere(units, sku)) {
                  DEC tmp = new DEC(sku, Float.parseFloat(units.getString(sku)));

                  decrements.add(tmp);
                }
              }

              for (DEC d : decrements) {

                // get to row of sku
                String getSupply = "SELECT * FROM supply WHERE sku = '" + d.sku + "'";
                ResultSet supplyQ = stmt.executeQuery(getSupply);
                supplyQ.next();

                // get current quanitity of sku
                Float cur_amount = Float.parseFloat(supplyQ.getString("quantity"));

                // get new quantity from table and order count
                d.amount = cur_amount - (d.amount * Integer.parseInt(count));
                
                System.out.println(d.amount.toString());
  
                // update supply at sku 
                String setSupply = "UPDATE supply SET quantity = " + d.amount.toString() + " WHERE sku = '" + d.sku + "'";
                stmt.executeUpdate(setSupply);

              }
  
            } catch (Exception err) {
              JOptionPane.showMessageDialog(null, err.toString());
            } 
          } 
        } 
        );

      }
      
    } catch (Exception error) {
      //JOptionPane.showMessageDialog(null, error.toString());
    }
    
    // Initializing the first JTable
    JTable jt = new JTable(tableModel);
    tableModel.addColumn("Item");
    tableModel.addColumn("Quantity");
    tableModel.addColumn("Price");

    // adding it to JScrollPane
    JScrollPane sp = new JScrollPane(jt);
    middle.add(sp);

    // Initializing the second JTable
    JTable jtotal = new JTable(tableModel1);
    tableModel1.addColumn("Total");
    tableModel1.insertRow(0,new Object[] {sum});

    // adding it to JScrollPane
    JScrollPane sp1 = new JScrollPane(jtotal);
    sp1.setPreferredSize(new Dimension(100, 40));

    // adding jtotal to bottom
    bottom.add(sp1);

    // create submit button
    JButton submit=new JButton("Submit Order");
    bottom.add(submit);

    // action for submit button
    submit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent a) {
        try{
            //prompting for how much the customer is ordering
          JOptionPane.showMessageDialog(fr,"Your Order Has Been Submitted");
          for(int i = 0; i < sqlstmts.size();i++)
          {
            stmt.executeUpdate(sqlstmts.get(i));
          }

          sqlstmts.clear();
          tableModel.setRowCount(0);
          tableModel1.setValueAt(0,0,0);
          
        }
        catch (Exception err) {
          JOptionPane.showMessageDialog(null, err.toString());
        } } } );
          

      // add SERVER ORDER to tabbedpane
      tabbedPane.addTab("SERVER ORDER", server);

      // DESIGN PREVIOUS ORDERS for server
      JPanel previousOrders = new JPanel();

      // set ordering trends panel to box layout
      previousOrders.setLayout(new BoxLayout(previousOrders, BoxLayout.PAGE_AXIS));

      // create ordering trends instance
      new PreviousOrders(stmt, previousOrders);

      // add ordering trends to tabbed panel
      tabbedPane.addTab("PREVIOUS ORDERS", previousOrders); 

      // add all tabs to main panel
      panel.add(tabbedPane, BorderLayout.CENTER);

      // show the UI
      fr.pack();
      fr.setVisible(true);
}
  } 
