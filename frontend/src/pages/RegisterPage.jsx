import React, { useState } from 'react';
import { Form, Input, Button, Card, Typography, message, Radio, Divider } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, GoogleOutlined, GithubOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/authService';

const { Title, Text } = Typography;

const RegisterPage = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values) => {
    setLoading(true);
    try {
      // Split fullName into firstName and lastName
      const nameParts = values.fullName.trim().split(' ');
      const firstName = nameParts.pop(); // Last word is usually firstName in Vietnamese, or you can just split it
      const lastName = nameParts.length > 0 ? nameParts.join(' ') : firstName;

      const payload = {
        email: values.email,
        password: values.password,
        firstName: firstName,
        lastName: lastName,
        role: values.role
      };

      const response = await authService.register(payload);
      message.success('Đăng ký thành công! Vui lòng nhập mã OTP để xác thực.');
      navigate('/verify-email', { state: { email: values.email } });
    } catch (error) {
      console.error('Register error details:', error.response?.data);
      const errorMsg = error.response?.data?.message || error.response?.data?.error || 'Đăng ký thất bại!';
      message.error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-120px)] py-8">
      <Card className="w-full max-w-md shadow-lg border border-gray-100 dark:border-leetgray-700 bg-white dark:bg-leetgray-800">
        <div className="text-center mb-8">
          <Title level={2} className="!text-gray-900 dark:!text-white mb-2">Create Account</Title>
          <Text className="text-gray-500 dark:text-gray-400">Join us to start learning</Text>
        </div>

        <Form
          name="register_form"
          onFinish={onFinish}
          layout="vertical"
          size="large"
        >
          <Form.Item
            name="fullName"
            rules={[{ required: true, message: 'Vui lòng nhập Họ và Tên!' }]}
          >
            <Input prefix={<UserOutlined className="text-gray-400" />} placeholder="Họ và Tên" />
          </Form.Item>

          <Form.Item
            name="role"
            initialValue="STUDENT"
            rules={[{ required: true, message: 'Vui lòng chọn loại tài khoản!' }]}
          >
            <Radio.Group className="w-full flex">
              <Radio.Button value="STUDENT" className="flex-1 text-center">Học viên</Radio.Button>
              <Radio.Button value="INSTRUCTOR" className="flex-1 text-center">Giảng viên</Radio.Button>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            name="email"
            rules={[
              { required: true, message: 'Vui lòng nhập Email!' },
              { type: 'email', message: 'Email không hợp lệ!' }
            ]}
          >
            <Input prefix={<MailOutlined className="text-gray-400" />} placeholder="Email" />
          </Form.Item>
          
          <Form.Item
            name="password"
            rules={[
              { required: true, message: 'Vui lòng nhập Mật khẩu!' },
              { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự!' }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined className="text-gray-400" />}
              placeholder="Mật khẩu"
            />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: 'Vui lòng xác nhận Mật khẩu!' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('Hai mật khẩu không khớp!'));
                },
              }),
            ]}
          >
            <Input.Password
              prefix={<LockOutlined className="text-gray-400" />}
              placeholder="Xác nhận Mật khẩu"
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" className="w-full mt-4" loading={loading}>
              Register
            </Button>
          </Form.Item>

          <Divider className="my-4 text-gray-400 font-normal text-sm border-gray-200 dark:border-gray-700">or register with</Divider>

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
            Already have an account? <Link to="/login" className="text-leetaccent hover:text-orange-400 transition-colors">Sign in</Link>
          </div>
        </Form>
      </Card>
    </div>
  );
};

export default RegisterPage;
