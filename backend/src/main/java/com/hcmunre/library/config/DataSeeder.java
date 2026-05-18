package com.hcmunre.library.config;

import com.hcmunre.library.entity.*;
import com.hcmunre.library.enums.*;
import com.hcmunre.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

        private final NguoiDungRepository nguoiDungRepository;
        private final SachRepository sachRepository;
        private final NhaXuatBanRepository nhaXuatBanRepository;
        private final TacGiaRepository tacGiaRepository;
        private final TheLoaiRepository theLoaiRepository;
        private final CuonSachRepository cuonSachRepository;
        private final PhieuMuonRepository phieuMuonRepository;
        private final ChiTietPhieuMuonRepository chiTietPhieuMuonRepository;
        private final DatChoRepository datChoRepository;
        private final PhieuPhatRepository phieuPhatRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                seedUsers();
                if (sachRepository.count() == 0) {
                        seedCatalogData();
                }
        }

        private void seedUsers() {
                if (nguoiDungRepository.count() == 0) {
                        NguoiDung admin = NguoiDung.builder()
                                        .hoDem("Nguyễn Văn")
                                        .ten("Admin")
                                        .email("admin@library.edu.vn")
                                        .soDienThoai("0900000001")
                                        .matKhau(passwordEncoder.encode("Admin@123456"))
                                        .vaiTro(VaiTro.QUAN_TRI_VIEN)
                                        .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                                        .gioiTinh(GioiTinh.NAM)
                                        .build();

                        NguoiDung thuThu = NguoiDung.builder()
                                        .hoDem("Trần Thị")
                                        .ten("Thư Viện")
                                        .email("thuvien@library.edu.vn")
                                        .soDienThoai("0900000002")
                                        .matKhau(passwordEncoder.encode("ThuVien@123456"))
                                        .vaiTro(VaiTro.THU_THU)
                                        .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                                        .gioiTinh(GioiTinh.NU)
                                        .build();

                        NguoiDung docGia = NguoiDung.builder()
                                        .hoDem("Lê Văn")
                                        .ten("Độc Giả")
                                        .email("docgia@library.edu.vn")
                                        .soDienThoai("0900000003")
                                        .matKhau(passwordEncoder.encode("DocGia@123456"))
                                        .vaiTro(VaiTro.DOC_GIA)
                                        .trangThai(TrangThaiNguoiDung.HOAT_DONG)
                                        .gioiTinh(GioiTinh.NAM)
                                        .ngaySinh(LocalDate.of(2000, 1, 15))
                                        .build();

                        nguoiDungRepository.saveAll(Arrays.asList(admin, thuThu, docGia));
                        log.info(
                                        "✅ Đã tạo 3 tài khoản test: admin@library.edu.vn | thuvien@library.edu.vn | docgia@library.edu.vn");
                } else {
                        log.info("ℹ️ Bảng người dùng đã có dữ liệu — bỏ qua seed users");
                }
        }

        private void seedCatalogData() {
                // ===== THỂ LOẠI =====
                TheLoai vanHoc = TheLoai.builder().tenTheLoai("Văn học").moTa("Sách văn học trong và ngoài nước")
                                .build();
                TheLoai khoaHoc = TheLoai.builder().tenTheLoai("Khoa học").moTa("Sách khoa học tự nhiên và xã hội")
                                .build();
                TheLoai lichSu = TheLoai.builder().tenTheLoai("Lịch sử").moTa("Sách lịch sử Việt Nam và thế giới")
                                .build();
                TheLoai kyThuat = TheLoai.builder().tenTheLoai("Kỹ thuật - Công nghệ")
                                .moTa("Sách lập trình, kỹ thuật, CNTT")
                                .build();
                TheLoai thieuNhi = TheLoai.builder().tenTheLoai("Thiếu nhi").moTa("Sách dành cho thiếu nhi").build();
                TheLoai kinhTe = TheLoai.builder().tenTheLoai("Kinh tế").moTa("Sách kinh tế, tài chính, quản trị")
                                .build();
                TheLoai ngoaiNgu = TheLoai.builder().tenTheLoai("Ngoại ngữ").moTa("Sách học ngoại ngữ").build();
                theLoaiRepository.saveAll(Arrays.asList(vanHoc, khoaHoc, lichSu, kyThuat, thieuNhi, kinhTe, ngoaiNgu));
                log.info("✅ Đã seed {} thể loại", 7);

                // ===== NHÀ XUẤT BẢN =====
                NhaXuatBan kimDong = NhaXuatBan.builder()
                                .tenNhaXuatBan("Kim Đồng").diaChi("55 Quang Trung, Hà Nội")
                                .soDienThoai("0244000001").email("kimdong@nxb.vn").build();
                NhaXuatBan nxbTre = NhaXuatBan.builder()
                                .tenNhaXuatBan("NXB Trẻ").diaChi("161B Lý Chính Thắng, TP.HCM")
                                .soDienThoai("0284000001").email("nxbtre@nxb.vn").build();
                NhaXuatBan giaoDoc = NhaXuatBan.builder()
                                .tenNhaXuatBan("Giáo Dục Việt Nam").diaChi("81 Trần Hưng Đạo, Hà Nội")
                                .soDienThoai("0244000002").email("giaoduc@nxb.vn").build();
                NhaXuatBan khoaHocKT = NhaXuatBan.builder()
                                .tenNhaXuatBan("Khoa Học và Kỹ Thuật").diaChi("70 Trần Hưng Đạo, Hà Nội")
                                .soDienThoai("0244000003").email("khkt@nxb.vn").build();
                NhaXuatBan theGioi = NhaXuatBan.builder()
                                .tenNhaXuatBan("Thế Giới").diaChi("46 Trần Hưng Đạo, Hà Nội")
                                .soDienThoai("0244000004").email("thegioi@nxb.vn").build();
                nhaXuatBanRepository.saveAll(Arrays.asList(kimDong, nxbTre, giaoDoc, khoaHocKT, theGioi));
                log.info("✅ Đã seed {} nhà xuất bản", 5);

                // ===== TÁC GIẢ =====
                TacGia nguyenNhatAnh = TacGia.builder().hoDem("Nguyễn Nhật").ten("Ánh")
                                .tieuSu("Nhà văn Việt Nam nổi tiếng với các tác phẩm thiếu nhi").build();
                TacGia toHoai = TacGia.builder().hoDem("Tô").ten("Hoài")
                                .tieuSu("Nhà văn Việt Nam, tác giả Dế Mèn Phiêu Lưu Ký")
                                .build();
                TacGia nguyenDu = TacGia.builder().hoDem("Nguyễn").ten("Du")
                                .tieuSu("Đại thi hào dân tộc Việt Nam, tác giả Truyện Kiều").build();
                TacGia fujikoFFujio = TacGia.builder().hoDem("Fujiko F.").ten("Fujio")
                                .tieuSu("Họa sĩ người Nhật Bản, tác giả bộ truyện tranh Doraemon").build();
                TacGia namQuocChanh = TacGia.builder().hoDem("Nam Quốc").ten("Chánh")
                                .tieuSu("Tác giả sách kỹ năng và kinh tế")
                                .build();
                TacGia danBrown = TacGia.builder().hoDem("Dan").ten("Brown")
                                .tieuSu("Nhà văn người Mỹ nổi tiếng với các tiểu thuyết bí ẩn").build();
                tacGiaRepository.saveAll(
                                Arrays.asList(nguyenNhatAnh, toHoai, nguyenDu, fujikoFFujio, namQuocChanh, danBrown));
                log.info("✅ Đã seed {} tác giả", 6);

                // ===== SÁCH =====
                Sach sachToi = Sach.builder()
                                .tenSach("Tôi Thấy Hoa Vàng Trên Cỏ Xanh")
                                .maIsbn("9786041040267")
                                .namXuatBan(2010)
                                .soTrang(382)
                                .moTa("Câu chuyện tuổi thơ đầy xúc cảm của Nguyễn Nhật Ánh về tình anh em và những mùa hè ở làng quê.")
                                .giaTien(89000.0)
                                .donGiaPhatTheoNgay(5000.0)
                                .nhaXuatBan(nxbTre)
                                .danhSachTacGia(Arrays.asList(nguyenNhatAnh))
                                .danhSachTheLoai(Arrays.asList(vanHoc, thieuNhi))
                                .build();

                Sach sachMatTinhYeu = Sach.builder()
                                .tenSach("Mắt Biếc")
                                .maIsbn("9786041091498")
                                .namXuatBan(2016)
                                .soTrang(304)
                                .moTa("Câu chuyện tình yêu buồn bã, đẹp như một bài thơ, kể về chàng trai si tình với đôi mắt biếc của Hà Lan.")
                                .giaTien(75000.0)
                                .donGiaPhatTheoNgay(5000.0)
                                .nhaXuatBan(nxbTre)
                                .danhSachTacGia(Arrays.asList(nguyenNhatAnh))
                                .danhSachTheLoai(Arrays.asList(vanHoc))
                                .build();

                Sach sachDeDe = Sach.builder()
                                .tenSach("Dế Mèn Phiêu Lưu Ký")
                                .maIsbn("9786041020207")
                                .namXuatBan(2019)
                                .soTrang(196)
                                .moTa("Câu chuyện phiêu lưu ly kỳ của chú Dế Mèn nhỏ bé qua ngòi bút tài hoa của Tô Hoài.")
                                .giaTien(65000.0)
                                .donGiaPhatTheoNgay(4000.0)
                                .nhaXuatBan(kimDong)
                                .danhSachTacGia(Arrays.asList(toHoai))
                                .danhSachTheLoai(Arrays.asList(thieuNhi, vanHoc))
                                .build();

                Sach sachTruyenKieu = Sach.builder()
                                .tenSach("Truyện Kiều")
                                .maIsbn("9786049450012")
                                .namXuatBan(2018)
                                .soTrang(320)
                                .moTa("Kiệt tác văn học chữ Nôm của đại thi hào Nguyễn Du, kể về cuộc đời đầy bi kịch của Thúy Kiều.")
                                .giaTien(95000.0)
                                .donGiaPhatTheoNgay(5000.0)
                                .nhaXuatBan(giaoDoc)
                                .danhSachTacGia(Arrays.asList(nguyenDu))
                                .danhSachTheLoai(Arrays.asList(vanHoc, lichSu))
                                .build();

                Sach sachDoraemon = Sach.builder()
                                .tenSach("Doraemon - Tập 1")
                                .maIsbn("9786041047433")
                                .namXuatBan(2021)
                                .soTrang(192)
                                .moTa("Bộ truyện tranh kinh điển về chú mèo máy Doraemon đến từ tương lai với chiếc túi thần kỳ.")
                                .giaTien(55000.0)
                                .donGiaPhatTheoNgay(3000.0)
                                .nhaXuatBan(kimDong)
                                .danhSachTacGia(Arrays.asList(fujikoFFujio))
                                .danhSachTheLoai(Arrays.asList(thieuNhi))
                                .build();

                Sach sachLapTrinh = Sach.builder()
                                .tenSach("Lập Trình Java Cơ Bản")
                                .maIsbn("9786045615560")
                                .namXuatBan(2020)
                                .soTrang(450)
                                .moTa("Giáo trình lập trình Java từ cơ bản đến nâng cao, phù hợp cho sinh viên CNTT.")
                                .giaTien(120000.0)
                                .donGiaPhatTheoNgay(6000.0)
                                .nhaXuatBan(khoaHocKT)
                                .danhSachTacGia(Arrays.asList(namQuocChanh))
                                .danhSachTheLoai(Arrays.asList(kyThuat))
                                .build();

                Sach sachDaVinci = Sach.builder()
                                .tenSach("Mật Mã Da Vinci")
                                .maIsbn("9786041149373")
                                .namXuatBan(2019)
                                .soTrang(576)
                                .moTa("Tiểu thuyết hành động - bí ẩn nổi tiếng toàn cầu của Dan Brown về một bí mật tôn giáo chấn động.")
                                .giaTien(145000.0)
                                .donGiaPhatTheoNgay(7000.0)
                                .nhaXuatBan(theGioi)
                                .danhSachTacGia(Arrays.asList(danBrown))
                                .danhSachTheLoai(Arrays.asList(vanHoc))
                                .build();

                Sach sachKinhTe = Sach.builder()
                                .tenSach("Tư Duy Làm Giàu")
                                .maIsbn("9786041087804")
                                .namXuatBan(2022)
                                .soTrang(280)
                                .moTa("Cuốn sách về tư duy tài chính và cách thức xây dựng sự nghiệp thành công.")
                                .giaTien(98000.0)
                                .donGiaPhatTheoNgay(5000.0)
                                .nhaXuatBan(nxbTre)
                                .danhSachTacGia(Arrays.asList(namQuocChanh))
                                .danhSachTheLoai(Arrays.asList(kinhTe))
                                .build();

                Sach sachLichSu = Sach.builder()
                                .tenSach("Lịch Sử Việt Nam Qua Các Thời Kỳ")
                                .maIsbn("9786049450029")
                                .namXuatBan(2021)
                                .soTrang(512)
                                .moTa("Toàn cảnh lịch sử dân tộc Việt Nam từ thời dựng nước đến hiện đại.")
                                .giaTien(135000.0)
                                .donGiaPhatTheoNgay(6000.0)
                                .nhaXuatBan(giaoDoc)
                                .danhSachTacGia(Arrays.asList(namQuocChanh))
                                .danhSachTheLoai(Arrays.asList(lichSu))
                                .build();

                Sach sachTiengAnh = Sach.builder()
                                .tenSach("English Grammar In Use")
                                .maIsbn("9781316631713")
                                .namXuatBan(2019)
                                .soTrang(394)
                                .moTa("Sách ngữ pháp tiếng Anh toàn diện của Raymond Murphy, phù hợp cho trình độ trung cấp.")
                                .giaTien(185000.0)
                                .donGiaPhatTheoNgay(8000.0)
                                .nhaXuatBan(theGioi)
                                .danhSachTacGia(Arrays.asList(danBrown))
                                .danhSachTheLoai(Arrays.asList(ngoaiNgu))
                                .build();

                List<Sach> danhSachSach = sachRepository.saveAll(Arrays.asList(
                                sachToi, sachMatTinhYeu, sachDeDe, sachTruyenKieu, sachDoraemon,
                                sachLapTrinh, sachDaVinci, sachKinhTe, sachLichSu, sachTiengAnh));
                log.info("✅ Đã seed {} cuốn sách", danhSachSach.size());

                // ===== CUỐN SÁCH =====
                int stt = 1;
                for (Sach sach : danhSachSach) {
                        for (int i = 1; i <= 3; i++) {
                                String maVach = String.format("CS-%03d-%04d", sach.getMaSach(), stt++);
                                TrangThaiCuonSach trangThai = (i == 1 && stt % 5 == 0) ? TrangThaiCuonSach.DANG_MUON
                                                : TrangThaiCuonSach.SAN_SANG;
                                CuonSach cuonSach = CuonSach.builder()
                                                .maVach(maVach)
                                                .trangThai(trangThai)
                                                .tinhTrangVatLy(TinhTrangVatLy.BINH_THUONG)
                                                .sach(sach)
                                                .build();
                                cuonSachRepository.save(cuonSach);
                        }
                }
                log.info("✅ Đã seed cuốn sách cho mỗi đầu sách");

                // ===== PHIẾU MƯỢN MẪU =====
                NguoiDung docGiaSeed = nguoiDungRepository.findByEmail("docgia@library.edu.vn").orElse(null);
                if (docGiaSeed != null) {
                        // Phiếu mượn đang mượn
                        List<CuonSach> cuonSachSanSang = cuonSachRepository.findBySach_MaSachAndTrangThai(
                                        sachToi.getMaSach(), TrangThaiCuonSach.SAN_SANG);
                        if (!cuonSachSanSang.isEmpty()) {
                                CuonSach cuon = cuonSachSanSang.get(0);
                                cuon.setTrangThai(TrangThaiCuonSach.DANG_MUON);
                                cuonSachRepository.save(cuon);

                                PhieuMuon phieuDangMuon = PhieuMuon.builder()
                                                .nguoiDung(docGiaSeed)
                                                .ngayMuon(LocalDateTime.now().minusDays(7))
                                                .trangThaiPhieu(TrangThaiPhieuMuon.CHUA_HOAN_TAT)
                                                .build();
                                phieuDangMuon = phieuMuonRepository.save(phieuDangMuon);

                                ChiTietPhieuMuon chiTietDangMuon = ChiTietPhieuMuon.builder()
                                                .phieuMuon(phieuDangMuon)
                                                .cuonSach(cuon)
                                                .tinhTrangLucMuon(TinhTrangVatLy.BINH_THUONG)
                                                .donGiaPhatApDung(cuon.getSach().getDonGiaPhatTheoNgay())
                                                .trangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.DANG_MUON)
                                                .hanTraBanDau(LocalDateTime.now().plusDays(7))
                                                .hanTraHienTai(LocalDateTime.now().plusDays(7))
                                                .soLanGiaHan(0)
                                                .build();
                                chiTietPhieuMuonRepository.save(chiTietDangMuon);

                                // Phiếu mượn quá hạn
                                List<CuonSach> cuonSachSanSang2 = cuonSachRepository.findBySach_MaSachAndTrangThai(
                                                sachMatTinhYeu.getMaSach(), TrangThaiCuonSach.SAN_SANG);
                                if (!cuonSachSanSang2.isEmpty()) {
                                        CuonSach cuonQuaHan = cuonSachSanSang2.get(0);
                                        cuonQuaHan.setTrangThai(TrangThaiCuonSach.DANG_MUON);
                                        cuonSachRepository.save(cuonQuaHan);

                                        PhieuMuon phieuQuaHan = PhieuMuon.builder()
                                                        .nguoiDung(docGiaSeed)
                                                        .ngayMuon(LocalDateTime.now().minusDays(30))
                                                        .trangThaiPhieu(TrangThaiPhieuMuon.CHUA_HOAN_TAT)
                                                        .build();
                                        phieuQuaHan = phieuMuonRepository.save(phieuQuaHan);

                                        ChiTietPhieuMuon chiTietQuaHan = ChiTietPhieuMuon.builder()
                                                        .phieuMuon(phieuQuaHan)
                                                        .cuonSach(cuonQuaHan)
                                                        .tinhTrangLucMuon(TinhTrangVatLy.BINH_THUONG)
                                                        .donGiaPhatApDung(cuonQuaHan.getSach()
                                                                        .getDonGiaPhatTheoNgay() != null
                                                                                        ? cuonQuaHan.getSach()
                                                                                                        .getDonGiaPhatTheoNgay()
                                                                                        : 5000.0)
                                                        .trangThaiChiTietPhieuMuon(TrangThaiChiTietPhieuMuon.QUA_HAN)
                                                        .hanTraBanDau(LocalDateTime.now().minusDays(15))
                                                        .hanTraHienTai(LocalDateTime.now().minusDays(15))
                                                        .soLanGiaHan(0)
                                                        .build();
                                        chiTietQuaHan = chiTietPhieuMuonRepository.save(chiTietQuaHan);

                                        // Phiếu phạt cho phiếu quá hạn
                                        PhieuPhat phieuPhat = PhieuPhat.builder()
                                                        .chiTietPhieuMuon(chiTietQuaHan)
                                                        .soTienPhat(75000.0)
                                                        .lyDoPhat("Trả sách trễ 15 ngày")
                                                        .trangThaiThanhToan(TrangThaiThanhToan.CHUA_THANH_TOAN)
                                                        .build();
                                        phieuPhatRepository.save(phieuPhat);
                                }

                                // Đặt chỗ mẫu
                                DatCho datCho = DatCho.builder()
                                                .nguoiDung(docGiaSeed)
                                                .sach(sachLapTrinh)
                                                .thoiGianDatCho(LocalDateTime.now())
                                                .hanGiuCho(LocalDateTime.now().plusDays(3))
                                                .trangThai(TrangThaiDatCho.DANG_CHO)
                                                .build();
                                datChoRepository.save(datCho);
                                log.info("✅ Đã seed dữ liệu phiếu mượn và đặt chỗ mẫu");
                        }
                }
        }
}
