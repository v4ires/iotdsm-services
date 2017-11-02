package model;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * University of São Paulo
 * IoT Repository Module
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
@Entity
@Table(name = "tb_sensor_measure")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorMeasure {

    @Expose
    String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_measure_type_id", nullable = false)
    SensorMeasureType sensorMeasureType;

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Expose
    private Date create_time;
}
