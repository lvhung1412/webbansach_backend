package vn.lvhung.webbansach_backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import vn.lvhung.webbansach_backend.entity.NguoiDung;

public interface UserSecurityService extends UserDetailsService {

    public NguoiDung findByUsername(String tenDangNhap);
}
