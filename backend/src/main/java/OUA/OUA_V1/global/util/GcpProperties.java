package OUA.OUA_V1.global.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.cloud.gcp")
public class GcpProperties {
    private Credentials credentials;
    private String projectId;
    private String bucket;

    @Getter
    @Setter
    public static class Credentials {
        private String location;
    }
}