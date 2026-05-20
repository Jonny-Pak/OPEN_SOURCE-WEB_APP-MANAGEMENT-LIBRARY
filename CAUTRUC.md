# CẤU TRÚC DỰ ÁN (PROJECT STRUCTURE)

Tài liệu này mô tả chi tiết cấu trúc thư mục và vai trò của từng thành phần trong dự án **Quản lý Thư viện Web App**. Dự án được xây dựng theo mô hình Client-Server phân tách rõ ràng giữa **Backend (Spring Boot)** và **Frontend (Vue.js 3 + TypeScript + Vite)**.

---

## 1. Cấu Trúc Thư Mục Tổng Quan (Overview Structure)


```text
OPEN_SOURCE-WEB_APP-MANAGEMENT-LIBRARY/
├── .git/                      # Thư mục quản lý phiên bản Git
├── .vscode/                   # Cấu hình IDE VS Code dùng chung
├── backend/                   # Mã nguồn phía máy chủ (Spring Boot)
├── frontend/                  # Mã nguồn giao diện người dùng (Vue.js 3)
├── logs/                      # Thư mục lưu trữ nhật ký hoạt động hệ thống (Logs)
├── .gitignore                 # Các tệp tin được cấu hình để Git bỏ qua
├── check_user.js              # Kịch bản bổ trợ (Helper script) trong phát triển
└── README.md                  # Tài liệu hướng dẫn cài đặt và vận hành hệ thống
```







### Chi tiết các phần cốt lõi:
- **`backend/`**: Xử lý toàn bộ logic nghiệp vụ (Business Logic), cung cấp RESTful API, quản lý cơ sở dữ liệu và bảo mật.
- **`frontend/`**: Giao diện Single Page Application (SPA) tương tác với người dùng qua trình duyệt, kết nối API từ Backend.
- **`logs/`**: Chứa thông tin ghi nhật ký (logging) của hệ thống giúp dễ dàng theo dõi và debug lỗi trong môi trường thực tế.

---

## 2. Cấu Trúc Thư Mục Backend (Backend Directory Structure)

`backend/`
├──  `.mvn/`                  # Cấu hình của Maven Wrapper
├──  logs/                    # Thư mục chứa log riêng của Backend
├──  src/                     # Thư mục chứa toàn bộ mã nguồn Spring Boot
│   ├──  main/
│   │   ├──  java/com/hcmunre/library// # Gói mã nguồn chính (Package)
│   │   │   ├──  LibraryBackendApplication.java # File khởi chạy chính của ứng dụng
│   │   │   ├──  config/     # Cấu hình hệ thống (CORS, Security, Bean, Auditing, v.v.)
│   │   │   ├──  controller/ # Lớp tiếp nhận và định tuyến HTTP Request (REST APIs)
│   │   │   ├──  dto/        # Lớp trung gian truyền tải dữ liệu giữa Client và DB
│   │   │   │   ├──  request/    # Các DTO nhận dữ liệu đầu vào (Ví dụ: Đăng nhập, Thêm sách)
│   │   │   │   └──  response/   # Các DTO chuẩn hóa dữ liệu đầu ra trả về Client
│   │   │   ├──  entity/     # Lớp định nghĩa đối tượng dữ liệu ánh xạ xuống Database (JPA Hibernate)
│   │   │   ├──  enums/      # Lưu trữ các Enum định danh trạng thái, phân quyền (Role, Status,...)
│   │   │   ├──  exception/  # Lớp bắt và xử lý các biệt lệ toàn cục (Global Exception Handler)
│   │   │   ├──  repository/ # Interface truy vấn DB, kế thừa Spring Data JPA
│   │   │   ├──  scheduler/  # Xử lý các tác vụ tự động chạy nền theo chu kỳ thời gian
│   │   │   ├──  security/   # Phân hệ bảo mật (Cấu hình JWT Token, Filter, Authentication)
│   │   │   └──  service/    # Nơi chứa các Interface & Lớp triển khai nghiệp vụ chi tiết
│   │   └──  resources/
│   │       └──  application.yaml # Cấu hình DB, Port, JWT, Mail, và các thông số môi trường
│   └──  test/                # Chứa các đoạn mã kiểm thử đơn vị và tích hợp (Unit & Integration tests)
├──  mvnw / mvnw.cmd          # Maven Wrapper dùng để chạy Maven không cần cài đặt cục bộ
└──  pom.xml                  # File cấu hình thư viện và plugin Maven của dự án








### Vai trò các gói Java chính trong `com.hcmunre.library`:
1. **`config/`**: Khai báo các thiết lập quan trọng như quy tắc CORS để gọi API từ frontend, cấu hình mã hóa mật khẩu (`PasswordEncoder`), mapper tự động (`ModelMapper`).
2. **`controller/`**: Nơi tiếp nhận yêu cầu từ client, gọi service xử lý và trả về phản hồi theo chuẩn JSON (kết hợp các API endpoints như `/api/books`, `/api/borrow`).
3. **`dto/`**: Đảm bảo tính bảo mật và tối ưu hiệu suất bằng cách ngăn chặn client truy cập trực tiếp vào đối tượng DB (`entity`). Tách riêng request nhận từ Client và response gửi đi.
4. **`entity/`**: Các thực thể ánh xạ trực tiếp đến các bảng dữ liệu trong DB (Ví dụ: `Sach`, `DocGia`, `PhieuMuon`, `TacGia`, v.v.).
5. **`repository/`**: Nơi thực hiện CRUD (Create, Read, Update, Delete) trực tiếp với cơ sở dữ liệu dựa trên Spring Data JPA.
6. **`service/`**: Lớp chứa xương sống nghiệp vụ của ứng dụng (Ví dụ: Tính toán ngày trả sách trễ hạn, tính tiền phạt, kiểm tra số lượng sách còn trống để cho mượn).
7. **`security/`**: Xử lý việc xác thực tài khoản qua JWT (JSON Web Token), bảo vệ tài nguyên ứng dụng, kiểm tra quyền hạn trước khi truy cập API.

---

## 3. Cấu Trúc Thư Mục Frontend (Frontend Directory Structure)

Mã nguồn Frontend được xây dựng dựa trên **Vue.js 3** (Composition API), viết bằng **TypeScript** và được build bằng công cụ siêu tốc **Vite**.

📂 `frontend/`
├── 📂 public/                  # Chứa các tài nguyên tĩnh phân phối trực tiếp (Favicon, Logo,...)
├── 📂 src/                     # Thư mục mã nguồn chính của ứng dụng Vue.js
│   ├── 📄 App.vue              # Component gốc của toàn bộ ứng dụng Vue
│   ├── 📄 main.ts              # File cấu hình khởi tạo Vue App (gắn kết Router, Pinia, CSS)
│   ├── 📂 assets/              # Thư mục chứa các tài nguyên tĩnh như CSS/SCSS toàn cục, Hình ảnh
│   ├── 📂 components/          # Các UI Components nhỏ có tính tái sử dụng cao trên nhiều trang
│   │   ├── 📂 BookCard/        # Thẻ hiển thị thông tin thu nhỏ của sách
│   │   ├── 📂 Footer/          # Phần chân trang hệ thống
│   │   ├── 📂 Hero/            # Banner lớn ở trang chủ giới thiệu thư viện
│   │   ├── 📂 Navbar/          # Thanh điều hướng (Menu chính của trang)
│   │   └── 📂 admin/           # Các Component phụ trợ cho bảng điều khiển admin (Ví dụ: Sidebar admin)
│   ├── 📂 composables/         # Chứa các Vue Composition Functions (Hooks) có thể tái sử dụng logic
│   ├── 📂 router/              # Quản lý định tuyến trang (Routes) và kiểm soát quyền truy cập trang
│   ├── 📂 services/            # Định nghĩa kết nối API tới Backend (sử dụng Axios)
│   ├── 📂 stores/              # Quản lý trạng thái toàn cục bằng Pinia (như giỏ sách mượn, thông tin User đã login)
│   ├── 📂 types/               # Khai báo các kiểu dữ liệu và Interface TypeScript (auth, book, api,...)
│   └── 📂 views/               # Chứa các Component trang lớn đại diện cho mỗi đường dẫn (View)
│       ├── 📂 Home/            # Giao diện Trang chủ hệ thống
│       ├── 📂 BookList/        # Trang hiển thị danh sách sách & Tìm kiếm, Lọc sách của độc giả
│       ├── 📂 BookDetail/      # Trang chi tiết một cuốn sách cụ thể
│       ├── 📂 Cart/            # Trang giỏ sách độc giả đã chọn chờ gửi yêu cầu mượn
│       ├── 📂 Profile/         # Trang cá nhân của Độc giả, theo dõi lịch sử mượn trả
│       ├── 📂 Login/           # Trang đăng nhập tài khoản
│       ├── 📂 auth/            # Các trang xác thực khác (Đăng ký, Quên mật khẩu)
│       ├── 📄 KhongCoQuyenView.vue # Trang báo lỗi không đủ quyền truy cập (403 Forbidden)
│       ├── 📄 KhongTimThayView.vue # Trang báo lỗi đường dẫn không tồn tại (404 Not Found)
│       ├── 📂 About/ ...       # Các trang thông tin tĩnh khác (Liên hệ, Nội quy, Hướng dẫn)
│       └── 📂 admin/           # Khu vực quản lý dành cho Quản trị viên (Admin Panels)
│           ├── 📄 DashboardView.vue # Trang tổng quan số liệu thống kê (Số lượt mượn, sách quá hạn,...)
│           ├── 📄 SettingsView.vue  # Các cài đặt nâng cao hệ thống
│           ├── 📂 sach/         # Quản lý thông tin sách (Thêm, Sửa, Xóa danh mục sách)
│           ├── 📂 cuon-sach/    # Quản lý mã vạch, mã định danh cá biệt từng cuốn sách
│           ├── 📂 danh-muc/     # Quản lý thể loại sách (Khoa học, Văn học,...)
│           ├── 📂 doc-gia/      # Quản lý tài khoản và thông tin độc giả (Sinh viên, Giảng viên)
│           ├── 📂 muon-sach/    # Quản lý các phiếu mượn đang được mượn
│           ├── 📂 tra-sach/     # Xử lý quy trình nhận lại sách, cập nhật tình trạng sách trả
│           ├── 📂 dat-cho/      # Xử lý các yêu cầu giữ chỗ trước sách của độc giả
│           ├── 📂 duyet-gia-han/ # Duyệt các đơn xin gia hạn thời gian mượn của độc giả
│           ├── 📂 phat/         # Quản lý các hóa đơn phạt trễ hạn hoặc làm hư hỏng sách
│           └── 📂 nhat-ky/      # Nhật ký lưu vết hành động tác động hệ thống
├── 📄 index.html               # Tệp mẫu HTML chính của ứng dụng SPA
├── 📄 package.json             # Liệt kê các thư viện dependencies và kịch bản chạy dự án (npm scripts)
├── 📄 vite.config.ts           # File cấu hình công cụ build Vite (CORS proxy, alias, plugins)
└── 📄 tsconfig.json            # Cấu hình môi trường biên dịch TypeScript

### Các tính năng cốt lõi của Frontend:
1. **Module Admin (`views/admin/`)**: Tích hợp đầy đủ các nghiệp vụ thủ thư và quản trị viên, giao diện quản lý dạng bảng (Table), các biểu mẫu (Form) nhập liệu chi tiết hỗ trợ đắc lực công tác quản lý thư viện.
2. **Giao diện Độc giả (Client views)**: Trải nghiệm mượt mà, cho phép tìm kiếm sách đa tiêu chí, đánh dấu sách yêu thích, thêm sách vào giỏ mượn tương tự quy trình giỏ hàng E-commerce, và gửi yêu cầu online.
3. **Bảo mật phân quyền (Router guards)**: Tự động chặn các URL admin nếu user chưa đăng nhập hoặc đăng nhập dưới vai trò độc giả thông thường, đưa về trang `KhongCoQuyenView.vue` hoặc `Login`.
