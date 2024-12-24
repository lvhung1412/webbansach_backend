package vn.lvhung.webbansach_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="chi_tiet_don_hang")
public class ChiTietDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ma_chi_tiet_don_hang")
    private long maChiTietDonHang;
    @Column(name="so_luong")
    private int soLuong;
    @Column(name="gia_ban")
    private double giaBan;
    @Column(name = "da_danh_gia")
    private boolean daDanhGia; // đã đánh giá chưa

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_sach", nullable = false)
    private Sach sach;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_don_hang", nullable = false)
    private DonHang donHang;
}
