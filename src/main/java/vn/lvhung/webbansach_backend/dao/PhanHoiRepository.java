package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.PhanHoi;

@RepositoryRestResource(path = "feedbacks")
public interface PhanHoiRepository extends JpaRepository<PhanHoi, Integer> {
    long countBy();
}
