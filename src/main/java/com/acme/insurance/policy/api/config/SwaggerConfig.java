package com.acme.insurance.policy.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    public static final String SWAGGER_DESCRIPTION = "API responsável pelo gerenciamento do ciclo de vida das solicitações de apólice de seguros da seguradora ACME, utilizando arquitetura orientada a eventos. Permite o recebimento, consulta, validação, alteração de estado, cancelamento e publicação de eventos relacionados às solicitações de apólice. Integra-se com uma API de Fraudes para classificação de risco do cliente, aplicando regras específicas conforme o perfil do cliente e tipo de seguro. O serviço garante a consistência do fluxo, persistência dos dados e rastreabilidade do histórico de estados, além de publicar eventos para outros serviços da cadeia de seguros.";
    public static final String SWAGGER_TITLE = "Policy Request API";
    public static final String SWAGGER_VERSION = "1.0.0";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .description(SWAGGER_DESCRIPTION)
                        .title(SWAGGER_TITLE)
                        .version(SWAGGER_VERSION));
    }

    @Bean
    public GroupedOpenApi groupOpenApi() {
        String[] paths = {"/api/**"};
        String[] packagesToscan = {"com.acme.insurance.policy.api"};
        return GroupedOpenApi.builder().group("v1").pathsToMatch(paths).packagesToScan(packagesToscan)
                .build();
    }
}