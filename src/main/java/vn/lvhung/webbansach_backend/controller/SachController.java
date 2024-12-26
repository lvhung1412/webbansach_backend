package vn.lvhung.webbansach_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.lvhung.webbansach_backend.dao.SachRepository;
import vn.lvhung.webbansach_backend.service.sach.SachService;

@RestController
@RequestMapping("/book")
public class SachController {
    @Autowired
    private SachService sachService;

    @PostMapping(path = "/add-book")
    public ResponseEntity<?> save(@RequestBody JsonNode jsonData) {
        try {
            return sachService.save(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi r");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(path = "/update-book")
    public ResponseEntity<?> update(@RequestBody JsonNode jsonData) {
        try{
            return sachService.update(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi r");
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/get-total")
    public long getTotal() {
        return sachService.getTotalSach();
    }
}
