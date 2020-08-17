package com.movie.wathchview.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Data
@Document(collection="movie")
public class Movie implements Serializable {
    private static final long serialVersionUID = -2829033005578753232L;

    @Id
    private int movieDbId;
    private String title;
    private double popularity;
    private int voteCount;
    private boolean isVideo;
    private String posterPath;
    private boolean isAdult;
    private String backDropPath;
    private String originalLanguage;
    private String originalTitle;
    private List<Integer> genreIds;
    private String voteAverage;
    private String overView;
    private String releaseDate;
    private List<Map<String,Object>> results;

}
