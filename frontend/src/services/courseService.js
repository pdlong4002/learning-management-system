import api from './api';

export const courseService = {
  getCourses: async (page = 0, size = 10) => {
    return api.get(`/courses?page=${page}&size=${size}`);
  },
  getCourseDetail: async (id) => {
    return api.get(`/courses/${id}`);
  },
  getMyCreatedCourses: async (page = 0, size = 10) => {
    return api.get(`/courses/instructor/my-courses?page=${page}&size=${size}`);
  },
  getPendingCourses: async (page = 0, size = 10) => {
    return api.get(`/courses/admin/pending-courses?page=${page}&size=${size}`);
  },
  createCourse: async (courseData) => {
    return api.post('/courses', courseData);
  },
  updateCourse: async (id, courseData) => {
    return api.put(`/courses/${id}`, courseData);
  },
  changeCourseStatus: async (id, status) => {
    return api.patch(`/courses/${id}/status?status=${status}`);
  },
  createSection: async (courseId, sectionData) => {
    return api.post(`/courses/${courseId}/sections`, sectionData);
  },
  updateSection: async (sectionId, sectionData) => {
    return api.put(`/sections/${sectionId}`, sectionData);
  },
  deleteSection: async (sectionId) => {
    return api.delete(`/sections/${sectionId}`);
  },
  createLesson: async (sectionId, lessonData) => {
    return api.post(`/sections/${sectionId}/lessons`, lessonData);
  },
  updateLesson: async (lessonId, lessonData) => {
    return api.put(`/lessons/${lessonId}`, lessonData);
  },
  deleteLesson: async (lessonId) => {
    return api.delete(`/lessons/${lessonId}`);
  }
};
