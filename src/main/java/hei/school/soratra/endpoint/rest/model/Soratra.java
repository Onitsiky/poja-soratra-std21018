package hei.school.soratra.endpoint.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.net.URL;

@AllArgsConstructor
@Data
@Builder
@ToString
public class Soratra {
    private String originalUrl;
    private String transformed_url;
}
