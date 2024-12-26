package vn.lvhung.webbansach_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.lvhung.webbansach_backend.service.donhang.DonHangService;
import vn.lvhung.webbansach_backend.service.giohang.GioHangService;

@RestController
@RequestMapping("/order")
public class DonHangController {
    @Autowired
    private DonHangService donHangService;
    @PostMapping("/add-order")
    public ResponseEntity<?> save (@RequestBody JsonNode jsonData) {
        try{
            return donHangService.save(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update-order") // update các trạng thái
    public ResponseEntity<?> update (@RequestBody JsonNode jsonData) {
        try{
            return donHangService.update(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/cancel-order") // khi thanh toán mà huỷ thanh toán
    public ResponseEntity<?> cancle (@RequestBody JsonNode jsonNode) {
        try{
            return donHangService.cancel(jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
