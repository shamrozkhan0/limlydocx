package com.limlydocx.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "document-Data")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class DashboardDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String owner;

    private LocalDate CreatedOn;

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "name can only contain letters , numbers and _")
    @Column(unique = true)
    @NotBlank(message = "Name cannot be blank")
    private String name;






}
