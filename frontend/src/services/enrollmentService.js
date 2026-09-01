import api from './api';

export const enrollmentService = {
  enrollCourse: async (courseId) => {
    return api.post(`/enrollments/${courseId}`);
  },
  getMyCourses: async (page = 0, size = 10) => {
    return api.get(`/enrollments/my-courses?page=${page}&size=${size}`);
  }
};
