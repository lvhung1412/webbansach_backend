package vn.lvhung.webbansach_backend.dao;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.HinhAnh;
import vn.lvhung.webbansach_backend.entity.Sach;

import java.util.List;

@RepositoryRestResource(path = "hinh-anh")
public interface HinhAnhRepository extends JpaRepository<HinhAnh,Integer> {
    public List<HinhAnh> findHinhAnhBySach(Sach sach);
    @Modifying
    @Transactional
    @Query("DELETE FROM HinhAnh h WHERE h.la_thumbnail = false AND h.sach.maSach = :maSach")
    public void deleteHinhAnhWithFalseThumbnailByMaSach(@Param("maSach") int maSach);

}
