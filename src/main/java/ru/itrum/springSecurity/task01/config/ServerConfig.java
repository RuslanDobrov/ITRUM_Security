package ru.itrum.springSecurity.task01.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Value("${ssl_keystore_path}")
    private String sslKeystorePath;
    @Value("${ssl_keystore_password}")
    private String sslKeystorePassword;
    @Value("${ssl_keystore_type}")
    private String sslKeystoreType;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer() {
        return server -> {
            server.setPort(8081);
            server.setSsl(getSsl());
        };
    }

    private Ssl getSsl() {
        Ssl ssl = new Ssl();
        ssl.setKeyStore(sslKeystorePath);
        ssl.setKeyStorePassword(sslKeystorePassword);
        ssl.setKeyStoreType(sslKeystoreType);
        return ssl;
    }
}
