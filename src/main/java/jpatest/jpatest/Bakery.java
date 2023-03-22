package jpatest.jpatest;

import lombok.Data;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Bakery {

    @Id @GeneratedValue @Column(name = "bakery_id")
    Long id;

    String name;

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY)
    List<Bread> breads = new ArrayList<>();
}
