package utils.sql;

import lombok.Builder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Builder
public class JDBConnection {

    public String user;
    public String pass;
    public String urlConn;
    public String classDriver;
    public String databaseType;

    private static Connection conn;

    public JDBConnection(String user, String pass, String urlConn, String classDriver, String databaseType) {
        this.user = user;
        this.pass = pass;
        this.urlConn = urlConn;
        this.classDriver = classDriver;
        this.databaseType = databaseType;
    }

    public Connection getJDBConn() {
        try {
            Class.forName(classDriver).newInstance();
            conn = DriverManager.getConnection(urlConn, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void close() {
        if(conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
