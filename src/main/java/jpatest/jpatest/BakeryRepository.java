package jpatest.jpatest;

import org.hibernate.annotations.BatchSize;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BakeryRepository extends JpaRepository<Bakery, Long> {

    List<Bakery> findAll();

    @Query(value = "select distinct b from Bakery b join fetch b.breads")
    List<Bakery> findAllJoinFetch();
}
