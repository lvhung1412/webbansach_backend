package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.Genre;

@RepositoryRestResource(path = "genre")
public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
