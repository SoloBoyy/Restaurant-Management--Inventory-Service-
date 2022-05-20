import java.sql.*;
import java.util.List;
import javax.swing.*;
import java.util.ArrayList;
 
  public class jdbcpostgreSQLGUI {
    public static void main(String args[]) throws SQLException {
      dbSetup my = new dbSetup();
      String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315906_34db";
      //Building the connection
      Connection conn = null;
      try {
          Class.forName("org.postgresql.Driver");
          conn = DriverManager.getConnection(dbConnectionString,my.user,my.pswd);
          
      } catch (Exception e) {
          e.printStackTrace();
          System.err.println(e.getClass().getName()+": "+e.getMessage());
          System.exit(0);
      }//end try catch
      boolean close = false; // boolean to check for frame close
      try 
      {
       Statement stmt = conn.createStatement();
       List<String> fill = new ArrayList<String>();
       ResultSet result = stmt.executeQuery("SELECT * FROM menukey");
       while (result.next())
       {
         fill.add(result.getString("price"));
       }
       //JFrame frame = new JFrame();
        new Login(fill, result, stmt);
       // new ManagerPanel(stmt, frame);

      }
      catch (Exception e)
      {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
      }
     
      //closing the connection
      try {
        while(true) { 
          if (close) { // close the connection because frame is closed
            conn.close();
            JOptionPane.showMessageDialog(null,"Connection Closed.");
            break;
          }
        }
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
    }//end main
  }//end Class
