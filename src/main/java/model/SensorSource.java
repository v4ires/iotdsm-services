package model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tb_sensor_source")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorSource extends BasicEntity {
    String name;
    String description;
}
