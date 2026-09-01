import React, { useState } from 'react';
import { Typography, Button, Card, Radio, message, Divider } from 'antd';
import { useSelector } from 'react-redux';
import { useNavigate, Navigate } from 'react-router-dom';
import { orderService } from '../services/orderService';

const { Title, Text } = Typography;

const CheckoutPage = () => {
  const { items, total } = useSelector((state) => state.cart);
  const [paymentMethod, setPaymentMethod] = useState('VNPAY');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  if (items.length === 0) {
    return <Navigate to="/cart" replace />;
  }

  const handlePayment = async () => {
    setLoading(true);
    try {
      const courseIds = items.map(item => item.id);
      const response = await orderService.checkout({ courseIds, paymentMethod });
      
      // The backend should return a payment URL to redirect to (e.g. VNPay sandbox URL)
      if (response && response.data && response.data.paymentUrl) {
        window.location.href = response.data.paymentUrl;
      } else {
        // Fallback if backend just processes it directly (for testing)
        message.success('Thanh toán thành công (Test mode)!');
        navigate('/payment-result?status=success');
      }
    } catch (error) {
      console.error('Checkout error:', error);
      message.error('Khởi tạo thanh toán thất bại!');
      setLoading(false);
    }
  };

  const formatPrice = (price) => {
    if (price === 0) return 'Miễn phí';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  };

  return (
    <div className="max-w-4xl mx-auto py-8 px-4">
      <Title level={2} className="!text-gray-900 dark:!text-white mb-8">Thanh toán</Title>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Payment Methods */}
        <div className="space-y-6">
          <Card className="bg-white dark:bg-leetgray-800 border border-gray-100 dark:border-leetgray-700 shadow-sm">
            <Title level={4} className="!text-gray-900 dark:!text-white mb-6">Phương thức thanh toán</Title>
            
            <div className="flex flex-col gap-4">
              <div 
                className={`cursor-pointer border-2 p-5 rounded-xl transition-all duration-300 flex items-center gap-4 ${paymentMethod === 'VNPAY' ? 'border-leetaccent bg-orange-50 dark:bg-orange-900/10' : 'border-gray-200 dark:border-leetgray-700 hover:border-gray-300 dark:hover:border-gray-600'}`}
                onClick={() => setPaymentMethod('VNPAY')}
              >
                <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${paymentMethod === 'VNPAY' ? 'border-leetaccent' : 'border-gray-400'}`}>
                  {paymentMethod === 'VNPAY' && <div className="w-2.5 h-2.5 bg-leetaccent rounded-full"></div>}
                </div>
                <div className="w-12 h-12 bg-blue-100 flex items-center justify-center rounded-lg overflow-hidden shrink-0">
                  <span className="text-blue-600 font-bold text-sm">VNPAY</span>
                </div>
                <div>
                  <div className="font-bold text-gray-900 dark:text-white text-base mb-1">Thanh toán qua VNPAY</div>
                  <div className="text-sm text-gray-500 dark:text-gray-400">Hỗ trợ thẻ ATM, Visa, Mastercard, QR Code</div>
                </div>
              </div>
              
              <div 
                className={`cursor-pointer border-2 p-5 rounded-xl transition-all duration-300 flex items-center gap-4 ${paymentMethod === 'MOMO' ? 'border-leetaccent bg-orange-50 dark:bg-orange-900/10' : 'border-gray-200 dark:border-leetgray-700 hover:border-gray-300 dark:hover:border-gray-600'}`}
                onClick={() => setPaymentMethod('MOMO')}
              >
                <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${paymentMethod === 'MOMO' ? 'border-leetaccent' : 'border-gray-400'}`}>
                  {paymentMethod === 'MOMO' && <div className="w-2.5 h-2.5 bg-leetaccent rounded-full"></div>}
                </div>
                <div className="w-12 h-12 bg-pink-100 flex items-center justify-center rounded-lg overflow-hidden shrink-0">
                  <span className="text-pink-600 font-bold text-sm">MOMO</span>
                </div>
                <div>
                  <div className="font-bold text-gray-900 dark:text-white text-base mb-1">Ví MoMo</div>
                  <div className="text-sm text-gray-500 dark:text-gray-400">Quét mã QR để thanh toán</div>
                </div>
              </div>
            </div>
          </Card>
        </div>

        {/* Order Summary */}
        <div>
          <Card className="bg-white dark:bg-leetgray-800 border border-gray-100 dark:border-leetgray-700 shadow-sm sticky top-24">
            <Title level={4} className="!text-gray-900 dark:!text-white mb-4">Tóm tắt đơn hàng</Title>
            
            <div className="space-y-3 mb-6 max-h-60 overflow-y-auto pr-2">
              {items.map(item => (
                <div key={item.id} className="flex justify-between items-start gap-4">
                  <Text className="text-gray-700 dark:text-gray-300 line-clamp-2">{item.title}</Text>
                  <Text className="font-medium text-gray-900 dark:text-white whitespace-nowrap">{formatPrice(item.price)}</Text>
                </div>
              ))}
            </div>
            
            <Divider className="border-gray-200 dark:border-leetgray-700" />
            
            <div className="flex justify-between items-end mb-6">
              <Text className="text-lg font-bold text-gray-900 dark:text-white">Tổng thanh toán:</Text>
              <Title level={2} className="!text-leetaccent !m-0">{formatPrice(total)}</Title>
            </div>
            
            <Button 
              type="primary" 
              size="large" 
              className="w-full h-12 text-lg font-bold bg-gradient-to-r from-leetaccent to-orange-500 border-0 hover:shadow-lg"
              loading={loading}
              onClick={handlePayment}
            >
              Tiến hành thanh toán
            </Button>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
