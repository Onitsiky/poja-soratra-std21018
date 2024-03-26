package hei.school.soratra.repository;

import hei.school.soratra.repository.model.Soratra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoratraRepository extends JpaRepository<Soratra, String> {}
