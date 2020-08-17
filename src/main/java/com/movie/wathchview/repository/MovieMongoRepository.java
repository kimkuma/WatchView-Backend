package com.movie.wathchview.repository;

import com.movie.wathchview.domain.Movie;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MovieMongoRepository extends ReactiveMongoRepository<Movie,String> {
    Flux<Movie> findAll();
    Flux<Movie> findByTitleLike(String name);
}