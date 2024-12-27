package vn.lvhung.webbansach_backend.service.cart;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface CartService {
    public ResponseEntity<?> save(JsonNode jsonNode);
    public ResponseEntity<?> update(JsonNode jsonNode);

}
