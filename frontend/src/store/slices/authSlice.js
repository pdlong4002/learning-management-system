import { createSlice } from '@reduxjs/toolkit';

let savedUser = null;
try {
  const userStr = localStorage.getItem('user');
  if (userStr && userStr !== 'undefined') {
    savedUser = JSON.parse(userStr);
  }
} catch (e) {
  console.error("Failed to parse user from local storage", e);
}

const initialState = {
  user: savedUser,
  token: localStorage.getItem('token') !== 'null' ? localStorage.getItem('token') : null,
  isAuthenticated: (!!localStorage.getItem('token') && localStorage.getItem('token') !== 'null') || !!savedUser,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    loginSuccess: (state, action) => {
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.isAuthenticated = true;
      if (action.payload.token) {
        localStorage.setItem('token', action.payload.token);
      } else {
        localStorage.removeItem('token');
      }
      localStorage.setItem('user', JSON.stringify(action.payload.user));
    },
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    },
  },
});

export const { loginSuccess, logout } = authSlice.actions;
export default authSlice.reducer;
