package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.Book;
import vn.lvhung.webbansach_backend.entity.FavoriteBook;
import vn.lvhung.webbansach_backend.entity.User;

import java.util.List;

@RepositoryRestResource(path = "favorite-books")
public interface FavoriteBookRepository extends JpaRepository<FavoriteBook, Integer> {
    public FavoriteBook findFavoriteBookByBookAndUser(Book book, User user);
    public List<FavoriteBook> findFavoriteBooksByUser(User user);
}
