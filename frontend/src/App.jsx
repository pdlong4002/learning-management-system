import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import MainLayout from './layouts/MainLayout';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';
import CoursesPage from './pages/CoursesPage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';
import PaymentResultPage from './pages/PaymentResultPage';
import MyCoursesPage from './pages/MyCoursesPage';
import LearningLayout from './layouts/LearningLayout';
import LearningPage from './pages/LearningPage';
import VerifyEmailPage from './pages/VerifyEmailPage';
import CourseDetailPage from './pages/CourseDetailPage';
import OAuth2RedirectHandler from './pages/OAuth2RedirectHandler';
import InstructorDashboardPage from './pages/InstructorDashboardPage';
import CourseBuilderPage from './pages/CourseBuilderPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';

const InstructorRoute = ({ children }) => {
  const { user, isAuthenticated } = useSelector((state) => state.auth);
  if (!isAuthenticated) return <Navigate to="/login" />;
  if (user?.role !== 'ROLE_INSTRUCTOR' && user?.role !== 'ROLE_ADMIN') return <Navigate to="/" />;
  return children;
};

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({ error, errorInfo });
    console.error("Uncaught error:", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: '20px', color: 'red', backgroundColor: '#fee' }}>
          <h1>Something went wrong.</h1>
          <details style={{ whiteSpace: 'pre-wrap' }}>
            {this.state.error && this.state.error.toString()}
            <br />
            {this.state.errorInfo?.componentStack}
          </details>
        </div>
      );
    }
    return this.props.children;
  }
}

function App() {
  return (
    <ErrorBoundary>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route index element={<HomePage />} />
            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
            <Route path="verify-email" element={<VerifyEmailPage />} />
            <Route path="oauth2/redirect" element={<OAuth2RedirectHandler />} />
            <Route path="profile" element={<ProfilePage />} />
            <Route path="courses" element={<CoursesPage />} />
            <Route path="course/:id" element={<CourseDetailPage />} />
            <Route path="my-courses" element={<MyCoursesPage />} />
            <Route path="cart" element={<CartPage />} />
            <Route path="checkout" element={<CheckoutPage />} />
            <Route path="payment-result" element={<PaymentResultPage />} />
            <Route path="instructor/dashboard" element={<InstructorRoute><InstructorDashboardPage /></InstructorRoute>} />
            <Route path="instructor/course/:id" element={<InstructorRoute><CourseBuilderPage /></InstructorRoute>} />
            <Route path="admin/dashboard" element={<InstructorRoute><AdminDashboardPage /></InstructorRoute>} />
          </Route>
          <Route path="/learn" element={<LearningLayout />}>
            <Route path=":id" element={<LearningPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ErrorBoundary>
  );
}

export default App;
