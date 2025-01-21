package com.limlydocx.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Document")

public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
//
//    @OneToMany(mappedBy = "document" ,cascade = CascadeType.ALL , orphanRemoval = true)
//    private List<DocImage> images = new ArrayList<>();


//    @Column(columnDefinition = "JSON")
    private String content;

    public DocumentEntity(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public DocumentEntity() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
