import React, { useState, useEffect } from 'react';
import { Form, Input, Button, Card, Typography, message } from 'antd';
import { MailOutlined, SafetyCertificateOutlined } from '@ant-design/icons';
import { useNavigate, useLocation, Navigate } from 'react-router-dom';
import { authService } from '../services/authService';
import { useDispatch } from 'react-redux';
import { loginSuccess } from '../store/slices/authSlice';

const { Title, Text } = Typography;

const VerifyEmailPage = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();

  // If user navigated here directly without an email in state, redirect to login
  const email = location.state?.email;

  useEffect(() => {
    if (!email) {
      message.error("Vui lòng đăng nhập hoặc đăng ký trước.");
      navigate('/login');
    }
  }, [email, navigate]);

  if (!email) {
    return null;
  }

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const payload = {
        email: email,
        otp: values.otp
      };
      
      const response = await authService.verifyEmail(payload);
      
      // Auto login after verification
      dispatch(loginSuccess({
        user: { email: email, role: 'ROLE_STUDENT' }, // TokenResponse doesn't return user, so mock it for now
        token: response.accessToken
      }));
      
      message.success('Xác thực Email thành công! Chào mừng bạn.');
      navigate('/'); // Redirect to home
    } catch (error) {
      console.error('Verify error details:', error.response?.data);
      const errorMsg = error.response?.data?.message || error.response?.data?.error || 'Mã OTP không hợp lệ hoặc đã hết hạn!';
      message.error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-120px)] py-8 bg-gray-50 dark:bg-[#111]">
      <Card className="w-full max-w-md shadow-xl border-0 rounded-2xl bg-white dark:bg-[#1a1a1a]">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-500 mb-4">
            <SafetyCertificateOutlined className="text-3xl" />
          </div>
          <Title level={2} className="!text-gray-900 dark:!text-white mb-2">Xác thực Email</Title>
          <Text className="text-gray-500 dark:text-gray-400">
            Chúng tôi đã gửi mã xác thực 6 số đến email:<br/>
            <strong className="text-gray-800 dark:text-gray-200">{email}</strong>
          </Text>
        </div>

        <Form
          name="verify_form"
          onFinish={onFinish}
          layout="vertical"
          size="large"
        >
          <Form.Item
            name="otp"
            rules={[
              { required: true, message: 'Vui lòng nhập mã OTP!' },
              { len: 6, message: 'Mã OTP phải có đúng 6 chữ số!' },
              { pattern: /^[0-9]+$/, message: 'Mã OTP chỉ bao gồm chữ số!' }
            ]}
          >
            <Input 
              prefix={<MailOutlined className="text-gray-400" />} 
              placeholder="Nhập mã OTP 6 số" 
              maxLength={6}
              className="text-center text-xl tracking-widest font-mono"
            />
          </Form.Item>

          <Form.Item className="mt-8 mb-0">
            <Button 
              type="primary" 
              htmlType="submit" 
              className="w-full h-12 bg-leetaccent hover:bg-orange-500 border-0 rounded-lg text-base font-medium shadow-md hover:shadow-lg transition-all" 
              loading={loading}
            >
              Xác nhận
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default VerifyEmailPage;
