import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import com.opencsv.CSVReader;  

/*
CSCE 315
9-27-2021 Lab
 */
public class jdbcpostgreSQL {

  //Commands to run this script
  //This will compile all java files in this directory
  //javac *.java
  //This command tells the file where to find the postgres jar which it needs to execute postgres commands, then executes the code
  //Windows: java -cp ".;postgresql-42.2.8.jar" jdbcpostgreSQL
  //Mac/Linux: java -cp ".:postgresql-42.2.8.jar" jdbcpostgreSQL

  //MAKE SURE YOU ARE ON VPN or TAMU WIFI TO ACCESS DATABASE
  public static void main(String args[]) throws Exception {
    // Initialize CSV Reader
    // Scanner definition
    //Building the connection with your credentials

    //dbSetup hides my username and password
    dbSetup my = new dbSetup();
    String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315906_34db";
    Connection conn = null;

    
  //Connecting to the database
  try {
    //Class.forName("org.postgresql.Driver");
    conn = DriverManager.getConnection(dbConnectionString,my.user,my.pswd);
    
    Statement stmt = conn.createStatement();
    
   } catch (Exception e) {
    e.printStackTrace();
    System.err.println(e.getClass().getName()+": "+e.getMessage());
    System.exit(0);
}
    
    System.out.println("Opened database successfully");

    System.out.print("Enter how many CSV's you are importing: ");
    String num = System.console().readLine();
    System.out.println("Total CSV's : "+ num);
    int length = Integer.parseInt(num);
  
for(int z = 0; z <length; z++)
    {

    System.out.print("Enter File Name : ");
    String filename = System.console().readLine();
    System.out.println("Entered File Name : "+ filename);
    
    System.out.print("Enter Table Name : ");
    String tablename = System.console().readLine();
    System.out.println("Entered Table Name : "+ tablename);
      
  filename = "csv/"+filename;

  int counter = 0;
  
  if(filename.contains("First"))
  {
    counter = 1;
  }
  else if(filename.contains("Second"))
  {
    counter = 2;
  }
  else if(filename.contains("Third"))
  {
    counter = 3;
  }
  else if(filename.contains("Fourth"))
  {
    counter = 4;
  }
  
/* for (int j = 0; j < length; j++)
  {
    
    
    
*/
    CSVReader reader = null; 
    //Array list to hold csv contents 
    List<String> fill = new ArrayList<String>();
    try  
    {  
      //parsing a CSV file into CSVReader class constructor  
      reader = new CSVReader(new FileReader(filename));  
      String [] nextLine;  
      //reads one line at a time  
      while ((nextLine = reader.readNext()) != null)  
        {  
          for(String token : nextLine)  
            {  
             fill.add(token);
             //System.out.println(token);
           }  
        }  
      }    
    catch (Exception e)   
    {  
      e.printStackTrace();  
    }  
    
    try{
      //create a statement object
      Statement stmt = conn.createStatement();
      
      if(filename.contains("MenuKey"))
       {
       for(int i = 4; i < fill.size()-3; i+=4)
       {
         String tempPrice = "";
         if(fill.get(i+3).contains("$"))
         {
           tempPrice = fill.get(i+3).substring(1);
         }
         System.out.println("TempPrice: "+ tempPrice);
         
         System.out.println(Integer.parseInt(fill.get(i))+ "\n"+ fill.get(i+1) + "\n" + fill.get(i+2) + "\n" + fill.get(i+3) + "\n");
         //Fills table
         String sqlStatement = String.format("INSERT INTO "+tablename+" VALUES (%d, '%s','%s', %f)",Integer.parseInt(fill.get(i)), fill.get(i+1), fill.get(i+2), Float.valueOf(tempPrice).floatValue() );
 
         int result = stmt.executeUpdate(sqlStatement);

         System.out.println("--------------------Query Results--------------------");

         System.out.println(result);
         }
      } 
        else if (filename.contains("Week"))
        {
          System.out.println("Week Sales file");

          String day = "";
          int cell = 0;

          List<String> days = new ArrayList<String>();
          List<String> items = new ArrayList<String>();
          List<Float> prices = new ArrayList<Float>();

          for(int i = 0; i < fill.size();i++)
          {
            if(fill.get(i).contains("day"))
            {
              days.add(fill.get(i));
            }
          }

          // get item prices
          String sqlStatement = String.format("SELECT * FROM menukey");

          ResultSet menukey = stmt.executeQuery(sqlStatement);
          
          while(!fill.get(0).contains("Sunday"))
          {
            fill.remove(0);
          }
          while (menukey.next()) // get prices from menukey table
          { 
            items.add(menukey.getString("item"));
            prices.add(menukey.getFloat("price"));
          }
          
          int count = -1;
          while (cell < fill.size()-4)
          {

            String value = fill.get(cell);
            double price = 0;

            if (value.equals("") || value.equals(null)) {
              cell++;
              value = " ";
              continue;
            }

            // look for weekdays
            if(value.contains("day"))
            {
              count++;
              cell+=6;
            }
            else if(value.contains("5"))
            {
              value = days.get(count);
            } 

              for (int i = 0; i < items.size(); i++) // get value of price for item
              {
                if (items.get(i).equals(fill.get(cell))) {
                  price = prices.get(i);
                 // System.out.println (price);
                }
              }

              double total = price * Integer.parseInt(fill.get(cell+1));

              String date = fill.get(cell+2);

              int result;

              if (tablename.equals("weeksales")) {
                // fills SQL statement for weeksales
                sqlStatement = String.format("INSERT INTO "+tablename+" VALUES ('%s', %d, '%s', %d, %d, %f)", date, counter, value, Integer.parseInt(fill.get(cell)), Integer.parseInt(fill.get(cell+1)), total);
                
              } else if (tablename.equals("orders")) {

                // fills SQL statement for orders
                sqlStatement = String.format("INSERT INTO "+tablename+" VALUES (%d, %d, %f, '%s')", Integer.parseInt(fill.get(cell)), Integer.parseInt(fill.get(cell+1)), total, date);
              }

                // insert into psql table
                result = stmt.executeUpdate(sqlStatement);

              //OUTPUT 
              System.out.println("--------------------Query Results--------------------");
              System.out.println(sqlStatement);
              System.out.println(result); 

              cell += 4; // check for new type
              continue;

            
          }
          cell++;
        }
        else if(filename.contains("order"))
        { 
          System.out.println("Supply Order file");

          String date_format = "";
          String type = "";
          float price = 0;
          float tot_price = 0;

          int cell = 0;
          while (cell < fill.size() - 13)
          {
            String value = fill.get(cell); // the actual string in the cell

            //System.out.println(value);

            if (value.equals("") || value.equals(null)) {
              cell++;
              continue;
            }

            if (value.equals("Date")) {
              // parsing date
              System.out.println(value);
              String[] date = fill.get(cell+1).split("/");
              date_format = date[2] + "-" + date[0] + "-" + date[1];
              cell++;
              continue;
            }

            if (value.equals("Food")) {
              System.out.println(value);
              type = "Food";
              cell += 14;
            }

            if (value.equals("Bib")) {
              System.out.println(value);
              type = "Bib";
              cell += 14;
            }

            if (value.equals("Bottles")) {
              System.out.println(value);
              type = "Bottles";
              cell += 14;
            }

            if (value.equals("Serving")) {
              System.out.println(value);
              type = "Serving";
              cell += 14;
            }

            if (value.equals("Janitorial")) {
              System.out.println(value);
              type = "Janitorial";
              cell += 14;
            }
        
            if (type == "Food") {

              //System.out.println(fill.get(cell));

              // parsing price
              String str_price = fill.get(cell+7).replace("$", "");
              price = Float.parseFloat(str_price);
              
              // parsing tot_price
              String str_tot_price = fill.get(cell+8).replace("$", "");
              str_tot_price = str_tot_price.replace(",", "");
              tot_price = Float.parseFloat(str_tot_price);

              // parse trouble chars from details
              String desc_detail = fill.get(cell+11).replace("'", "");

              // fills SQL statement
              String sqlStatement = String.format("INSERT INTO "+tablename+" VALUES ('%s', '%s', '%s', %f, %d, '%s', '%s', %d, %f, %f, '%s', %d, '%s', '%s')", 
                                    type, fill.get(cell), fill.get(cell+1), Float.parseFloat(fill.get(cell+2)), 
                                    Integer.parseInt(fill.get(cell+3)), fill.get(cell+4), fill.get(cell+5), Integer.parseInt(fill.get(cell+6)), price, tot_price, 
                                    fill.get(cell+9), Integer.parseInt(fill.get(cell+10)), desc_detail, date_format);
              
              int result = stmt.executeUpdate(sqlStatement);

              //OUTPUT
              System.out.println("--------------------Query Results--------------------");
              System.out.println(sqlStatement);
              System.out.println(result);

              cell += 12; // check for new type
              continue;
            }

            if (type == "Bib") {

              //System.out.println(fill.get(cell));

              // parsing price
              String str_price = fill.get(cell+7).replace("$", "");
              price = Float.parseFloat(str_price);
              
              // parsing tot_price
              String str_tot_price = fill.get(cell+8).replace("$", "");
              str_tot_price = str_tot_price.replace(",", "");
              tot_price = Float.parseFloat(str_tot_price);

              // fills SQL statement
              String sqlStatement = String.format("INSERT INTO "+tablename+" VALUES ('%s', '%s', '%s', %d, %d, '%s', '%s', %d, %f, %f, '%s', %d, '%s', '%s')", 
                                    type, fill.get(cell), fill.get(cell+1), Integer.parseInt(fill.get(cell+2)), 
                                    Integer.parseInt(fill.get(cell+3)), fill.get(cell+4), fill.get(cell+5), Integer.parseInt(fill.get(cell+6)), price, tot_price, 
                                    fill.get(cell+9), Integer.parseInt(fill.get(cell+10)), fill.get(cell+11), date_format);

              int result = stmt.executeUpdate(sqlStatement);

              //OUTPUT
              System.out.println("--------------------Query Results--------------------");
              System.out.println(sqlStatement);
              System.out.println(result);

              cell += 12; // check for new type
              continue;
            }
          
            if (type == "Bottles") {

              //System.out.println(fill.get(cell));

              // parsing price
              String str_price = fill.get(cell+7).replace("$", "");
              price = Float.parseFloat(str_price);
              
              // parsing tot_price
              String str_tot_price = fill.get(cell+8).replace("$", "");
              str_tot_price = str_tot_price.replace(",", "");
              tot_price = Float.parseFloat(str_tot_price);

              // fills SQL statement
              String sqlStatement = String.format("INSERT INTO "+tablename+" VALUES ('%s', '%s', '%s', %d, %d, '%s', '%s', %d, %f, %f, '%s', %d, '%s', '%s')", 
                                    type, fill.get(cell), fill.get(cell+1), Integer.parseInt(fill.get(cell+2)), 
                                    Integer.parseInt(fill.get(cell+3)), fill.get(cell+4), fill.get(cell+5), Integer.parseInt(fill.get(cell+6)), price, tot_price, 
                                    fill.get(cell+9), Integer.parseInt(fill.get(cell+10)), fill.get(cell+11), date_format);

              int result = stmt.executeUpdate(sqlStatement);

              //OUTPUT
              System.out.println("--------------------Query Results--------------------");
              System.out.println(sqlStatement);
              System.out.println(result);

              cell += 12; // check for new type
              continue;  
            }

            if (type == "Serving") {

              //System.out.println(fill.get(cell));

              // parsing price
              String str_price = fill.get(cell+7).replace("$", "");
              price = Float.parseFloat(str_price);
              
              // parsing tot_price
              String str_tot_price = fill.get(cell+8).replace("$", "");
              str_tot_price = str_tot_price.replace(",", "");
              tot_price = Float.parseFloat(str_tot_price);

              // fills SQL statement
              String sqlStatement = String.format("INSERT INTO "+tablename+" VALUES ('%s', '%s', '%s', %d, %d, '%s', '%s', %d, %f, %f, '%s', %d, '%s', '%s')", 
                                    type, fill.get(cell), fill.get(cell+1), Integer.parseInt(fill.get(cell+2)), 
                                    Integer.parseInt(fill.get(cell+3)), fill.get(cell+4), fill.get(cell+5), Integer.parseInt(fill.get(cell+6)), price, tot_price, 
                                    fill.get(cell+9), Integer.parseInt(fill.get(cell+10)), fill.get(cell+11), date_format);

              int result = stmt.executeUpdate(sqlStatement);

              //OUTPUT
              System.out.println("--------------------Query Results--------------------");
              System.out.println(sqlStatement);
              System.out.println(result);

              cell += 12; // check for new type
              continue;  
            }

            if (type == "Janitorial") {

              //System.out.println(fill.get(cell));

              // parsing price
              String str_price = fill.get(cell+7).replace("$", "");
              price = Float.parseFloat(str_price);
              
              // parsing tot_price
              String str_tot_price = fill.get(cell+8).replace("$", "");
              str_tot_price = str_tot_price.replace(",", "");
              tot_price = Float.parseFloat(str_tot_price);

              // fills SQL statement
              String sqlStatement = String.format("INSERT INTO "+tablename+" VALUES ('%s', '%s', '%s', %d, %d, '%s', '%s', %d, %f, %f, '%s', %d, '%s', '%s')", 
                                    type, fill.get(cell), fill.get(cell+1), Integer.parseInt(fill.get(cell+2)), 
                                    Integer.parseInt(fill.get(cell+3)), fill.get(cell+4), fill.get(cell+5), Integer.parseInt(fill.get(cell+6)), price, tot_price, 
                                    fill.get(cell+9), Integer.parseInt(fill.get(cell+10)), fill.get(cell+11), date_format);

              int result = stmt.executeUpdate(sqlStatement);

              //OUTPUT
              System.out.println("--------------------Query Results--------------------");
              System.out.println(sqlStatement);
              System.out.println(result);

              cell += 12; // check for new type
              continue;
            }
            
            cell++;
          }
        }
        else if (filename.contains("Unit"))
        {
        for(int i = 34; i < fill.size()-20; i+=34)
        {
        
          //Fills table
          String sqlStatement = String.format("INSERT INTO "+tablename+" VALUES ('%s', %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f)", 
                                                                                                    fill.get(i),
                                                                                                    Float.parseFloat(fill.get(i+1)), 
                                                                                                    Float.parseFloat(fill.get(i+2)),
                                                                                                    Float.parseFloat(fill.get(i+3)),
                                                                                                    Float.parseFloat(fill.get(i+4)),
                                                                                                    Float.parseFloat(fill.get(i+5)),
                                                                                                    Float.parseFloat(fill.get(i+6)),
                                                                                                    Float.parseFloat(fill.get(i+7)),
                                                                                                    Float.parseFloat(fill.get(i+8)),
                                                                                                    Float.parseFloat(fill.get(i+9)),
                                                                                                    Float.parseFloat(fill.get(i+10)),
                                                                                                    Float.parseFloat(fill.get(i+11)),
                                                                                                    Float.parseFloat(fill.get(i+12)),
                                                                                                    Float.parseFloat(fill.get(i+13)),
                                                                                                    Float.parseFloat(fill.get(i+14)),
                                                                                                    Float.parseFloat(fill.get(i+15)),
                                                                                                    Float.parseFloat(fill.get(i+16)),
                                                                                                    Float.parseFloat(fill.get(i+17)),
                                                                                                    Float.parseFloat(fill.get(i+18)),
                                                                                                    Float.parseFloat(fill.get(i+19)),
                                                                                                    Float.parseFloat(fill.get(i+20)),
                                                                                                    Float.parseFloat(fill.get(i+21)),
                                                                                                    Float.parseFloat(fill.get(i+22)),
                                                                                                    Float.parseFloat(fill.get(i+23)),
                                                                                                    Float.parseFloat(fill.get(i+24)),
                                                                                                    Float.parseFloat(fill.get(i+25)),
                                                                                                    Float.parseFloat(fill.get(i+26)),
                                                                                                    Float.parseFloat(fill.get(i+27)),
                                                                                                    Float.parseFloat(fill.get(i+28)),
                                                                                                    Float.parseFloat(fill.get(i+29)),
                                                                                                    Float.parseFloat(fill.get(i+30)),
                                                                                                    Float.parseFloat(fill.get(i+31)),
                                                                                                    Float.parseFloat(fill.get(i+32)),
                                                                                                    Float.parseFloat(fill.get(i+33)));
  
          int result = stmt.executeUpdate(sqlStatement);
 
          System.out.println("--------------------Query Results--------------------");
 
          System.out.println(result);
          }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }

   
    }
 //closing the connection
 try {
  conn.close();
  System.out.println("Connection Closed.");
} catch(Exception e) {
  System.out.println("Connection NOT Closed.");
}//end try catch
  }//end main 
}//}//end Class
