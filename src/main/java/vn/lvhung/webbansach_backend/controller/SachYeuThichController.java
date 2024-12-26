package vn.lvhung.webbansach_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.lvhung.webbansach_backend.dao.NguoiDungRepository;
import vn.lvhung.webbansach_backend.dao.SachRepository;
import vn.lvhung.webbansach_backend.dao.SachYeuThichRepository;
import vn.lvhung.webbansach_backend.entity.NguoiDung;
import vn.lvhung.webbansach_backend.entity.Sach;
import vn.lvhung.webbansach_backend.entity.SachYeuThich;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("favorite-book")
public class SachYeuThichController {
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private SachYeuThichRepository sachYeuThichRepository;

    @GetMapping("/get-favorite-book/{idUser}")
    public ResponseEntity<?> getAllFavoriteBookByIdUser(@PathVariable Integer idUser) {
        try{
            NguoiDung user = nguoiDungRepository.findById(idUser).get();
            List<SachYeuThich> danhSachSachYeuThich = sachYeuThichRepository.findSachYeuThichByNguoiDung(user);
            List<Integer> danhSachMaSachYeuThich = new ArrayList<>();
            for (SachYeuThich sachYeuThich : danhSachSachYeuThich) {
                danhSachMaSachYeuThich.add(sachYeuThich.getSach().getMaSach());
            }
            return ResponseEntity.ok().body(danhSachMaSachYeuThich);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/add-book")
    public ResponseEntity<?> save(@RequestBody JsonNode jsonNode) {
        try{
            int maSach = Integer.parseInt(formatStringByJson(jsonNode.get("idBook").toString()));
            int maNguoiDung = Integer.parseInt(formatStringByJson(jsonNode.get("idUser").toString()));

            Sach sach = sachRepository.findById(maSach).get();
            NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).get();

            SachYeuThich sachYeuThich = SachYeuThich.builder().sach(sach).nguoiDung(nguoiDung).build();

            sachYeuThichRepository.save(sachYeuThich);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-book")
    public ResponseEntity<?> remove(@RequestBody JsonNode jsonNode) {
        try{
            int maSach = Integer.parseInt(formatStringByJson(jsonNode.get("idBook").toString()));
            int maNguoiDung = Integer.parseInt(formatStringByJson(jsonNode.get("idUser").toString()));

            Sach sach = sachRepository.findById(maSach).get();
            NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).get();

            SachYeuThich sachYeuThich = sachYeuThichRepository.findSachYeuThichBySachAndNguoiDung(sach, nguoiDung);

            sachYeuThichRepository.delete(sachYeuThich);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
