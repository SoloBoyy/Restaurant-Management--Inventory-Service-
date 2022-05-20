import java.sql.*;
import java.util.List;
import java.awt.*;    
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.Border;
import java.awt.event.*;  
import java.util.ArrayList;
import javax.swing.JFrame;  
import javax.swing.SwingUtilities;  
import java.lang.*;


public class RestockReport{

		// Constructor
		RestockReport(Statement stmt, JPanel fr){

			// Create the Panels and Buttons ***************************************************************************************

			// Panel to keep all the option buttons 
			JPanel labelPanel = new JPanel();

			// Panel to hold the table 
			JPanel tablePanel = new JPanel();

			// Panel to hold the buttons
			JPanel buttonPanel = new JPanel();

			// Label as the title of the window
			JLabel titleLabel = new JLabel("Restock Report");

			// Button to view Current Fill Levels
			JButton currentFillButton = new JButton("View Current Fill Levels");

			// Button to view previous Restock Report
			JButton prevReportButton = new JButton("View Previous Restock Report");

			// Button to view Fill Levels
			JButton viewFillButton = new JButton("Edit Fill Levels");

			// Button to generate report
			JButton genReportButton = new JButton("Generate Report");

			// Button to update Inventory
			JButton updateInventoryButton = new JButton("Update Inventory");

			// Button to Submit Changes to Fill Level
			JButton editFillButton = new JButton("Change Fill Level");


			

			// Adds Functionality to Buttons *****************************************************************************************

			// Current Fills Button is pushed
			currentFillButton.addActionListener( new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent a) {
					try{

						// Clear the Table Section To put a new table in
						tablePanel.removeAll();
						// Create new Table
						JTable currentFillTable = generateTable(stmt);

						// Put Table in a scroll Pane and edit dimensions
						JScrollPane scrollpane = new JScrollPane(currentFillTable);
						currentFillTable.setPreferredScrollableViewportSize(new Dimension(1100, 400));

						// Add scrollPane and refresh page
						tablePanel.add(scrollpane);
						SwingUtilities.updateComponentTreeUI(fr);
						
						// If error show message
					} catch (Exception e){
            			JOptionPane.showMessageDialog(null, e.toString());
          			}
				}
			});


			// Previous Report Button is pushed
			prevReportButton.addActionListener (new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent a) {
					try{

						// Clear the Table Section To put a new table in
						tablePanel.removeAll();

						// Generate the most up to date Restock Report
						JTable reportTable = showReport(stmt);

						// If table is empty
						if (reportTable == null){
							JLabel noReport = new JLabel("There are no Restock Reports");
							tablePanel.add(noReport);
							SwingUtilities.updateComponentTreeUI(fr);
							return;
						}

						// Put table in a scroll pane and edit dimensions
						JScrollPane scrollpane = new JScrollPane(reportTable);
						reportTable.setPreferredScrollableViewportSize(new Dimension(1100, 400));

						// Add scrollpane and refresh page
						tablePanel.add(scrollpane);
						tablePanel.add(updateInventoryButton);
						SwingUtilities.updateComponentTreeUI(fr);
					}catch (Exception e){
            			JOptionPane.showMessageDialog(null, e.toString());
          			}
				}
			});


			// Edit Fill Level Button is pushed
			viewFillButton.addActionListener (new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent a){
					try{

					// Remove all TablePanel to make room for new table
					tablePanel.removeAll();

					// Titles for each of the columns 
					String[] columns = {"SKU", "Name", "Fill Level"};

					// List for data in table
					List<List<Object>> data = new ArrayList<List<Object>>();

					// Get data from Table
					ResultSet result = stmt.executeQuery("SELECT * FROM supply");

					while (result.next()){
						List<Object> row = new ArrayList<Object>();

						row.add(result.getString("sku"));
						row.add(result.getString("description"));
						row.add(result.getInt("delivered"));

						data.add(row);
					}

					// Convert List[][] to Object[][]
					Object[][] dataArray = data.stream().map(l -> l.stream().toArray(Object[]::new)).toArray(Object[][]::new);

					// Create Table & Set only the Fill Level Column to Editable
					JTable changeFillTable = new JTable(dataArray, columns){
						@Override
              			public boolean isCellEditable(int row, int column) {
	                 		
	                 				return false;
              			}
					};

					// Put Table in a scroll Pane and edit dimensions
						JScrollPane scrollpane = new JScrollPane(changeFillTable);
						changeFillTable.setPreferredScrollableViewportSize(new Dimension(1100, 400));


						// Add scrollPane and refresh page
						tablePanel.add(scrollpane);
						tablePanel.add(editFillButton);
						SwingUtilities.updateComponentTreeUI(fr);
				} catch (Exception e){
            			JOptionPane.showMessageDialog(null, e.toString());
          		}}
			});


			editFillButton.addActionListener (new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent a){
					try{
							// Create Pop up Elements
							JTextField SKU = new JTextField();
							JTextField newFill = new JTextField();
							Object[] input = {
						              "SKU", SKU,
						              "Desired Fill Level", newFill};	

						    // Create Pop up Window
						    int option = JOptionPane.showConfirmDialog(null, input, "Edit Fill Level", JOptionPane.OK_CANCEL_OPTION);
						    if (option == JOptionPane.OK_OPTION){

						    	// Check to see if Fields are empty
						    	if (SKU.getText().equals("")){
						    		JOptionPane.showMessageDialog(null,"Must Enter SKU Number!");
						    	}

						    	// Check to see if 
						    	if (!isNumeric(newFill.getText())){
						    		JOptionPane.showMessageDialog(null, "Must Enter A Numeric New FIll Level!");
						    	}

						    	// Update Fill Level
						    	String sqlstmt = "UPDATE supply SET delivered = " + Integer.parseInt(newFill.getText()) + " WHERE sku = '" + SKU.getText() + "'";
						    	int result = stmt.executeUpdate(sqlstmt);

						    	
						    	SwingUtilities.updateComponentTreeUI(fr);
						    }
					}catch (Exception e){
			            JOptionPane.showMessageDialog(null, e.toString());
			        }
				}
			});
		


			// Generate Report Button is pushed
			genReportButton.addActionListener (new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent a){
					try{

						// Create a second statement
						dbSetup my = new dbSetup();
      					String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315906_34db";
      					Connection conn = null;
      					Class.forName("org.postgresql.Driver");
          				conn = DriverManager.getConnection(dbConnectionString,my.user,my.pswd);
          				Statement stmt2 = conn.createStatement();

						// Refresh Report Table
						int refresh = stmt.executeUpdate("TRUNCATE TABLE restockreport");

						// Find items that are less than 100% Filled and add them to the restock report database
						ResultSet result = stmt.executeQuery("SELECT * FROM supply WHERE quantity < delivered");
						while (result.next()){
							String SKU = result.getString("sku");
							String NAME = result.getString("description");
							String STOREDBY = result.getString("delivered_by");
							int FILLLEVEL = result.getInt("delivered");
							float CURRENTQUANTITY = result.getFloat("quantity");
							float PRICE = result.getFloat("price");
							int TOFILL = (int)Math.ceil(FILLLEVEL - CURRENTQUANTITY);

							String sqlstmt = "INSERT INTO restockreport VALUES ('" + SKU + "', '" + NAME + "', '" + STOREDBY + "', " + FILLLEVEL + 
																				", " + CURRENTQUANTITY + ", " + TOFILL + ", " 
																				+ Math.round((TOFILL * PRICE) * 100.0) / 100.0  + ")";

							int update = stmt2.executeUpdate(sqlstmt);
						}

						// Clear Table Panel for new table
						tablePanel.removeAll();

						// Generate the new Report
						JTable reportTable = showReport(stmt);

						// If table is empty
						if (reportTable == null){
							JLabel noReport = new JLabel("There are no Restock Reports");
							tablePanel.add(noReport);
							SwingUtilities.updateComponentTreeUI(fr);
							return;
						}

						// Put table in a scroll pane and edit dimensions
						JScrollPane scrollpane = new JScrollPane(reportTable);
						reportTable.setPreferredScrollableViewportSize(new Dimension(1100, 400));

						// Add scrollpane and refresh page
						tablePanel.add(scrollpane);
						tablePanel.add(updateInventoryButton);
						SwingUtilities.updateComponentTreeUI(fr);

					} catch (Exception e){
            			JOptionPane.showMessageDialog(null, e.toString());
          			}
				}
			});


			// Update Inventory Button is pushed
			updateInventoryButton.addActionListener ( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent a){
					try{

						// Create a second statement
						dbSetup my = new dbSetup();
      					String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315906_34db";
      					Connection conn = null;
      					Class.forName("org.postgresql.Driver");
          				conn = DriverManager.getConnection(dbConnectionString,my.user,my.pswd);
          				Statement stmt2 = conn.createStatement();

						// Grab data from Restock Report
						ResultSet result = stmt.executeQuery("SELECT * FROM restockreport");
						while(result.next()){
							String SKU = result.getString("sku");
							int ORDER = result.getInt("quantity_to_order");
							float QUANT = result.getFloat("current_quantity");
							float newNum = ORDER + QUANT;

							String sqlStmt = "UPDATE supply SET quantity = " + newNum + " WHERE sku = '" + SKU + "'";
							int result2 = stmt2.executeUpdate(sqlStmt);
						}

					}catch (Exception e){
            			JOptionPane.showMessageDialog(null, e.toString());
          			}
				}
			});

			

			// Add the buttons, tables, & panels to the main GUI ****************************************************************************************

			// Add buttons and table to Panels
			labelPanel.add(titleLabel);
			buttonPanel.add(currentFillButton);
			buttonPanel.add(prevReportButton);
			buttonPanel.add(viewFillButton);
			buttonPanel.add(genReportButton);

			//Adds panels to main GUI
			fr.add(labelPanel, BorderLayout.PAGE_START);
			fr.add(tablePanel, BorderLayout.CENTER);
			fr.add(buttonPanel, BorderLayout.PAGE_END);
		}

		// HELPER FUNCTIONs *********************************************************************************************************

			// generateTable	-	Creates the table to show current fill levels
			// showReport		-	Grabs data from a previous or for a newly made report
			// getPercentage 	-	Returns the percentage of how fill an item is

			public JTable generateTable(Statement stmt){
				try{
					// Titles for each of the columns 
					String[] columns = {
						"SKU", "Name", "Fill Level", "Stored By", "Quantity Status"
					};

					// List for data in table
					List<List<Object>> data = new ArrayList<List<Object>>();

					// Get data from Table
					ResultSet result = stmt.executeQuery("SELECT * FROM supply");

					while (result.next()){
						List<Object> row = new ArrayList<Object>();

						row.add(result.getString("sku"));
						row.add(result.getString("description"));
						row.add(result.getInt("delivered"));
						row.add(result.getString("delivered_by"));
						row.add(getPercentage(result) + "%");

						data.add(row);
					}

					// Convert List[][] to Object[][]
					Object[][] dataArray = data.stream().map(l -> l.stream().toArray(Object[]::new)).toArray(Object[][]::new);

					JTable currentFillTable = new JTable(dataArray, columns){
						@Override
              			public boolean isCellEditable(int row, int column) {
                 		//all cells false to editable
                 		return false;
              			}
					};

					return currentFillTable;

	            } catch (Exception e){
            		JOptionPane.showMessageDialog(null, e.toString());
            		return null;
          		}

			}


			public JTable showReport(Statement stmt){
				try{

					// Check to see if there is any data from a previous Report
					ResultSet count = stmt.executeQuery("SELECT COUNT(*) AS total FROM restockreport");
					count.next();
					if (count.getInt("total") == 0){
						return null;
					} 

					// Generate Table
					// Titles for each of the columns
					String [] columns = {
						"SKU", "Name", "Stored By", "Fill Level", "Current Quantity", "Quantity to Order", "Resupply Price"
					};

					// List for data in table
					List<List<Object>> data = new ArrayList<List<Object>>();

					// Get data from Table
					ResultSet result = stmt.executeQuery("SELECT * FROM restockreport");
					double totalPrice = 0;

					while (result.next()){
						List<Object> row = new ArrayList<Object>();

						double price = result.getFloat("resupply_price");
						totalPrice += price;

						row.add(result.getString("sku"));
						row.add(result.getString("name"));
						row.add(result.getString("stored_by"));
						row.add(result.getInt("fill_level"));
						row.add(result.getFloat("current_quantity"));
						row.add(result.getInt("quantity_to_order"));
						row.add(price);

						data.add(row);
					}
					

					// Convert List[][] to Object[][]
					Object[][] dataArray = data.stream().map(l -> l.stream().toArray(Object[]::new)).toArray(Object[][]::new);

					JTable reportTable = new JTable(dataArray, columns){
						@Override
              			public boolean isCellEditable(int row, int column) {
                 		//all cells false to editable
                 		return false;
              			}
					};

					return reportTable;

				} catch (Exception e){
            		JOptionPane.showMessageDialog(null, e.toString());
            		return null;
          		}
			}

			public float getPercentage(ResultSet result){
				try{
					float percentage = (result.getFloat("quantity") / result.getInt("delivered")) * 100;

					if (percentage > 100){
						percentage = 100;
					}

					return percentage;
				} catch (Exception e){
					JOptionPane.showMessageDialog(null, e.toString());
					return 0;
				}
			}


			public static boolean isNumeric(String strNum) {
			    if (strNum == "") {
			        return false;
			    }
			    try {
			        int d = Integer.parseInt(strNum);
			    } catch (NumberFormatException nfe) {
			        return false;
			    }
			    return true;
			}

}