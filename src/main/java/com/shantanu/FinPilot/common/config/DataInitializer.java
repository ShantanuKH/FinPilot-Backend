package com.shantanu.FinPilot.common.config;

import com.shantanu.FinPilot.user.entity.Role;
import com.shantanu.FinPilot.user.entity.RoleType;
import com.shantanu.FinPilot.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
//    We can also write
//    @Autowired
//    private RoleRepository roleRepository;


    @Override
    public void run(String... args) {

        if (roleRepository.findByName(RoleType.ROLE_USER).isEmpty()) {

            roleRepository.save(
                    Role.builder()
                            .name(RoleType.ROLE_USER)
                            .build()
            );
        }

        if (roleRepository.findByName(RoleType.ROLE_ADMIN).isEmpty()) {

            roleRepository.save(
                    Role.builder()
                            .name(RoleType.ROLE_ADMIN)
                            .build()
            );
        }
    }
}