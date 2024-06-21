package com.michael.document.config;

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


@Configuration
@EnableJpaAuditing
@EnableAsync
public class ApplicationConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }



//    @Bean
//    CommandLineRunner commandLineRunner (RoleRepository roleRepository){
//        return args -> {
//            RequestContext.setUserId(0l);
//
//            var userRole = new RoleEntity();
//            userRole.setName(Authority.USER.name());
//            userRole.setAuthority(Authority.USER);
//            roleRepository.save(userRole);
//
//            var adminRole = new RoleEntity();
//            adminRole.setName(Authority.ADMIN.name());
//            adminRole.setAuthority(Authority.ADMIN);
//            roleRepository.save(adminRole);
//
//            RequestContext.start();
//        };
//    }


}
