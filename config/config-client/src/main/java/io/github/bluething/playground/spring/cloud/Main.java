package io.github.bluething.playground.spring.cloud;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "io.github.bluething.playground.spring.cloud")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

@ConfigurationProperties(prefix = "app.server")
record AppProperties(String id,
                            String secret) {
}

@Component
class AppComponent {
    private final AppProperties appProperties;

    @Autowired
    AppComponent(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @PostConstruct
    void print() {
        System.out.printf("Id: %s, secret: %s\n", appProperties.id(), appProperties.secret());
    }
}