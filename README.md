# 📚 Web App Quản Lý Thư Viện

Dự án môn học: Phát triển phần mềm mã nguồn mở.
Đội ngũ phát triển: Nhóm 3 thành viên.

## 🚀 Công Nghệ Sử Dụng

Dự án áp dụng mô hình **Client-Server tách biệt (RESTful API + SPA)**.

### 1. Backend (Cung cấp REST API)
- **Ngôn ngữ & Framework:** Java, Spring Boot 3.x
- **Database:** MySQL / PostgreSQL (TBD)
- **Kiến trúc:** Phân lớp (Layered Architecture)

### 2. Frontend (Giao diện người dùng)
- **Ngôn ngữ & Framework:** Vue.js 3, TypeScript, Vite
- **State Management:** Pinia
- **Routing:** Vue Router

---

## Cấu Trúc Thư Mục Dự Án

```text
library_system/
│
├── backend/                       # Source code Backend (Spring Boot)
│   └── src/main/java/com/hcmunre/library/
│       ├── config/                # Cấu hình hệ thống (CORS, Cloudinary, OpenAPI...)
│       ├── controller/            # Nhận HTTP Requests, trả về JSON Responses
│       ├── dto/                   # Data Transfer Objects (Request/Response format)
│       ├── entity/                # (Models) Map với các bảng trong Database
│       ├── enums/                 # Định nghĩa các hằng số, trạng thái
│       ├── exception/             # Xử lý lỗi tập trung (GlobalExceptionHandler)
│       ├── repository/            # Tương tác với Database (Spring Data JPA)
│       ├── scheduler/             # Các tác vụ chạy ngầm định kỳ (Cron jobs)
│       ├── security/              # Cấu hình bảo mật, xử lý JWT Token
│       └── service/               # Chứa logic nghiệp vụ (Business logic)
│
├── frontend/                      # Source code Frontend (Vue 3 + TypeScript)
│   ├── src/
│   │   ├── assets/                # Hình ảnh, CSS toàn cục, Font chữ
│   │   ├── components/            # Các UI component dùng chung (Layout, Dialog...)
│   │   ├── composables/           # Các custom hook (useToast, usePagination...)
│   │   ├── router/                # Cấu hình đường dẫn, phân quyền các trang
│   │   ├── services/              # Các file gọi API kết nối Backend (Axios)
│   │   ├── stores/                # Pinia state management (Auth, Settings...)
│   │   ├── types/                 # Định nghĩa Interface/Type cho TypeScript
│   │   └── views/                 # Các màn hình chính (Admin, User, Auth...)
│   └── package.json
│
├── CAUTRUC.md                     # Tài liệu giải thích chi tiết cấu trúc thư mục
└── README.md                      # Tài liệu tổng quan dự án
```

👉 **Để xem giải thích chi tiết chức năng của từng thư mục và tệp tin, vui lòng tham khảo file [CAUTRUC.md](./CAUTRUC.md).**

---

## 🌿 Quy Trình Git & Làm Việc Nhóm (Git Workflow)

Dự án áp dụng mô hình GitFlow cơ bản. Tuyệt đối **không push code trực tiếp lên nhánh `main`**.

### 1. Cấu Trúc Nhánh (Branches)
- `main`: Code hoàn thiện, ổn định, dùng để nộp bài hoặc deploy production.
- `develop`: Nhánh chính để phát triển. Các tính năng mới sẽ được merge vào đây.
- `feature/<tên-tính-năng>`: Nhánh con tạo ra từ `develop` để làm từng tính năng riêng biệt. (VD: `feature/login`, `feature/book-crud`).
- `bugfix/<tên-bug>`: Nhánh sửa lỗi phát sinh.

### 2. Chuẩn Đặt Tên Commit (Conventional Commits)
Khi commit code, yêu cầu bắt buộc ghi message theo chuẩn sau (bằng tiếng Anh):
- `feat: <description>`: Thêm tính năng mới (VD: *feat: add JWT authentication API*)
- `fix: <description>`: Sửa lỗi (bug) (VD: *fix: resolve crash when borrowing non-existent book*)
- `docs: <description>`: Cập nhật tài liệu (VD: *docs: update project structure in README*)
- `style: <description>`: Format code, sửa CSS/UI không ảnh hưởng logic (VD: *style: align homepage buttons*)
- `refactor: <description>`: Tối ưu, sửa lại code nhưng không thay đổi chức năng (VD: *refactor: extract fine calculation logic*)
- `chore: <description>`: Các công việc vặt như cấu hình, cập nhật thư viện (VD: *chore: add axios dependency*)

### 3. Quy Trình Push Code & Pull Request (PR)
1. **Trước khi bắt tay vào code:**
   ```bash
   git checkout develop
   git pull origin develop             # Cập nhật code mới nhất từ nhóm
   git checkout -b feature/ten-tinh-nang # Tạo nhánh mới để làm việc
   ```
2. **Trong lúc code:**
   ```bash
   git add .
   git commit -m "feat: short description of your work in English"
   ```
3. **Khi làm xong tính năng:**
   ```bash
   git push origin feature/ten-tinh-nang
   ```
4. **Tạo Pull Request:** Lên GitHub, tạo 1 Pull Request để gộp nhánh `feature/ten-tinh-nang` của bạn vào nhánh `develop`.
5. **Review Code:** Nhờ 1 thành viên khác trong nhóm xem qua code, nếu OK thì ấn Merge Pull Request.
6. Xóa nhánh `feature` đó đi và lặp lại bước 1 cho tính năng tiếp theo.

---

## ⚙️ Hướng Dẫn Cài Đặt

### Yêu cầu hệ thống
- **Java 17** trở lên
- **Node.js 18** trở lên & npm
- **PostgreSQL 14** trở lên

### Bước 1: Cài đặt Cơ sở dữ liệu (PostgreSQL)
1. Mở PostgreSQL (qua pgAdmin hoặc CLI) và tạo một database mới tên là `quanlythuvien`.
2. Mở file `backend/src/main/resources/application.yaml`.
3. Tìm đến phần `spring.datasource` và cập nhật `username`, `password` cho khớp với PostgreSQL của máy bạn:
   ```yaml
   datasource:
     url: jdbc:postgresql://localhost:5432/quanlythuvien
     username: postgres
     password: 123456 # Thay bằng mật khẩu của bạn
   ```

### Bước 2: Chạy Backend (Spring Boot)
1. Mở terminal, di chuyển vào thư mục `backend/`:
   ```bash
   cd backend
   ```
2. Chạy ứng dụng bằng Maven Wrapper:
   - Trên Windows: `.\mvnw spring-boot:run`
   - Trên Mac/Linux: `./mvnw spring-boot:run`
3. Backend sẽ khởi động và chạy tại: `http://localhost:8080`. (Cơ sở dữ liệu sẽ tự động được tạo bảng nhờ Hibernate).

### Bước 3: Chạy Frontend (Vue.js)
1. Mở một terminal mới, di chuyển vào thư mục `frontend/`:
   ```bash
   cd frontend
   ```
2. Cài đặt các thư viện (dependencies):
   ```bash
   npm install
   ```
3. Khởi động server phát triển:
   ```bash
   npm run dev
   ```
4. Mở trình duyệt và truy cập vào: `http://localhost:5173`. Hệ thống đã sẵn sàng sử dụng!
