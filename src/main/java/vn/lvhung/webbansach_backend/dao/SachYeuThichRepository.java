package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.lvhung.webbansach_backend.entity.Sach;
import vn.lvhung.webbansach_backend.entity.SachYeuThich;

public interface SachYeuThichRepository extends JpaRepository<SachYeuThich,Integer> {
}
