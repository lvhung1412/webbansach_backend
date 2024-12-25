package vn.lvhung.webbansach_backend.service.giohang;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface GioHangService {
    public ResponseEntity<?> save(JsonNode jsonNode);
    public ResponseEntity<?> update(JsonNode jsonNode);
}
