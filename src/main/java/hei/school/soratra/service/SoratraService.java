package hei.school.soratra.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import hei.school.soratra.endpoint.rest.model.Soratra;
import hei.school.soratra.file.BucketComponent;
import hei.school.soratra.repository.SoratraRepository;
import jakarta.ws.rs.NotFoundException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SoratraService {
  private final BucketComponent bucketComponent;
  private final SoratraRepository repository;
  private static final String ORIGINAL_FILE_PREFIX = "original_";
  private static final String TRANSFORMED_FILE_PREFIX = "transformed_";
  private static final String FILE_EXTENSION = ".txt";
  private static final Long PRESIGNED_URL_DURATION_IN_SECONDS = 3600L;

  public void processAndUpload(String id, byte[] input) throws IOException {
    String content = new String(input, UTF_8);
    File originalFile = createAndWriteInputInFile(id, content.toLowerCase());
    File tranformedFile = createAndWriteInputInFile(id, content.toUpperCase());

    String originalFilePath = ORIGINAL_FILE_PREFIX + originalFile.getName();
    String transformedFilePath = TRANSFORMED_FILE_PREFIX + tranformedFile.getName();
    bucketComponent.upload(originalFile, originalFilePath);
    bucketComponent.upload(tranformedFile, transformedFilePath);
    hei.school.soratra.repository.model.Soratra toSave =
        new hei.school.soratra.repository.model.Soratra(id, originalFilePath, transformedFilePath);
    repository.save(toSave);
  }

  public Soratra getFileUrls(String id) {
    hei.school.soratra.repository.model.Soratra actual = getObjectById(id);
    String originalFileKey = actual.getOriginalKey();
    String transformedFileKey = actual.getTransformedKey();
    String originalUrl = getPresignedURL(originalFileKey, PRESIGNED_URL_DURATION_IN_SECONDS);
    String transformedUrl = getPresignedURL(transformedFileKey, PRESIGNED_URL_DURATION_IN_SECONDS);
    return new Soratra(originalUrl, transformedUrl);
  }

  private hei.school.soratra.repository.model.Soratra getObjectById(String id) {
    return repository.findById(id).orElseThrow(NotFoundException::new);
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
