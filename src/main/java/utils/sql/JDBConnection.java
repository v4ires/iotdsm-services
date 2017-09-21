package utils.sql;

import lombok.Builder;

import java.sql.Connection;
import java.sql.DriverManager;

@Builder
public class JDBConnection {

    public String user;
    public String pass;
    public String urlConn;
    public String classDriver;

    private static Connection conn;

    public JDBConnection(String user, String pass, String urlConn, String classDriver) {
        this.user = user;
        this.pass = pass;
        this.urlConn = urlConn;
        this.classDriver = classDriver;
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
}
