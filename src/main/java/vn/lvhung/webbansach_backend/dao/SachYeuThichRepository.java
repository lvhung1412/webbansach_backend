package vn.lvhung.webbansach_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.lvhung.webbansach_backend.entity.NguoiDung;
import vn.lvhung.webbansach_backend.entity.Sach;
import vn.lvhung.webbansach_backend.entity.SachYeuThich;

import java.util.List;

@RepositoryRestResource(path = "favorite-books")
public interface SachYeuThichRepository extends JpaRepository<SachYeuThich,Integer> {
    public SachYeuThich findSachYeuThichBySachAndNguoiDung(Sach sach, NguoiDung nguoiDung);
    public List<SachYeuThich> findSachYeuThichByNguoiDung(NguoiDung nguoiDung);


}
