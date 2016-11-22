package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDBaccess {

	 Connection conn;

	   public static void main(String[] args) throws SQLException {
	     UserDBaccess app = new UserDBaccess();

	     app.connectionToDerby();
	     app.normalDbUsage();
	   }

	   public void connectionToDerby() throws SQLException {
	     // -------------------------------------------
	     // URL format is
	     // jdbc:derby:<local directory to save data>
	     // -------------------------------------------
	     String dbUrl = "jdbc:derby:C:/database.demo/db;create=true";
	     conn = DriverManager.getConnection(dbUrl);
	   }

	   public void normalDbUsage() throws SQLException {
	     Statement stmt = conn.createStatement();

	     // drop table
	     stmt.executeUpdate("Drop Table users");

	     // create table
	     stmt.executeUpdate("Create table users (id int primary key, name varchar(30))");

	     // insert 2 rows
	     stmt.executeUpdate("insert into users values (1,'tom')");
	     stmt.executeUpdate("insert into users values (2,'peter')");

	     // query
	     ResultSet rs = stmt.executeQuery("SELECT * FROM users");

	     // print out query result
	     while (rs.next()) { 
	       System.out.printf("%d\t%s\n", rs.getInt("id"), rs.getString("name"));
	     }
	   } 
	}
