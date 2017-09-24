package model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tb_sensor_measure")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorMeasure extends BasicEntity{
    String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_measure_type_id", nullable = false)
    SensorMeasureType sensorMeasureType;
}
