package model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "tb_sensor")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sensor extends BasicEntity {

    String name;
    String description;
    double latitude;
    double longitude;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "tb_sensor_has_sensor_measure_type",  joinColumns = {
            @JoinColumn(name = "sensor_id", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "sensor_measure_type_id",
                    nullable = false, updatable = false) })
    Set<SensorMeasureType> sensorMeasures;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_source_id", nullable = false)
    SensorSource sensorSource;
}
