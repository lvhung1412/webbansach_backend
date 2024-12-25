package vn.lvhung.webbansach_backend.service.giohang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.lvhung.webbansach_backend.dao.GioHangRepository;
import vn.lvhung.webbansach_backend.dao.NguoiDungRepository;
import vn.lvhung.webbansach_backend.entity.GioHang;
import vn.lvhung.webbansach_backend.entity.NguoiDung;
import vn.lvhung.webbansach_backend.entity.Sach;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GioHangServiceImpl implements GioHangService{
    private final ObjectMapper objectMapper;
    @Autowired
    public NguoiDungRepository nguoiDungRepository;
    @Autowired
    public GioHangRepository gioHangRepository;
    public GioHangServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<?> save(JsonNode jsonData) {
        try{
            int idUser = 0;

            // Danh sách item của data vừa truyền
            List<GioHang> cartItemDataList = new ArrayList<>();
            for (JsonNode jsonDatum : jsonData) {
                GioHang cartItemData = objectMapper.treeToValue(jsonDatum, GioHang.class);
                idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonDatum.get("idUser"))));
                cartItemDataList.add(cartItemData);
            }
            Optional<NguoiDung> user = nguoiDungRepository.findById(idUser);
            // Danh sách item của user
            List<GioHang> cartItemList = user.get().getListCartItems();

            // Lặp qua từng item và xử lý
            for (GioHang cartItemData : cartItemDataList) {
                boolean isHad = false;
                for (GioHang cartItem : cartItemList) {
                    // Nếu trong cart của user có item đó rồi thì sẽ update lại quantity
                    if (cartItem.getSach().getMaSach() == cartItemData.getSach().getMaSach()) {
                        cartItem.setSoLuong(cartItem.getSoLuong() + cartItemData.getSoLuong());
                        isHad = true;
                        break;
                    }
                }
                // Nếu chưa có thì thêm mới item đó
                if (!isHad) {
                    GioHang cartItem = new GioHang();
                    cartItem.setNguoiDung(user.get());
                    cartItem.setSoLuong(cartItemData.getSoLuong());
                    cartItem.setSach(cartItemData.getSach());
                    cartItemList.add(cartItem);
                }
            }
            user.get().setListCartItems(cartItemList);
            NguoiDung newUser = nguoiDungRepository.save(user.get());


            if (cartItemDataList.size() == 1) {
                List<GioHang> cartItemListTemp = newUser.getListCartItems();
                return ResponseEntity.ok(cartItemListTemp.get(cartItemList.size() - 1).getMaGioHang());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }

    @Override
    public ResponseEntity<?> update(JsonNode jsonData) {
        try{
            int idCart = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idCart"))));
            int quantity = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("quantity"))));
            Optional<GioHang> cartItem = gioHangRepository.findById(idCart);
            cartItem.get().setSoLuong(quantity);
            gioHangRepository.save(cartItem.get());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
