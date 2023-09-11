package com.blogify.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Marcin Seweryn",
                        email = "marcinsewerynn@gmail.com"
                ),
                description = "OpenApi documentation for Blogify Blog REST API",
                title = "Blogify documentation",
                version = "1.1.0",
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                ),
                termsOfService = "http://localhost:8080/blogify/terms-of-service"
        ),
        servers = {
                @Server(
                        description = "localhost",
                        url = "http://localhost:8080/blogify/api/1.1"
                ),
                @Server(
                        description = "Prod ENV",
                        url = "http://blogify-env.eba-yrg2gm78.eu-north-1.elasticbeanstalk.com/blogify"
                )
        }
//       , security = @SecurityRequirement(name = "bearerAuth") // globally - per class we could change authentication type for each controller
)
@SecurityScheme( // use schemes to define more than one
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
