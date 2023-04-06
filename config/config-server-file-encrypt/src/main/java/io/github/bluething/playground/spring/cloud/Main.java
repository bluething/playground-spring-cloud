package io.github.bluething.playground.spring.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@EnableConfigServer
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

@Configuration
class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests((authz) ->
                        authz.requestMatchers("/decrypt").denyAll()
                                .anyRequest().authenticated())
                .csrf().disable()
                .httpBasic();
        return httpSecurity.build();
    }
}