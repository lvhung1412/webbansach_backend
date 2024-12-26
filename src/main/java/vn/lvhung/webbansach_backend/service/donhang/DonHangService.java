package vn.lvhung.webbansach_backend.service.donhang;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface DonHangService {
    public ResponseEntity<?> save(JsonNode jsonData);
    public ResponseEntity<?> update(JsonNode jsonData);
    public ResponseEntity<?> cancel (JsonNode jsonData);
}
