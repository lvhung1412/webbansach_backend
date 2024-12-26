package vn.lvhung.webbansach_backend.service.nguoidung;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.lvhung.webbansach_backend.dao.NguoiDungRepository;
import vn.lvhung.webbansach_backend.dao.QuyenRepository;
import vn.lvhung.webbansach_backend.entity.NguoiDung;
import vn.lvhung.webbansach_backend.entity.Quyen;
import vn.lvhung.webbansach_backend.entity.ThongBao;
import vn.lvhung.webbansach_backend.security.JwtResponse;
import vn.lvhung.webbansach_backend.service.JWT.JwtService;
import vn.lvhung.webbansach_backend.service.UploadImage.UploadImageService;
import vn.lvhung.webbansach_backend.service.email.EmailService;
import vn.lvhung.webbansach_backend.service.utils.Base64ToMultipartFileConverter;

import java.sql.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class NguoiDungServiceImpl implements NguoiDungService{
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private QuyenRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UploadImageService uploadImageService;
    @Autowired
    private JwtService jwtService;
    private final ObjectMapper objectMapper;

    public NguoiDungServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<?> register(NguoiDung nguoiDung) {
        // Kiểm tra username đã tồn tại chưa
        if (nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {
            return ResponseEntity.badRequest().body(new ThongBao("Username đã tồn tại."));
        }

        // Kiểm tra email
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            return ResponseEntity.badRequest().body(new ThongBao("Email đã tồn tại."));
        }

        // Mã hoá mật khẩu
        String encodePassword = passwordEncoder.encode(nguoiDung.getMatKhau());
        nguoiDung.setMatKhau(encodePassword);

        nguoiDung.setAvatar("");

        // Tạo mã kích hoạt cho người dùng
        nguoiDung.setMaKichHoat(generateActivationCode());
        nguoiDung.setDaKichHoat(false);

        // Cho role mặc định
        List<Quyen> roleList = new ArrayList<>();
        roleList.add(roleRepository.findByTenQuyen("CUSTOMER"));
        nguoiDung.setDanhSachQuyen(roleList);

        // Lưu vào database
        nguoiDungRepository.save(nguoiDung);

        // Gửi email cho người dùng để kích hoạt
        sendEmailActivation(nguoiDung.getEmail(),nguoiDung.getMaKichHoat());

        return ResponseEntity.ok("Đăng ký thành công!");
    }

    @Override
    public ResponseEntity<?> save(JsonNode userJson, String option) {
        try{
            NguoiDung user = objectMapper.treeToValue(userJson, NguoiDung.class);

            // Kiểm tra tên đăng nhập đã tồn tại chưa
            if (!option.equals("update")) {
                if (nguoiDungRepository.existsByTenDangNhap(user.getTenDangNhap())) {
                    return ResponseEntity.badRequest().body(new ThongBao("Username đã tồn tại."));
                }

                // Kiểm tra email
                if (nguoiDungRepository.existsByEmail(user.getEmail())) {
                    return ResponseEntity.badRequest().body(new ThongBao("Email đã tồn tại."));
                }
            }

            // Set ngày sinh cho user
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Instant instant = Instant.from(formatter.parse(formatStringByJson(String.valueOf(userJson.get("dateOfBirth")))) );
            java.sql.Date dateOfBirth = new java.sql.Date(Date.from(instant).getTime());
            user.setNgaySinh(dateOfBirth);

            // Set role cho user
            int idRoleRequest = Integer.parseInt(String.valueOf(userJson.get("role")));
            Optional<Quyen> role = roleRepository.findById(idRoleRequest);
            List<Quyen> roles = new ArrayList<>();
            roles.add(role.get());
            user.setDanhSachQuyen(roles);

            // Mã hoá mật khẩu
            if (!(user.getMatKhau() == null)) { // Trường hợp là thêm hoặc thay đổi password
                String encodePassword = passwordEncoder.encode(user.getMatKhau());
                user.setMatKhau(encodePassword);
            } else {
                // Trường hợp cho update không thay đổi password
                Optional<NguoiDung> userTemp = nguoiDungRepository.findById(user.getMaNguoiDung());
                user.setMatKhau(userTemp.get().getMatKhau());
            }

            // Set avatar
            String avatar = (formatStringByJson(String.valueOf((userJson.get("avatar")))));
            if (avatar.length() > 500) {
                MultipartFile avatarFile = Base64ToMultipartFileConverter.convert(avatar);
                String avatarURL = uploadImageService.uploadImage(avatarFile, "User_" + user.getMaNguoiDung());
                user.setAvatar(avatarURL);
            }

            nguoiDungRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("thành công");
    }

    @Override
    public ResponseEntity<?> delete(int id) {
        try{
            Optional<NguoiDung> user = nguoiDungRepository.findById(id);

            if (user.isPresent()) {
                String imageUrl = user.get().getAvatar();

                if (imageUrl != null) {
                    uploadImageService.deleteImage(imageUrl);
                }

                nguoiDungRepository.deleteById(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("thành công");
    }

    @Override
    public ResponseEntity<?> changePassword(JsonNode userJson) {
        try{
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(userJson.get("idUser"))));
            String newPassword = formatStringByJson(String.valueOf(userJson.get("newPassword")));
//            System.out.println(idUser);
//            System.out.println(newPassword);
            Optional<NguoiDung> user = nguoiDungRepository.findById(idUser);
            user.get().setMatKhau(passwordEncoder.encode(newPassword));
            nguoiDungRepository.save(user.get());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> changeAvatar(JsonNode userJson) {
        try{
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(userJson.get("idUser"))));
            String dataAvatar = formatStringByJson(String.valueOf(userJson.get("avatar")));

            Optional<NguoiDung> user = nguoiDungRepository.findById(idUser);

            // Xoá đi ảnh trước đó trong cloudinary
            if (user.get().getAvatar().length() > 0) {
                uploadImageService.deleteImage(user.get().getAvatar());
            }

            if (Base64ToMultipartFileConverter.isBase64(dataAvatar)) {
                MultipartFile avatarFile = Base64ToMultipartFileConverter.convert(dataAvatar);
                String avatarUrl = uploadImageService.uploadImage(avatarFile, "User_" + idUser);
                user.get().setAvatar(avatarUrl);
            }

            NguoiDung newUser =  nguoiDungRepository.save(user.get());
            final String jwtToken = jwtService.generateToken(newUser.getTenDangNhap());
            return ResponseEntity.ok(new JwtResponse(jwtToken));

        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> updateProfile(JsonNode userJson) {
        try{
            NguoiDung userRequest = objectMapper.treeToValue(userJson, NguoiDung.class);
            Optional<NguoiDung> user = nguoiDungRepository.findById(userRequest.getMaNguoiDung());

            user.get().setHoDem(userRequest.getHoDem());
            user.get().setTen(userRequest.getTen());
            // Format lại ngày sinh
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Instant instant = Instant.from(formatter.parse(formatStringByJson(String.valueOf(userJson.get("dateOfBirth")))));
            java.sql.Date dateOfBirth = new java.sql.Date(Date.from(instant).getTime());

            user.get().setNgaySinh(dateOfBirth);
            user.get().setSoDienThoai(userRequest.getSoDienThoai());
            user.get().setDiaChiGiaoHang(userRequest.getDiaChiGiaoHang());
            user.get().setGioiTinh(userRequest.getGioiTinh());

            nguoiDungRepository.save(user.get());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> forgotPassword(JsonNode jsonNode) {
        try{
            NguoiDung user = nguoiDungRepository.findByEmail(formatStringByJson(jsonNode.get("email").toString()));

            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // Đổi mật khẩu cho user
            String passwordTemp = generateTemporaryPassword();
            user.setMatKhau(passwordEncoder.encode(passwordTemp));
            nguoiDungRepository.save(user);

            // Gửi email đê nhận mật khẩu
            sendEmailForgotPassword(user.getEmail(), passwordTemp);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    private String generateActivationCode() {
        return UUID.randomUUID().toString();
    }

    private void sendEmailActivation(String email, String activationCode) {
//        String endpointFE = "https://d451-203-205-27-198.ngrok-free.app";
        String endpointFE = "http://localhost:3000";
        String url = endpointFE + "/active/" + email + "/" + activationCode;
        String subject = "Kích hoạt tài khoản";
        String message = "Cảm ơn bạn đã là thành viên của chúng tôi. Vui lòng kích hoạt tài khoản!: <br/> Mã kích hoạt: <strong>"+ activationCode +"<strong/>";
        message += "<br/> Click vào đây để <a href="+ url +">kích hoạt</a>";
        try {
            emailService.sendMessage("laihung1412@gmail.com", email, subject, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEmailForgotPassword(String email, String password) {
        String subject = "Reset mật khẩu";
        String message = "Mật khẩu tạm thời của bạn là: <strong>" + password + "</strong>";
        message += "<br/> <span>Vui lòng đăng nhập và đổi lại mật khẩu của bạn</span>";
        try {
            emailService.sendMessage("laihung1412@gmail.com", email, subject, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateTemporaryPassword() {
        return RandomStringUtils.random(10, true, true);
    }

    public ResponseEntity<?> activeAccount(String email, String activationCode) {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email);
        if (nguoiDung == null) {
            return ResponseEntity.badRequest().body(new ThongBao("Người dùng không tồn tại!"));
        }
        if (nguoiDung.isDaKichHoat()) {
            return ResponseEntity.badRequest().body(new ThongBao("Tài khoản đã được kích hoạt"));
        }
        if (nguoiDung.getMaKichHoat().equals(activationCode)) {
            nguoiDung.setDaKichHoat(true);
            nguoiDungRepository.save(nguoiDung);
        } else {
            return ResponseEntity.badRequest().body(new ThongBao("Mã kích hoạt không chính xác!"));
        }
        return ResponseEntity.ok("Kích hoạt thành công");
    }
    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
