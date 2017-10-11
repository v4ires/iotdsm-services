package controllers;

import com.google.gson.Gson;
import javafx.beans.binding.BooleanBinding;
import model.Sensor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import persistence.SensorSQL;
import spark.Request;
import spark.Response;
import spark.Route;
import utils.PropertiesReader;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;
import utils.sql.SQLQueryDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SensorController {

    public static Route serveSensorListPage = (Request request, Response response) -> {
        response.status(200);
        response.type("application/json");

        if(Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE")))
        {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            List<Sensor> sensors = new GenericJPA<>(Sensor.class).findAll(new CustomTransation(session, transaction));

            return sensors;
        }
        else
        {
            JDBConnection jdbConnection = JDBConnection
                    .builder().user(PropertiesReader.getValue("USER")).pass(PropertiesReader.getValue("PASSWORD"))
                    .urlConn("jdbc:"+PropertiesReader.getValue("DATABASETYPE")+"://"+PropertiesReader.getValue("HOST")+":"+PropertiesReader.getValue("PORT")+"/"+PropertiesReader.getValue("DATABASE"))
                    .classDriver(PropertiesReader.getValue("DRIVER"))
                    .build();

            SensorSQL sensorSql = new SensorSQL(jdbConnection);

            try {
                List<Sensor> sensors = (List<Sensor>) (Object)sensorSql.select_sql(SQLQueryDatabase.mySqlSensorQuery);

                return sensors;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<Sensor>();
            }
        }
    };
}
