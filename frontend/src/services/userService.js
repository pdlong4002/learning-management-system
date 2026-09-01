import api from './api';

export const userService = {
  getProfile: async () => {
    return api.get('/users/profile'); // Assuming a profile endpoint exists or will exist
  },
  updateProfile: async (data) => {
    return api.put('/users', data); 
  },
  changePassword: async (data) => {
    return api.put('/users/change-password', data);
  },
  updateAvatar: async (base64Image) => {
    return api.put('/users/avatar', { imageUrl: base64Image });
  }
};
