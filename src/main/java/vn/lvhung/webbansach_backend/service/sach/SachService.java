package vn.lvhung.webbansach_backend.service.sach;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface SachService {
    public ResponseEntity<?> save(JsonNode sachJson);
    public ResponseEntity<?> update(JsonNode sachJson);
    public long getTotalSach();
}
