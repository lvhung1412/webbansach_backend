package vn.lvhung.webbansach_backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import vn.lvhung.webbansach_backend.entity.User;

public interface UserSecurityService extends UserDetailsService {
    public User findByUsername(String username);
}
