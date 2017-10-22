package model;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tb_sensor_measure_type")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorMeasureType extends BasicEntity {
    @Expose
    String name;

    @Expose
    String unit;
}
