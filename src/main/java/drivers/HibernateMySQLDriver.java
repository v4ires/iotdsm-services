package drivers;

import com.google.gson.Gson;
import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import model.SensorSource;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.GenericJPA;
import utils.hibernate.CustomTransation;
import utils.hibernate.HibernateUtil;
import utils.sql.JDBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class HibernateMySQLDriver implements DatabaseDriver {

    @Override
    public Sensor getSensorById(long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Sensor s = new GenericJPA<>(Sensor.class).findById(new CustomTransation(session, transaction), id);
        session.close();

        return s;
    }

    @Override
    public List<Sensor> getSensors() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        List<Sensor> s = new GenericJPA<>(Sensor.class).findAll(new CustomTransation(session, transaction));
        session.close();

        return s;
    }

    @Override
    public List<SensorMeasure> getSensorMeasuresOnInterval(Sensor sensor, SensorMeasureType sensorMeasureType, Date startDate, Date endDate) {
        return null;
    }

    @Override
    public boolean addSensor(Sensor sensor) {
        return false;
    }

    @Override
    public boolean addSensorMeasure(SensorMeasure sensorMeasure) {
        return false;
    }

    @Override
    public boolean deleteSensor(Sensor sensor) {
        return false;
    }
}
