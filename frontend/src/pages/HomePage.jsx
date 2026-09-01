import React, { useState, useEffect } from 'react';
import { Typography, Button, Spin, message } from 'antd';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { courseService } from '../services/courseService';

const { Title, Text } = Typography;

const HomePage = () => {
  const { user, isAuthenticated } = useSelector((state) => state.auth);
  const navigate = useNavigate();
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const response = await courseService.getCourses(0, 10);
        // Spring Data Page object returns data in content array
        setCourses(response.data?.content || []);
      } catch (error) {
        console.error('Failed to fetch courses', error);
        message.error('Không thể tải danh sách khóa học!');
      } finally {
        setLoading(false);
      }
    };
    fetchCourses();
  }, []);

  const formatPrice = (price) => {
    if (price === 0) return 'Free';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  };

  return (
    <div className="pb-12">
      {/* Hero Section */}
      <div className="relative bg-gradient-to-br from-orange-50 via-white to-blue-50 dark:from-gray-900 dark:via-leetgray-800 dark:to-black rounded-3xl p-10 sm:p-16 mb-12 overflow-hidden shadow-xl dark:shadow-2xl border border-orange-100 dark:border-gray-800">
        <div className="absolute top-0 right-0 -mr-20 -mt-20 w-96 h-96 bg-leetaccent rounded-full mix-blend-multiply dark:mix-blend-lighten filter blur-[100px] opacity-20 dark:opacity-40 animate-pulse"></div>
        <div className="absolute bottom-0 left-0 -ml-20 -mb-20 w-80 h-80 bg-blue-500 rounded-full mix-blend-multiply dark:mix-blend-lighten filter blur-[100px] opacity-10 dark:opacity-30"></div>

        <div className="relative z-10 max-w-2xl">
          <Title level={1} className="!text-gray-900 dark:!text-white !text-4xl sm:!text-6xl !font-black mb-6 leading-tight">
            {isAuthenticated ? (
              <>Welcome back, <br /><span className="text-transparent bg-clip-text bg-gradient-to-r from-leetaccent to-orange-400 dark:to-orange-300">{user?.fullName}</span>!</>
            ) : (
              <>Level Up Your <span className="text-transparent bg-clip-text bg-gradient-to-r from-leetaccent to-orange-400 dark:to-orange-300">Engineering</span> Skills</>
            )}
          </Title>
          <Text className="text-gray-600 dark:text-gray-300 text-lg sm:text-xl block mb-8 leading-relaxed font-light">
            Master algorithms, system design, and build production-ready applications with our curated premium courses. Join thousands of developers today.
          </Text>

          {!isAuthenticated && (
            <Button
              type="primary"
              size="large"
              className="h-14 px-10 text-lg font-bold bg-gradient-to-r from-leetaccent to-orange-500 border-0 shadow-[0_0_20px_rgba(255,161,22,0.4)] hover:shadow-[0_0_30px_rgba(255,161,22,0.6)] hover:scale-105 transition-all duration-300 rounded-xl"
              onClick={() => navigate('/login')}
            >
              Start Learning Now
            </Button>
          )}
        </div>
      </div>

      <div className="flex justify-between items-end mb-8">
        <div>
          <Title level={2} className="!text-gray-900 dark:!text-white !mb-2 !font-bold">
            Featured Courses
          </Title>
          <Text className="text-gray-500 dark:text-gray-400">Hand-picked courses to accelerate your career</Text>
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center items-center h-64">
          <Spin size="large" />
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
          {courses.map(course => (
            <div
              key={course.id}
              onClick={() => navigate(`/course/${course.id}`)}
              className="group bg-white dark:bg-leetgray-800 rounded-2xl overflow-hidden shadow-sm hover:shadow-2xl border border-gray-100 dark:border-leetgray-700 hover:border-leetaccent/50 dark:hover:border-leetaccent/50 hover:-translate-y-2 transition-all duration-300 flex flex-col cursor-pointer"
            >
              <div
                className="h-48 w-full relative bg-cover bg-center overflow-hidden"
                style={{ backgroundImage: `url(${course.thumbnailUrl || '/default-course.png'})` }}
              >
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent opacity-60 group-hover:opacity-80 transition-opacity duration-300"></div>

                <div className="absolute top-3 left-3 bg-black/60 backdrop-blur-md text-white px-3 py-1 text-xs rounded-full font-semibold border border-white/10">
                  {course.category?.name || 'Development'}
                </div>

                <div className="absolute bottom-3 right-3 bg-leetaccent text-white px-3 py-1.5 text-sm rounded-lg font-bold shadow-lg transform group-hover:scale-110 transition-transform duration-300">
                  {formatPrice(course.price)}
                </div>
              </div>

              <div className="p-6 flex flex-col flex-grow">
                <Title level={4} className="!text-gray-900 dark:!text-white !text-lg !font-bold mb-3 line-clamp-2 group-hover:text-leetaccent dark:group-hover:text-leetaccent transition-colors">
                  {course.title}
                </Title>

                <Text className="text-gray-500 dark:text-gray-400 text-sm line-clamp-2 mb-4 flex-grow">
                  {course.description || "No description available for this course."}
                </Text>

                <div className="flex items-center justify-between mt-auto pt-4 border-t border-gray-100 dark:border-gray-700">
                  <div className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center text-white text-xs font-bold shadow-inner">
                      {course.instructor?.fullName?.charAt(0) || 'U'}
                    </div>
                    <Text className="text-sm font-medium text-gray-700 dark:text-gray-300">
                      {course.instructor?.fullName || 'Unknown'}
                    </Text>
                  </div>

                  <div className="flex items-center gap-1 text-yellow-500">
                    <span className="text-sm font-bold">{course.averageRating || "0.0"}</span>
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>
          ))}
          {courses.length === 0 && (
            <div className="col-span-full flex flex-col items-center justify-center py-20 bg-gray-50 dark:bg-leetgray-800/50 rounded-2xl border border-dashed border-gray-200 dark:border-gray-700">
              <svg className="w-16 h-16 text-gray-400 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 002-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
              <Text className="text-gray-500 dark:text-gray-400 text-lg">Chưa có khóa học nào được xuất bản.</Text>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default HomePage;
