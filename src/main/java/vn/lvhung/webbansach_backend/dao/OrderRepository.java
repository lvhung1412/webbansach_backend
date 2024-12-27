package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.Order;
import vn.lvhung.webbansach_backend.entity.User;

@RepositoryRestResource(path = "orders")
public interface OrderRepository extends JpaRepository<Order, Integer> {
    public Order findFirstByUserOrderByIdOrderDesc(User user);
}
