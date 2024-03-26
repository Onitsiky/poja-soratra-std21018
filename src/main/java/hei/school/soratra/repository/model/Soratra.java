package hei.school.soratra.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@ToString
@Table(name = "\"soratra\"")
@Entity
public class Soratra implements Serializable {
  @Id private String id;
  private String originalKey;
  private String transformedKey;
}
