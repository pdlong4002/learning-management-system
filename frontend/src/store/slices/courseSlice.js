import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  currentCourse: null,
  searchQuery: '',
  selectedCategory: null,
};

const courseSlice = createSlice({
  name: 'course',
  initialState,
  reducers: {
    setCurrentCourse: (state, action) => {
      state.currentCourse = action.payload;
    },
    setSearchQuery: (state, action) => {
      state.searchQuery = action.payload;
    },
    setSelectedCategory: (state, action) => {
      state.selectedCategory = action.payload;
    },
  },
});

export const { setCurrentCourse, setSearchQuery, setSelectedCategory } = courseSlice.actions;
export default courseSlice.reducer;
