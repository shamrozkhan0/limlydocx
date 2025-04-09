package com.limlydocx.repository;

import com.limlydocx.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public  interface  DocumentRepository  extends JpaRepository<DocumentEntity, Long>{

    Optional<DocumentEntity> findEditorFileById(String id);

}