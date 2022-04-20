package itmo.blps.domain;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "stat")
@ToString(of = {"id", "date", "users", "money", "courses", "certificates"})
@Data
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long id;

    @Column(name = "stat_time")
    private Date date;

    private Long users;
    private Long money;
    private Long courses;
    private Long certificates;
}