import React, { useState, useEffect } from 'react';
import { Typography, Form, Input, Button, Tabs, message, Spin, Upload, Avatar, Divider, Row, Col } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, CameraOutlined, CheckCircleOutlined, SafetyCertificateOutlined, SwapOutlined } from '@ant-design/icons';
import { useSelector, useDispatch } from 'react-redux';
import { userService } from '../services/userService';
import { enrollmentService } from '../services/enrollmentService';
import { loginSuccess } from '../store/slices/authSlice';
import api from '../services/api';

const { Title, Text } = Typography;

const ProfilePage = () => {
  const { user } = useSelector((state) => state.auth);
  const dispatch = useDispatch();
  
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();
  
  const [loading, setLoading] = useState(false);
  const [loadingRole, setLoadingRole] = useState(false);
  const [avatarLoading, setAvatarLoading] = useState(false);
  
  const [stats, setStats] = useState({ totalCourses: 0, completedCourses: 0 });

  useEffect(() => {
    if (user) {
      form.setFieldsValue({
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
      });

      // Fetch real stats
      const fetchStats = async () => {
        try {
          const res = await enrollmentService.getMyCourses(0, 50);
          const enrollments = res.data?.data?.content || [];
          const totalCourses = res.data?.data?.totalElements || 0;
          const completedCourses = enrollments.filter(e => e.progressPercent === 100).length;
          setStats({ totalCourses, completedCourses });
        } catch (error) {
          console.error("Failed to fetch stats", error);
        }
      };
      fetchStats();
    }
  }, [user, form]);

  const handleUpdateProfile = async (values) => {
    setLoading(true);
    try {
      const response = await userService.updateProfile(values);
      message.success('Cập nhật thông tin thành công!');
      // Update Redux state with new user info
      dispatch(loginSuccess({ user: response.data, token: localStorage.getItem('token') }));
    } catch (error) {
      console.error(error);
      message.error(error.response?.data?.message || 'Cập nhật thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = async (values) => {
    setLoading(true);
    try {
      await userService.changePassword({
        oldPassword: values.currentPassword,
        newPassword: values.newPassword
      });
      message.success('Đổi mật khẩu thành công!');
      passwordForm.resetFields();
    } catch (error) {
      console.error(error);
      message.error(error.response?.data?.message || 'Đổi mật khẩu thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleSwitchRole = async (newRole) => {
    setLoadingRole(true);
    try {
      const response = await api.patch('/users/role', { role: newRole });
      message.success('Chuyển đổi vai trò thành công!');
      
      // Update Redux state with new user info AND token!
      dispatch(loginSuccess({ 
        user: response.data.user, 
        token: response.data.accessToken 
      }));
    } catch (error) {
      console.error(error);
      message.error(error.response?.data?.message || 'Lỗi khi chuyển đổi vai trò');
    } finally {
      setLoadingRole(false);
    }
  };

  // Convert File to Base64
  const getBase64 = (img, callback) => {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result));
    reader.readAsDataURL(img);
  };

  const beforeUpload = (file) => {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
    if (!isJpgOrPng) {
      message.error('Bạn chỉ có thể upload file định dạng JPG/PNG!');
    }
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
      message.error('Kích thước ảnh phải nhỏ hơn 2MB!');
    }
    return isJpgOrPng && isLt2M;
  };

  const handleAvatarChange = (info) => {
    if (info.file.status === 'uploading') {
      setAvatarLoading(true);
      return;
    }
    if (info.file.status === 'done' || info.file.originFileObj) {
      getBase64(info.file.originFileObj, async (imageUrl) => {
        try {
          setAvatarLoading(true);
          const response = await userService.updateAvatar(imageUrl);
          dispatch(loginSuccess({ user: response.data, token: localStorage.getItem('token') }));
          message.success('Cập nhật ảnh đại diện thành công!');
        } catch (error) {
          console.error(error);
          message.error('Cập nhật ảnh đại diện thất bại. Server có thể cần khởi động lại.');
        } finally {
          setAvatarLoading(false);
        }
      });
    }
  };

  const tabItems = [
    {
      key: '1',
      label: <span className="flex items-center gap-2 font-medium text-base px-2"><UserOutlined /> Thông tin cá nhân</span>,
      children: (
        <div className="py-4">
          <Form
            form={form}
            layout="vertical"
            onFinish={handleUpdateProfile}
            requiredMark={false}
            className="modern-form"
          >
            <Row gutter={16}>
              <Col xs={24} sm={12}>
                <Form.Item
                  name="firstName"
                  label={<span className="font-semibold text-gray-700 dark:text-gray-300">Tên (First Name)</span>}
                  rules={[{ required: true, message: 'Vui lòng nhập tên!' }]}
                >
                  <Input size="large" className="rounded-lg bg-gray-50 dark:bg-[#1a1a1a] dark:text-white dark:border-gray-700 hover:border-leetaccent focus:border-leetaccent" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item
                  name="lastName"
                  label={<span className="font-semibold text-gray-700 dark:text-gray-300">Họ (Last Name)</span>}
                  rules={[{ required: true, message: 'Vui lòng nhập họ!' }]}
                >
                  <Input size="large" className="rounded-lg bg-gray-50 dark:bg-[#1a1a1a] dark:text-white dark:border-gray-700 hover:border-leetaccent focus:border-leetaccent" />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item
              name="email"
              label={<span className="font-semibold text-gray-700 dark:text-gray-300">Email</span>}
            >
              <Input size="large" disabled prefix={<MailOutlined className="text-gray-400" />} className="rounded-lg !bg-gray-100 dark:!bg-[#111] dark:!text-gray-400 dark:!border-gray-800" />
            </Form.Item>
            
            <Divider className="dark:border-gray-800 my-8" />

            <Form.Item>
              <Button type="primary" htmlType="submit" loading={loading} size="large" className="bg-leetaccent hover:bg-orange-400 border-0 h-12 px-8 font-bold shadow-lg shadow-orange-500/30 rounded-lg transition-transform hover:-translate-y-1">
                Lưu Thay Đổi
              </Button>
            </Form.Item>
          </Form>
        </div>
      )
    },
    {
      key: '2',
      label: <span className="flex items-center gap-2 font-medium text-base px-2"><SafetyCertificateOutlined /> Bảo mật</span>,
      children: (
        <div className="py-4 max-w-lg">
          <div className="mb-6 p-4 bg-orange-50 dark:bg-leetaccent/10 border border-orange-200 dark:border-leetaccent/30 rounded-xl flex items-start gap-3">
            <LockOutlined className="text-leetaccent text-xl mt-1" />
            <div>
              <h4 className="font-bold text-gray-900 dark:text-white m-0">Thay đổi mật khẩu</h4>
              <p className="text-sm text-gray-600 dark:text-gray-400 m-0 mt-1">Nên sử dụng mật khẩu mạnh bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt để bảo vệ tài khoản.</p>
            </div>
          </div>
          
          <Form
            form={passwordForm}
            layout="vertical"
            onFinish={handleChangePassword}
            requiredMark={false}
          >
            <Form.Item
              name="currentPassword"
              label={<span className="font-semibold text-gray-700 dark:text-gray-300">Mật khẩu hiện tại</span>}
              rules={[{ required: true, message: 'Vui lòng nhập mật khẩu hiện tại!' }]}
            >
              <Input.Password size="large" prefix={<LockOutlined className="text-gray-400" />} className="rounded-lg bg-gray-50 dark:bg-[#1a1a1a] dark:text-white dark:border-gray-700" />
            </Form.Item>

            <Form.Item
              name="newPassword"
              label={<span className="font-semibold text-gray-700 dark:text-gray-300">Mật khẩu mới</span>}
              rules={[
                { required: true, message: 'Vui lòng nhập mật khẩu mới!' },
                { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự!' }
              ]}
            >
              <Input.Password size="large" prefix={<LockOutlined className="text-gray-400" />} className="rounded-lg bg-gray-50 dark:bg-[#1a1a1a] dark:text-white dark:border-gray-700" />
            </Form.Item>

            <Form.Item
              name="confirmPassword"
              label={<span className="font-semibold text-gray-700 dark:text-gray-300">Xác nhận mật khẩu mới</span>}
              dependencies={['newPassword']}
              rules={[
                { required: true, message: 'Vui lòng xác nhận mật khẩu!' },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('newPassword') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('Mật khẩu xác nhận không khớp!'));
                  },
                }),
              ]}
            >
              <Input.Password size="large" prefix={<CheckCircleOutlined className="text-gray-400" />} className="rounded-lg bg-gray-50 dark:bg-[#1a1a1a] dark:text-white dark:border-gray-700" />
            </Form.Item>

            <Form.Item className="mt-8">
              <Button type="primary" htmlType="submit" loading={loading} size="large" className="bg-gray-800 dark:bg-gray-700 hover:bg-black dark:hover:bg-gray-600 border-0 h-12 px-8 font-bold shadow-md rounded-lg w-full sm:w-auto">
                Cập Nhật Mật Khẩu
              </Button>
            </Form.Item>
          </Form>
        </div>
      )
    },
    {
      key: '3',
      label: <span className="flex items-center gap-2 font-medium text-base px-2"><SwapOutlined /> Chức năng Hệ thống</span>,
      children: (
        <div className="py-4 max-w-lg">
          <div className="mb-6 p-6 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-xl text-center">
             <h3 className="font-black text-xl text-gray-900 dark:text-white m-0 mb-2">
               Tài khoản hiện tại: <span className="text-blue-600 dark:text-blue-400">{user.role === 'ROLE_INSTRUCTOR' ? 'Giảng viên' : 'Học viên'}</span>
             </h3>
             <p className="text-gray-600 dark:text-gray-400 m-0 mb-6">
               Bạn có thể tự do chuyển đổi qua lại giữa tài khoản Học viên và Giảng viên bất cứ lúc nào để trải nghiệm toàn bộ tính năng của hệ thống.
             </p>
             <Button 
               type="primary" 
               size="large" 
               className="bg-blue-600 hover:bg-blue-700 border-0 h-12 px-8 font-bold shadow-lg shadow-blue-500/30 rounded-lg w-full"
               onClick={() => handleSwitchRole(user.role === 'ROLE_INSTRUCTOR' ? 'STUDENT' : 'INSTRUCTOR')}
               loading={loadingRole}
             >
               {user.role === 'ROLE_INSTRUCTOR' ? 'Chuyển sang Học viên' : 'Trở thành Giảng viên'}
             </Button>
          </div>
        </div>
      )
    }
  ];

  if (!user) {
    return <div className="flex justify-center items-center h-64"><Spin size="large" /></div>;
  }

  const uploadButton = (
    <div className="flex flex-col items-center justify-center text-gray-500 hover:text-leetaccent transition-colors">
      {avatarLoading ? <Spin /> : <CameraOutlined className="text-2xl mb-2" />}
      <div className="font-medium">Thay Avatar</div>
    </div>
  );

  return (
    <div className="max-w-5xl mx-auto py-4 sm:py-8">
      <div className="bg-white/80 dark:bg-[#111111]/80 backdrop-blur-xl border border-gray-100 dark:border-gray-800 rounded-3xl shadow-2xl overflow-hidden">
        
        {/* Banner Area */}
        <div className="h-40 sm:h-48 bg-gradient-to-r from-orange-400 via-orange-500 to-leetaccent relative">
          <div className="absolute inset-0 bg-black/10"></div>
          {/* Decorative shapes */}
          <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full blur-3xl -translate-y-1/2 translate-x-1/3"></div>
        </div>
        
        <div className="px-6 sm:px-12 pb-12">
          {/* Avatar Section - overlaps banner */}
          <div className="flex flex-col sm:flex-row items-center sm:items-end gap-6 -mt-16 sm:-mt-20 relative z-10 mb-8">
            <div className="relative group">
              <Upload
                name="avatar"
                listType="picture-circle"
                className="avatar-uploader !w-32 !h-32 sm:!w-40 sm:!h-40 bg-white dark:bg-[#1a1a1a] rounded-full shadow-xl border-4 border-white dark:border-[#111111] overflow-hidden"
                showUploadList={false}
                beforeUpload={beforeUpload}
                onChange={handleAvatarChange}
                customRequest={({ file, onSuccess }) => {
                  setTimeout(() => onSuccess("ok"), 0);
                }}
              >
                {user.imageUrl ? (
                  <div className="relative w-full h-full rounded-full overflow-hidden">
                    <img src={user.imageUrl} alt="avatar" className="w-full h-full object-cover" />
                    <div className="absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                      {uploadButton}
                    </div>
                  </div>
                ) : (
                  uploadButton
                )}
              </Upload>
            </div>
            
            <div className="text-center sm:text-left mb-2 sm:mb-6">
              <Title level={2} className="!text-gray-900 dark:!text-white !mb-0 !font-black tracking-tight drop-shadow-md">
                {user.firstName ? `${user.firstName} ${user.lastName || ''}` : user.email.split('@')[0]}
              </Title>
              <Text className="text-gray-500 dark:text-gray-400 font-medium">
                {user.role === 'ROLE_INSTRUCTOR' ? 'Giảng viên' : user.role === 'ROLE_ADMIN' ? 'Quản trị viên' : 'Học viên'}
              </Text>
            </div>
            
            {/* Stats/Badges right side */}
            <div className="hidden md:flex ml-auto items-center gap-6 mb-6">
               <div className="text-center px-4 py-2 bg-gray-50 dark:bg-[#1a1a1a] rounded-xl border border-gray-100 dark:border-gray-800 shadow-sm">
                  <div className="text-2xl font-black text-gray-900 dark:text-white">{stats.totalCourses}</div>
                  <div className="text-xs font-semibold text-gray-500 uppercase tracking-widest">Khóa học</div>
               </div>
               <div className="text-center px-4 py-2 bg-orange-50 dark:bg-leetaccent/10 rounded-xl border border-orange-100 dark:border-leetaccent/20 shadow-sm">
                  <div className="text-2xl font-black text-leetaccent">{stats.completedCourses}</div>
                  <div className="text-xs font-semibold text-orange-400 uppercase tracking-widest">Hoàn thành</div>
               </div>
            </div>
          </div>

          <div className="mt-8">
            <Tabs 
              defaultActiveKey="1" 
              items={tabItems} 
              className="custom-profile-tabs"
              tabBarStyle={{ 
                borderBottom: '1px solid #e5e7eb',
                marginBottom: '24px'
              }}
            />
          </div>
        </div>
      </div>
      
      {/* Quick custom styling for profile specific overrides to avoid global pollution */}
      <style>{`
        .dark .custom-profile-tabs .ant-tabs-nav::before {
          border-bottom-color: #374151 !important;
        }
        .avatar-uploader .ant-upload {
          width: 100% !important;
          height: 100% !important;
          border-radius: 50% !important;
          border: 0 !important;
          overflow: hidden;
          background: transparent !important;
        }
      `}</style>
    </div>
  );
};

export default ProfilePage;
