package com.prithvi.thebankapplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "The Bank of Republic of People",
                description = "Backend Rest APIs for the bank",
                version = "v1.0",
                contact = @Contact(
                        name = "Prithvi More",
                        email = "prithvi33.temp@gmail.com",
                        url = "https://github.com/LuciferMorningstar33/the-bank-application"
                ),
                license = @License(
                        name = "The Bank of Republic of People",
                        url = "https://github.com/LuciferMorningstar33/the-bank-application"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "The Bank App Documentation",
                url = "https://github.com/LuciferMorningstar33/the-bank-application"
        )
)
public class TheBankApplication {

    public static void main(String[] args) {

        SpringApplication.run(TheBankApplication.class, args);
    }

}
