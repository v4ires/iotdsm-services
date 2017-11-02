package model;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * University of São Paulo
 * IoT Repository Module
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
@Entity
@Table(name = "tb_sensor")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sensor extends BasicEntity {

    @Expose
    String name;
    @Expose
    String description;
    @Expose
    double latitude;
    @Expose
    double longitude;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "tb_sensor_has_sensor_measure_type", joinColumns = {
            @JoinColumn(name = "sensor_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "sensor_measure_type_id",
                    nullable = false, updatable = false)})
    Set<SensorMeasureType> sensorMeasures;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sensor_source_id", nullable = false)
    @Expose
    SensorSource sensorSource;
}
