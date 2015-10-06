package main.example;

/**
 * Created by Sergey_Stefoglo1 on 9/23/2015.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;

class ConnectionPool {
    public Connection con = null;
    public Connection getConnection() throws Exception, SQLException
    {
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
                   }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.print(e);
        }
        try{
            ResourceBundle labels = ResourceBundle.getBundle("config");
            con=DriverManager.getConnection(labels.getString("database"),
                    labels.getString("user"), labels.getString("password"));

        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.print(e);
        }

        return con;
    }
    public void removeConnection() throws SQLException
    {
        con.close();
    }
}

    /**
     * private constructor for static class
     */
