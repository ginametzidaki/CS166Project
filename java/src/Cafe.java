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
      try{
         System.out.print("Check!!");
         //System.out.println("ASHDFKJAHSDLHJ;ADHGJAHSDJGADSJKGHAKJDHGK");
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Cafe

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
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
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Cafe.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Cafe esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Cafe object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Cafe (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Go to Menu");
                System.out.println("2. Update Profile");
                System.out.println("3. Place a Order");
                System.out.println("4. Update a Order");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: Menu(esql); break;
                   case 2: UpdateProfile(esql); break;
                   case 3: PlaceOrder(esql); break;
                   case 4: UpdateOrder(esql); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    **/
   public static void CreateUser(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user phone: ");
         String phone = in.readLine();
         
	    String type="Customer";
	    String favItems="";

				 String query = String.format("INSERT INTO USERS (phoneNum, login, password, favItems, type) VALUES ('%s','%s','%s','%s','%s')", phone, login, password, favItems, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Cafe esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
    else
      System.out.print("\tNo such user exists!");
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

  public static void Menu(Cafe esql){}

  public static void UpdateProfile(Cafe esql){
      try {
         boolean updateprofile = true;
         //boolean nouser = true;         
         //boolean notuniquelogin = true;
         boolean confirmlogin = false;
         boolean isManager = false;
         String login = null;
         String password = null;
         String userToUpdate = null;

         while (updateprofile) {  
            System.out.print("\tConfirm user login: ");
            login = in.readLine();
            System.out.print("\tConfirm user password: ");
            password = in.readLine();

            String query = String.format("SELECT * FROM USERS WHERE login = '%s' AND password = '%s'", login, password);
            int userNum = esql.executeQuery(query);
            if (userNum > 0) {
               confirmlogin = true;
               String findType = String.format("SELECT type FROM USERS WHERE login = '%s'", login);
               List<List<String>> getAccountType = esql.executeQueryAndReturnResult(findType);
               String AccountType = getAccountType.get(0).get(0);
               String managerSpaceString = "Manager ";
               String managerString = "Manager";
               if(AccountType.equals(managerString) || AccountType.equals(managerSpaceString)) {
                  isManager = true;
               }
            }
            else {
               System.out.println("Cannot verify login details.");
               System.out.println("Please select from the following options");
               System.out.println("1. Retry login confirmation");
               System.out.println("2. Back to main menu");
               switch (readChoice()) {
                  case 1: break;
                  case 2: updateprofile = false; break;
                  default: System.out.println("Unrecognized choice!"); break;
               }
            }

            while(confirmlogin) {
               if(isManager) {
                  boolean nouser = true; 
                  while(nouser) {
                     System.out.println("Enter login of user you would like to update: ");
                     userToUpdate = in.readLine();
                     query = String.format("SELECT * FROM USERS WHERE login = '%s'", userToUpdate);
                     userNum = esql.executeQuery(query);
                     if (userNum == 0) {
                        System.out.println("No user with that login.");
                        System.out.println("Please select from the following options:");
                        System.out.println("1. Try new user login");
                        System.out.println("2. Back to main menu");
                        switch (readChoice()) {
                           case 1: break;
                           case 2: nouser = false; confirmlogin = false; updateprofile = false; break;
                           default: System.out.println("Unrecognized choice!"); break;
                        }
                     }
                     else {
                        nouser = false;
                     }
                  }
               }
               else {
                  userToUpdate = login;
               }
               System.out.println("UPDATE PROFILE MENU");
               System.out.println("-------------------");
               System.out.println("1. Update user login");
               System.out.println("2. Update password");
               System.out.println("3. Update phone number");
               System.out.println("4. Update favorite items");
               System.out.println("5. Update user type (MANAGER ONLY)");
               System.out.println(".........................");
               System.out.println("9. Back to Main Menu");
               
               switch (readChoice()){
                  case 1: //updating user login
                     //cant change PK without foreign keys, so retrieve all other information for user and insert new row to table instead
                     boolean checklogin = true;
                     while(checklogin) {
                        System.out.println("Enter updated user login:");
                        String updatedLogin = in.readLine();
                        //check if someone else has the user first since unique
                        query = String.format("SELECT * FROM USERS WHERE login = '%s'", updatedLogin);
                        userNum = esql.executeQuery(query);
                        if(userNum > 0) {
                           System.out.println("User login already exists!");
                           System.out.println("Please select from the following options:");
                           System.out.println("1. Enter a different user login");
                           System.out.println("2. Go back to update menu");
                           switch(readChoice()) {
                              case 1: break;
                              case 2: checklogin = false; break;
                              default: System.out.println("Unrecognized choice!");
                           }
                        }
                        else {
                           String findPassword = String.format("SELECT password FROM USERS WHERE login = '%s'", userToUpdate);
                           List<List<String>> getPassword = esql.executeQueryAndReturnResult(findPassword);
                           String thePassword = getPassword.get(0).get(0);
                           String findPhoneNum = String.format("SELECT phoneNum FROM USERS WHERE login = '%s'", userToUpdate);
                           List<List<String>> getPhoneNum = esql.executeQueryAndReturnResult(findPhoneNum);
                           String phoneNum = getPhoneNum.get(0).get(0);
                           String findFavItems = String.format("SELECT favItems FROM USERS WHERE login = '%s'", userToUpdate);
                           List<List<String>> getFavItems = esql.executeQueryAndReturnResult(findFavItems);
                           String favItems = getFavItems.get(0).get(0);
                           String findTheType = String.format("SELECT type FROM USERS WHERE login = '%s'", userToUpdate);
                           List<List<String>> getTheType = esql.executeQueryAndReturnResult(findTheType);
                           String theType = getTheType.get(0).get(0);

                           query = String.format("DELETE FROM users WHERE login = '%s'", userToUpdate);
                           esql.executeUpdate(query);
                           userToUpdate = updatedLogin;
                           login = updatedLogin;

                           query = String.format("INSERT INTO users (phoneNum, login, password, favItems, type) VALUES ('%s', '%s', '%s', '%s', '%s')", phoneNum, updatedLogin, thePassword, favItems, theType);
                           esql.executeUpdate(query);

                           System.out.println("User login successfully updated!");
                           System.out.println("Please select from the following options: ");
                           System.out.println("1. Go back to update menu");
                           System.out.println("2. Go back to main menu");
                           switch(readChoice()) {
                              case 1: checklogin = false; break;
                              case 2: checklogin = false; confirmlogin = false; updateprofile = false;  break;
                              default: System.out.println("Unrecognized choice!");
                           }
                        }
                     }
                     break;
                  case 2: //updating password
                     System.out.printf("Enter new user password for '%s': ", userToUpdate);
                     String newUserPassword = in.readLine();

                     query = String.format("UPDATE users SET password = '%s' WHERE login = '%s'", newUserPassword, userToUpdate);
                     esql.executeUpdate(query);

                     System.out.println("User password successfully updated!");
                     System.out.println("Please select from the following options: ");
                     System.out.println("1. Go back to update menu");
                     System.out.println("2. Go back to main menu");
                     switch(readChoice()) {
                        case 1: break;
                        case 2: confirmlogin = false; updateprofile = false;  break;
                        default: System.out.println("Unrecognized choice!");
                     }
                     break;
                  case 3: //updating phone number
                  boolean checknum = true;
                  while(checknum) {
                     System.out.println("Enter updated user phone number:");
                     String updatedPhoneNumber = in.readLine();
                     //check if someone else has the user first since unique
                     query = String.format("SELECT * FROM USERS WHERE phoneNum = '%s'", updatedPhoneNumber);
                     userNum = esql.executeQuery(query);
                     if(userNum > 0) {
                        System.out.println("That phone number is already is use!");
                        System.out.println("Please select from the following options:");
                        System.out.println("1. Enter a different user phone number");
                        System.out.println("2. Go back to update menu");
                        switch(readChoice()) {
                           case 1: break;
                           case 2: checknum = false; break;
                           default: System.out.println("Unrecognized choice!");
                        }
                     }
                     else {
                        String findPassword = String.format("SELECT password FROM USERS WHERE login = '%s'", userToUpdate);
                        List<List<String>> getPassword = esql.executeQueryAndReturnResult(findPassword);
                        String thePassword = getPassword.get(0).get(0);
                        String findLogin = String.format("SELECT login FROM USERS WHERE login = '%s'", userToUpdate);
                        List<List<String>> getLogin = esql.executeQueryAndReturnResult(findLogin);
                        String userLogin = getLogin.get(0).get(0);
                        String findFavItems = String.format("SELECT favItems FROM USERS WHERE login = '%s'", userToUpdate);
                        List<List<String>> getFavItems = esql.executeQueryAndReturnResult(findFavItems);
                        String favItems = getFavItems.get(0).get(0);
                        String findTheType = String.format("SELECT type FROM USERS WHERE login = '%s'", userToUpdate);
                        List<List<String>> getTheType = esql.executeQueryAndReturnResult(findTheType);
                        String theType = getTheType.get(0).get(0);

                        query = String.format("DELETE FROM users WHERE login = '%s'", userToUpdate);
                        esql.executeUpdate(query);

                        query = String.format("INSERT INTO users (phoneNum, login, password, favItems, type) VALUES ('%s', '%s', '%s', '%s', '%s')", updatedPhoneNumber, userLogin, thePassword, favItems, theType);
                        esql.executeUpdate(query);

                        System.out.println("User phone number successfully updated!");
                        System.out.println("Please select from the following options: ");
                        System.out.println("1. Go back to update menu");
                        System.out.println("2. Go back to main menu");
                        switch(readChoice()) {
                           case 1: checknum = false; break;
                           case 2: checknum = false; confirmlogin = false; updateprofile = false; break;
                           default: System.out.println("Unrecognized choice!");
                        }
                     }
                  }
                     
                     break;
                  case 4: //updating favorite items
                     System.out.printf("Enter new favorite items for '%s': ", userToUpdate);
                     String newUserFavItems = in.readLine();

                     query = String.format("UPDATE users SET favItems = '%s' WHERE login = '%s'", newUserFavItems, userToUpdate);
                     esql.executeUpdate(query);

                     System.out.println("User favorite items successfully updated!");
                     System.out.println("Please select from the following options: ");
                     System.out.println("1. Go back to update menu");
                     System.out.println("2. Go back to main menu");
                     switch(readChoice()) {
                        case 1: break;
                        case 2: confirmlogin = false; updateprofile = false;  break;
                        default: System.out.println("Unrecognized choice!");
                     }
                     break;
                  case 5: //update user type
                     if(!isManager) {
                        System.out.println("********MANAGER ONLY********");
                     }
                     else {
                        System.out.printf("Enter new user type for '%s': ", userToUpdate);
                        String newUserType = in.readLine();

                        query = String.format("UPDATE users SET type = '%s' WHERE login = '%s'", newUserType, userToUpdate);
                        esql.executeUpdate(query);

                        System.out.println("User type successfully updated!");
                        System.out.println("Please select from the following options: ");
                        System.out.println("1. Go back to update menu");
                        System.out.println("2. Go back to main menu");
                        switch(readChoice()) {
                           case 1: if(userToUpdate.equals(login) && !newUserType.equals("Manager")) { isManager = false; }break;
                           case 2: confirmlogin = false; updateprofile = false;  break;
                           default: System.out.println("Unrecognized choice!");
                        }
                     }
                     break;
                  case 9: confirmlogin = false; updateprofile = false; break;
                  default : System.out.println("Unrecognized choice!"); break;
               }
            }
         }
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

  public static void PlaceOrder(Cafe esql){}

  public static void UpdateOrder(Cafe esql){}

}//end Cafe

