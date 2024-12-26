package vn.lvhung.webbansach_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.lvhung.webbansach_backend.dao.NguoiDungRepository;
import vn.lvhung.webbansach_backend.dao.PhanHoiRepository;
import vn.lvhung.webbansach_backend.entity.NguoiDung;
import vn.lvhung.webbansach_backend.entity.PhanHoi;

import java.sql.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
public class PhanHoiController {
    private final ObjectMapper objectMapper;
    @Autowired
    private PhanHoiRepository phanHoiRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    public PhanHoiController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PutMapping("/update-feedback/{idFeedback}")
    public ResponseEntity<?> update(@PathVariable int maPhanHoi) {
        Optional<PhanHoi> feedback = phanHoiRepository.findById(maPhanHoi);
        if (feedback.isPresent()) {
            feedback.get().setDaDoc(true);
            phanHoiRepository.save(feedback.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Thành công");
    }

    @PostMapping("/add-feedback")
    public ResponseEntity<?> add(@RequestBody JsonNode jsonData) {
        try {
            NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(formatStringByJson(String.valueOf(jsonData.get("user"))));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Instant instant = Instant.from(formatter.parse(formatStringByJson(String.valueOf(jsonData.get("dateCreated")))));
            java.sql.Date dateCreated = new java.sql.Date(Date.from(instant).getTime());

            PhanHoi phanHoi = PhanHoi.builder()
                    .tieuDe(formatStringByJson(String.valueOf(jsonData.get("title"))))
                    .noiDung(formatStringByJson(String.valueOf(jsonData.get("comment"))))
                    .daDoc(false)
                    .ngayTao(dateCreated)
                    .nguoiDung(nguoiDung).build();

            phanHoiRepository.save(phanHoi);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok("Thành công");
    }
    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }

}
