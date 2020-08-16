package com.movie.wathchview.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection="employee")
public class Employee implements Serializable {
    private static final long serialVersionUID = -591038038004800307L;

    @Id
    private String id;
    private String depart;
    private String ename;
    private int height;
    private String status;

}
