package model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

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
    //LocalDate data_time;
}
