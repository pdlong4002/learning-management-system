# PHẠM ĐỨC LONG

**Email:** [longphamk2@gmail.com](mailto:longphamk2@gmail.com) | **SĐT:** 0332481087
**LinkedIn:** [linkedin.com/in/pdlong21](https://www.linkedin.com/in/pdlong21) | **GitHub:** [github.com/pdlong4002](https://github.com/pdlong4002)

---

## 📝 Tóm tắt (Summary)

Sinh viên năm cuối ngành Kỹ thuật Phần mềm định hướng phát triển Backend, có kinh nghiệm thực tế xây dựng các ứng dụng Spring Boot và RESTful APIs. Đang tích cực nghiên cứu và ứng dụng kiến trúc vi dịch vụ (microservices) hướng sự kiện cùng hệ thống phân tán (distributed architecture) vào các dự án học thuật.

---

## 🎓 Học vấn (Education)

**Trường Đại học Văn Hiến** | *Dự kiến tốt nghiệp: 2026*
Cử nhân Khoa học Máy tính -- Kỹ thuật Phần mềm | **GPA: 3.2/4**
Tại: TP. Hồ Chí Minh, Việt Nam

- **Khóa luận tốt nghiệp (Capstone Project):** *Nghiên cứu và phát triển hệ thống phát hiện ảnh ngụy tạo do trí tuệ nhân tạo sinh ra sử dụng Deep Learning kết hợp Explainable Artificial Intelligence (XAI)*.
- **Câu lạc bộ:** Câu lạc bộ Lập trình Thi đấu -- Đội tuyển rèn luyện OLP/ICPC.

---

## 💼 Kinh nghiệm làm việc (Experience)

**Nhân viên Kiểm thử Phần mềm (Software Tester)** | *Tháng 2/2026 -- Tháng 5/2026*
**HiAI Company** | *TP. Hồ Chí Minh, Việt Nam*

- Thực hiện kiểm thử thủ công (manual testing) cho các ứng dụng web để xác định các lỗi chức năng và vấn đề về trải nghiệm người dùng (usability).
- Thiết kế và thực thi các kịch bản kiểm thử (test cases) dựa trên yêu cầu phần mềm và luồng người dùng (user workflows).
- Kiểm thử các RESTful APIs bằng Postman để xác thực dữ liệu gửi/nhận và chức năng của backend.
- Báo cáo, theo dõi và xác minh lỗi sử dụng Jira với các bước tái hiện chi tiết và kết quả mong đợi.
- Phối hợp với đội ngũ lập trình viên để tái hiện vấn đề, kiểm tra lại (retest) các bản sửa lỗi và cải thiện chất lượng phần mềm trước khi phát hành.

---

## 🚀 Dự án tiêu biểu (Projects)

### 1. Learning Management System (LMS - Udemy Clone) - Backend | *Năm: 2026*

- **Công nghệ (Tech Stack):** Java 21, Spring Boot 4, Spring Security, Spring Data JPA, MySQL, Redis, JWT, OAuth2
- **Module Thanh toán (Payment Module):** Xây dựng module thanh toán đa cổng (VNPay, MoMo, ZaloPay) bằng Strategy Pattern, giúp thêm nhà cung cấp thanh toán mới mà không làm ảnh hưởng code hiện tại.
- **Bảo mật Thanh toán (Payment Security):** Xác thực tính toàn vẹn giao dịch từ các Webhook IPN bất đồng bộ bằng chữ ký HMAC (SHA-256/SHA-512) để phát hiện các request bị giả mạo.
- **Xác thực (Authentication):** Kết hợp đăng nhập bằng JWT với Spring Boot OAuth2 (Google Login) và phân quyền truy cập dựa trên vai trò (RBAC) cho các endpoint được bảo vệ.
- **Bộ nhớ đệm (Caching):** Thêm bộ nhớ đệm Redis cho danh mục khóa học và các truy vấn tìm kiếm nhằm giảm tải cho database khi có lượng truy cập đồng thời.

### 2. AirGo -- Flight Booking System | *Năm: 2026*

- **Công nghệ (Tech Stack):** Java 21, Spring Boot 4, Spring Cloud (Gateway, OpenFeign), Kafka, Redis, MySQL, Docker
- **Vi dịch vụ (Microservices):** Chia nhỏ backend thành các dịch vụ Đặt vé (Booking), Chỗ ngồi (Seat) và Thanh toán (Payment), mỗi dịch vụ có cơ sở dữ liệu riêng, giao tiếp thông qua các event của Kafka.
- **Giữ chỗ (Seat Reservation):** Sử dụng distributed locks dựa trên TTL của Redis kết hợp với khóa lạc quan của JPA (`@Version`) để ngăn chặn đặt trùng ghế khi nhiều người dùng thao tác cùng lúc.
- **Đồng bộ Dữ liệu (Data Consistency):** Sử dụng mô hình Saga qua Kafka để đồng bộ dịch vụ Đặt vé và Thanh toán, kết hợp gọi đồng bộ (OpenFeign) và lập lịch (scheduler) rollback để quản lý Chỗ ngồi.
- **Cổng API (API Gateway):** Thêm xác thực JWT và giới hạn tốc độ (rate limiting) dựa trên Redis tại tầng API Gateway.

---

## 🏃 Hoạt động (Activities)

**Đội tuyển Lập trình Thi đấu ICPC** | *2024 -- Hiện tại*
**Trường Đại học Văn Hiến** | *TP. Hồ Chí Minh, Việt Nam*

- Tham gia huấn luyện giải quyết vấn đề thuật toán và các kỳ thi lập trình thi đấu.
- Rèn luyện kỹ năng về cấu trúc dữ liệu, thuật toán và kỹ năng giải quyết vấn đề cho các kỳ thi ICPC/OLP.

---

## 🛠 Kỹ năng Chuyên môn (Technical Skills)

- **Ngôn ngữ Lập trình:** Java, C++, Python, SQL
- **Frontend:** HTML, CSS, Bootstrap, Thymeleaf
- **Backend:** Spring Boot, Spring MVC, Spring Security, REST APIs, JWT, OAuth2, JPA/Hibernate, Apache Kafka
- **Cơ sở dữ liệu:** MySQL, MongoDB, Redis
- **Công cụ & CI/CD:** Git, GitHub, Docker, Postman, Swagger, GitHub Actions

---

## 🏆 Cuộc thi tham gia (Participant)

- **Olympic Tin học Sinh viên Việt Nam** (2025)
- **ICPC Asia Regional Contest | Ho Chi Minh City Site** (2025)
