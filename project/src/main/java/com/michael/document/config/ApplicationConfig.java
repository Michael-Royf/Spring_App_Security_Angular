package com.michael.document.config;

import com.michael.document.constant.Constants;
import com.michael.document.entity.RoleEntity;
import com.michael.document.entity.base.RequestContext;
import com.michael.document.enumerations.Authority;
import com.michael.document.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableJpaAuditing
@EnableAsync
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
        return args -> {
            RequestContext.setUserId(0l);
            if (roleRepository.count() == 0) {
                var userRole = new RoleEntity();
                userRole.setName(Authority.USER.name());
                userRole.setAuthority(Authority.USER);
                roleRepository.save(userRole);

                var adminRole = new RoleEntity();
                adminRole.setName(Authority.ADMIN.name());
                adminRole.setAuthority(Authority.ADMIN);
                roleRepository.save(adminRole);

                var superAdminRole = new RoleEntity();
                userRole.setName(Authority.SUPER_ADMIN.name());
                userRole.setAuthority(Authority.SUPER_ADMIN);
                roleRepository.save(superAdminRole);

                var managerRole = new RoleEntity();
                userRole.setName(Authority.MANAGER.name());
                userRole.setAuthority(Authority.MANAGER);
                roleRepository.save(managerRole);
            }

            RequestContext.start();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(Constants.STRENGTH);
    }
}
