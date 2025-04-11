package com.limlydocx.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@Entity
@Table(name = "dashboard")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String owner;

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "name can only contain letters , numbers and _")
    @Column(unique = true)
    @NotBlank(message = "Name cannot be blank")
    private String name;

}
