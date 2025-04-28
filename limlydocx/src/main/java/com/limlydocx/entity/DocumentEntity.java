package com.limlydocx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "document")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Pattern(regexp = "^[a-zA-Z0-9_ ]+$", message = "name can only contain letters , numbers and _")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3)
    private String fileName;


    private LocalDate uploadOn;


    @NotBlank(message = "Cannot find you account info please do logout and login again")
    private String username;


//  @OneToMany(mappedBy = "guest")
    private List<String> guest = new ArrayList<>();


    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;


    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}
