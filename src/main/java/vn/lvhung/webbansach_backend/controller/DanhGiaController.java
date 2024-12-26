package vn.lvhung.webbansach_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.lvhung.webbansach_backend.dao.*;
import vn.lvhung.webbansach_backend.entity.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RequestMapping("/review")
@RestController
public class DanhGiaController {
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private DonHangRepository donHangRepository;
    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private SuDanhGiaRepository suDanhGiaRepository;
    private final ObjectMapper objectMapper;

    public DanhGiaController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @PostMapping("/add-review")
    public ResponseEntity<?> save(@RequestBody JsonNode jsonNode) {
        try{
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idUser"))));
            int idOrder = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idOrder"))));
            int idBook = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idBook"))));
            float ratingValue = Float.parseFloat(formatStringByJson(String.valueOf(jsonNode.get("ratingPoint"))));
            String content = formatStringByJson(String.valueOf(jsonNode.get("content")));

            NguoiDung nguoiDung = nguoiDungRepository.findById(idUser).get();
            DonHang donHang = donHangRepository.findById(idOrder).get();
            List<ChiTietDonHang> danhSachChiTietDonHang = chiTietDonHangRepository.findChiTietDonHangByDonHang(donHang);
            Sach sach = sachRepository.findById(idBook).get();

            for (ChiTietDonHang chiTietDonHang : danhSachChiTietDonHang) {
                if (chiTietDonHang.getSach().getMaSach() == idBook) {
                    chiTietDonHang.setDaDanhGia(true);
                    SuDanhGia review = new SuDanhGia();
                    review.setSach(sach);
                    review.setNguoiDung(nguoiDung);
                    review.setNhanXet(content);
                    review.setDiemXepHang(ratingValue);
                    review.setChiTietDonHang(chiTietDonHang);

                    // Lấy thời gian hiện tại
                    Instant instant = Instant.now();

                    // Chuyển đổi thành timestamp
                    Timestamp timestamp = Timestamp.from(instant);
                    review.setThoiGianDanhGia(timestamp);
                    chiTietDonHangRepository.save(chiTietDonHang);
                    suDanhGiaRepository.save(review);
                    break;
                }
            }

            // Set lại rating trung bình của quyển sách đó
            List<SuDanhGia> danhSachDanhGia = suDanhGiaRepository.findAll();
            double sum = 0; // Tổng rating
            int n = 0; // Số lượng rating
            for (SuDanhGia danhGia : danhSachDanhGia) {
                if (danhGia.getSach().getMaSach() == idBook) {
                    n++;
                    sum += danhGia.getDiemXepHang();
                }
            }
            double ratingAvg = sum / n;
            sach.setTrungBinhXepHang(ratingAvg);
            sachRepository.save(sach);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-review")
    public ResponseEntity<?> updateReview(@RequestBody JsonNode jsonNode) {
        try{
            SuDanhGia reviewRequest = objectMapper.treeToValue(jsonNode, SuDanhGia.class);
            SuDanhGia review = suDanhGiaRepository.findById(reviewRequest.getMaDanhGia()).get();
            review.setNhanXet(reviewRequest.getNhanXet());
            review.setDiemXepHang(reviewRequest.getDiemXepHang());

            suDanhGiaRepository.save(review);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/get-review")
    public ResponseEntity<?> getReview(@RequestBody JsonNode jsonNode) {
        try{
            int idOrder = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idOrder"))));
            int idBook = Integer.parseInt(formatStringByJson(String.valueOf(jsonNode.get("idBook"))));

            DonHang donHang = donHangRepository.findById(idOrder).get();
            Sach sach = sachRepository.findById(idBook).get();
            List<ChiTietDonHang> danhSachChiTietDonHang = chiTietDonHangRepository.findChiTietDonHangByDonHang(donHang);
            for (ChiTietDonHang chiTietDonHang : danhSachChiTietDonHang) {
                if (chiTietDonHang.getSach().getMaSach() == sach.getMaSach()) {
                    SuDanhGia review = suDanhGiaRepository.findSuDanhGiaByChiTietDonHang(chiTietDonHang);
                    SuDanhGia reviewResponse = new SuDanhGia();
                    reviewResponse.setMaDanhGia(review.getMaDanhGia());
                    reviewResponse.setNhanXet(review.getNhanXet());
                    reviewResponse.setThoiGianDanhGia(review.getThoiGianDanhGia());
                    reviewResponse.setDiemXepHang(review.getDiemXepHang());
                    return ResponseEntity.status(HttpStatus.OK).body(reviewResponse);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }


    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }

}
