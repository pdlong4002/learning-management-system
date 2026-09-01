import React from 'react';
import { Typography, Button, List, Avatar, Card, Empty, message } from 'antd';
import { DeleteOutlined, ShoppingCartOutlined } from '@ant-design/icons';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { removeFromCart, clearCart } from '../store/slices/cartSlice';

const { Title, Text } = Typography;

const CartPage = () => {
  const { items, total } = useSelector((state) => state.cart);
  const { isAuthenticated } = useSelector((state) => state.auth);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleRemove = (id) => {
    dispatch(removeFromCart(id));
    message.success('Đã xóa khóa học khỏi giỏ hàng');
  };

  const handleCheckout = () => {
    if (!isAuthenticated) {
      message.warning('Vui lòng đăng nhập để tiếp tục thanh toán');
      navigate('/login');
      return;
    }
    navigate('/checkout');
  };

  const formatPrice = (price) => {
    if (price === 0) return 'Miễn phí';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  };

  return (
    <div className="max-w-5xl mx-auto py-8 px-4">
      <Title level={2} className="!text-gray-900 dark:!text-white mb-8 flex items-center gap-2">
        <ShoppingCartOutlined /> Giỏ hàng của bạn
      </Title>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="col-span-2">
          {items.length === 0 ? (
            <Card className="bg-white dark:bg-leetgray-800 border border-gray-100 dark:border-leetgray-700 py-10">
              <Empty
                description={<span className="text-gray-500 dark:text-gray-400">Giỏ hàng trống</span>}
              >
                <Button type="primary" onClick={() => navigate('/courses')}>
                  Khám phá khóa học
                </Button>
              </Empty>
            </Card>
          ) : (
            <List
              className="bg-white dark:bg-leetgray-800 rounded-xl border border-gray-100 dark:border-leetgray-700 p-4"
              itemLayout="horizontal"
              dataSource={items}
              renderItem={(item) => (
                <List.Item
                  actions={[
                    <Button 
                      type="text" 
                      danger 
                      icon={<DeleteOutlined />} 
                      onClick={() => handleRemove(item.id)}
                    >
                      Xóa
                    </Button>
                  ]}
                  className="border-b border-gray-100 dark:border-leetgray-700 last:border-0"
                >
                  <List.Item.Meta
                    avatar={
                      <Avatar 
                        shape="square" 
                        size={80} 
                        src={item.thumbnailUrl || '/default-course.png'} 
                        className="rounded-lg"
                      />
                    }
                    title={
                      <a onClick={() => navigate(`/course/${item.id}`)} className="text-lg font-bold text-gray-900 dark:text-white hover:text-leetaccent">
                        {item.title}
                      </a>
                    }
                    description={
                      <div>
                        <Text className="text-sm text-gray-500 block">{item.instructor?.fullName || 'Giảng viên'}</Text>
                        <Text className="text-leetaccent font-bold text-lg mt-1 block">{formatPrice(item.price)}</Text>
                      </div>
                    }
                  />
                </List.Item>
              )}
            />
          )}
        </div>

        <div className="col-span-1">
          <Card className="bg-white dark:bg-leetgray-800 border border-gray-100 dark:border-leetgray-700 shadow-sm sticky top-24">
            <Title level={4} className="!text-gray-900 dark:!text-white mb-6">Tổng cộng</Title>
            <div className="flex justify-between items-end mb-6">
              <Text className="text-gray-500">Thành tiền:</Text>
              <Title level={2} className="!text-gray-900 dark:!text-white !m-0">{formatPrice(total)}</Title>
            </div>
            
            <Button 
              type="primary" 
              size="large" 
              className="w-full h-12 text-lg font-bold bg-leetaccent hover:bg-orange-400 border-0 mb-4"
              disabled={items.length === 0}
              onClick={handleCheckout}
            >
              Thanh toán ngay
            </Button>
            
            <Text className="text-xs text-gray-400 text-center block">
              Bằng việc thanh toán, bạn đồng ý với Điều khoản sử dụng của chúng tôi.
            </Text>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default CartPage;
