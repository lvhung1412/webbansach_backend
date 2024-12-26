package vn.lvhung.webbansach_backend.service.donhang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.lvhung.webbansach_backend.dao.*;
import vn.lvhung.webbansach_backend.entity.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DonHangServiceImpl implements DonHangService{
    private final ObjectMapper objectMapper;
    @Autowired
    private DonHangRepository donHangRepository;
    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private ThanhToanRepository thanhToanRepository;
    public DonHangServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<?> save(JsonNode jsonData) {
        try{

            DonHang orderData = objectMapper.treeToValue(jsonData, DonHang.class);
            orderData.setTongTien(orderData.getTongTienSanPham());
            orderData.setNgayTao(Date.valueOf(LocalDate.now()));
            orderData.setTrangThai("Đang xử lý");

            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idUser"))));
            Optional<NguoiDung> user = nguoiDungRepository.findById(idUser);
            orderData.setNguoiDung(user.get());

            int idPayment = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idPayment"))));
            Optional<ThanhToan> payment = thanhToanRepository.findById(idPayment);
            orderData.setThanhToan(payment.get());

            DonHang newOrder = donHangRepository.save(orderData);

            JsonNode jsonNode = jsonData.get("book");
            for (JsonNode node : jsonNode) {
                int quantity = Integer.parseInt(formatStringByJson(String.valueOf(node.get("quantity"))));
                Sach bookResponse = objectMapper.treeToValue(node.get("book"), Sach.class);
                Optional<Sach> book = sachRepository.findById(bookResponse.getMaSach());
                book.get().setSoLuong(book.get().getSoLuong() - quantity);
                book.get().setSoLuongDaBan(book.get().getSoLuongDaBan() + quantity);

                ChiTietDonHang orderDetail = new ChiTietDonHang();
                orderDetail.setSach(book.get());
                orderDetail.setSoLuong(quantity);
                orderDetail.setDonHang(newOrder);
                orderDetail.setGiaBan(quantity * book.get().getGiaBan());
                orderDetail.setDaDanhGia(false);
                chiTietDonHangRepository.save(orderDetail);
                sachRepository.save(book.get());
            }

            gioHangRepository.deleteGioHangByMaNguoiDung(user.get().getMaNguoiDung());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public ResponseEntity<?> update(JsonNode jsonData) {
        try{
            int idOrder =  Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idOrder"))));
            String status = formatStringByJson(String.valueOf(jsonData.get("status")));
            Optional<DonHang> order = donHangRepository.findById(idOrder);
            order.get().setTrangThai(status);

            // Lấy ra order detail
            if (status.equals("Bị huỷ")) {
                List<ChiTietDonHang> orderDetailList = chiTietDonHangRepository.findChiTietDonHangByDonHang(order.get());
                for (ChiTietDonHang orderDetail : orderDetailList) {
                    Sach bookOrderDetail = orderDetail.getSach();
                    bookOrderDetail.setSoLuongDaBan(bookOrderDetail.getSoLuongDaBan() - orderDetail.getSoLuong());
                    bookOrderDetail.setSoLuong(bookOrderDetail.getSoLuong() + orderDetail.getSoLuong());
                    sachRepository.save(bookOrderDetail);
                }
            }

            donHangRepository.save(order.get());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> cancel(JsonNode jsonData) {
        try{
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idUser"))));
            NguoiDung user = nguoiDungRepository.findById(idUser).get();

            DonHang order = donHangRepository.findFirstByNguoiDungOrderByMaDonHangDesc(user);
            order.setTrangThai("Bị huỷ");

            List<ChiTietDonHang> orderDetailList = chiTietDonHangRepository.findChiTietDonHangByDonHang(order);
            for (ChiTietDonHang orderDetail : orderDetailList) {
                Sach bookOrderDetail = orderDetail.getSach();
                bookOrderDetail.setSoLuongDaBan(bookOrderDetail.getSoLuongDaBan() - orderDetail.getSoLuong());
                bookOrderDetail.setSoLuong(bookOrderDetail.getSoLuong() + orderDetail.getSoLuong());
                sachRepository.save(bookOrderDetail);
            }

            donHangRepository.save(order);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
