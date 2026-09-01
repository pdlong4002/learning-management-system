import React, { useEffect, useState } from 'react';
import { Result, Button, Card, Spin } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { clearCart } from '../store/slices/cartSlice';

const PaymentResultPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();
  const [status, setStatus] = useState('processing');
  
  useEffect(() => {
    // In a real app, VNPay/MoMo redirects here with query params like: ?vnp_ResponseCode=00 or ?status=success
    const searchParams = new URLSearchParams(location.search);
    const vnp_ResponseCode = searchParams.get('vnp_ResponseCode');
    const simpleStatus = searchParams.get('status'); // for testing
    
    // Simulate processing delay
    const timer = setTimeout(() => {
      if (vnp_ResponseCode === '00' || simpleStatus === 'success') {
        setStatus('success');
        dispatch(clearCart()); // Clear cart on success
      } else if (vnp_ResponseCode || simpleStatus === 'failed') {
        setStatus('error');
      } else {
        // Assume success if no params for testing purposes, or error. 
        // Let's assume error if we arrive here randomly without params.
        setStatus('error');
      }
    }, 1500);
    
    return () => clearTimeout(timer);
  }, [location, dispatch]);

  return (
    <div className="flex items-center justify-center min-h-[calc(100vh-200px)] py-10">
      <Card className="w-full max-w-lg shadow-xl border border-gray-100 dark:border-leetgray-700 bg-white dark:bg-leetgray-800 rounded-2xl">
        {status === 'processing' && (
          <div className="text-center py-10">
            <Spin size="large" />
            <h3 className="mt-4 text-gray-600 dark:text-gray-300">Đang xử lý kết quả thanh toán...</h3>
          </div>
        )}
        
        {status === 'success' && (
          <Result
            status="success"
            title={<span className="text-gray-900 dark:text-white">Thanh toán thành công!</span>}
            subTitle={<span className="text-gray-500 dark:text-gray-400">Cảm ơn bạn đã mua khóa học. Bạn có thể bắt đầu học ngay bây giờ.</span>}
            extra={[
              <Button type="primary" key="learning" size="large" onClick={() => navigate('/my-courses')} className="bg-leetaccent hover:bg-orange-400 border-0">
                Vào học ngay
              </Button>,
              <Button key="home" size="large" onClick={() => navigate('/')}>
                Về trang chủ
              </Button>,
            ]}
          />
        )}
        
        {status === 'error' && (
          <Result
            status="error"
            title={<span className="text-gray-900 dark:text-white">Thanh toán thất bại!</span>}
            subTitle={<span className="text-gray-500 dark:text-gray-400">Đã có lỗi xảy ra trong quá trình giao dịch hoặc bạn đã hủy giao dịch.</span>}
            extra={[
              <Button type="primary" key="cart" size="large" onClick={() => navigate('/cart')}>
                Quay lại giỏ hàng
              </Button>,
            ]}
          />
        )}
      </Card>
    </div>
  );
};

export default PaymentResultPage;
