package vn.lvhung.webbansach_backend.service.book;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface BookService {
    public ResponseEntity<?> save(JsonNode bookJson);
    public ResponseEntity<?> update(JsonNode bookJson);
    public long getTotalBook();
}
