package vn.lvhung.webbansach_backend.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.GioHang;

@RepositoryRestResource(path = "cart-items")
public interface GioHangRepository extends JpaRepository<GioHang,Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM GioHang c WHERE  c.nguoiDung.maNguoiDung = :maNguoiDung")
    public void deleteGioHangByMaNguoiDung(@Param("maNguoiDung") int maNguoiDung);
}
