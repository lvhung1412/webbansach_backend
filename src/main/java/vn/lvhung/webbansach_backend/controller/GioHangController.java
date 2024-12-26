package vn.lvhung.webbansach_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.lvhung.webbansach_backend.service.giohang.GioHangService;

@RestController
@RequestMapping("/cart-item")
public class GioHangController {
    @Autowired
    private GioHangService gioHangService;
    @PostMapping("/add-item")
    public ResponseEntity<?> add(@RequestBody JsonNode jsonData) {
        try{
            return gioHangService.save(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update-item")
    private ResponseEntity<?> update(@RequestBody JsonNode jsonData) {
        try{
            gioHangService.update(jsonData);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
