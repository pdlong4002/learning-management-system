import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { message, Spin } from 'antd';
import { loginSuccess } from '../store/slices/authSlice';
import api from '../services/api';

const OAuth2RedirectHandler = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const fetchUser = async () => {
      const urlParams = new URLSearchParams(location.search);
      const success = urlParams.get('success');

      if (success === 'true') {
        try {
          // Token is now in HttpOnly cookie, so we can directly call /me
          const response = await api.get('/users/me');
          
          dispatch(loginSuccess({
            user: response.data,
            // We don't save token to localStorage for OAuth2, we rely on the cookie
            token: null 
          }));
          
          message.success('Đăng nhập thành công!');
          navigate('/');
        } catch (error) {
          console.error('Failed to fetch user profile after OAuth2 login:', error);
          message.error('Lỗi khi tải thông tin tài khoản!');
          navigate('/login');
        }
      } else {
        message.error('Đăng nhập thất bại hoặc bị huỷ!');
        navigate('/login');
      }
    };

    fetchUser();
  }, [location, navigate, dispatch]);

  return (
    <div className="flex justify-center items-center h-screen bg-gray-50 dark:bg-leetgray-900">
      <div className="text-center">
        <Spin size="large" />
        <h2 className="mt-4 text-gray-700 dark:text-gray-300">Đang xác thực thông tin...</h2>
      </div>
    </div>
  );
};

export default OAuth2RedirectHandler;
