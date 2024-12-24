package vn.lvhung.webbansach_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name ="van_chuyen")
public class VanChuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_van_chuyen")
    private int maVanChuyen;

    @Column(name = "ten_van_chuyen")
    private String tenVanChuyen;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "chi_phi_van_chuyen")
    private double chiPhiVanChuyen;

    @OneToMany(mappedBy = "vanChuyen", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<DonHang> danhSachDonHang;
}
