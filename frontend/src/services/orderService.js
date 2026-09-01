import api from './api';

export const orderService = {
  checkout: async (checkoutData) => {
    // checkoutData: { courseIds: [], paymentMethod: 'VNPAY' | 'MOMO' }
    return api.post('/orders/checkout', checkoutData);
  },
  getMyOrders: async (page = 0, size = 10) => {
    return api.get(`/orders/my-orders?page=${page}&size=${size}`);
  },
  getOrderById: async (orderId) => {
    return api.get(`/orders/${orderId}`);
  }
};
