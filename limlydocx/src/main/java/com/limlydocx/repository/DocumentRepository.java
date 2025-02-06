package com.limlydocx.repository;

import com.limlydocx.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long>{

    String getDocumentById(UUID id);
}
