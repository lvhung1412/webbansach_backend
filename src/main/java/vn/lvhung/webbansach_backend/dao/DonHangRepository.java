package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import vn.lvhung.webbansach_backend.entity.ChiTietDonHang;
import vn.lvhung.webbansach_backend.entity.DonHang;
import vn.lvhung.webbansach_backend.entity.NguoiDung;

@RepositoryRestResource(path = "don-hang")
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {
    public DonHang findFirstByNguoiDungOrderByMaDonHangDesc(NguoiDung nguoiDung);

}
