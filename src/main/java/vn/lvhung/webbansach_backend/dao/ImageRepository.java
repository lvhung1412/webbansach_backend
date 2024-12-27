package vn.lvhung.webbansach_backend.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.Book;
import vn.lvhung.webbansach_backend.entity.Image;

import java.util.List;

@RepositoryRestResource(path = "images")
public interface ImageRepository extends JpaRepository<Image, Integer> {
    public List<Image> findImagesByBook(Book book);
    @Modifying
    @Transactional
    @Query("DELETE FROM Image i WHERE i.isThumbnail = false AND i.book.idBook = :bookId")
    public void deleteImagesWithFalseThumbnailByBookId(@Param("bookId") int bookId);
}
