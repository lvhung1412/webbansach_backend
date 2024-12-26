package vn.lvhung.webbansach_backend.service.nguoidung;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import vn.lvhung.webbansach_backend.entity.NguoiDung;

public interface NguoiDungService {
    public ResponseEntity<?> register(NguoiDung nguoiDung);
    public ResponseEntity<?> save(JsonNode userJson, String option);
    public ResponseEntity<?> delete(int id);
    public ResponseEntity<?> changePassword(JsonNode userJson);
    public ResponseEntity<?> changeAvatar(JsonNode userJson);
    public ResponseEntity<?> updateProfile(JsonNode userJson);
    public ResponseEntity<?> forgotPassword(JsonNode jsonNode);
}
