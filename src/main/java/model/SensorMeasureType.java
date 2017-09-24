package model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "tb_sensor_measure_type")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorMeasureType extends BasicEntity {
    String name;
    String unit;
}
