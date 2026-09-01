import { createSlice } from '@reduxjs/toolkit';

const loadState = () => {
  try {
    const serializedState = localStorage.getItem('cartState');
    if (serializedState === null) {
      return { items: [], total: 0 };
    }
    return JSON.parse(serializedState);
  } catch (err) {
    return { items: [], total: 0 };
  }
};

const saveState = (state) => {
  try {
    const serializedState = JSON.stringify(state);
    localStorage.setItem('cartState', serializedState);
  } catch (err) {
    // Ignore write errors
  }
};

const initialState = loadState();

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addToCart: (state, action) => {
      const course = action.payload;
      const existingItem = state.items.find(item => item.id === course.id);
      if (!existingItem) {
        state.items.push(course);
        state.total += course.price;
        saveState(state);
      }
    },
    removeFromCart: (state, action) => {
      const courseId = action.payload;
      const existingItem = state.items.find(item => item.id === courseId);
      if (existingItem) {
        state.items = state.items.filter(item => item.id !== courseId);
        state.total -= existingItem.price;
        saveState(state);
      }
    },
    clearCart: (state) => {
      state.items = [];
      state.total = 0;
      saveState(state);
    },
  },
});

export const { addToCart, removeFromCart, clearCart } = cartSlice.actions;
export default cartSlice.reducer;
