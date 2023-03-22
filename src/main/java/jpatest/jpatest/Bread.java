package jpatest.jpatest;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Bread {

    @Id @GeneratedValue @Column(name = "bread_id")
    Long id;

    String name;

}
