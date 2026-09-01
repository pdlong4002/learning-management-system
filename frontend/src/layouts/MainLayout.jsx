import React, { useState, useEffect } from 'react';
import { ConfigProvider, theme, Button, Layout, Dropdown, Badge } from 'antd';
import { BulbOutlined, BulbFilled, UserOutlined, LogoutOutlined, ShoppingCartOutlined, BookOutlined } from '@ant-design/icons';
import { Outlet, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';

import { logout } from '../store/slices/authSlice';
import { clearCart } from '../store/slices/cartSlice';

const { Header, Content } = Layout;

const MainLayout = () => {
  const [isDarkMode, setIsDarkMode] = useState(false);
  const { user, isAuthenticated } = useSelector((state) => state.auth);
  const cartItems = useSelector((state) => state.cart.items);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    const savedTheme = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

    if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
      setIsDarkMode(true);
      document.documentElement.classList.add('dark');
    }
  }, []);

  const toggleTheme = () => {
    setIsDarkMode(!isDarkMode);
    if (!isDarkMode) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('theme', 'light');
    }
  };

  const handleLogout = () => {
    dispatch(clearCart());
    dispatch(logout());
    navigate('/login');
  };

  const userMenuItems = [
    ...(user?.role === 'ROLE_ADMIN' ? [{
      key: 'admin-dashboard',
      label: 'Admin Dashboard',
      icon: <BulbOutlined />,
      onClick: () => navigate('/admin/dashboard')
    }] : []),
    ...(user?.role === 'ROLE_INSTRUCTOR' || user?.role === 'ROLE_ADMIN' ? [{
      key: 'instructor-dashboard',
      label: 'Instructor Dashboard',
      icon: <BulbOutlined />,
      onClick: () => navigate('/instructor/dashboard')
    }] : []),
    {
      key: 'my-courses',
      label: 'My Courses',
      icon: <BookOutlined />,
      onClick: () => navigate('/my-courses')
    },
    {
      key: 'profile',
      label: 'My Profile',
      icon: <UserOutlined />,
      onClick: () => navigate('/profile')
    },
    {
      key: 'logout',
      label: 'Logout',
      icon: <LogoutOutlined />,
      danger: true,
      onClick: handleLogout
    }
  ];

  return (
    <ConfigProvider
      theme={{
        algorithm: isDarkMode ? theme.darkAlgorithm : theme.defaultAlgorithm,
        token: {
          colorPrimary: '#FFA116',
          borderRadius: 4,
          fontFamily: 'Inter, sans-serif'
        },
      }}
    >
      <Layout className="min-h-screen !bg-transparent">
        <Header className="flex items-center justify-between px-6 !bg-white/90 dark:!bg-leetgray-900/90 backdrop-blur-md border-b border-gray-200/50 dark:border-leetgray-700/50 transition-colors duration-300 sticky top-0 z-50 shadow-sm">
          <div className="flex items-center gap-3 cursor-pointer group" onClick={() => navigate('/')}>
            <div className="w-10 h-10 bg-gradient-to-br from-orange-400 to-leetaccent rounded-xl flex items-center justify-center font-black text-white text-xl shadow-lg group-hover:scale-105 transition-transform">L</div>
            <span className="text-2xl font-black text-transparent bg-clip-text bg-gradient-to-r from-gray-900 to-gray-600 dark:from-white dark:to-gray-300 hidden sm:block tracking-tight">LMS Platform</span>
          </div>

          <div className="flex items-center gap-4">
            <Button type="text" className="hidden sm:block font-medium text-gray-700 dark:text-gray-300 hover:text-leetaccent" onClick={() => navigate('/courses')}>
              Courses
            </Button>
            <Badge count={cartItems.length} size="small" offset={[-4, 4]}>
              <Button
                type="text"
                icon={<ShoppingCartOutlined className="text-xl !text-gray-800 dark:!text-gray-300 hover:!text-leetaccent" />}
                onClick={() => navigate('/cart')}
                className="flex items-center justify-center hover:!bg-gray-100 dark:hover:!bg-gray-800 rounded-lg"
              />
            </Badge>
            <Button
              type="text"
              icon={isDarkMode ? <BulbFilled className="!text-leetaccent text-lg" /> : <BulbOutlined className="!text-gray-800 text-lg" />}
              onClick={toggleTheme}
              className="flex items-center justify-center hover:!bg-gray-100 dark:hover:!bg-gray-800 rounded-lg"
            />
            {isAuthenticated ? (
              <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
                <Button type="text" className="flex items-center gap-2 px-2 !text-gray-700 dark:!text-gray-300 hover:!bg-gray-100 dark:hover:!bg-gray-800 rounded-lg">
                  <UserOutlined className="text-lg" />
                  <span className="hidden sm:inline font-medium">{user?.fullName}</span>
                </Button>
              </Dropdown>
            ) : (
              <Button type="primary" onClick={() => navigate('/login')}>Sign In</Button>
            )}
          </div>
        </Header>

        <Content className="p-4 sm:p-8 max-w-7xl mx-auto w-full">
          <Outlet />
        </Content>
      </Layout>
    </ConfigProvider>
  );
};

export default MainLayout;
