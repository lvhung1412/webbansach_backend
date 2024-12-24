package vn.lvhung.webbansach_backend.service.sach;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.lvhung.webbansach_backend.dao.HinhAnhRepository;
import vn.lvhung.webbansach_backend.dao.SachRepository;
import vn.lvhung.webbansach_backend.dao.TheLoaiRepository;
import vn.lvhung.webbansach_backend.entity.HinhAnh;
import vn.lvhung.webbansach_backend.entity.Sach;
import vn.lvhung.webbansach_backend.entity.TheLoai;
import vn.lvhung.webbansach_backend.service.UploadImage.UploadImageService;
import vn.lvhung.webbansach_backend.service.utils.Base64ToMultipartFileConverter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class SachServiceImpl implements SachService{
    private final ObjectMapper objectMapper;

    @Autowired
    private SachRepository sachRepository;

    @Autowired
    private TheLoaiRepository theLoaiRepository;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired
    private UploadImageService uploadImageService;

    public SachServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<?> save(JsonNode sachJson) {
        try{
            Sach sach = objectMapper.treeToValue(sachJson, Sach.class);

            // Lưu thể loại sách
            List<Integer> danhSachMaTheLoai = objectMapper.readValue(sachJson.get("idGenres").traverse(), new TypeReference<List<Integer>>() {
            });

            List<TheLoai> danhSachTheLoai = new ArrayList<>();
            for (int maTheLoai: danhSachMaTheLoai){
                Optional<TheLoai> theLoai = theLoaiRepository.findById(maTheLoai);
                danhSachTheLoai.add(theLoai.get());
            }
            sach.setDanhSachTheLoai(danhSachTheLoai);

            // Lưu trước để lấy id sach đặt tên cho ảnh
            Sach sachMoi = sachRepository.save(sach);

            // Lưu thumbnail cho ảnh
            String dataThumbnail = formatStringByJson(String.valueOf((sachJson.get("thumbnail"))));

            HinhAnh thumbnail = new HinhAnh();
            thumbnail.setSach(sachMoi);
            thumbnail.setLa_thumbnail(true);
            MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
            String thumbnailUrl = uploadImageService.uploadImage(multipartFile, "Book_"+ sachMoi.getMaSach());
            thumbnail.setDuongDan(thumbnailUrl);

            List<HinhAnh> danhSachHinhAnh = new ArrayList<>();
            danhSachHinhAnh.add(thumbnail);

            // lưu những ảnh có liên quan
            String dataRelatedImg = formatStringByJson(String.valueOf((sachJson.get("relatedImg"))));
            List<String> arrDataRelatedImg = objectMapper.readValue(sachJson.get("relatedImg").traverse(), new TypeReference<List<String>>() {
            });

            for(int i = 0; i< arrDataRelatedImg.size(); i++){
                String img = arrDataRelatedImg.get(i);
                HinhAnh hinhAnh = new HinhAnh();
                hinhAnh.setSach(sachMoi);
                hinhAnh.setLa_thumbnail(false);
                MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                String imgURL = uploadImageService.uploadImage(relatedImgFile,"Book_" + sachMoi.getMaSach()+"."+i);
                hinhAnh.setDuongDan(imgURL);
                danhSachHinhAnh.add(hinhAnh);
            }

            sachMoi.setDanhSachHinhAnh(danhSachHinhAnh);

            // Cập nhật lại ảnh
            sachRepository.save(sachMoi);

            return  ResponseEntity.ok("success!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> update(JsonNode sachJson) {
        try {
            Sach sach = objectMapper.treeToValue(sachJson,Sach.class);
            List<HinhAnh> danhSachHinhAnh = hinhAnhRepository.findHinhAnhBySach(sach);

            // lưu thể loại của sách
            List<Integer> danhSachMaTheLoai = objectMapper.readValue(sachJson.get("idGenres").traverse(), new TypeReference<List<Integer>>() {
            });
            List<TheLoai> danhSachTheLoai = new ArrayList<>();
            for (int maTheLoai : danhSachMaTheLoai){
                Optional<TheLoai> theLoai = theLoaiRepository.findById(maTheLoai);
                danhSachTheLoai.add(theLoai.get());
            }
            sach.setDanhSachTheLoai(danhSachTheLoai);

            // Kiểm tra xem thumbnail có thay đổi khoong
            String dataThumbnail = formatStringByJson(String.valueOf((sachJson.get("thumbnail"))));
            if (Base64ToMultipartFileConverter.isBase64(dataThumbnail)) {
                for (HinhAnh hinhAnh : danhSachHinhAnh) {
                    if (hinhAnh.isLa_thumbnail()) {
//                        image.setDataImage(dataThumbnail);
                        MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
                        String thumbnailUrl = uploadImageService.uploadImage(multipartFile, "Book_" + sach.getMaSach());
                        hinhAnh.setDuongDan(thumbnailUrl);
                        hinhAnhRepository.save(hinhAnh);
                        break;
                    }
                }
            }

            Sach sachMoi = sachRepository.save(sach);

            // kiểm tra ảnh có liên quan
            List<String> arrDataRelatedImg = objectMapper.readValue(sachJson.get("relatedImg").traverse(), new TypeReference<List<String>>() {
            });

            // xem có xóa tất cả ở bên FE không?
            boolean isCheckDelete = true;

            for(String img : arrDataRelatedImg){
                if(!Base64ToMultipartFileConverter.isBase64(img)){
                    isCheckDelete = false;
                }
            }

            // nếu xóa hết tất cả
            if(isCheckDelete){
                hinhAnhRepository.deleteHinhAnhWithFalseThumbnailByMaSach(sachMoi.getMaSach());
                HinhAnh thumnailTemp = danhSachHinhAnh.get(0);
                danhSachHinhAnh.clear();
                danhSachHinhAnh.add(thumnailTemp);
                for(int i =0 ; i< arrDataRelatedImg.size() ;i++){
                    String img = arrDataRelatedImg.get(i);
                    HinhAnh hinhAnh = new HinhAnh();
                    hinhAnh.setSach(sachMoi);

                    hinhAnh.setLa_thumbnail(false);
                    MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                    String imURL = uploadImageService.uploadImage(relatedImgFile,"Book_"+sachMoi.getMaSach()+"."+i);
                    hinhAnh.setDuongDan(imURL);
                    danhSachHinhAnh.add(hinhAnh);
                }
            }else{
                // nếu không xóa hết tất cả ( giữ nguyên ảnh hoặc thêm ảnh vào)
                for(int i =0 ; i< arrDataRelatedImg.size() ;i++){
                    String img = arrDataRelatedImg.get(i);
                    if(Base64ToMultipartFileConverter.isBase64(img)){
                        HinhAnh hinhAnh = new HinhAnh();
                        hinhAnh.setSach(sachMoi);

                        hinhAnh.setLa_thumbnail(false);
                        MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                        String imURL = uploadImageService.uploadImage(relatedImgFile,"Book_"+sachMoi.getMaSach()+"."+i);
                        hinhAnh.setDuongDan(imURL);
                        danhSachHinhAnh.add(hinhAnh);
                    }
                }
            }

            sachMoi.setDanhSachHinhAnh(danhSachHinhAnh);
            // Cập nhật lại ảnh
            sachRepository.save(sachMoi);

            return ResponseEntity.ok("Success!");
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public long getTotalSach() {
        return sachRepository.count();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }

}
