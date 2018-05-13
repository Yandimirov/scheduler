package ru.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.scheduler.config.StorageProperties;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@SpringBootApplication
@ComponentScan(value = "ru.scheduler")
@ServletComponentScan(value = "ru.scheduler")
@EnableSpringConfigured
@EnableAutoConfiguration
@EnableConfigurationProperties(StorageProperties.class)
@EnableScheduling
@EnableSwagger2
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(
                        Collections.singletonList(new ParameterBuilder()
                                .name("x-auth-token")
                                .description("token")
                                .modelRef(new ModelRef("string"))
                                .parameterType("header")
                                .required(true)
                                .build()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("(/api.*)|(/auth)"))
                .build();
    }
}
