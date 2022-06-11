/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Cafe {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
         new InputStreamReader(System.in));

   /**
    * Creates a new instance of Cafe
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Cafe(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try {
         System.out.print("Check!!");
         // System.out.println("ASHDFKJAHSDLHJ;ADHGJAHSDJGADSJKGHAKJDHGK");
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      } catch (Exception e) {
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      } // end catch
   }// end Cafe

   /**
    * Method to execute an update SQL statement. Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate(String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the update instruction
      stmt.executeUpdate(sql);

      // close the instruction
      stmt.close();
   }// end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = false; // set to false to not get col name
      while (rs.next()) {
         if (outputHeader) {
            for (int i = 1; i <= numCol; i++) {
               System.out.print(rsmd.getColumnName(i) + "\t");
            }
            System.out.println();
            outputHeader = false;
         }
         for (int i = 1; i <= numCol; ++i)
            System.out.print(rs.getString(i) + "\n"); // newline instead of tab
         // System.out.println ();
         ++rowCount;
      } // end while
      stmt.close();
      return rowCount;
   }// end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      /*
       ** obtains the metadata object for the returned result set. The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result = new ArrayList<List<String>>();
      while (rs.next()) {
         List<String> record = new ArrayList<String>();
         for (int i = 1; i <= numCol; ++i)
            record.add(rs.getString(i));
         result.add(record);
         ++rowCount; // *****ADDED FOR TEST FIX IF NEEDED */
      } // end while
      stmt.close();
      return result;
   }// end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT). This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery(String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery(query);

      int rowCount = 0;

      // iterates through the result set and count nuber of results.
      while (rs.next()) {
         rowCount++;
      } // end while
      stmt.close();
      return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
      Statement stmt = this._connection.createStatement();

      ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
      if (rs.next())
         return rs.getInt(1);
      return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup() {
      try {
         if (this._connection != null) {
            this._connection.close();
         } // end if
      } catch (SQLException e) {
         // ignored.
      } // end try
   }// end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login
    *             file>
    */
   public static void main(String[] args) {
      if (args.length != 3) {
         System.err.println(
               "Usage: " +
                     "java [-classpath <classpath>] " +
                     Cafe.class.getName() +
                     " <dbname> <port> <user>");
         return;
      } // end if

      Greeting();
      Cafe esql = null;
      try {
         // use postgres JDBC driver.
         Class.forName("org.postgresql.Driver").newInstance();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Cafe(dbname, dbport, user, "");

         boolean keepon = true;
         while (keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");

            String authorizedUser = null;
            String userType = null;

            switch (readChoice()) {
               case 1:
                  CreateUser(esql);
                  break;
               case 2:
                  authorizedUser = LogIn(esql);
                  userType = CheckUserType(esql, authorizedUser);
                  break;
               case 9:
                  keepon = false;
                  break;
               default:
                  System.out.println("Unrecognized choice!");
                  break;
            }// end switch

            if (authorizedUser != null) {
               boolean isCustomer = false;
               boolean isEmployee = false;
               boolean isManager = false;
               if (userType.equals("Customer") || userType.equals("Customer ") || userType.equals(" Customer")
                     || userType.equals(" Customer ")) {
                  isCustomer = true;
               }
               if (userType.equals("Employee") || userType.equals(" Employee") || userType.equals("Employee ")
                     || userType.equals(" Employee ")) {
                  isEmployee = true;
               }
               if (userType.equals("Manager ") || userType.equals(" Manager ") || userType.equals("Manager")
                     || userType.equals(" Manager")) {
                  isManager = true;
               }

               while (isCustomer) {
                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. View Menu");
                  System.out.println("2. Update Your Profile");
                  System.out.println("3. Place an Order");
                  System.out.println("4. Update an Order");
                  System.out.println("5. View Your Recent Orders");
                  System.out.println(".........................");
                  System.out.println("9. Log out");
                  switch (readChoice()) {
                     case 1:
                        Menu(esql);
                        break;
                     case 2:
                        authorizedUser = UpdateProfile(esql, authorizedUser, isManager);
                        break;
                     case 3:
                        PlaceOrder(esql);
                        break;
                     case 4:
                        UpdateOrder(esql);
                        break;
                     case 9:
                        isCustomer = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }// end customer menu switch
               } // end customer menu while

               while (isEmployee) {
                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. View Menu");
                  System.out.println("2. Update Your Profile");
                  System.out.println("3. Place an Order");
                  System.out.println("4. Update an Order");
                  System.out.println("5. View Your Recent Orders");
                  System.out.println("6. Update Customer Order Payment");
                  System.out.println(".........................");
                  System.out.println("9. Log out");
                  switch (readChoice()) {
                     case 1:
                        Menu(esql);
                        break;
                     case 2:
                        authorizedUser = UpdateProfile(esql, authorizedUser, isManager);
                        break;
                     case 3:
                        PlaceOrder(esql);
                        break;
                     case 4:
                        UpdateOrder(esql);
                        break;
                     case 5:
                        break;
                     case 6:
                        break;
                     case 9:
                        isEmployee = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }// end employee menu switch
               } // end employee menu while

               while (isManager) {
                  System.out.println("MAIN MENU");
                  System.out.println("---------");
                  System.out.println("1. View Menu");
                  System.out.println("2. Update Menu");
                  System.out.println("3. Update Your Profile");
                  System.out.println("4. Update Other User Profile");
                  System.out.println("5. Place an Order");
                  System.out.println("6. Update an Order");
                  System.out.println("7. View Your Recent Orders");
                  System.out.println("8. Update Customer Order Payment");
                  System.out.println(".........................");
                  System.out.println("9. Log out");
                  switch (readChoice()) {
                     case 1:
                        Menu(esql);
                        break;
                     case 2:
                        UpdateMenu(esql);
                        break;
                     case 3:
                        authorizedUser = UpdateProfile(esql, authorizedUser, isManager);
                        break;
                     case 4:
                        UpdateOtherUserProfile(esql, isManager);
                        break;
                     case 5:
                        PlaceOrder(esql);
                        break;
                     case 6:
                        UpdateOrder(esql);
                        break;
                     case 7:
                        break;
                     case 8:
                        break;
                     case 9:
                        isManager = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }// end manager menu switch
               } // end manager menu while
            } // ends if statement
         } // end while
      } catch (Exception e) {
         System.err.println(e.getMessage());
      } finally {
         // make sure to cleanup the created table and close the connection.
         try {
            if (esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup();
               System.out.println("Done\n\nBye !");
            } // end if
         } catch (Exception e) {
            // ignored.
         } // end try
      } // end try
   }// end main

   public static void Greeting() {
      System.out.println(
            "\n\n*******************************************************\n" +
                  "              Welcome to Cafe Filoksenia!  	               \n" +
                  "*******************************************************\n");
   }// end Greeting

   /*
    * Reads the users choice given from the keyboard
    * 
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please select from the above options: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         } // end try
      } while (true);
      return input;
   }// end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/
   public static void CreateUser(Cafe esql) {
      try {
         String phone = "";
         String login = "";
         boolean enteringUser = true;
         while (enteringUser) {
            System.out.print("\tEnter user login(CASE SENSITIVE!): ");
            login = in.readLine();
            if (!LoginExists(esql, login)) {
               enteringUser = false;
            }
         }
         System.out.print("\tEnter user password(CASE SENSITIVE!): ");
         String password = in.readLine();
         boolean enteringPhone = true;
         while (enteringPhone) {
            System.out.print("\tEnter user phone: ");
            phone = in.readLine();
            boolean checkPhone = CheckPhoneNumber(phone);
            if (checkPhone) {
               // makes it fit with database structure
               phone = FixPhoneNumber(phone);
            }
            if (!PhoneNumberExists(esql, phone)) {
               enteringPhone = false;
            }
         }

         String type = "Customer";
         String favItems = "";

         String query = String.format(
               "INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone,
               login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println("User successfully created!");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end CreateUser

   /*
    * Check log in credentials for an existing user
    * 
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql) {
      try {
         System.out.print("\tEnter user login (CASE SENSITIVE!): ");
         String login = in.readLine();
         System.out.print("\tEnter user password(CASE SENSITIVE!): ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
         if (userNum > 0)
            return login;
         else
            System.out.print("\tNo such user exists!");
         return null;
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return null;
      }
   }// end LogIn

   // Rest of the functions definition go in here

   /* How manager can update menu */
   public static void UpdateMenu(Cafe esql) {
      try {
         boolean updatingMenu = true;
         while (updatingMenu) {
            System.out.println("How would you like to update the menu?");
            System.out.println("1. Add a new menu item");
            System.out.println("2. Edit a specific item");
            System.out.println("3. Delete a menu item");
            System.out.println("4. Done updating menu");
            switch (readChoice()) {
               case 1:
                  AddItemToMenu(esql);
                  break;

               case 2:
                  EditItemOnMenu(esql);
                  break;

               case 3:
                  DeleteMenuItem(esql);
                  break;

               case 4:
                  updatingMenu = false;
                  break;

               default:
                  System.out.println("Unrecognized choice!");
            }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   /* Deletes a menu item */
   public static void DeleteMenuItem(Cafe esql) {
      boolean confirmingItemName = true;
      try {
         while (confirmingItemName) {
            System.out.println("Enter item name to delete: ");
            String itemName = in.readLine();
            if (!ItemOnMenu(esql, itemName)) {
               System.out.println("1. Enter another item name");
               System.out.println("2. Go back to updating menu options");
               switch (readChoice()) {
                  case 1:
                     break;
                  case 2:
                     confirmingItemName = false;
                     break;
                  default:
                     System.out.println("Unrecognized choice!");
                     break;
               }
            } else {
               String query = String.format("DELETE FROM menu WHERE itemName = '%s'", itemName);
               esql.executeUpdate(query);
               System.out.println(itemName + " successfully deleted off menu!");
               confirmingItemName = false;
            }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   /* Edits a item based on item name user enters */
   public static void EditItemOnMenu(Cafe esql) {
      try {
         String itemName = "";
         String updatedName = "";
         String type = "";
         String price = "";
         String description = "";
         String imageURL = "";
         String query = "";

         boolean confirmingItemName = true;
         boolean editingItem = false;

         while (confirmingItemName) {
            System.out.println("Enter item name to edit: ");
            itemName = in.readLine();
            if (!ItemOnMenu(esql, itemName)) {
               System.out.println("1. Enter another item name");
               System.out.println("2. Go back to menu updating options");
               switch (readChoice()) {
                  case 1:
                     break;
                  case 2:
                     confirmingItemName = false;
                     editingItem = false;
                     break;
                  default:
                     System.out.println("Unrecognized choice!");
                     break;
               }
            } else {
               editingItem = true;
               String findType = String.format("SELECT type FROM Menu WHERE itemName = '%s'", itemName);
               List<List<String>> getType = esql.executeQueryAndReturnResult(findType);
               type = getType.get(0).get(0);

               String findPrice = String.format("SELECT price FROM Menu WHERE itemName = '%s'", itemName);
               List<List<String>> getPrice = esql.executeQueryAndReturnResult(findPrice);
               price = getPrice.get(0).get(0);

               String findDescription = String.format("SELECT description FROM Menu WHERE itemName = '%s'", itemName);
               List<List<String>> getDescription = esql.executeQueryAndReturnResult(findDescription);
               price = getDescription.get(0).get(0);

               String findImageURL = String.format("SELECT imageURL FROM Menu WHERE itemName = '%s'", itemName);
               List<List<String>> getImageURL = esql.executeQueryAndReturnResult(findImageURL);
               price = getImageURL.get(0).get(0);
               confirmingItemName = false;
            }

            while (editingItem) {
               System.out.println("Editing options: ");
               System.out.println("1. Edit item name");
               System.out.println("2. Edit item type");
               System.out.println("3. Edit item price");
               System.out.println("4. Edit item description");
               System.out.println("5. Edit item imageURL");
               System.out.println("6. Done editing");
               switch (readChoice()) {
                  case 1: // edit name
                     System.out.println("Enter new item name: ");
                     updatedName = in.readLine();
                     if (ItemOnMenu(esql, updatedName)) {
                        System.out.print(itemName + "'s name not updated.");
                        break;
                     }
                     query = String.format("UPDATE menu SET itemName = '%s' WHERE itemName = '%s'", updatedName,
                           itemName);
                     esql.executeUpdate(query);
                     System.out.println("Successfully updated old item name " + itemName + " to " + updatedName);
                     editingItem = false;
                     break;

                  case 2: // edit type
                     boolean editingType = true;
                     while (editingType) {
                        System.out.println("Select what to update item type to: ");
                        System.out.println("1. Drinks");
                        System.out.println("2. Soup");
                        System.out.println("3. Sweets");
                        switch (readChoice()) {
                           case 1:
                              type = "Drinks";
                              break;
                           case 2:
                              type = "Soup";
                              break;
                           case 3:
                              type = "Sweets";
                              break;
                           default:
                              System.out.println("Unrecognized choice!");
                              break;
                        }
                        query = String.format("UPDATE menu SET type = '%s' WHERE itemName = '%s'", type, itemName);
                        esql.executeUpdate(query);
                        System.out.println("Successfully updated type!");
                        editingType = false;
                     }
                     editingItem = false;
                     break;

                  case 3: // edit price
                     System.out.println("Enter updated cents portion of item price: ");
                     price = in.readLine();
                     price = "." + price;
                     System.out.println("Enter updated dollar portion of item price: ");
                     price = in.readLine() + price;
                     query = String.format("UPDATE menu SET price = '%s' WHERE itemName = '%s'", price, itemName);
                     esql.executeUpdate(query);
                     System.out.println("Successfully updated price!");
                     editingItem = false;
                     break;

                  case 4: // edit description
                     System.out.println("Enter updated item description: ");
                     description = in.readLine();
                     query = String.format("UPDATE menu SET description = '%s' WHERE itemName = '%s'", description,
                           itemName);
                     esql.executeUpdate(query);
                     System.out.println("Successfully updated description!");
                     editingItem = false;
                     break;

                  case 5: // edit imageURL
                     System.out.println("Enter updated image URL: ");
                     imageURL = in.readLine();
                     query = String.format("UPDATE menu SET imageURL = '%s' WHERE itemName = '%s'", imageURL, itemName);
                     esql.executeUpdate(query);
                     System.out.println("Successfully updated image URL!");
                     editingItem = false;
                     break;

                  case 6: // done editing
                     editingItem = false;
                     break;
               }
            }

         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   /* Add new menu item */
   public static void AddItemToMenu(Cafe esql) {
      try {
         String itemName = "";
         String type = "";
         String price = "";
         String description = "";
         String imageURL = "";
         boolean addingItem = true;
         boolean confirmingItemName = true;
         boolean confirmingItemType = true;
         boolean confirmingItemInfo = true;

         while (addingItem) {
            while (confirmingItemName) {
               System.out.println("Enter item name: ");
               itemName = in.readLine();
               if (ItemOnMenu(esql, itemName)) {
                  System.out.println("1. Enter another item name");
                  System.out.println("2. Go back to menu updating options");
                  switch (readChoice()) {
                     case 1:
                        break;
                     case 2:
                        addingItem = false;
                        confirmingItemName = false;
                        confirmingItemType = false;
                        confirmingItemInfo = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }
               } else {
                  confirmingItemName = false;
               }
            }

            while (confirmingItemType) {
               System.out.println("Select item type: ");
               System.out.println("1. Drinks");
               System.out.println("2. Soup");
               System.out.println("3. Sweets");
               System.out.println("4. Back to viewing menu options");
               switch (readChoice()) {
                  case 1:
                     type = "Drinks";
                     confirmingItemType = false;
                     break;
                  case 2:
                     type = "Soup";
                     confirmingItemType = false;
                     break;
                  case 3:
                     type = "Sweets";
                     confirmingItemType = false;
                     break;
                  case 4:
                     confirmingItemType = false;
                  default:
                     System.out.println("Unrecognized choice!");
                     break;
               }
            }

            while (confirmingItemInfo) {
               System.out.println("Enter cents portion of item price: ");
               price = in.readLine();
               price = "." + price;

               System.out.println("Enter dollar portion of item price: ");
               price = in.readLine() + price;

               System.out.println("Enter item description: ");
               description = in.readLine();

               System.out.println("Enter item image URL:");
               imageURL = in.readLine();

               String query = String.format(
                     "INSERT INTO Menu (itemName, type, price, description, imageURL) VALUES ('%s','%s','%s','%s','%s')",
                     itemName, type, price, description, imageURL);
               esql.executeUpdate(query);

               System.out.println("New item successfully added to menu!");
               System.out.println();

               confirmingItemInfo = false;
            }
            addingItem = false;
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   /* How to pick style of viewing menu */
   public static void Menu(Cafe esql) {
      try {
         boolean viewingMenu = true;
         while (viewingMenu) {
            System.out.println("How would you like to view the menu?");
            System.out.println("1. By item name");
            System.out.println("2. By item type");
            System.out.println("3. View the full menu");
            System.out.println("4. Done viewing menu");
            switch (readChoice()) {
               case 1: // item name
                  MenuByItemName(esql);
                  break;

               case 2: // item type
                  MenuByItemType(esql);
                  break;

               case 3: // full menu
                  ViewFullMenu(esql);
                  break;

               case 4:
                  viewingMenu = false;
                  break;

               default:
                  System.out.println("Unrecognized choice!");
                  break;
            }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end Menu

   /* Displays full menu */
   public static void ViewFullMenu(Cafe esql) {
      try {
         String getInfo = String.format("SELECT * FROM Menu");
         esql.executeQueryAndPrintResult(getInfo);
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end ViewFullMenu

   /* Displays menu by item type selected */
   public static void MenuByItemType(Cafe esql) {
      try {
         String type = null;
         boolean confirmingItemType = true;
         boolean confirmedType = false;

         System.out.println("here");

         while (confirmingItemType) {
            System.out.println("Item Type Choices: ");
            System.out.println("1. Drinks");
            System.out.println("2. Soup");
            System.out.println("3. Sweets");
            System.out.println("4. Back to viewing menu options");
            switch (readChoice()) {
               case 1:
                  type = "Drinks";
                  confirmedType = true;
                  break;
               case 2:
                  type = "Soup";
                  confirmedType = true;
                  break;
               case 3:
                  type = "Sweets";
                  confirmedType = true;
                  break;
               case 4:
                  confirmingItemType = false;
               default:
                  System.out.println("Unrecognized choice!");
                  break;
            }

            if (confirmedType) {
               String getInfo = String
                     .format("SELECT itemName, price, description, imageURL FROM Menu WHERE type = '%s'", type);
               esql.executeQueryAndPrintResult(getInfo);

               confirmingItemType = false;

            }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end MenuByItemType

   /*
    * Returns true is item type is on menu, returns false if item type is not on
    * menu *NOT CURRENTLY IN USE*
    */
   public static boolean ItemTypeOnMenu(Cafe esql, String type) {
      try {
         String query = String.format("SELECT type FROM Menu WHERE itemName = '%s'", type);
         int userNum = esql.executeQuery(query);
         if (userNum > 0) {
            System.out.println(type + " on the menu!");
            return true;
         } else {
            System.out.println(type + " not on the menu!");
            return false;
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return false;
      }
   }// end ItemTypeOnMenu

   /* How to show items when getting input of item name */
   public static void MenuByItemName(Cafe esql) {
      try {
         boolean confirmingItemName = true;
         while (confirmingItemName) {
            System.out.println("Enter the item name you are looking for: ");
            String name = in.readLine();
            if (!ItemOnMenu(esql, name)) {
               System.out.println("1. Enter another item name");
               System.out.println("2. Go back to menu viewing options");
               switch (readChoice()) {
                  case 1:
                     break;
                  case 2:
                     confirmingItemName = false;
                     break;
                  default:
                     System.out.println("Unrecognized choice!");
                     break;
               }
            } else {
               String getInfo = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", name);
               System.out.print("Name: ");
               esql.executeQueryAndPrintResult(getInfo);

               getInfo = String.format("SELECT type FROM Menu WHERE itemName = '%s'", name);
               System.out.print("Type: ");
               esql.executeQueryAndPrintResult(getInfo);

               getInfo = String.format("SELECT price FROM Menu WHERE itemName = '%s'", name);
               System.out.print("Price: ");
               esql.executeQueryAndPrintResult(getInfo);

               getInfo = String.format("SELECT description FROM Menu WHERE itemName = '%s'", name);
               System.out.print("Description: ");
               esql.executeQueryAndPrintResult(getInfo);

               getInfo = String.format("SELECT imageURL FROM Menu WHERE itemName = '%s'", name);
               System.out.print("URL: ");
               esql.executeQueryAndPrintResult(getInfo);
               confirmingItemName = false;
            }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end MenuByItemName

   /* Returns true if item name entered is in database, false otherwise */
   public static boolean ItemOnMenu(Cafe esql, String name) {
      try {
         String query = String.format("SELECT itemName FROM Menu WHERE itemName = '%s'", name);
         int userNum = esql.executeQuery(query);
         if (userNum > 0) {
            System.out.println(name + " is on the menu!");
            return true;
         } else {
            System.out.println(name + " is not on the menu!");
            return false;
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return false;
      }
   }

   /* returns a string that states the users type after login */
   public static String CheckUserType(Cafe esql, String authorizedUser) {
      try {
         if (authorizedUser.equals(null)) {
            return null;
         } else {
            String findType = String.format("SELECT type FROM USERS WHERE login = '%s'", authorizedUser);
            List<List<String>> getAccountType = esql.executeQueryAndReturnResult(findType);
            String accountType = getAccountType.get(0).get(0);
            return accountType;
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return null;
      }
   }// end CheckUserType

   /* returns boolean true if login is in db, false if login is not in db */
   public static boolean LoginExists(Cafe esql, String userLogin) {
      try {
         String query = String.format("SELECT * FROM USERS WHERE login = '%s'", userLogin);
         int userNum = esql.executeQuery(query);
         if (userNum > 0) {
            System.out.println("User login exists");
            return true;
         } else {
            System.out.println("User login does not exist");
            return false;
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return false;
      }
   }// end LoginExists

   /* Formats phone number */
   public static String FixPhoneNumber(String userPhoneNumber) {
      String countryCode = "+1(";
      String closeAreaCode = ")";
      String middleDash = "-";
      int endOfAreaCode = 2; // index where area code ends
      int endOf3Dig = 5; // index where first three digits end
      // 888 888 8888
      String fixedNumber = countryCode + userPhoneNumber.substring(0, endOfAreaCode + 1)
            + closeAreaCode + userPhoneNumber.substring(endOfAreaCode + 1, endOf3Dig + 1)
            + middleDash + userPhoneNumber.substring(endOf3Dig + 1);

      return fixedNumber;
   }// end FixPhoneNumber

   /* Checks that phone number was entered correctly */
   public static boolean CheckPhoneNumber(String userPhoneNumber) {
      if (userPhoneNumber.length() != 10) { // checks number of chars
         System.out.println("Phone number must be 10 digits!");
         return false;
      }
      if (!userPhoneNumber.matches("[0-9]+")) {// checks only digits were inputted
         System.out.println("Phone number must only contain digits!");
         return false;
      } else {
         return true;
      }
   }// end CheckPhoneNumber

   public static boolean PhoneNumberExists(Cafe esql, String userPhoneNumber) {
      try {
         String query = String.format("SELECT * FROM USERS WHERE phoneNum = '%s'", userPhoneNumber);
         int userNum = esql.executeQuery(query);
         if (userNum > 0) {
            System.out.println("User phone number exists");
            return true;
         } else {
            System.out.println("User phone number does not exist");
            return false;
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return false;
      }
   }// end PhoneNumberExists

   /* General function to get to options to update a profile */
   public static String UpdateProfile(Cafe esql, String authorizedUser, Boolean checkManager) {
      try {
         boolean updatingProfile = true;
         String userToUpdate = authorizedUser;

         /*
          * Outermost while loop gets the user to confirms login details and sees if the
          * user is a manager
          */
         while (updatingProfile) {

            System.out.println("UPDATE PROFILE MENU");
            System.out.println("-------------------");
            System.out.println("1. Update user login");
            System.out.println("2. Update password");
            System.out.println("3. Update phone number");
            System.out.println("4. Update favorite items");

            if (checkManager) {
               System.out.println("5. Update user type");
            }
            System.out.println(".........................");
            System.out.println("9. Back to Main Menu");

            switch (readChoice()) {

               case 1: // updating user login
                  userToUpdate = UpdateUserLogin(esql, userToUpdate);
                  break;

               case 2: // updating password
                  UpdateUserPassword(esql, userToUpdate);
                  break;

               case 3: // updating phone number
                  UpdateUserPhoneNumber(esql, userToUpdate);
                  break;

               case 4: // updating favorite items
                  UpdateFavoriteItems(esql, userToUpdate);
                  break;

               case 5: // updating user type as manager only
                  if (!checkManager) {
                     System.out.println("Unrecognized choice!");
                     break;
                  }

                  else {
                     UpdateUserType(esql, userToUpdate);
                  }
                  break;

               case 9:
                  updatingProfile = false;
                  break;

               default:
                  System.out.println("Unrecognized choice!");
                  break;
            }
         }
         return userToUpdate;

      } catch (Exception e) {
         System.err.println(e.getMessage());
         return null;
      }
   }// end of UpdateProfile

   /* How to update favorite items */
   public static void UpdateFavoriteItems(Cafe esql, String userToUpdate) {
      try {
         System.out.printf("Enter new favorite items for '%s': ", userToUpdate);
         String newUserFavItems = in.readLine();

         String query = String.format("UPDATE users SET favItems = '%s' WHERE login = '%s'", newUserFavItems,
               userToUpdate);
         esql.executeUpdate(query);

         System.out.println("User favorite items successfully updated!");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   /* How managers can update other user's profiles */
   public static void UpdateOtherUserProfile(Cafe esql, boolean checkManager) {
      try {
         boolean userSelection = true;
         while (userSelection) {
            System.out.println("Enter login of user you would like to update: ");
            String userSelected = in.readLine();

            if (!LoginExists(esql, userSelected)) {
               System.out.println("1. Try new user login");
               System.out.println("2. Back to main menu");

               switch (readChoice()) {
                  case 1:
                     break;

                  case 2:
                     userSelection = false;
                     break;

                  default:
                     System.out.println("Unrecognized choice!");
                     break;
               }
            } else {
               userSelected = UpdateProfile(esql, userSelected, checkManager);
               userSelection = false;
            }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end of UpdateOtherUserProfile

   /* How to update user login in table */
   public static String UpdateUserLogin(Cafe esql, String userToUpdate) {
      try {
         boolean loginUpdating = true;
         while (loginUpdating) {
            System.out.println("Enter updated user login:");
            String updatedLogin = in.readLine();
            // check if someone else has the user first since unique
            if (LoginExists(esql, updatedLogin)) {
               System.out.println("1. Enter a different user login");
               System.out.println("2. Go back to update menu");
               switch (readChoice()) {
                  case 1:
                     break;
                  case 2:
                     loginUpdating = false;
                     break;
                  default:
                     System.out.println("Unrecognized choice!");
                     break;
               }
            }

            else {
               System.out.printf("Updating user login '%s' to '%s'\n", userToUpdate, updatedLogin);
               String query = String.format("UPDATE users SET login = '%s' WHERE login = '%s'", updatedLogin,
                     userToUpdate);
               esql.executeUpdate(query);

               userToUpdate = updatedLogin;

               System.out.println("User login successfully updated!");

               loginUpdating = false;
            }
         }
         return userToUpdate;
      } catch (Exception e) {
         System.err.println(e.getMessage());
         return userToUpdate;
      }
   }// end of UpdateUserLogin

   /* How to update password for user */
   public static void UpdateUserPassword(Cafe esql, String userToUpdate) {
      try {
         System.out.printf("Enter new user password for '%s': ", userToUpdate);
         String newUserPassword = in.readLine();

         String query = String.format("UPDATE users SET password = '%s' WHERE login = '%s'", newUserPassword,
               userToUpdate);
         esql.executeUpdate(query);

         System.out.println("User password successfully updated!");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end of UpdateUserPassword

   /* How to update phone number of user */
   public static void UpdateUserPhoneNumber(Cafe esql, String userToUpdate) {
      try {
         boolean updatingPhoneNum = true;
         boolean formattingCorrect = false;
         while (updatingPhoneNum) {
            System.out.println("Enter updated user phone number: ");
            String updatedPhoneNumber = in.readLine();

            // checks if proper input
            boolean phoneCheck = CheckPhoneNumber(updatedPhoneNumber);
            if (!phoneCheck) {
               System.out.println("1. Enter a different user phone number");
               System.out.println("2. Go back to update menu");
               switch (readChoice()) {
                  case 1:
                     break;
                  case 2:
                     updatingPhoneNum = false;
                     break;
                  default:
                     System.out.println("Unrecognized choice!");
                     break;
               }
            } else {
               updatedPhoneNumber = FixPhoneNumber(updatedPhoneNumber);
               formattingCorrect = true;
            }

            while (formattingCorrect) {
               boolean existingPhone = PhoneNumberExists(esql, updatedPhoneNumber);
               if (existingPhone) {
                  System.out.println("1. Enter a different user phone number");
                  System.out.println("2. Go back to update menu");
                  switch (readChoice()) {
                     case 1:
                        formattingCorrect = false;
                        break;
                     case 2:
                        formattingCorrect = false;
                        updatingPhoneNum = false;
                        break;
                     default:
                        System.out.println("Unrecognized choice!");
                        break;
                  }
               } else {
                  System.out.printf("Updating user phone number to '%s'\n", updatedPhoneNumber);
                  String query = String.format("UPDATE users SET phoneNum = '%s' WHERE login = '%s'",
                        updatedPhoneNumber, userToUpdate);
                  esql.executeUpdate(query);

                  System.out.println("User phone number successfully updated!");

                  updatingPhoneNum = false;
                  formattingCorrect = false;

               }
            }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end UpdateUserPhoneNumber

   /* How manager can update user type */
   public static void UpdateUserType(Cafe esql, String userToUpdate) {
      try {
         String newUserType = "Customer";

         System.out.printf("Select new user type for '%s': \n", userToUpdate);
         System.out.println("1. Customer");
         System.out.println("2. Employee");
         System.out.println("3. Manager");
         switch (readChoice()) {
            case 1:
               newUserType = "Customer";
               break;

            case 2:
               newUserType = "Employee";
               break;

            case 3:
               newUserType = "Manager";
               break;

            default:
               System.out.println("Unrecognized choice!");
               break;

         }

         String query = String.format("UPDATE users SET type = '%s' WHERE login = '%s'", newUserType, userToUpdate);
         esql.executeUpdate(query);

         System.out.println("User type successfully updated to " + newUserType + "!");
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }// end UpdateUserType

   public static void PlaceOrder(Cafe esql) {

      try {
         // get user input
         System.out.println("Enter name for order: ");
         String orderName = in.readLine();
         // print menu
         FullMenu(esql);
         // choose from menu

         // validate data
         // perform SQL call
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }

   }

   public static void UpdateOrder(Cafe esql) {
      try {
         System.out.println("What's the order ID?");
         String OrderID = in.readLine();
         System.out.println("Are you a manager or employee?");
         System.out.println("1. Manager/Employee");
         System.out.println("2. Other");
         int userChoice = in.read();

         switch (userChoice) {
            case 1:
               System.out.println("Has payment been made? (True or False)");
               String paymentChoice = in.readLine();
               String query = String.format("UPDATE ORDERS SET Paid = %s WHERE OrderID = '%s'", paymentChoice, OrderID);
               esql.executeUpdate(query);
               break;
            case 2:
               System.out.println("You are not a manager or employee. You cannot update orders.");
               break;
         }

      } catch (Exception e) {
         System.err.println(e.getMessage());
      }

   }

}// end Cafe
