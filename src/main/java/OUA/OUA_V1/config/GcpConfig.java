package OUA.OUA_V1.config;

import OUA.OUA_V1.global.util.GcpProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
public class GcpConfig {

    private final GcpProperties gcpProperties;

    @Bean
    public Storage googleCloudStorage() throws IOException {
        InputStream keyFile = ResourceUtils.getURL(gcpProperties.getCredentials().getLocation())
                .openStream();
        return StorageOptions.newBuilder()
                .setProjectId(gcpProperties.getProjectId())
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();
    }
}
