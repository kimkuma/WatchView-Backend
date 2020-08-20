package com.movie.wathchview.config;

import com.movie.wathchview.domain.Employee;
import com.movie.wathchview.domain.Movie;
import com.movie.wathchview.repository.EmployeeMongoRepository;
import com.movie.wathchview.repository.MovieMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Slf4j
public class RouterFunctionConfig {

    private final EmployeeMongoRepository employeeDb;

    private final WebClient webClient;

    private final MovieMongoRepository movieDb;

    @Value("${movieapi.key}")
    private String key;

    @Value("${movieapi.url}")
    private String url;

    @Value("${movieapi.imagepath}")
    private String imagepath;

    @Autowired
    public RouterFunctionConfig(EmployeeMongoRepository employeeDb, MovieMongoRepository movieDb, WebClient webClient) {
        this.employeeDb = employeeDb;
        this.movieDb = movieDb;
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

        String query = request.queryParam("name").orElse("");

        movieDb.findByTitleLike(query).collectList().subscribe(
                movieList -> {
                    // DB 조회시 데이터가 없으면 인서트 처리
                    if(movieList.size() == 0) {
                        webClient
                                .mutate()
                                .baseUrl(url)
                                .build()
                                .get()
                                .uri("/search/movie?api_key={key}&language=ko&region=KR&query={name}",key, query)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve().bodyToMono(
                                    Movie.class
                        ).subscribe(
                                movie -> {
                                    movie.getResults().forEach(
                                            movieData -> this.MovieDataInsert(movie, movieData)
                                    );
                                }
                        );
                    }
                }
        );

        Flux<Movie> movie = movieDb.findByTitleLike(query);

        Mono<ServerResponse> res = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
                 movie, Movie.class
//                webClient
//                        .mutate()
//                        .baseUrl(url)
//                        .build()
//                        .get()
//                        .uri("/search/movie?api_key={key}&language=ko&region=KR&query={name}",key, query)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .retrieve().bodyToMono(
//                                Movie.class
//                        ), Employee.class
        );

        return res;
    }

    /**
     * MovieAPI 에서 조회된 값을  DB 에 Insert
     * @param movie
     * @param getMovieApiData
     */
    public void MovieDataInsert(Movie movie, Map<String,Object> getMovieApiData ) {
        movie.setTitle((String) getMovieApiData.get("title")); // 제목
        movie.setOriginalTitle((String) getMovieApiData.get("original_title")); //원제목
        movie.setAdult((Boolean) getMovieApiData.get("adult"));  // 성인여부
        movie.setPosterPath(imagepath + (String) getMovieApiData.get("poster_path"));
        movie.setBackDropPath(imagepath + (String) getMovieApiData.get("backdrop_path"));
        movie.setMovieDbId((Integer) getMovieApiData.get("id")); // movidbId
        movie.setPopularity((Double) getMovieApiData.get("popularity")); //인기
        movie.setVideo((Boolean) getMovieApiData.get("video")); //비디오출시여
        movie.setVoteCount((Integer) getMovieApiData.get("vote_count"));// 추천수
        movie.setVoteAverage(String.valueOf(getMovieApiData.get("vote_average")) ); // 평점
        movie.setReleaseDate((String) getMovieApiData.get("release_date")); // 개봉
        movie.setOriginalLanguage((String) getMovieApiData.get("original_language"));
        movie.setOverView((String) getMovieApiData.get("overview")); //줄거리
        movie.setGenreIds((List<Integer>) getMovieApiData.get("genre_ids"));
        movie.setResults(null);
        //인서트 처리
        movieDb.insert(movie).subscribe();
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
            Mono<ServerResponse> res = ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromProducer( mapper, Movie.class));
            return res;
        });



        return response;
    }
}
