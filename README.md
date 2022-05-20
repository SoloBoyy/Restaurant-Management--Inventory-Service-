# Laynes - Inventory and Customer Service Java PR
Repository for CSCE-315 Team 34 Project 2

Implemented using Java, Java Swing, SQL, and PostgreSQL. This codebase is a desktop application for Layne's that will track orders, customers, items, supplies, and customer payments. Orders will be tied to a customer, consist of items, track when the order was placed, and have a payment type. Use of items will change the supply and changes in supply will be used to know when to order more of a particular product. Customers will also be tracked through associating them with an order, payment, and means of communication to let them know their order’s progress. 

Each of the previously mentioned entities will have a unique ID and several relationships that will influence actions that need to be taken by the business. The orders will consist of particular items and when an order is placed those items will decrease the supply which will then allow the business to know what to order. We will also track when orders are placed and when the last time supplies were ordered. Customers will also be tracked along with their most recent orders and payment methods to facilitate payment and a quick and easy customer experience. 

### Dependencies

* Java JDK
* SQL and PostgreSQL
* Access to Tamu network, either directly or through a VPN in order to access the database

## Compiling 
1. javac -cp ".;opencsv-3.8.jar" *.java
2. java -cp ".;postgresql-42.2.8.jar;opencsv-3.8.jar" jdbcpostgreSQLGUI


## Authors

Rishabh Kumar - richiekumar@tamu.edu
  
James Barret - jamesb59@tamu.edu
  
Logan Bowen - bowen.logan@tamu.edu
  
Juan Chavez - juanchavez@tamu.edu
  
  
