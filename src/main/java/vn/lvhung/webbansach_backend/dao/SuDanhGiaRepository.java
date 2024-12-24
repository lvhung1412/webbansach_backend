package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.ChiTietDonHang;
import vn.lvhung.webbansach_backend.entity.SuDanhGia;

@RepositoryRestResource(path = "su-danh-gia")
public interface SuDanhGiaRepository extends JpaRepository<SuDanhGia,Long> {
    public SuDanhGia findSuDanhGiaByChiTietDonHang(ChiTietDonHang chiTietDonHang);
    public long countBy();

}
