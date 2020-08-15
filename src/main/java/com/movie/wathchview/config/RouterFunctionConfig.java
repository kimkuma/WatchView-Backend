package com.movie.wathchview.config;

import com.movie.wathchview.domain.Employee;
import com.movie.wathchview.domain.MoviePopular;
import com.movie.wathchview.repository.EmployeeMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Slf4j
public class RouterFunctionConfig {

    private final EmployeeMongoRepository employeeDb;

    private final WebClient webClient;

    @Value("${movieapi.key}")
    private String key;

    @Value("${movieapi.url}")
    private String url;

    public RouterFunctionConfig(EmployeeMongoRepository employeeDb, WebClient webClient) {
        this.employeeDb = employeeDb;
        this.webClient = webClient;
    }

    @Bean
    public RouterFunction<?> function() {
//        path에 파라미터가 담겼을때
//        return route(GET("/searchMovie/{id}"), this::searchMovie);
                //.andRoute();
        return route(GET("/searchMovie"), this::searchMovie);
    }

    public Mono<ServerResponse> searchMovie(ServerRequest request) {
        //String a = request.pathVariable("id");path에 파라미터가 담겼을때
        String query = request.queryParam("name").orElse("");

        Mono<ServerResponse> res = ServerResponse.ok().body(
                webClient
                        .mutate()
                        .baseUrl(url)
                        .build()
                        .get()
                        .uri("/search/movie?api_key={key}&language=ko&region=KR&query={name}",key, query)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve().bodyToMono(
                                MoviePopular.class
                ), Employee.class
        );

        return res;
    }

    @Bean
    public RouterFunction<ServerResponse> findAll() {  //find 라는 get방식이 요청오면 동작하는 메소드

        final RequestPredicate predicate = GET("/find").and(RequestPredicates.accept(MediaType.TEXT_PLAIN));

        RouterFunction<ServerResponse> response = route(predicate, (request)->{
            Flux<Employee> mapper = employeeDb.findAll();  //만들어준 전체 조회 메소드

            employeeDb.findByEname("good").collectList().subscribe(System.out::println);  //만들어준 단일 검색
//            db.findRegexByText(".*go.*").collectList().subscribe(System.out::println);  //만들어준 like 검색

//            Pageable page = PageRequest.of(0, 2);
//            db.findRegexPagingByText(".*go.*", page).collectList().subscribe(System.out::println);  //만들어준 페이징처리 검색

            //등록, insert 메소드는 ReactiveMongoRepository 클래스 메소드이다.
            Employee doc = new Employee();
            doc.setDepart("개발연구팀");
            doc.setEname("테스트");
            employeeDb.insert(doc).subscribe(System.out::println);

//            //수정, save 메소드는 ReactiveMongoRepository 클래스 메소드이다.
//            db.findByText("good").flatMap( target ->{  //findByText 메소드는 만들어준 단일 검색 메소드이다.
//                target.setNumber(555555);              //flatMap을 통해서 대상의 number필드값을 바꾼 뒤에
//                return db.save(target);  //save를 통해서 해당 필드 내용을 교체하여 주고 있다.
//            }).subscribe(System.out::println);
//
//            //삭제, deleteById 메소드는 ReactiveMongoRepository 클래스 메소드이다.
//            db.findByText("good")
//                    .flatMap( target -> db.deleteById(target.getId()))
//                    .subscribe(System.out::println);

            //위 내용을 토대로 응답은 아래처럼 해 주면 된다.
            Mono<ServerResponse> res = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromProducer( mapper,MoviePopular.class));
            return res;
        });



        return response;
    }
}
