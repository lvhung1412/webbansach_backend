package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import vn.lvhung.webbansach_backend.entity.ChiTietDonHang;
import vn.lvhung.webbansach_backend.entity.DonHang;

import java.util.List;

@RepositoryRestResource(path = "chi-tiet-don-hang")
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {
    public List<ChiTietDonHang> findChiTietDonHangByDonHang(DonHang donHang);

}
