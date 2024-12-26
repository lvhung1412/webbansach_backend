package vn.lvhung.webbansach_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Entity
@Data
@Table(name = "don_hang")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_don_hang")
    private int maDonHang;

    @Column(name = "ngay_tao")
    private Date ngayTao;
    @Column(name = "dia_chi_nhan_hang", length = 512)
    private String diaChiNhanHang;
    @Column(name = "so_dien_thoai")
    private String soDienThoai;
    @Column(name = "ho_ten_nguoi_nhan")
    private String hoTenNguoiNhan; // Họ và tên của khách hàng (tuỳ chỉnh)
    @Column(name = "tong_tien_san_pham")
    private double tongTienSanPham;
    @Column(name = "chi_phi_giao_hang")
    private double chiPhiGiaoHang;
    @Column(name = "chi_phi_thanh_toan")
    private double chiPhiThanhToan;
    @Column(name = "tong_tien")
    private double tongTien;
    @Column(name = "trang_thai")
    private String trangThai; // Trạng thái của đơn hàng
    @Column(name = "ghi_chu")
    private String ghiChu;

    @OneToMany(mappedBy = "donHang", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChiTietDonHang> danhSachChiTietDonHang;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_hinh_thuc_thanh_toan")
    private ThanhToan thanhToan;

    @ManyToOne(cascade = {
            CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH
    })
    @JoinColumn(name = "ma_hinh_thuc_giao_hang")
    private VanChuyen vanChuyen;
}