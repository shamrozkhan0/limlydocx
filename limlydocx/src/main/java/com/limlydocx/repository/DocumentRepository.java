package com.limlydocx.repository;

import com.limlydocx.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public  interface  DocumentRepository  extends JpaRepository<DocumentEntity, Long>{
//    Optional<DocumentEntity> findEditorFileById(UUID id);
    List<DocumentEntity> getAllDocumentByCreator(String creator);

    @Transactional
    void deleteById(UUID id);

}