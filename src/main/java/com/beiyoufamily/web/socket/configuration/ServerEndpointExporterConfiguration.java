package com.beiyoufamily.web.socket.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author Yi Dai daiyi.lucky@qq.com
 * @since 2023/8/8 14:40
 */

@Configuration
public class ServerEndpointExporterConfiguration {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
