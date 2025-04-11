package com.limlydocx.service;

import com.limlydocx.entity.Dashboard;
import com.limlydocx.repository.DashboardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Log4j2
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public void createUserDashboard(Authentication authentication, String name){
        String username = authentication.getName();



        Dashboard dashboard = new Dashboard();
        dashboard.setOwner(username);
//        dashboard.getContent();
        dashboard.setName(name);

       log.info("Dashboard credential {} , {}", username, name);

        try {
            dashboardRepository.save(dashboard);
        } catch (Exception ex){
            ex.printStackTrace();
        }



    }

}
