package edu.usp.icmc.lasdpc.model;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros viniciusaires@usp.br
 */
@Data
@MappedSuperclass
public class BasicEntity implements Serializable {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Expose
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date create_time;
}
