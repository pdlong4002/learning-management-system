import React, { useState } from 'react';
import { Form, Input, Button, Card, Typography, message, Divider } from 'antd';
import { UserOutlined, LockOutlined, GoogleOutlined, GithubOutlined } from '@ant-design/icons';
import { useDispatch } from 'react-redux';
import { useNavigate, Link } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import { loginSuccess } from '../store/slices/authSlice';
import { authService } from '../services/authService';

const { Title, Text } = Typography;

const LoginPage = () => {
  const [loading, setLoading] = useState(false);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const response = await authService.login(values);
      if (response && response.accessToken) {
        const decoded = jwtDecode(response.accessToken);
        dispatch(loginSuccess({
          user: {
            id: 0, // Backend does not return ID in token currently
            email: decoded.sub,
            fullName: decoded.sub.split('@')[0], // Fallback name
            role: decoded.role || 'USER'
          },
          token: response.accessToken
        }));
        message.success('Đăng nhập thành công!');
        navigate('/');
      } else {
        message.error('Đăng nhập thất bại, vui lòng kiểm tra lại!');
      }
    } catch (error) {
      console.error('Login error details:', error.response?.data);
      const errorMsg = error.response?.data?.message || error.response?.data?.error || 'Tài khoản hoặc mật khẩu không chính xác!';
      message.error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-120px)]">
      <Card className="w-full max-w-md shadow-lg border border-gray-100 dark:border-leetgray-700 bg-white dark:bg-leetgray-800">
        <div className="text-center mb-8">
          <Title level={2} className="!text-gray-900 dark:!text-white mb-2">Welcome Back</Title>
          <Text className="text-gray-500 dark:text-gray-400">Please sign in to continue</Text>
        </div>

        <Form
          name="login_form"
          initialValues={{ remember: true }}
          onFinish={onFinish}
          layout="vertical"
          size="large"
        >
          <Form.Item
            name="email"
            rules={[{ required: true, message: 'Vui lòng nhập Email!' }, { type: 'email', message: 'Email không hợp lệ!' }]}
          >
            <Input prefix={<UserOutlined className="text-gray-400" />} placeholder="Email" />
          </Form.Item>
          
          <Form.Item
            name="password"
            rules={[{ required: true, message: 'Vui lòng nhập Mật khẩu!' }]}
          >
            <Input.Password
              prefix={<LockOutlined className="text-gray-400" />}
              placeholder="Mật khẩu"
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" className="w-full mt-4" loading={loading}>
              Sign In
            </Button>
          </Form.Item>

          <Divider className="my-4 text-gray-400 font-normal text-sm border-gray-200 dark:border-gray-700">or continue with</Divider>

          <div className="flex gap-4 mb-6">
            <Button
              className="flex-1 flex items-center justify-center h-10 border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-300 hover:text-blue-500 hover:border-blue-500"
              icon={<GoogleOutlined />}
              onClick={() => window.location.href = 'http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:3000/oauth2/redirect'}
            >
              Google
            </Button>
            <Button
              className="flex-1 flex items-center justify-center h-10 border-gray-300 dark:border-gray-600 dark:bg-gray-800 dark:text-gray-300 hover:text-gray-900 hover:border-gray-900 dark:hover:text-white dark:hover:border-white"
              icon={<GithubOutlined />}
              onClick={() => window.location.href = 'http://localhost:8080/oauth2/authorize/github?redirect_uri=http://localhost:3000/oauth2/redirect'}
            >
              GitHub
            </Button>
          </div>
          
          <div className="text-center text-sm text-gray-500 dark:text-gray-400">
            Don't have an account? <Link to="/register" className="text-leetaccent hover:text-orange-400 transition-colors">Register here</Link>
          </div>
        </Form>
      </Card>
    </div>
  );
};

export default LoginPage;
