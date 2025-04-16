package com.limlydocx.repository;

import com.limlydocx.entity.DashboardDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<DashboardDocumentEntity, Integer> {

    List<DashboardDocumentEntity> getAllDocumentByOwner(String username);

}
