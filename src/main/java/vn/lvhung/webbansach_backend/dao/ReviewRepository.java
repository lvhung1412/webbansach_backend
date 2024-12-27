package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.OrderDetail;
import vn.lvhung.webbansach_backend.entity.Review;

@RepositoryRestResource(path = "reviews")
public interface ReviewRepository extends JpaRepository<Review, Long> {
    public Review findReviewByOrderDetail(OrderDetail orderDetail);
    public long countBy();
}
