import api from './api';

export const progressService = {
  completeLesson: async (lessonId) => {
    return api.post(`/progress/${lessonId}/complete`);
  },
  getCourseProgress: async (courseId) => {
    return api.get(`/progress/course/${courseId}`);
  }
};
