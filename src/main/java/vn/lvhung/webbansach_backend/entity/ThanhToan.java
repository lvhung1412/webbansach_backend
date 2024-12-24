package vn.lvhung.webbansach_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="thanh_toan")
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thanh_toan")
    private int maThanhToan;

    @Column(name = "ten_thanh_toan")
    private int tenThanhToan;

    @Column(name = "mo_ta")
    private int moTa;

    @Column(name = "chi_phi_thanh_toan")
    private int chiPhiThanhToan;

    @OneToMany(mappedBy = "thanhToan",fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<DonHang> danhSachDonHang;
}
