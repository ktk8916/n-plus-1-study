package jpatest.jpatest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    private void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;

        public void dbInit1(){
            Bread bread1 = new Bread();
            bread1.setName("단팥빵");
            em.persist(bread1);

            Bread bread2 = new Bread();
            bread2.setName("소금빵");
            em.persist(bread2);

            Bread bread3 = new Bread();
            bread3.setName("소보로");
            em.persist(bread3);

            Bakery bakery = new Bakery();
            bakery.setName("팥고당");
            bakery.setBreads(new ArrayList<>(Arrays.asList(bread1, bread2, bread3)));
            em.persist(bakery);
        }
        public void dbInit2(){
            Bread bread1 = new Bread();
            bread1.setName("델리만쥬");
            em.persist(bread1);

            Bread bread2 = new Bread();
            bread2.setName("붕어빵");
            em.persist(bread2);

            Bread bread3 = new Bread();
            bread3.setName("식빵");
            em.persist(bread3);

            Bakery bakery = new Bakery();
            bakery.setName("나폴레옹");
            bakery.setBreads(new ArrayList<>(Arrays.asList(bread1, bread2, bread3)));
            em.persist(bakery);
        }
    }
}
