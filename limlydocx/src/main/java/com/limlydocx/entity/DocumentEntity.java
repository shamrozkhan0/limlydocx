package com.limlydocx.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fileName;

    private LocalDate uploadOn;

    @NotBlank(message = "Cannot find you account info please do logout and login again")
    private String creator;

//  @OneToMany(mappedBy = "guest")
    private List<String> guest = new ArrayList<>();

}
