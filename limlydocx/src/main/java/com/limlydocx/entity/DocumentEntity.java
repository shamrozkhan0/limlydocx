package com.limlydocx.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

/**
 * This is the document model in which the document crendentials is saved like:
 * id
 * creater
 * name
 * uploadon
 * Guest
 *
 * Now i am thinking to add the following
 *
 * content of document
 *
 */

public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Pattern(regexp = "^[a-zA-Z0-9_ ]+$", message = "name can only contain letters , numbers and _")
    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3 , max = 16)
    private String fileName;


    private LocalDate uploadOn;

    @NotBlank(message = "Cannot find you account info please do logout and login again")
    private String creator;


//  @OneToMany(mappedBy = "guest")
    private List<String> guest = new ArrayList<>();

    @ManyToOne
    private User user;


    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

}
