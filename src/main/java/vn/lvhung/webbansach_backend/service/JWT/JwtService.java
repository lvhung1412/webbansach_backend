package vn.lvhung.webbansach_backend.service.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import vn.lvhung.webbansach_backend.entity.NguoiDung;
import vn.lvhung.webbansach_backend.entity.Quyen;
import vn.lvhung.webbansach_backend.service.UserService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtService {
    public static final String SERECT = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @Autowired
    private UserService userService;

    // Tạo JWT dựa trên tên đang nhập
    public String generateToken(String tenDangNhap){
        Map<String, Object> claims = new HashMap<>();
        NguoiDung nguoiDung = userService.findByUsername(tenDangNhap);

        boolean isAdmin = false;
        boolean isStaff = false;
        boolean isUser = false;

        if(nguoiDung != null && nguoiDung.getDanhSachQuyen().size() > 0){
            List<Quyen> list = nguoiDung.getDanhSachQuyen();
            for(Quyen q: list){
                if(q.getTenQuyen().equals("ADMIN")){
                    isAdmin = true;
                }
                if(q.getTenQuyen().equals("STAFF")){
                    isStaff = true;
                }
                if(q.getTenQuyen().equals("USER")){
                    isUser = true;
                }
            }
        }
        claims.put("isAdmin", isAdmin);
        claims.put("isStaff", isStaff);
        claims.put("isUser", isUser);

        return createToken(claims, tenDangNhap);
    }

    // Tạo JWT với các claim đã chọn
    private  String createToken(Map<String, Object> claims, String tenDangNhap){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(tenDangNhap)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+30*60*1000)) // JWT hết hạn sau 30 phút
                .signWith(SignatureAlgorithm.HS256,getSigneKey())
                .compact();
    }

    // Lấy serect key
    private Key getSigneKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SERECT);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Trích xuất thông tin
    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(getSigneKey()).parseClaimsJws(token).getBody();
    }

    // Trích xuất thông tin cho 1 claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction){
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // Kiểm tra tời gian hết hạn từ JWT
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    // Kiểm tra thời gian hết hạn từ JWT
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Kiểm tra cái JWT đã hết hạn
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // Kiểm tra tính hợp lệ của token
    public Boolean validateToken(String token, UserDetails userDetails){
        final String tenDangNhap = extractUsername(token);
        System.out.println(tenDangNhap);
        return (tenDangNhap.equals(userDetails.getUsername())&&!isTokenExpired(token));
    }
}
