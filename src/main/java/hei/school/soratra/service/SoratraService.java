package hei.school.soratra.service;

import hei.school.soratra.endpoint.rest.model.Soratra;
import hei.school.soratra.file.BucketComponent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@AllArgsConstructor
public class SoratraService {
    private final BucketComponent bucketComponent;
    private static final String ORIGINAL_FILE_PREFIX = "original_";
    private static final String TRANSFORMED_FILE_PREFIX = "transformed_";
    private static final String FILE_EXTENSION = ".txt";
    private static final Long PRESIGNED_URL_DURATION_IN_SECONDS = 3600L;

    public void processAndUpload(String id, byte[] input) throws IOException {
            String content = new String(input, UTF_8);
            File originalFile = createAndWriteInputInFile(id, content.toLowerCase());
            File tranformedFile = createAndWriteInputInFile(id, content.toUpperCase());

            String originalFilePath = ORIGINAL_FILE_PREFIX + originalFile.getPath();
            String transformedFilePath = TRANSFORMED_FILE_PREFIX + tranformedFile.getPath();
            bucketComponent.upload(originalFile, originalFilePath);
            bucketComponent.upload(tranformedFile, transformedFilePath);
    }

    public Soratra getFileUrls(String id) {
        String originalFileKey = ORIGINAL_FILE_PREFIX + id + FILE_EXTENSION;
        String transformedFileKey = TRANSFORMED_FILE_PREFIX + id + FILE_EXTENSION;
        String originalUrl = getPresignedURL(originalFileKey, PRESIGNED_URL_DURATION_IN_SECONDS);
        String transformedUrl = getPresignedURL(transformedFileKey, PRESIGNED_URL_DURATION_IN_SECONDS);
        return new Soratra(originalUrl, transformedUrl);
    }

    private File createAndWriteInputInFile(String id, String input) throws IOException {
            File file = File.createTempFile(id, FILE_EXTENSION);
            FileWriter writer = new FileWriter(file);
            writer.write(input);
            writer.close();
            return file;
    }

    public String getPresignedURL(String key, Long durationInSeconds) {
        Instant now = Instant.now();
        Instant expirationInstant = now.plusSeconds(durationInSeconds);
        Duration expirationDuration = Duration.between(now, expirationInstant);
        return bucketComponent.presign(key, expirationDuration).toString();
    }
}
