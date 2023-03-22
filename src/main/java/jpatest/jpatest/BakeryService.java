package jpatest.jpatest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BakeryService {

    private final BakeryRepository bakeryRepository;

    public List<Bakery> findAll(){
        return bakeryRepository.findAll();
    }

    public List<Bakery> findAllJoinFetch(){
        return bakeryRepository.findAllJoinFetch();
    }

}
