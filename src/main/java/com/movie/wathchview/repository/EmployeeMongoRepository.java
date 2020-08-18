package com.movie.wathchview.repository;

import com.movie.wathchview.domain.Employee;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface EmployeeMongoRepository extends ReactiveMongoRepository<Employee,String> {

    Flux<Employee> findAll();

    Flux<Employee> findByEname(String ename);

}

