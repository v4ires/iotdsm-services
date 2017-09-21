package persistence;

import utils.sql.JDBConnection;

public class SensorSQL extends GenericSQL {

    public SensorSQL(JDBConnection jdbconn) {
        super(jdbconn);
    }
}
