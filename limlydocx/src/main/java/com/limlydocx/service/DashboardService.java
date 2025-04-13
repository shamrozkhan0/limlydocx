package com.limlydocx.service;

import com.limlydocx.entity.Dashboard;
import com.limlydocx.globalVariable.GlobalVariable;
import com.limlydocx.repository.DashboardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
@Log4j2
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final GlobalVariable globalVariable;


    /**
     *  Get user document from the database by using user username
     *
     * @param authentication
     * @param name
     */
    public void createUserDashboard(Authentication authentication, String name) {

        String username = globalVariable.getUsername(authentication);

        Dashboard dashboard = new Dashboard();
        dashboard.setOwner(username);
        dashboard.setDate(LocalDate.now());
        dashboard.setName(name);
        log.info("Dashboard credential {} , {}", username, name);

        try {
            dashboardRepository.save(dashboard);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }




}
