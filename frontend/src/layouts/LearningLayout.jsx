import React, { useState, useEffect } from 'react';
import { ConfigProvider, theme, Layout, Button, Tooltip } from 'antd';
import { ArrowLeftOutlined, BulbOutlined, BulbFilled } from '@ant-design/icons';
import { Outlet, useNavigate } from 'react-router-dom';

const { Header, Content } = Layout;

const LearningLayout = () => {
  const [isDarkMode, setIsDarkMode] = useState(false);
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

  return (
    <ConfigProvider
      theme={{
        algorithm: isDarkMode ? theme.darkAlgorithm : theme.defaultAlgorithm,
        token: {
          colorPrimary: '#FFA116',
          fontFamily: 'Inter, sans-serif'
        },
      }}
    >
      <Layout className="h-screen overflow-hidden !bg-gray-50 dark:!bg-[#0a0a0a]">
        <Header className="flex items-center justify-between px-4 sm:px-6 !bg-white dark:!bg-[#1a1a1a] border-b border-gray-200 dark:border-gray-800 h-14 leading-[56px]">
          <div className="flex items-center gap-4">
            <Tooltip title="Quay lại danh sách khóa học">
              <Button 
                type="text" 
                icon={<ArrowLeftOutlined className="text-gray-600 dark:text-gray-300" />} 
                onClick={() => navigate('/my-courses')}
                className="flex items-center justify-center hover:bg-gray-100 dark:hover:bg-gray-800"
              />
            </Tooltip>
            <div className="w-8 h-8 bg-gradient-to-br from-orange-400 to-leetaccent rounded-lg flex items-center justify-center font-black text-white text-sm shadow-md">L</div>
            <span className="font-bold text-gray-800 dark:text-gray-200 hidden sm:block">Learning Zone</span>
          </div>
          
          <div className="flex items-center">
            <Tooltip title="Chuyển chế độ giao diện">
              <Button 
                type="text" 
                icon={isDarkMode ? <BulbFilled className="!text-leetaccent" /> : <BulbOutlined className="!text-gray-600" />} 
                onClick={toggleTheme}
                className="flex items-center justify-center hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg"
              />
            </Tooltip>
          </div>
        </Header>
        
        <Content className="flex flex-col h-[calc(100vh-56px)] overflow-hidden">
          <Outlet />
        </Content>
      </Layout>
    </ConfigProvider>
  );
};

export default LearningLayout;
