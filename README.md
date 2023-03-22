# n-plus-1-study

# N+1

Q. N + 1 문제는 무엇이고 이것이 발생하는 이유와 이를 해결하는 방법을 설명해주세요.

연관 관계가 설정된 엔티티를 조회할 경우에 조회된 데이터 갯수(n) 만큼 연관관계의 조회 쿼리가 추가로 발생하여 데이터를 읽어오게 되는 것을 N+1문제라고 합니다.

JPQL에서는 연관관계 데이터를 무시하고 해당 엔티티 기준으로 쿼리를 조회하기 때문에 연관된 엔티티 데이터가 필요한 경우 지정한 시점(EAGER, LAZER)에 조회를 별도로 호출하여 발생합니다.

이를 해결하기 위해 JPQL로 fetch join을 사용한 쿼리를 작성하거나,  EntityGraph에 연관관계 필드명을 넣어주거나, BatchSize옵션을 주어 해결할 수 있습니다.

- N+1
    
    ```java
    @Entity
    @Data
    public class Bakery {
    
        @Id @GeneratedValue
        Long id;
    
        String name;
    
        @OneToMany(fetch = FetchType.LAZY)
        List<Bread> breads = new ArrayList<>();
    }
    
    ...
    
    @Entity
    @Data
    public class Bread {
    
        @Id @GeneratedValue
        Long id;
    
        String name;
    }
    ```
    
    - 테스트 데이터 생성
        
        ```java
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
        ```
        
    
    OneToMany 관계의 빵집과 빵 엔티티를 생성하고 findAll()을 하였을 때 
    
    쿼리가 총 3번 (빵집 + 빵집의 수)
    
    실행되는 것을 확인할 수 있음
    
    ```java
    Hibernate: select bakery0_.id as id1_0_, bakery0_.name as name2_0_ from bakery bakery0_
    Hibernate: select breads0_.bakery_id as bakery_i1_1_0_, breads0_.breads_id as breads_i2_1_0_, bread1_.id as id1_2_1_, bread1_.name as name2_2_1_ from bakery_breads breads0_ inner join bread bread1_ on breads0_.breads_id=bread1_.id where breads0_.bakery_id=?
    Hibernate: select breads0_.bakery_id as bakery_i1_1_0_, breads0_.breads_id as breads_i2_1_0_, bread1_.id as id1_2_1_, bread1_.name as name2_2_1_ from bakery_breads breads0_ inner join bread bread1_ on breads0_.breads_id=bread1_.id where breads0_.bakery_id=?
    ```
    
    이는 JPA가 생성해서 실행한 JPQL이 연관관계 데이터를 무시하고 해당 엔티티를 기준으로 조회하기 때문이다
    
    첫 쿼리에 빵 데이터가 담겨오지 않으니, **빵 데이터가 필요할 때** 빵집에 개수 만큼 맞는 빵 데이터를 추가로 조회하는 것이 N+1문제  
    
    - `FetchType`과 상관없이 발생한다 !
        
        FetchType이란 JPA에서 연관관계의 데이터를 가져올 때 사용하는 전략
        
        즉시 로딩(Eager Loading)
        
        - 연관된 엔티티를 즉시 조회
        - 실무에서 엔티티간의 관계가 복잡해질수록 조인으로 인한 성능 저하가 있고, 쿼리를 예측할 수 없음
        
        → 지연 로딩 사용 권장…
        
        지연 로딩(Lazy Loading)
        
        - 연관된 엔티티를 프록시로 조회 후, 프록시를 실제 사용할 때 DB에서 조회
        - 프록시를 실제 사용할 때 DB에서 조회
        
        N+1 문제가 발생하는 것은 JPA가 만들어준 JPQL의 특성이다. 지연 로딩으로 코드를 작성하여도 연관된 엔티티를 사용해야 할 때 쿼리가 날아가는 것은 변함없음
        
- Fetch join
    
    ```sql
    SELECT * 
    FROM BAKERY, BREAD
    WHERE BAKERY.BREAD_ID = BREAD_ID;
    ```
    
    우리가 원하는 SQL은 다음과 같이 모든 데이터를 한 번에 조회해오는 형태가 될 것이다
    
    이를 구현하기 위해 JPQL에서 제공해주는 Fetch join을 사용할 수 있다
    
    ```sql
    @Repository
    public interface BakeryRepository extends JpaRepository<Bakery, Long> {
    
        List<Bakery> findAll();
    
        @Query(value = "select distinct b from Bakery b join fetch b.breads")
        List<Bakery> findAllJoinFetch();
    }
    ```
    
    ```sql
    Hibernate: select distinct bakery0_.bakery_id as bakery_i1_0_0_, bread2_.bread_id as bread_id1_2_1_, bakery0_.name as name2_0_0_, bread2_.name as name2_2_1_, breads1_.bakery_bakery_id as bakery_b1_1_0__, breads1_.breads_bread_id as breads_b2_1_0__ from bakery bakery0_ inner join bakery_breads breads1_ on bakery0_.bakery_id=breads1_.bakery_bakery_id inner join bread bread2_ on breads1_.breads_bread_id=bread2_.bread_id
    ```
    
    [https://offetuoso.github.io/blog/develop/backend/orm-jpa-basic/jpql-fetch-join/](https://offetuoso.github.io/blog/develop/backend/orm-jpa-basic/jpql-fetch-join/)
    
- EntityGraph
    
    연관관계를 조회하는 메서드에 @EntityGraph 어노테이션을 사용해서 해결할 수 있다
    
    ```java
    @Repository
    public interface BakeryRepository extends JpaRepository<Bakery, Long> {
    
        @EntityGraph(attributePaths = {"breads"})
        List<Bakery> findAll();
    }
    ```
    
    ```sql
    Hibernate: select bakery0_.bakery_id as bakery_i1_0_0_, bread2_.bread_id as bread_id1_2_1_, bakery0_.name as name2_0_0_, bread2_.name as name2_2_1_, breads1_.bakery_bakery_id as bakery_b1_1_0__, breads1_.breads_bread_id as breads_b2_1_0__ from bakery bakery0_ left outer join bakery_breads breads1_ on bakery0_.bakery_id=breads1_.bakery_bakery_id left outer join bread bread2_ on breads1_.breads_bread_id=bread2_.bread_id
    ```
    
    [https://velog.io/@wogh126/Spring-Data-JPA-Entity-Graph](https://velog.io/@wogh126/Spring-Data-JPA-Entity-Graph)
    
- BatchSize
    
    BatchSize를 지정하면 여러 개의 프록시 객체를 조회할 때 where 절이 같은 여러 개의 select 쿼리를 하나의 in 쿼리로 만들어줌 
    
    ```java
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
    ```
    
    ```java
    Hibernate: select bakery0_.bakery_id as bakery_i1_0_, bakery0_.name as name2_0_ from bakery bakery0_
    Hibernate: select breads0_.bakery_bakery_id as bakery_b1_1_1_, breads0_.breads_bread_id as breads_b2_1_1_, bread1_.bread_id as bread_id1_2_0_, bread1_.name as name2_2_0_ from bakery_breads breads0_ inner join bread bread1_ on breads0_.breads_bread_id=bread1_.bread_id where breads0_.bakery_bakery_id in (?, ?)
    ```
    
    빵집 엔티티를 조회하는 쿼리와 해당하는 빵집에 해당하는 빵들을 IN쿼리로 가져오는 쿼리 2개가 실행되는 것을 확인할 수 있다 
    
    [https://bcp0109.tistory.com/333](https://bcp0109.tistory.com/333)
    
