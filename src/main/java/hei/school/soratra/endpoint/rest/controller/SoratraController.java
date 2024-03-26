package hei.school.soratra.endpoint.rest.controller;

import hei.school.soratra.endpoint.rest.model.Soratra;
import hei.school.soratra.service.SoratraService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class SoratraController {
    private final SoratraService service;

    @PutMapping("/soratra/{id}")
    public ResponseEntity<?> uploadFile(
            @PathVariable String id,
            @RequestBody byte[] fileToUpload
    ) {
        try {
            service.processAndUpload(id, fileToUpload);
            return ResponseEntity.ok().body(null);
        } catch (IOException e) {
            return ResponseEntity.ok("KO");
        }
    }

    @GetMapping("/soratra/{id}")
    public ResponseEntity<?> getFilesUrls(@PathVariable String id) {
        try {
            Soratra response = service.getFileUrls(id);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("File with id=" + id + " not found.");
        }

    }
}
