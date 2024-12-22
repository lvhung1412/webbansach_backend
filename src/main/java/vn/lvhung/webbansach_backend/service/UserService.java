package vn.lvhung.webbansach_backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import vn.lvhung.webbansach_backend.entity.NguoiDung;

public interface UserService extends UserDetailsService {

    public NguoiDung findByUsername(String tenDangNhap);
}
