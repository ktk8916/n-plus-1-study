package jpatest.jpatest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final BakeryService bakeryService;

    @GetMapping("/")
    public List<Bakery> findAll(){
        return bakeryService.findAll();
    }

    @GetMapping("/joinfetch")
    public List<Bakery> findAllJoinFetch(){
        return bakeryService.findAllJoinFetch();
    }

}
