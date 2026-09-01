# Learning Management System (LMS)

A modern, scalable Learning Management System built with Spring Boot (Java 21) and React (Vite). This platform empowers instructors to create and sell courses while providing students with an intuitive, seamless learning experience.

## 🚀 Tech Stack

### Backend
- **Framework:** Spring Boot 3 & Java 21
- **Database:** MySQL & Spring Data JPA
- **Security:** Spring Security with OAuth2 (Google/GitHub)
- **Authentication:** JWT (JSON Web Tokens) managed via **Redis** (Token Blacklisting & Refresh Tokens)
- **Mapping:** MapStruct & Lombok
- **Payment Integration:** VNPay & Momo E-Wallets
- **CI/CD:** GitHub Actions (Automated Maven Testing & Build)

### Frontend
- **Framework:** React.js (Vite)
- **Styling:** Tailwind CSS / CSS Modules
- **State Management:** (TBD based on implementation - e.g. Redux Toolkit / Context API)

## 🌟 Key Features

- **User Authentication:** Local registration with OTP verification, OAuth2 Login (Google, GitHub).
- **Secure Sessions:** Redis-backed JWT architecture for secure authentication and reliable logout (blacklisting).
- **Course Management:** Instructors can create categories, courses, sections, and lessons.
- **Payment Processing:** Integrated with VNPay and Momo for secure course enrollments.
- **Coupon System:** Discount codes for course purchases.
- **Progress Tracking:** Track user progress across lessons and sections.

## 🛠️ Getting Started

### Prerequisites
- JDK 21
- Node.js (v20+)
- MySQL (v8.0+)
- Redis Server (Running on port 6379)
- Maven

### Environment Configuration
The project uses `.env` files for secure configuration. Create a `.env` file in the root directory (or `backend/` directory) with the following secrets (these are ignored by git):

```env
# OAuth2
GOOGLE_CLIENT_ID=your_google_id
GOOGLE_CLIENT_SECRET=your_google_secret
GITHUB_CLIENT_ID=your_github_id
GITHUB_CLIENT_SECRET=your_github_secret

# Email Service
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_app_password

# Database
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_super_secret_jwt_key_here

# Payments
VNPAY_TMN_CODE=your_vnpay_code
VNPAY_HASH_SECRET=your_vnpay_secret
MOMO_PARTNER_CODE=your_momo_code
MOMO_ACCESS_KEY=your_momo_access_key
MOMO_SECRET_KEY=your_momo_secret_key
```

### Running the Backend

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Build the project (This will also generate MapStruct classes):
   ```bash
   mvn clean compile
   ```
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

### Running the Frontend

1. Navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```

## 📜 License
This project is licensed under the MIT License.
