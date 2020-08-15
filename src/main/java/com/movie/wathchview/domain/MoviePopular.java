package com.movie.wathchview.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;


@Data
@Document(collection="moviepopular")
@NoArgsConstructor
@AllArgsConstructor
public class MoviePopular implements Serializable {
    private static final long serialVersionUID = -2829033005578753232L;

    @Id
    private String id;
    private List<?> results;
}
