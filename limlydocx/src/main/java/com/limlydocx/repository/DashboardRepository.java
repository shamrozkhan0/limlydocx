package com.limlydocx.repository;

import com.limlydocx.entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Integer> {

    List<Dashboard> getAllDocumentByOwner(String username);

}
