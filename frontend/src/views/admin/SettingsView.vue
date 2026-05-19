<template>
  <div class="settings-view">
    <!-- Floating Toast Notification -->
    <Transition name="toast-fade">
      <div v-if="saved" class="floating-toast">
        <font-awesome-icon icon="fa-solid fa-circle-check" class="toast-icon" />
        <div class="toast-content">
          <h4>Thành công</h4>
          <p>Đã lưu cài đặt hệ thống thành công!</p>
        </div>
      </div>
    </Transition>

    <h2 class="tieu-de"><font-awesome-icon icon="fa-solid fa-screwdriver-wrench" /> Cài đặt hệ thống</h2>

    <!-- Premium Glassmorphic Tab Bar -->
    <div class="system-tabs-bar">
      <button
        v-for="tab in systemTabs"
        :key="tab.key"
        type="button"
        class="system-tab-btn"
        :class="{ 'active': activeSystemTab === tab.key }"
        @click="activeSystemTab = tab.key"
      >
        <font-awesome-icon :icon="tab.icon" class="tab-btn-icon" />
        <span>{{ tab.label }}</span>
      </button>
    </div>

    <!-- TABS CONTENT -->
    <div class="system-tabs-content">
      
      <!-- ===== TAB 1: CÀI ĐẶT CHUNG ===== -->
      <div v-if="activeSystemTab === 'chung'" class="tab-pane-content">
        <!-- QR Thanh toán -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-mobile-screen-button" class="card-icon" />
            <div>
              <h3>Mã QR Thanh toán chuyển khoản</h3>
              <p>Ảnh QR sẽ hiển thị khi độc giả/thủ thư chọn phương thức "Chuyển khoản".</p>
            </div>
          </div>

          <div class="qr-area">
            <div class="qr-preview" @click="triggerUpload">
              <img v-if="qrUrl" :src="qrUrl" alt="QR Thanh toán" class="qr-img" />
              <div v-else class="qr-placeholder">
                <font-awesome-icon icon="fa-solid fa-camera" class="qr-icon" />
                <span>Click để chọn ảnh QR</span>
              </div>
            </div>

            <div class="qr-actions">
              <input ref="fileInput" type="file" accept="image/*" hidden @change="onFileChange" />
              <button class="nut-upload" @click="triggerUpload">
                <font-awesome-icon icon="fa-solid fa-folder-open" /> {{ qrUrl ? 'Thay ảnh QR' : 'Chọn ảnh QR' }}
              </button>
              <button v-if="qrUrl" class="nut-xoa-qr" @click="xoaQR">
                <font-awesome-icon icon="fa-solid fa-trash-can" /> Xóa QR
              </button>
              <p class="hint">Hỗ trợ JPG, PNG, WebP. Nên dùng ảnh vuông ≥ 300×300px.</p>
              <div v-if="qrUrl" class="qr-status">
                <span class="dot-xanh"></span> Đã cấu hình QR thanh toán
              </div>
            </div>
          </div>

          <div v-if="saved" class="alert-ok">
            <font-awesome-icon icon="fa-solid fa-circle-check" /> Đã lưu cài đặt thành công!
          </div>
        </div>

        <!-- Thông tin thư viện -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-building-columns" class="card-icon" />
            <div>
              <h3>Thông tin thư viện</h3>
              <p>Hiển thị trong email và giao diện người dùng.</p>
            </div>
          </div>
          <div class="form-grid">
            <div class="form-group">
              <label>Tên thư viện</label>
              <input v-model="settings.tenThuVien" class="form-input" placeholder="Thư viện Đại học Tài nguyên Môi trường TP.HCM" />
            </div>
            <div class="form-group">
              <label>Số điện thoại liên hệ</label>
              <input v-model="settings.sdtLienHe" class="form-input" placeholder="028 XXXX XXXX" />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Địa chỉ</label>
              <input v-model="settings.diaChi" class="form-input" placeholder="236 Lê Văn Sỹ, Q.3, TP.HCM" />
            </div>
          </div>
          <button class="nut-luu" @click="luuCaiDat">
            <font-awesome-icon icon="fa-solid fa-floppy-disk" /> Lưu cài đặt
          </button>
        </div>

        <!-- Cấu hình trang chủ (Hero Banner) -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-house-laptop" class="card-icon" />
            <div>
              <h3>Cấu hình trang chủ (Hero Banner)</h3>
              <p>Điều chỉnh tiêu đề, mô tả, danh mục phổ biến và hình ảnh bìa nổi bật.</p>
            </div>
          </div>

          <div class="form-grid">
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Tiêu đề lớn Hero</label>
              <input v-model="settings.heroTitle" class="form-input" placeholder="Khám phá tri thức tại LibManage" />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Tiêu đề con Hero</label>
              <textarea v-model="settings.heroSubtitle" class="form-input" style="height: 80px; resize: vertical;" placeholder="Hàng ngàn cuốn sách, tài liệu và tài nguyên học tập đang chờ đón bạn. Bắt đầu hành trình tìm kiếm ngay hôm nay."></textarea>
            </div>

            <!-- 4 thể loại phổ biến -->
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Chọn 4 danh mục hiển thị ở phần phổ biến</label>
              <div class="form-grid form-grid--4" style="margin-top: 0.25rem;">
                <div v-for="idx in [0, 1, 2, 3]" :key="idx" class="form-group">
                  <label>Danh mục {{ idx + 1 }}</label>
                  <select v-model="settings.heroPopularGenres[idx]" class="form-input">
                    <option value="">-- Chọn thể loại --</option>
                    <option v-for="cat in listTheLoaiAll" :key="cat.maTheLoai" :value="cat.tenTheLoai">
                      {{ cat.tenTheLoai }}
                    </option>
                    <option v-if="listTheLoaiAll.length === 0" v-for="sc in staticCategories" :key="sc.name" :value="sc.name">
                      {{ sc.name }}
                    </option>
                  </select>
                </div>
              </div>
            </div>

            <!-- Ảnh nền Hero -->
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Danh sách hình ảnh trang bìa (Hỗ trợ nhiều ảnh để tự động chạy slide ảnh)</label>
              <div class="hero-images-manager" style="margin-top: 0.5rem;">
                <div class="hero-images-list" style="display: flex; gap: 1rem; flex-wrap: wrap; margin-bottom: 1rem;">
                  <div v-for="(img, idx) in settings.heroImages" :key="idx" class="hero-image-item" style="position: relative; width: 120px; height: 80px; border-radius: 8px; overflow: hidden; border: 1px solid var(--border);">
                    <img :src="img" style="width: 100%; height: 100%; object-fit: cover;" />
                    <button type="button" @click="removeHeroImage(idx)" style="position: absolute; top: 4px; right: 4px; background: rgba(239, 68, 68, 0.9); color: white; width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.75rem; border: none; cursor: pointer;">
                      <font-awesome-icon icon="fa-solid fa-xmark" />
                    </button>
                  </div>
                </div>

                <div class="add-image-actions" style="display: flex; gap: 1rem; align-items: center; flex-wrap: wrap;">
                  <input ref="heroFileInput" type="file" accept="image/*" hidden @change="onHeroFileChange" />
                  <button type="button" class="nut-upload" style="padding: 0.5rem 1rem; font-size: 0.825rem; font-weight: 600;" @click="triggerHeroUpload">
                    <font-awesome-icon icon="fa-solid fa-file-arrow-up" /> Tải ảnh lên
                  </button>
                  
                  <div style="flex: 1; display: flex; gap: 0.5rem;">
                    <input v-model="newImageUrl" class="form-input" style="flex: 1; padding: 0.5rem;" placeholder="Hoặc dán URL hình ảnh..." />
                    <button type="button" class="nut-luu" style="padding: 0.5rem 1rem; font-size: 0.825rem;" @click="addHeroImageUrl">
                      Thêm URL
                    </button>
                  </div>
                </div>
                <p class="hint" style="margin-top: 0.5rem;">Nếu cấu hình nhiều ảnh bìa, trang chủ sẽ tự động chạy hiệu ứng chuyển ảnh fade mượt mà sau mỗi 5 giây.</p>
              </div>
            </div>
          </div>

          <button class="nut-luu" @click="luuCaiDat">
            <font-awesome-icon icon="fa-solid fa-floppy-disk" /> Lưu cấu hình trang chủ
          </button>
        </div>
      </div>

      <!-- ===== TAB 2: QUY ĐỊNH & PHẠT ===== -->
      <div v-else-if="activeSystemTab === 'muon_phat'" class="tab-pane-content">
        <!-- Cấu hình mượn sách -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-book" class="card-icon" />
            <div>
              <h3>Cấu hình quy tắc mượn sách</h3>
              <p>Các thông số mặc định dùng khi tạo phiếu mượn mới.</p>
            </div>
          </div>
          <div class="form-grid">
            <div class="form-group">
              <label>Số ngày mượn mặc định</label>
              <input v-model.number="settings.soNgayMuon" type="number" class="form-input" min="1" max="60" />
            </div>
            <div class="form-group">
              <label>Số lần gia hạn tối đa</label>
              <input v-model.number="settings.soLanGiaHan" type="number" class="form-input" min="0" max="5" />
            </div>
            <div class="form-group">
              <label>Số ngày gia hạn mỗi lần</label>
              <input v-model.number="settings.soNgayGiaHan" type="number" class="form-input" min="1" max="30" />
            </div>
            <div class="form-group">
              <label>Số sách mượn tối đa cùng lúc</label>
              <input v-model.number="settings.soSachToiDa" type="number" class="form-input" min="1" max="20" />
            </div>
          </div>
          <button class="nut-luu" @click="luuCaiDat">
            <font-awesome-icon icon="fa-solid fa-floppy-disk" /> Lưu cài đặt
          </button>
        </div>

        <!-- Mức phạt -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-scale-balanced" class="card-icon" />
            <div>
              <h3>Cấu hình mức phạt</h3>
              <p>Các mức phạt áp dụng khi trả sách trễ, hư hỏng hoặc mất sách.</p>
            </div>
          </div>

          <div class="phat-tabs">
            <button
              v-for="tab in tabsPhat"
              :key="tab.key"
              class="phat-tab"
              :class="{ 'phat-tab--active': tabPhatHienTai === tab.key }"
              @click="tabPhatHienTai = tab.key"
            >
              <font-awesome-icon :icon="tab.icon" /> {{ tab.label }}
            </button>
          </div>

          <!-- Theo ngày -->
          <div v-if="tabPhatHienTai === 'theo_ngay'" class="form-grid">
            <div class="form-group">
              <label>Mức phạt mặc định / ngày (VND)</label>
              <div class="input-prefix-wrapper">
                <span class="input-prefix">₫</span>
                <input
                  v-model.number="settings.phat.mucPhatMacDinhTheoNgay"
                  type="number"
                  class="form-input form-input--prefix"
                  min="0"
                  step="1000"
                  placeholder="5000"
                />
              </div>
              <span class="form-hint">Áp dụng khi cuốn sách không có đơn giá phạt riêng.</span>
            </div>
            <div class="form-group">
              <label>Phạt tối đa / cuốn (VND, 0 = không giới hạn)</label>
              <div class="input-prefix-wrapper">
                <span class="input-prefix">₫</span>
                <input
                  v-model.number="settings.phat.mucPhatToiDaMoiCuon"
                  type="number"
                  class="form-input form-input--prefix"
                  min="0"
                  step="10000"
                  placeholder="500000"
                />
              </div>
            </div>
          </div>

          <!-- Theo tình trạng -->
          <div v-if="tabPhatHienTai === 'theo_tinh_trang'" class="form-grid form-grid--3">
            <div class="form-group">
              <label>📗 Sách hư nhẹ (VND)</label>
              <div class="input-prefix-wrapper">
                <span class="input-prefix">₫</span>
                <input
                  v-model.number="settings.phat.huNhe"
                  type="number"
                  class="form-input form-input--prefix"
                  min="0"
                  step="5000"
                />
              </div>
            </div>
            <div class="form-group">
              <label>📙 Sách hư vừa (VND)</label>
              <div class="input-prefix-wrapper">
                <span class="input-prefix">₫</span>
                <input
                  v-model.number="settings.phat.huVua"
                  type="number"
                  class="form-input form-input--prefix"
                  min="0"
                  step="5000"
                />
              </div>
            </div>
            <div class="form-group">
              <label>📕 Sách hư nặng (VND)</label>
              <div class="input-prefix-wrapper">
                <span class="input-prefix">₫</span>
                <input
                  v-model.number="settings.phat.huNang"
                  type="number"
                  class="form-input form-input--prefix"
                  min="0"
                  step="5000"
                />
              </div>
            </div>
          </div>

          <!-- Theo giá sách -->
          <div v-if="tabPhatHienTai === 'theo_gia_sach'" class="form-grid">
            <div class="form-group">
              <label>Hệ số bồi thường khi mất sách (ví dụ: 1.5 = đền 150% giá bìa)</label>
              <input
                v-model.number="settings.phat.heSoMatSach"
                type="number"
                class="form-input"
                min="1"
                max="5"
                step="0.1"
              />
            </div>
            <div class="form-group">
              <label>Mức đền bù tối thiểu khi mất sách (VND)</label>
              <div class="input-prefix-wrapper">
                <span class="input-prefix">₫</span>
                <input
                  v-model.number="settings.phat.matSachToiThieu"
                  type="number"
                  class="form-input form-input--prefix"
                  min="0"
                  step="10000"
                />
              </div>
            </div>
          </div>

          <!-- Preview tóm tắt -->
          <div class="phat-summary">
            <div class="summary-chip">
              <font-awesome-icon icon="fa-solid fa-clock" /> Phạt trễ: <b>{{ formatVND(settings.phat.mucPhatMacDinhTheoNgay) }}/ngày</b>
            </div>
            <div class="summary-chip">
              <font-awesome-icon icon="fa-solid fa-book" /> Hư nặng: <b>{{ formatVND(settings.phat.huNang) }}</b>
            </div>
            <div class="summary-chip">
              <font-awesome-icon icon="fa-solid fa-circle-xmark" /> Mất sách: <b>×{{ settings.phat.heSoMatSach }} giá bìa</b>
            </div>
          </div>

          <button class="nut-luu" @click="luuCaiDat">
            <font-awesome-icon icon="fa-solid fa-floppy-disk" /> Lưu cấu hình phạt
          </button>
        </div>
      </div>

      <!-- ===== TAB 3: GIỚI THIỆU & LIÊN HỆ ===== -->
      <div v-else-if="activeSystemTab === 'about_contact'" class="tab-pane-content">
        <!-- Cấu hình trang Giới thiệu -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-circle-info" class="card-icon" />
            <div>
              <h3>Cấu hình trang Giới thiệu</h3>
              <p>Điều chỉnh tiêu đề, mô tả và nội dung sứ mệnh hiển thị ở trang giới thiệu thư viện.</p>
            </div>
          </div>

          <div class="form-grid">
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Tiêu đề lớn Hero:</label>
              <input v-model="settings.aboutHeroTitle" class="form-input" />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Tiêu đề phụ Hero:</label>
              <RichTextEditor v-model="settings.aboutHeroSubtitle" placeholder="Nhập mô tả giới thiệu phụ..." />
            </div>
            <div class="form-group">
              <label>Badge Sứ mệnh:</label>
              <input v-model="settings.aboutMissionBadge" class="form-input" />
            </div>
            <div class="form-group">
              <label>Tiêu đề Sứ mệnh:</label>
              <input v-model="settings.aboutMissionTitle" class="form-input" />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Mô tả sứ mệnh - Đoạn 1:</label>
              <RichTextEditor v-model="settings.aboutMissionDesc1" placeholder="Nhập sứ mệnh đoạn 1..." />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Mô tả sứ mệnh - Đoạn 2:</label>
              <RichTextEditor v-model="settings.aboutMissionDesc2" placeholder="Nhập sứ mệnh đoạn 2..." />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Hình ảnh sứ mệnh:</label>
              <div style="display: flex; gap: 10px;">
                <input v-model="settings.aboutMissionImage" class="form-input" style="flex: 1;" />
                <input ref="aboutFileInput" type="file" accept="image/*" hidden @change="onAboutFileChange" />
                <button class="nut-upload" style="margin-top: 0;" @click="triggerAboutUpload">
                  <font-awesome-icon icon="fa-solid fa-folder-open" /> Chọn ảnh
                </button>
              </div>
              <div v-if="settings.aboutMissionImage" style="margin-top: 10px; max-width: 240px; border-radius: 8px; overflow: hidden; border: 1px solid rgba(255,255,255,0.1);">
                <img :src="settings.aboutMissionImage" alt="Sứ mệnh" style="width: 100%; display: block;" />
              </div>
            </div>
          </div>

          <button class="nut-luu" @click="luuCaiDat">
            <font-awesome-icon icon="fa-solid fa-floppy-disk" /> Lưu cấu hình Giới thiệu
          </button>
        </div>

        <!-- Cấu hình trang Liên hệ -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-address-book" class="card-icon" />
            <div>
              <h3>Cấu hình trang Liên hệ</h3>
              <p>Điều chỉnh tiêu đề, thông tin liên lạc và hình ảnh bản đồ hiển thị ở trang Liên hệ.</p>
            </div>
          </div>

          <div class="form-grid">
            <div class="form-group">
              <label>Tiêu đề chính:</label>
              <input v-model="settings.contactTitle" class="form-input" />
            </div>
            <div class="form-group">
              <label>Tiêu đề con:</label>
              <input v-model="settings.contactSubtitle" class="form-input" />
            </div>
            <div class="form-group">
              <label>Địa chỉ:</label>
              <input v-model="settings.contactDiaChi" class="form-input" />
            </div>
            <div class="form-group">
              <label>Điện thoại:</label>
              <input v-model="settings.contactDienThoai" class="form-input" />
            </div>
            <div class="form-group">
              <label>Email:</label>
              <input v-model="settings.contactEmail" class="form-input" />
            </div>
            <div class="form-group">
              <label>Giờ làm việc:</label>
              <input v-model="settings.contactGioLamViec" class="form-input" />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Đường dẫn hình ảnh Bản đồ:</label>
              <div style="display: flex; gap: 10px;">
                <input v-model="settings.contactMapImage" class="form-input" style="flex: 1;" />
                <input ref="contactMapFileInput" type="file" accept="image/*" hidden @change="onContactMapFileChange" />
                <button class="nut-upload" style="margin-top: 0;" @click="triggerContactMapUpload">
                  <font-awesome-icon icon="fa-solid fa-folder-open" /> Chọn ảnh
                </button>
              </div>
              <div v-if="settings.contactMapImage" style="margin-top: 10px; max-width: 240px; border-radius: 8px; overflow: hidden; border: 1px solid rgba(255,255,255,0.1);">
                <img :src="settings.contactMapImage" alt="Bản đồ" style="width: 100%; display: block;" />
              </div>
            </div>
          </div>

          <button class="nut-luu" @click="luuCaiDat">
            <font-awesome-icon icon="fa-solid fa-floppy-disk" /> Lưu cấu hình Liên hệ
          </button>
        </div>
      </div>

      <!-- ===== TAB 4: HƯỚNG DẪN & FAQS ===== -->
      <div v-else-if="activeSystemTab === 'guide_faq'" class="tab-pane-content">
        <!-- Cấu hình Hướng dẫn mượn sách -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-graduation-cap" class="card-icon" />
            <div>
              <h3>Cấu hình trang Hướng dẫn mượn sách</h3>
              <p>Chỉnh sửa các bước hướng dẫn, câu hỏi nhanh và lưu ý ở trang hướng dẫn mượn sách.</p>
            </div>
          </div>

          <div class="form-grid">
            <div class="form-group">
              <label>Tiêu đề trang:</label>
              <input v-model="settings.guideHeroTitle" class="form-input" placeholder="Hướng dẫn Mượn sách" />
            </div>
            <div class="form-group">
              <label>Mô tả ngắn:</label>
              <input v-model="settings.guideHeroSubtitle" class="form-input" placeholder="Mọi thứ bạn cần biết để bắt đầu hành trình khám phá..." />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Danh sách các bước hướng dẫn mượn sách:</label>
              <div class="visual-list-editor">
                <div v-for="(step, index) in guideSteps" :key="index" class="list-editor-card">
                  <div class="card-header-row">
                    <span class="step-badge">Bước {{ index + 1 }}</span>
                    <button type="button" class="btn-delete-item" @click="guideSteps.splice(index, 1)">
                      <font-awesome-icon icon="fa-solid fa-trash" /> Xóa bước này
                    </button>
                  </div>
                  <div class="form-grid">
                    <div class="form-group">
                      <label>Tiêu đề bước:</label>
                      <input v-model="step.title" class="form-input" placeholder="Tiêu đề bước..." />
                    </div>
                    <div class="form-group">
                      <label>Biểu tượng (Icon class):</label>
                      <input v-model="step.icon" class="form-input" placeholder="Ví dụ: fa-solid fa-magnifying-glass" />
                    </div>
                    <div class="form-group" style="grid-column: 1/-1;">
                      <label>Mô tả chi tiết:</label>
                      <textarea v-model="step.description" class="form-input" style="height: 60px; resize: vertical;" placeholder="Nhập mô tả bước..."></textarea>
                    </div>
                  </div>
                </div>
                <button type="button" class="btn-add-item" @click="guideSteps.push({ title: 'Bước mới', description: '', icon: 'fa-solid fa-book' })">
                  <font-awesome-icon icon="fa-solid fa-plus" /> Thêm bước hướng dẫn
                </button>
              </div>
            </div>

            <div class="form-group">
              <label>Các câu hỏi nhanh (FAQs ngắn):</label>
              <div class="visual-list-editor">
                <div v-for="(faq, index) in guideFaqs" :key="index" class="list-editor-card">
                  <div class="card-header-row">
                    <span class="step-badge">Câu hỏi {{ index + 1 }}</span>
                    <button type="button" class="btn-delete-item" @click="guideFaqs.splice(index, 1)">
                      <font-awesome-icon icon="fa-solid fa-trash" /> Xóa
                    </button>
                  </div>
                  <div class="form-grid">
                    <div class="form-group" style="grid-column: 1/-1;">
                      <label>Câu hỏi:</label>
                      <input v-model="faq.question" class="form-input" placeholder="Nhập câu hỏi..." />
                    </div>
                    <div class="form-group" style="grid-column: 1/-1;">
                      <label>Câu trả lời:</label>
                      <textarea v-model="faq.answer" class="form-input" style="height: 60px; resize: vertical;" placeholder="Nhập câu trả lời..."></textarea>
                    </div>
                  </div>
                </div>
                <button type="button" class="btn-add-item" @click="guideFaqs.push({ question: 'Câu hỏi mới?', answer: '' })">
                  <font-awesome-icon icon="fa-solid fa-plus" /> Thêm câu hỏi nhanh
                </button>
              </div>
            </div>

            <div class="form-group">
              <label>Quy tắc ngắn (Hiển thị đầu trang):</label>
              <div class="visual-list-editor">
                <div v-for="(rule, index) in guideRules" :key="index" class="simple-list-row">
                  <span class="row-index">{{ index + 1 }}</span>
                  <input v-model="guideRules[index]" class="form-input" style="flex: 1;" placeholder="Nhập quy tắc..." />
                  <button type="button" class="btn-delete-icon" @click="guideRules.splice(index, 1)">
                    <font-awesome-icon icon="fa-solid fa-trash" />
                  </button>
                </div>
                <button type="button" class="btn-add-item" @click="guideRules.push('Quy tắc mới')">
                  <font-awesome-icon icon="fa-solid fa-plus" /> Thêm quy tắc ngắn
                </button>
              </div>
            </div>
          </div>

          <button class="nut-luu" @click="luuCaiDat">
            <font-awesome-icon icon="fa-solid fa-floppy-disk" /> Lưu cấu hình Hướng dẫn
          </button>
        </div>

        <!-- Cấu hình trang FAQs -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-circle-question" class="card-icon" />
            <div>
              <h3>Cấu hình trang Câu hỏi thường gặp (FAQs)</h3>
              <p>Điều chỉnh tiêu đề và toàn bộ danh mục câu hỏi/trả lời hiển thị tại trang FAQs.</p>
            </div>
          </div>

          <div class="form-grid">
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Tiêu đề chính:</label>
              <input v-model="settings.faqTitle" class="form-input" />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Tiêu đề con:</label>
              <input v-model="settings.faqSubtitle" class="form-input" />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Quản lý các danh mục câu hỏi & Câu trả lời chi tiết:</label>
              <div class="visual-list-editor">
                <div v-for="(category, catIndex) in generalFaqs" :key="catIndex" class="list-editor-card category-card">
                  <div class="card-header-row">
                    <span class="step-badge category-badge">Danh mục: {{ category.title }}</span>
                    <button type="button" class="btn-delete-item" @click="generalFaqs.splice(catIndex, 1)">
                      <font-awesome-icon icon="fa-solid fa-trash" /> Xóa danh mục
                    </button>
                  </div>
                  <div class="form-grid" style="margin-bottom: 1rem;">
                    <div class="form-group">
                      <label>Tên danh mục:</label>
                      <input v-model="category.title" class="form-input" placeholder="Ví dụ: Tài khoản & Đăng nhập..." />
                    </div>
                    <div class="form-group">
                      <label>Biểu tượng (Icon class):</label>
                      <input v-model="category.icon" class="form-input" placeholder="Ví dụ: fa-solid fa-circle-user..." />
                    </div>
                  </div>

                  <!-- Nested Questions -->
                  <div class="nested-questions-section">
                    <h4>Danh sách các câu hỏi trong danh mục này:</h4>
                    <div v-for="(q, qIndex) in category.questions" :key="qIndex" class="nested-faq-row">
                      <div class="nested-faq-header">
                        <span>Câu hỏi {{ qIndex + 1 }}</span>
                        <button type="button" class="btn-delete-icon-small" @click="category.questions.splice(qIndex, 1)">
                          <font-awesome-icon icon="fa-solid fa-xmark" />
                        </button>
                      </div>
                      <div class="form-group" style="margin-bottom: 0.5rem;">
                        <label>Câu hỏi:</label>
                        <input v-model="q.question" class="form-input form-input-sm" />
                      </div>
                      <div class="form-group">
                        <label>Trả lời:</label>
                        <textarea v-model="q.answer" class="form-input form-input-sm" style="height: 60px; resize: vertical;"></textarea>
                      </div>
                    </div>
                    <button type="button" class="btn-add-nested" @click="category.questions.push({ id: 'faq-' + Date.now(), question: 'Câu hỏi mới?', answer: '' })">
                      <font-awesome-icon icon="fa-solid fa-plus" /> Thêm câu hỏi vào danh mục
                    </button>
                  </div>
                </div>
                <button type="button" class="btn-add-item" @click="generalFaqs.push({ title: 'Danh mục mới', icon: 'fa-solid fa-circle-question', questions: [] })">
                  <font-awesome-icon icon="fa-solid fa-plus" /> Thêm danh mục câu hỏi mới
                </button>
              </div>
            </div>
          </div>

          <button class="nut-luu" @click="luuCaiDat">
            <font-awesome-icon icon="fa-solid fa-floppy-disk" /> Lưu cấu hình FAQs
          </button>
        </div>
      </div>

      <!-- ===== TAB 5: TRANG NỘI QUY ===== -->
      <div v-else-if="activeSystemTab === 'rules'" class="tab-pane-content">
        <!-- Cấu hình trang Nội quy -->
        <div class="section-card">
          <div class="card-head">
            <font-awesome-icon icon="fa-solid fa-scale-balanced" class="card-icon" />
            <div>
              <h3>Cấu hình trang Nội quy thư viện</h3>
              <p>Điều chỉnh tiêu đề, các mục quy định chi tiết và lưu ý quan trọng tại trang Nội quy.</p>
            </div>
          </div>

          <div class="form-grid">
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Tiêu đề chính:</label>
              <input v-model="settings.rulesTitle" class="form-input" />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Tiêu đề con:</label>
              <input v-model="settings.rulesSubtitle" class="form-input" />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Lưu ý quan trọng (Chữ nổi bật ở dưới cùng):</label>
              <RichTextEditor v-model="settings.rulesImportantNote" placeholder="Thư viện là không gian chung cho tất cả chúng ta..." />
            </div>
            <div class="form-group" style="grid-column: 1/-1;">
              <label>Quản lý các nhóm nội quy & Quy định chi tiết:</label>
              <div class="visual-list-editor">
                <div v-for="(category, catIndex) in generalRules" :key="catIndex" class="list-editor-card category-card">
                  <div class="card-header-row">
                    <span class="step-badge category-badge">Nhóm nội quy: {{ category.title }}</span>
                    <button type="button" class="btn-delete-item" @click="generalRules.splice(catIndex, 1)">
                      <font-awesome-icon icon="fa-solid fa-trash" /> Xóa nhóm nội quy
                    </button>
                  </div>
                  <div class="form-grid" style="margin-bottom: 1rem;">
                    <div class="form-group">
                      <label>Tên nhóm nội quy:</label>
                      <input v-model="category.title" class="form-input" placeholder="Ví dụ: Quy định chung..." />
                    </div>
                    <div class="form-group">
                      <label>Biểu tượng (Icon class):</label>
                      <input v-model="category.icon" class="form-input" placeholder="Ví dụ: fa-solid fa-circle-info..." />
                    </div>
                  </div>

                  <!-- Rules List -->
                  <div class="nested-questions-section">
                    <h4>Danh sách các điều luật / quy định chi tiết:</h4>
                    <div v-for="(rule, ruleIndex) in category.rules" :key="ruleIndex" class="nested-rule-row">
                      <span class="row-index">{{ ruleIndex + 1 }}</span>
                      <input v-model="category.rules[ruleIndex]" class="form-input form-input-sm" style="flex: 1;" />
                      <button type="button" class="btn-delete-icon-small" @click="category.rules.splice(ruleIndex, 1)">
                        <font-awesome-icon icon="fa-solid fa-xmark" />
                      </button>
                    </div>
                    <button type="button" class="btn-add-nested" @click="category.rules.push('Quy định mới')">
                      <font-awesome-icon icon="fa-solid fa-plus" /> Thêm quy định mới vào nhóm
                    </button>
                  </div>
                </div>
                <button type="button" class="btn-add-item" @click="generalRules.push({ title: 'Nhóm nội quy mới', icon: 'fa-solid fa-list-check', rules: [] })">
                  <font-awesome-icon icon="fa-solid fa-plus" /> Thêm nhóm nội quy mới
                </button>
              </div>
            </div>
          </div>

          <button class="nut-luu" @click="luuCaiDat">
            <font-awesome-icon icon="fa-solid fa-floppy-disk" /> Lưu cấu hình Nội quy
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { theLoaiService } from '@/services/danhMucService'
import type { TheLoai } from '@/types/danhmuc'
import RichTextEditor from '@/components/admin/RichTextEditor.vue'

const STORAGE_KEY_QR = 'library_qr_url'
const STORAGE_KEY_SETTINGS = 'library_settings'

const fileInput = ref<HTMLInputElement | null>(null)
const qrUrl = ref('')
const saved = ref(false)

const staticCategories = [
  { name: 'Kỹ năng sống' },
  { name: 'Công nghệ' },
  { name: 'Văn học' },
  { name: 'Kinh tế' }
]

const aboutFileInput = ref<HTMLInputElement | null>(null)
const contactMapFileInput = ref<HTMLInputElement | null>(null)
const listTheLoaiAll = ref<TheLoai[]>([])
const heroFileInput = ref<HTMLInputElement | null>(null)
const newImageUrl = ref('')

const defaultSteps = [
  {
    title: 'Tìm kiếm sách',
    description: 'Sử dụng thanh tìm kiếm hoặc duyệt qua danh mục để tìm cuốn sách bạn cần.',
    icon: 'fa-solid fa-magnifying-glass'
  },
  {
    title: 'Đăng ký mượn',
    description: 'Nhấn nút "Mượn sách" tại trang chi tiết. Hệ thống sẽ ghi nhận yêu cầu của bạn.',
    icon: 'fa-solid fa-hand-holding-heart'
  },
  {
    title: 'Nhận sách',
    description: 'Đến quầy thủ thư, xuất trình mã số học sinh để nhận sách vật lý.',
    icon: 'fa-solid fa-book-open-reader'
  },
  {
    title: 'Trả sách đúng hạn',
    description: 'Hoàn trả sách trước ngày hết hạn để duy trì điểm tin cậy của bạn.',
    icon: 'fa-solid fa-calendar-check'
  }
]

const defaultGuideFaqs = [
  {
    question: 'Tôi có thể mượn tối đa bao nhiêu cuốn sách?',
    answer: 'Mỗi học sinh được mượn tối đa 03 cuốn sách cùng một lúc.'
  },
  {
    question: 'Thời gian mượn sách tối đa là bao lâu?',
    answer: 'Thời gian mượn mặc định là 14 ngày. Bạn có thể gia hạn thêm 07 ngày nếu không có ai khác đang đặt chỗ.'
  },
  {
    question: 'Nếu tôi làm mất hoặc hỏng sách thì sao?',
    answer: 'Trường hợp làm mất hoặc hỏng sách, bạn cần bồi thường theo giá trị hiện hành của cuốn sách cộng với phí xử lý nghiệp vụ.'
  },
  {
    question: 'Làm thế nào để gia hạn sách?',
    answer: 'Bạn có thể gia hạn trực tuyến tại trang "Cá nhân" hoặc đến trực tiếp quầy thủ thư.'
  }
]

const defaultGuideRules = [
  'Giữ gìn sách cẩn thận, không viết vẽ lên sách.',
  'Không cho người khác mượn lại thẻ học sinh của mình.',
  'Trả sách đúng hạn quy định để tránh bị phạt.',
  'Giữ trậtt tự và vệ sinh chung khi đọc sách tại thư viện.'
]

const defaultFaqs = [
  {
    title: 'Tài khoản & Đăng nhập',
    icon: 'fa-solid fa-circle-user',
    questions: [
      {
        id: 'acc-1',
        question: 'Làm thế nào để tôi có tài khoản thư viện?',
        answer: 'Tài khoản thư viện sẽ được quản trị viên nhà trường cấp riêng cho từng học sinh. Bạn không cần phải tự đăng ký. Hãy liên hệ văn phòng nhà trường hoặc thủ thư nếu bạn chưa nhận được thông tin đăng nhập.'
      },
      {
        id: 'acc-2',
        question: 'Tôi quên mật khẩu thì phải làm sao?',
        answer: 'Trường hợp quên mật khẩu, bạn hãy mang thẻ học sinh đến trực tiếp quầy thủ thư để yêu cầu cấp lại mật khẩu mới.'
      }
    ]
  },
  {
    title: 'Mượn & Trả sách',
    icon: 'fa-solid fa-book',
    questions: [
      {
        id: 'borrow-1',
        question: 'Quy trình mượn sách trực tuyến như thế nào?',
        answer: 'Bạn tìm cuốn sách muốn mượn, nhấn nút "Mượn sách" tại trang chi tiết. Sau đó, hãy đến thư viện trong vòng 24h để nhận sách vật lý từ thủ thư.'
      },
      {
        id: 'borrow-2',
        question: 'Tôi có thể mượn sách trong bao lâu?',
        answer: 'Thời hạn mượn sách thông thường là 14 ngày. Bạn có thể gia hạn thêm nếu sách đó không có người khác đang đặt trước.'
      },
      {
        id: 'borrow-3',
        question: 'Tôi có bị phạt nếu trả sách quá hạn không?',
        answer: 'Có, theo nội quy thư viện, học sinh trả sách quá hạn sẽ bị phạt tiền 1.000đ/ngày/cuốn sách.'
      }
    ]
  },
  {
    title: 'Dịch vụ khác',
    icon: 'fa-solid fa-bell',
    questions: [
      {
        id: 'svc-1',
        question: 'Thư viện có cung cấp chỗ ngồi học không?',
        answer: 'Có, thư viện có khu vực tự học hiện đại với đầy đủ ổ cắm điện và wifi miễn phí phục vụ học sinh.'
      },
      {
        id: 'svc-2',
        question: 'Tôi có thể đề xuất thư viện mua thêm sách mới không?',
        answer: 'Chắc chắn rồi! Bạn có thể gửi tên sách và tác giả vào mục "Góp ý" hoặc trao đổi trực tiếp với thủ thư.'
      }
    ]
  }
]

const defaultRules = [
  {
    title: 'Quy định chung',
    icon: 'fa-solid fa-circle-info',
    rules: [
      'Học sinh, giáo viên và nhân viên nhà trường khi vào thư viện phải mang theo thẻ học sinh/thẻ công chức.',
      'Giữ gìn trật tự, không làm ồn, không nói chuyện điện thoại trong khu vực đọc sách.',
      'Không mang thức ăn, nước uống (trừ nước lọc) vào thư viện.',
      'Trang phục chỉnh tề, lịch sự theo đúng quy định của nhà trường.'
    ]
  },
  {
    title: 'Mượn và Trả sách',
    icon: 'fa-solid fa-book-open-reader',
    rules: [
      'Mỗi lần mượn không quá 03 cuốn sách trong thời gian tối đa 14 ngày.',
      'Chỉ được mượn sách bằng thẻ của chính mình, không cho người khác mượn thẻ.',
      'Kiểm tra tình trạng sách trước khi mượn. Nếu phát hiện sách hư hỏng phải báo ngay cho thủ thư.',
      'Phải trả sách đúng hạn. Nếu muốn mượn tiếp phải làm thủ tục gia hạn.'
    ]
  },
  {
    title: 'Sử dụng thiết bị & Tài sản',
    icon: 'fa-solid fa-screwdriver-wrench',
    rules: [
      'Máy tính chỉ dùng để tra cứu thông tin và phục vụ học tập, không chơi game hay xem nội dung không phù hợp.',
      'Không tự ý thay đổi cấu hình máy tính hoặc cài đặt các phần mềm lạ.',
      'Sử dụng bàn ghế và trang thiết bị khác một cách cẩn thận, không viết vẽ lên bàn.',
      'Trước khi rời khỏi thư viện, phải xếp lại ghế và dọn dẹp khu vực ngồi.'
    ]
  },
  {
    title: 'Xử lý vi phạm',
    icon: 'fa-solid fa-triangle-exclamation',
    rules: [
      'Trả sách quá hạn sẽ bị phạt tiền theo quy định (1.000đ/ngày/cuốn).',
      'Làm mất sách phải bồi thường theo giá trị thực tế của sách tại thời điểm đó.',
      'Làm hư hỏng sách tùy mức độ sẽ phải bồi thường tiền hoặc mua lại sách mới.',
      'Vi phạm nghiêm trọng nội quy sẽ bị tạm dừng quyền sử dụng thư viện.'
    ]
  }
]

const settings = ref({
  tenThuVien: 'Thư viện ĐH Tài nguyên Môi trường TP.HCM',
  sdtLienHe: '028 5445 2222',
  diaChi: '236 Lê Văn Sỹ, Phường 1, Quận 3, TP.HCM',
  soNgayMuon: 14,
  soLanGiaHan: 2,
  soNgayGiaHan: 7,
  soSachToiDa: 5,
  phat: {
    mucPhatMacDinhTheoNgay: 5000,
    mucPhatToiDaMoiCuon: 500000,
    huNhe: 20000,
    huVua: 50000,
    huNang: 100000,
    heSoMatSach: 1.5,
    matSachToiThieu: 50000,
  },
  heroTitle: 'Khám phá tri thức tại LibManage',
  heroSubtitle: 'Hàng ngàn cuốn sách, tài liệu và tài nguyên học tập đang chờ đón bạn. Bắt đầu hành trình tìm kiếm ngay hôm nay.',
  heroImages: ['/hero-bg.png'],
  heroPopularGenres: ['Kỹ năng sống', 'Công nghệ', 'Văn học', 'Kinh tế'],
  aboutHeroTitle: 'Lan tỏa tri thức, Kết nối đam mê',
  aboutHeroSubtitle: 'LibManage không chỉ là một hệ thống quản lý thư viện, mà là cầu nối đưa tri thức đến gần hơn với cộng đồng học sinh và giáo viên.',
  aboutMissionBadge: 'Sứ mệnh của chúng tôi',
  aboutMissionTitle: 'Xây dựng nền tảng tri thức vững chắc cho tương lai',
  aboutMissionDesc1: 'Được thành lập từ năm 2010, thư viện của chúng tôi luôn nỗ lực không ngừng để trở thành trung tâm học tập và nghiên cứu hàng đầu. Chúng tôi tin rằng mỗi cuốn sách là một cánh cửa mở ra thế giới mới.',
  aboutMissionDesc2: 'Với hệ thống quản lý điện tử LibManage, chúng tôi cam kết mang lại trải nghiệm mượn sách hiện đại, minh bạch và hiệu quả nhất cho mọi người dùng.',
  aboutMissionImage: 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&q=80&w=800',
  
  // Guide (Hướng dẫn mượn)
  guideHeroTitle: 'Hướng dẫn Mượn sách',
  guideHeroSubtitle: 'Mọi thứ bạn cần biết để bắt đầu hành trình khám phá tri thức tại thư viện',
  guideStepsJson: JSON.stringify(defaultSteps, null, 2),
  guideFaqsJson: JSON.stringify(defaultGuideFaqs, null, 2),
  guideRulesJson: JSON.stringify(defaultGuideRules, null, 2),
  
  // FAQs page (Câu hỏi thường gặp)
  faqTitle: 'Chúng tôi có thể giúp gì cho bạn?',
  faqSubtitle: 'Tìm kiếm câu trả lời nhanh chóng cho các thắc mắc thường gặp nhất',
  faqsJson: JSON.stringify(defaultFaqs, null, 2),
  
  // Rules page (Nội quy)
  rulesTitle: 'Nội quy Thư viện',
  rulesSubtitle: 'Vì một môi trường học tập văn minh, hiện đại và công bằng cho tất cả mọi người',
  rulesImportantNote: 'Thư viện là không gian chung cho tất cả chúng ta. Việc tuân thủ nội quy không chỉ giúp bảo vệ tài sản nhà trường mà còn thể hiện văn hóa ứng xử của mỗi học sinh. Hãy cùng nhau xây dựng một cộng đồng tri thức văn minh!',
  rulesJson: JSON.stringify(defaultRules, null, 2),
  
  // Contact page (Liên hệ)
  contactTitle: 'Liên hệ với chúng tôi',
  contactSubtitle: 'Mọi góp ý hoặc thắc mắc của bạn đều là động lực để thư viện hoàn thiện hơn mỗi ngày',
  contactDiaChi: 'Số 123, Đường Tri Thức, Quận Cầu Giấy, Hà Nội',
  contactDienThoai: '(024) 3456 7890',
  contactEmail: 'thuvien@school.edu.vn',
  contactGioLamViec: 'Thứ 2 - Thứ 7: 07:30 - 21:00',
  contactMapImage: 'https://images.unsplash.com/photo-1524666041070-9d87656c25bb?auto=format&fit=crop&q=80&w=600'
})

// Tab phạt
const tabsPhat = [
  { key: 'theo_ngay', label: 'Theo ngày trễ', icon: 'fa-solid fa-calendar-days' },
  { key: 'theo_tinh_trang', label: 'Theo tình trạng', icon: 'fa-solid fa-clipboard-list' },
  { key: 'theo_gia_sach', label: 'Theo giá sách', icon: 'fa-solid fa-money-bill-wave' },
]
const tabPhatHienTai = ref('theo_ngay')

const systemTabs = [
  { key: 'chung', label: 'Cài đặt Chung', icon: 'fa-solid fa-gears' },
  { key: 'muon_phat', label: 'Quy định & Phạt', icon: 'fa-solid fa-scale-balanced' },
  { key: 'about_contact', label: 'Giới thiệu & Liên hệ', icon: 'fa-solid fa-address-book' },
  { key: 'guide_faq', label: 'Hướng dẫn & FAQs', icon: 'fa-solid fa-circle-question' },
  { key: 'rules', label: 'Trang Nội quy', icon: 'fa-solid fa-gavel' }
]
const activeSystemTab = ref('chung')

const guideSteps = ref<any[]>([])
const guideFaqs = ref<any[]>([])
const guideRules = ref<string[]>([])
const generalFaqs = ref<any[]>([])
const generalRules = ref<any[]>([])

function parseJsonSettings() {
  // Guide Steps
  try {
    guideSteps.value = JSON.parse(settings.value.guideStepsJson)
  } catch (e) {
    guideSteps.value = JSON.parse(JSON.stringify(defaultSteps))
  }

  // Guide FAQs
  try {
    guideFaqs.value = JSON.parse(settings.value.guideFaqsJson)
  } catch (e) {
    guideFaqs.value = JSON.parse(JSON.stringify(defaultGuideFaqs))
  }

  // Guide Rules
  try {
    guideRules.value = JSON.parse(settings.value.guideRulesJson)
  } catch (e) {
    guideRules.value = JSON.parse(JSON.stringify(defaultGuideRules))
  }

  // General FAQs
  try {
    generalFaqs.value = JSON.parse(settings.value.faqsJson)
  } catch (e) {
    generalFaqs.value = JSON.parse(JSON.stringify(defaultFaqs))
  }

  // General Rules
  try {
    generalRules.value = JSON.parse(settings.value.rulesJson)
  } catch (e) {
    generalRules.value = JSON.parse(JSON.stringify(defaultRules))
  }
}

onMounted(async () => {
  const saved_qr = localStorage.getItem(STORAGE_KEY_QR)
  if (saved_qr) qrUrl.value = saved_qr

  const saved_settings = localStorage.getItem(STORAGE_KEY_SETTINGS)
  if (saved_settings) {
    try {
      const parsed = JSON.parse(saved_settings)
      Object.assign(settings.value, parsed)
      if (parsed.phat) {
        Object.assign(settings.value.phat, parsed.phat)
      }
      if (parsed.heroPopularGenres) {
        settings.value.heroPopularGenres = parsed.heroPopularGenres
      }
      if (parsed.heroImages) {
        settings.value.heroImages = parsed.heroImages
      }
    } catch {}
  }

  parseJsonSettings()

  try {
    const list = await theLoaiService.danhSach()
    if (list) {
      listTheLoaiAll.value = list
    }
  } catch (err) {
    console.error('Không thể tải danh sách thể loại:', err)
  }
})

function triggerUpload() {
  fileInput.value?.click()
}

function onFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    alert('Ảnh quá lớn (tối đa 5MB)')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    const url = ev.target?.result as string
    qrUrl.value = url
    localStorage.setItem(STORAGE_KEY_QR, url)
    saved.value = true
    setTimeout(() => { saved.value = false }, 3000)
  }
  reader.readAsDataURL(file)
}

function xoaQR() {
  qrUrl.value = ''
  localStorage.removeItem(STORAGE_KEY_QR)
}

function triggerHeroUpload() {
  heroFileInput.value?.click()
}

function onHeroFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    alert('Ảnh quá lớn (tối đa 5MB)')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    const url = ev.target?.result as string
    if (!settings.value.heroImages) {
      settings.value.heroImages = []
    }
    settings.value.heroImages.push(url)
  }
  reader.readAsDataURL(file)
}

function addHeroImageUrl() {
  if (!newImageUrl.value.trim()) return
  if (!settings.value.heroImages) {
    settings.value.heroImages = []
  }
  settings.value.heroImages.push(newImageUrl.value.trim())
  newImageUrl.value = ''
}

function removeHeroImage(index: number) {
  if (settings.value.heroImages) {
    settings.value.heroImages.splice(index, 1)
  }
}

function triggerAboutUpload() {
  aboutFileInput.value?.click()
}

function onAboutFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    alert('Ảnh quá lớn (tối đa 5MB)')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    const url = ev.target?.result as string
    settings.value.aboutMissionImage = url
  }
  reader.readAsDataURL(file)
}

function triggerContactMapUpload() {
  contactMapFileInput.value?.click()
}

function onContactMapFileChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    alert('Ảnh quá lớn (tối đa 5MB)')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    const url = ev.target?.result as string
    settings.value.contactMapImage = url
  }
  reader.readAsDataURL(file)
}

function luuCaiDat() {
  // Serialize our visual array states back to JSON strings before saving!
  settings.value.guideStepsJson = JSON.stringify(guideSteps.value, null, 2)
  settings.value.guideFaqsJson = JSON.stringify(guideFaqs.value, null, 2)
  settings.value.guideRulesJson = JSON.stringify(guideRules.value, null, 2)
  settings.value.faqsJson = JSON.stringify(generalFaqs.value, null, 2)
  settings.value.rulesJson = JSON.stringify(generalRules.value, null, 2)

  localStorage.setItem(STORAGE_KEY_SETTINGS, JSON.stringify(settings.value))
  saved.value = true
  setTimeout(() => { saved.value = false }, 3000)
}

function formatVND(val: number) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val)
}
</script>

<style scoped>
.settings-view { animation: fadeInUp 0.4s ease; display: flex; flex-direction: column; gap: 1.5rem; }
.tieu-de { font-size: 1.4rem; font-weight: 700; color: var(--mau-chu); margin-bottom: 0; }

/* Premium Tabs Bar Styling */
.system-tabs-bar {
  display: flex;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  padding: 0.5rem;
  margin-bottom: 0.5rem;
  flex-wrap: wrap;
  backdrop-filter: blur(8px);
}
.system-tab-btn {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  padding: 0.65rem 1.25rem;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 8px;
  color: var(--mau-chu-mo);
  font-family: inherit;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
.system-tab-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--mau-chu);
}
.system-tab-btn.active {
  background: rgba(6, 182, 212, 0.12);
  border-color: rgba(6, 182, 212, 0.35);
  color: #06b6d4;
  box-shadow: 0 4px 12px rgba(6, 182, 212, 0.15);
}
.tab-btn-icon {
  font-size: 0.95rem;
}

/* Animations for switching tabs */
.tab-pane-content {
  animation: tabFadeIn 0.35s ease;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
@keyframes tabFadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section-card { background: var(--glass-nen); border: 1px solid var(--glass-vien); border-radius: 14px; padding: 1.5rem; display: flex; flex-direction: column; gap: 1.25rem; }
.card-head { display: flex; align-items: flex-start; gap: 1rem; }
.card-icon { font-size: 2rem; line-height: 1; flex-shrink: 0; }
.card-head h3 { margin: 0; font-size: 1rem; font-weight: 700; color: var(--mau-chu); }
.card-head p { margin: 0.25rem 0 0; font-size: 0.825rem; color: var(--mau-chu-mo); }

/* QR */
.qr-area { display: flex; gap: 1.5rem; align-items: flex-start; flex-wrap: wrap; }
.qr-preview {
  width: 180px; height: 180px; border: 2px dashed rgba(6,182,212,0.4); border-radius: 12px;
  display: flex; align-items: center; justify-content: center; cursor: pointer;
  background: rgba(6,182,212,0.04); overflow: hidden; flex-shrink: 0; transition: border-color 0.2s;
}
.qr-preview:hover { border-color: var(--mau-chinh); }
.qr-img { width: 100%; height: 100%; object-fit: contain; }
.qr-placeholder { display: flex; flex-direction: column; align-items: center; gap: 0.5rem; color: var(--mau-chu-mo); font-size: 0.825rem; }
.qr-icon { font-size: 2.5rem; }
.qr-actions { display: flex; flex-direction: column; gap: 0.75rem; justify-content: center; }
.nut-upload { padding: 0.65rem 1.25rem; background: var(--color-primary); border: none; border-radius: 8px; color: white; cursor: pointer; font-family: inherit; font-size: 0.875rem; font-weight: 600; }
.nut-xoa-qr { padding: 0.65rem 1.25rem; background: rgba(239,68,68,0.1); border: 1px solid rgba(239,68,68,0.3); border-radius: 8px; color: #ef4444; cursor: pointer; font-family: inherit; font-size: 0.875rem; }
.hint { font-size: 0.8rem; color: var(--mau-chu-mo); margin: 0; }
.qr-status { display: flex; align-items: center; gap: 0.4rem; font-size: 0.825rem; color: #51cf66; }
.dot-xanh { width: 8px; height: 8px; border-radius: 50%; background: #51cf66; animation: pulse 2s infinite; }
@keyframes pulse { 0%,100%{opacity:1} 50%{opacity:0.4} }
.alert-ok { padding: 0.75rem 1rem; background: rgba(81,207,102,0.1); border: 1px solid rgba(81,207,102,0.3); border-radius: 8px; color: #51cf66; font-size: 0.875rem; font-weight: 600; }

/* Form */
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.form-grid--3 { grid-template-columns: 1fr 1fr 1fr; }
.form-group { display: flex; flex-direction: column; gap: 0.375rem; }
.form-group label { font-size: 0.825rem; font-weight: 700; color: #334155; }
.form-input { padding: 0.7rem 1rem; background: #ffffff; border: 1px solid rgba(0, 0, 0, 0.12); border-radius: 8px; color: #1e293b; font-family: inherit; font-size: 0.875rem; outline: none; transition: all 0.2s ease; }
.form-input:focus { border-color: #06b6d4; box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.1); }
.form-hint { font-size: 0.75rem; color: var(--mau-chu-mo); margin-top: 0.2rem; }

/* Input prefix */
.input-prefix-wrapper { position: relative; display: flex; }
.input-prefix {
  position: absolute;
  left: 0.875rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--mau-chu-mo);
  pointer-events: none;
  user-select: none;
}
.form-input--prefix { padding-left: 2rem; }

.nut-luu { align-self: flex-start; padding: 0.65rem 1.5rem; background: var(--color-primary); border: none; border-radius: 8px; color: white; cursor: pointer; font-family: inherit; font-weight: 600; }

/* Phạt tabs */
.phat-tabs { display: flex; gap: 0.5rem; flex-wrap: wrap; }
.phat-tab {
  padding: 0.5rem 1rem;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 8px;
  color: var(--mau-chu-mo);
  cursor: pointer;
  font-family: inherit;
  font-size: 0.825rem;
  font-weight: 500;
  transition: all 0.2s;
}
.phat-tab:hover { background: rgba(6,182,212,0.08); color: var(--mau-chu); }
.phat-tab--active {
  background: rgba(6,182,212,0.15);
  border-color: rgba(6,182,212,0.4);
  color: #0891b2;
  font-weight: 700;
}

/* Summary chips */
.phat-summary {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  padding: 0.75rem;
  background: rgba(6,182,212,0.05);
  border: 1px solid rgba(6,182,212,0.15);
  border-radius: 10px;
}
.summary-chip {
  font-size: 0.8rem;
  color: var(--mau-chu);
  padding: 0.35rem 0.75rem;
  background: rgba(6,182,212,0.08);
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 0.35rem;
}
.summary-chip b { color: #0891b2; }

@media (max-width: 600px) {
  .form-grid { grid-template-columns: 1fr; }
  .form-grid--3 { grid-template-columns: 1fr; }
}

.hero-images-manager {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  padding: 1.25rem;
  border-radius: 12px;
}
.hero-image-item {
  position: relative;
  width: 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.15);
  transition: all 0.2s;
  flex-shrink: 0;
}
.hero-image-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 10px -1px rgba(0, 0, 0, 0.25);
}

/* Floating Toast Success Notification */
.floating-toast {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 9999;
  background: rgba(15, 23, 42, 0.95);
  border: 1px solid rgba(16, 185, 129, 0.3);
  padding: 1rem 1.5rem;
  border-radius: 12px;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.3), 0 0 15px rgba(16, 185, 129, 0.2);
  display: flex;
  align-items: center;
  gap: 0.875rem;
  color: white;
  backdrop-filter: blur(8px);
  min-width: 320px;
}
.toast-icon {
  font-size: 1.75rem;
  color: #10b981; /* Green success accent */
}
.toast-content h4 {
  margin: 0;
  font-size: 0.95rem;
  font-weight: 700;
  color: #10b981;
}
.toast-content p {
  margin: 0.15rem 0 0 0;
  font-size: 0.825rem;
  color: rgba(255, 255, 255, 0.8);
}

/* Toast Transition Animations */
.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}
.toast-fade-enter-from {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}
.toast-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}

/* Visual List Editor */
.visual-list-editor {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  background: rgba(0, 0, 0, 0.02);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  padding: 1.25rem;
  margin-top: 0.5rem;
}
.list-editor-card {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 10px;
  padding: 1.25rem;
  position: relative;
  transition: all 0.25s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.02);
}
.list-editor-card:hover {
  border-color: rgba(6, 182, 212, 0.4);
  box-shadow: 0 6px 15px rgba(6, 182, 212, 0.06);
}
.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}
.step-badge {
  font-size: 0.8rem;
  font-weight: 700;
  color: #0891b2;
  background: rgba(6, 182, 212, 0.08);
  padding: 0.35rem 0.75rem;
  border-radius: 6px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.category-badge {
  color: #10b981;
  background: rgba(16, 185, 129, 0.08);
}
.btn-delete-item {
  background: rgba(239, 68, 68, 0.06);
  border: 1px solid rgba(239, 68, 68, 0.15);
  color: #dc2626;
  padding: 0.4rem 0.85rem;
  border-radius: 6px;
  font-size: 0.775rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.35rem;
}
.btn-delete-item:hover {
  background: rgba(239, 68, 68, 0.12);
  color: #b91c1c;
  border-color: rgba(239, 68, 68, 0.3);
}
.btn-add-item {
  align-self: flex-start;
  background: rgba(6, 182, 212, 0.06);
  border: 1px dashed rgba(6, 182, 212, 0.3);
  color: #0891b2;
  padding: 0.65rem 1.25rem;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.btn-add-item:hover {
  background: rgba(6, 182, 212, 0.12);
  border-style: solid;
  color: #06b6d4;
}
.simple-list-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}
.row-index {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  color: #475569;
}
.btn-delete-icon {
  background: transparent;
  border: none;
  color: rgba(239, 68, 68, 0.6);
  cursor: pointer;
  padding: 0.5rem;
  transition: color 0.2s;
  font-size: 0.9rem;
}
.btn-delete-icon:hover {
  color: #ef4444;
}

/* FAQs Category Card & Nesting */
.category-card {
  border-left: 3px solid #10b981;
}
.nested-questions-section {
  background: #f8fafc;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  padding: 1rem;
  margin-top: 1rem;
}
.nested-questions-section h4 {
  margin: 0 0 1rem;
  font-size: 0.85rem;
  font-weight: 700;
  color: #1e293b;
}
.nested-faq-row {
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 6px;
  padding: 0.875rem;
  margin-bottom: 0.75rem;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.01);
}
.nested-faq-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.775rem;
  font-weight: 700;
  color: #475569;
  margin-bottom: 0.5rem;
}
.btn-delete-icon-small {
  background: transparent;
  border: none;
  color: rgba(239, 68, 68, 0.6);
  cursor: pointer;
  font-size: 0.8rem;
  display: flex;
  align-items: center;
  justify-content: center;
}
.btn-delete-icon-small:hover {
  color: #ef4444;
}
.btn-add-nested {
  background: #ffffff;
  border: 1px dashed rgba(0, 0, 0, 0.15);
  color: #475569;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.375rem;
  margin-top: 0.5rem;
}
.btn-add-nested:hover {
  border-color: #06b6d4;
  color: #0891b2;
  background: rgba(6, 182, 212, 0.02);
}
.nested-rule-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}
.form-input-sm {
  font-size: 0.825rem;
  padding: 0.45rem 0.75rem;
}

/* Explicit Light Styling override for visual inputs */
.visual-list-editor .form-input {
  background: #f8fafc !important;
  border: 1px solid rgba(0, 0, 0, 0.1) !important;
  color: #0f172a !important;
}
.visual-list-editor .form-input:focus {
  background: #ffffff !important;
  border-color: #06b6d4 !important;
  box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.1) !important;
}
.visual-list-editor label {
  color: #334155 !important;
}
</style>
