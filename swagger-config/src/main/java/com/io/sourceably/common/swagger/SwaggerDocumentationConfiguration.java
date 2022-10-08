package com.io.sourceably.common.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

@Configuration
public class SwaggerDocumentationConfiguration {

    @Value("${openapi.server.url:}")
    private String url;
 /*   ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Person REST CRUD operations API in Spring-Boot 2")
                .description(
                        "Sample REST API for centalized documentation using Spring Boot and spring-fox swagger 2 ").version("2.0")
                .termsOfServiceUrl("").contact(new Contact("Sandeep Sharma", "", "")).build();
    }
    @Bean
    UiConfiguration uiConfig()
    {
        return UiConfigurationBuilder.builder().supportedSubmitMethods(UiConfiguration.Constants.NO_SUBMIT_METHODS).build();
    }
    @Bean
    public OpenAPI configureControllerPackageAndConvertors() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.io.rpm")).build()
                .apiInfo(apiInfo())
                .securitySchemes(Arrays.asList(apiKey()));
    }
    private ApiKey apiKey() {
        return new ApiKey("apiKey", "Authorization", "header");
    }
*/
 @Bean
 public OpenAPI customOpenAPI() {
     OpenAPI openAPI= new OpenAPI().info(apiInfo());
     openAPI.components(new Components().addSecuritySchemes("bearer-jwt",
             new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                     .in(SecurityScheme.In.HEADER).name("Authorization")));
     if(!ObjectUtils.isEmpty(url)){
         openAPI.addServersItem(serversItem());
     }
     return openAPI;
 }
    private Server serversItem() {
        Server serversItem =new Server();
        serversItem.url(url);
        return serversItem;
    }
    private Info apiInfo() {
        return new Info()
                .title("Human cloning API")
                .description("API for creating clone who will fight in the clones wars")
                .version("2.0")
                .contact(apiContact())
                .license(apiLicence());
    }

    private License apiLicence() {
        return new License()
                .name("MIT Licence")
                .url("https://opensource.org/licenses/mit-license.php");
    }

    private Contact apiContact() {
        return new Contact()
                .name("Erwan LE TUTOUR")
                .email("erwanletutour.elt@gmail.com")
                .url("https://github.com/ErwanLT");
    }
}
