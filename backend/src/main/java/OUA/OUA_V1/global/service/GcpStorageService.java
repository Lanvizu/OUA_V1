package OUA.OUA_V1.global.service;

import OUA.OUA_V1.global.exception.GcpStorageServerException;
import OUA.OUA_V1.global.util.GcpProperties;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.ByteBuffer;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GcpStorageService {

    private final Storage storage;
    private final GcpProperties gcpProperties;

    public String uploadImage(MultipartFile image) {

        String uuid = UUID.randomUUID().toString();
        String ext = image.getContentType();

        BlobId blobId = BlobId.of(gcpProperties.getBucket(), uuid);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(ext)
                .build();
        try (WriteChannel writer = storage.writer(blobInfo)) {
            byte[] imageData = image.getBytes();
            writer.write(ByteBuffer.wrap(imageData));
        } catch (Exception e) {
            throw new GcpStorageServerException();
        }
        return uuid;
    }

    public void deleteImage(String objectName) {
        try {
            BlobId blobId = BlobId.of(gcpProperties.getBucket(), objectName);
            boolean delete = storage.delete(blobId);
            if (!delete) {
                throw new GcpStorageServerException();
            }
        } catch (Exception e) {
            throw new GcpStorageServerException();
        }
    }
}
