package services;

import controllers.SensorController;
import deserialization.IDeserializer;
import model.Sensor;
import model.SensorMeasure;
import model.SensorMeasureType;
import model.SensorSource;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.SensorMeasureRepository;
import repositories.SensorMeasureTypeRepository;
import repositories.SensorRepository;
import repositories.SensorSourceRepository;
import utils.PropertiesReader;
import utils.hibernate.CustomTransaction;
import utils.hibernate.HibernateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros <viniciusaires@usp.br>
 */
public class SensorService {

    private static final Logger log = LoggerFactory.getLogger(SensorService.class);

    /**
     *
     */
    public long deserializeMeasures(IDeserializer deserializer) {
        List<SensorMeasure> smList;
        Long insertedMeasures = 0L;
        Long resetCount = 0L;

        CustomTransaction hibernateTransaction = null;
        if (Boolean.parseBoolean(PropertiesReader.getValue("USEHIBERNATE"))) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();
            hibernateTransaction = new CustomTransaction(session, transaction);
        }

        SensorSourceRepository _sensorSourceRepository = new SensorSourceRepository(hibernateTransaction);
        SensorRepository _sensorRepository = new SensorRepository(hibernateTransaction);
        SensorMeasureTypeRepository _sensorMeasureTypeRepository = new SensorMeasureTypeRepository(hibernateTransaction);
        SensorMeasureRepository _sensorMeasureRepository = new SensorMeasureRepository(hibernateTransaction);

        try {
            smList = (List<SensorMeasure>) (Object) deserializer.readArray();

            Map<String, SensorMeasureType> insertedSensorMeasureType = new HashMap<String, SensorMeasureType>();
            Map<String, SensorSource> insertedSensorSource = new HashMap<String, SensorSource>();

            while (smList != null) {
                List<Sensor> insertedSensor = new ArrayList<Sensor>();

                for (SensorMeasure sm : smList) {

                    if (!insertedSensorSource.containsKey(sm.getSensor().getSensorSource().getName())) {
                        _sensorSourceRepository.addSensorSource(sm.getSensor().getSensorSource());
                        insertedSensorSource.put(sm.getSensor().getSensorSource().getName(), sm.getSensor().getSensorSource());
                    }

                    if (!insertedSensorMeasureType.containsKey(sm.getSensorMeasureType().getName())) {
                        _sensorMeasureTypeRepository.addSensorMeasureType(sm.getSensorMeasureType());
                        insertedSensorMeasureType.put(sm.getSensorMeasureType().getName(), sm.getSensorMeasureType());
                    }

                    if (!insertedSensor.contains(sm.getSensor())) {
                        _sensorRepository.addSensor(sm.getSensor());
                        insertedSensor.add(sm.getSensor());
                    }

                    _sensorMeasureRepository.addSensorMeasure(sm);
                    insertedMeasures++;
                    resetCount++;
                }

                if (resetCount > 50000) {
                    resetCount = 0L;
                    if (hibernateTransaction != null) {
                        hibernateTransaction.commit();

                        Session session = HibernateUtil.getSessionFactory().openSession();
                        Transaction transaction = session.beginTransaction();

                        hibernateTransaction = new CustomTransaction(session, transaction);

                        _sensorSourceRepository.setHibernateTransaction(hibernateTransaction);
                        _sensorRepository.setHibernateTransaction(hibernateTransaction);
                        _sensorMeasureTypeRepository.setHibernateTransaction(hibernateTransaction);
                        _sensorMeasureRepository.setHibernateTransaction(hibernateTransaction);
                    }
                }

                smList.clear();
                smList = (List<SensorMeasure>) (Object) deserializer.readArray();
            }

            if (hibernateTransaction != null)
                hibernateTransaction.commit();

            return insertedMeasures;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
            return -1L;
        } finally {
            _sensorSourceRepository.close();
            _sensorRepository.close();
            _sensorMeasureTypeRepository.close();
            _sensorMeasureRepository.close();
        }
    }
}
