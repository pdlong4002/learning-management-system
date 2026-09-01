import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Button, Spin, message, Divider, Collapse, Tag } from 'antd';
import { UserOutlined, PlayCircleOutlined, ClockCircleOutlined, StarFilled } from '@ant-design/icons';
import { courseService } from '../services/courseService';
import { useSelector, useDispatch } from 'react-redux';
import { addToCart } from '../store/slices/cartSlice';

const { Title, Text, Paragraph } = Typography;
const { Panel } = Collapse;

const CourseDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { isAuthenticated } = useSelector((state) => state.auth);
  
  const cartItems = useSelector((state) => state.cart.items);
  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isEnrolled, setIsEnrolled] = useState(false);
  const [checkingEnrollment, setCheckingEnrollment] = useState(false);

  const formatPrice = (price) => {
    if (price === 0) return 'Free';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  };

  useEffect(() => {
    const fetchCourseDetail = async () => {
      try {
        const response = await courseService.getCourseDetail(id);
        setCourse(response.data);
      } catch (error) {
        console.error('Failed to fetch course details', error);
        message.error('Không thể tải chi tiết khóa học!');
        navigate('/'); // Redirect back if error
      } finally {
        setLoading(false);
      }
    };
    
    if (id) {
      fetchCourseDetail();
    }
  }, [id, navigate]);

  // Check enrollment status when authenticated
  useEffect(() => {
    const checkEnrollment = async () => {
      if (isAuthenticated) {
        try {
          setCheckingEnrollment(true);
          // In a real app, you might have a specific endpoint to check 1 course
          // Here we fetch all enrollments and check if this course is in there
          const { enrollmentService } = await import('../services/enrollmentService');
          const res = await enrollmentService.getMyCourses();
          // Depending on API response, it might be res.data or res.data.content
          const enrollments = res.data || [];
          
          // Check if any enrollment matches this course ID
          const enrolled = enrollments.some(e => 
            e.course?.id === Number(id) || e.courseId === Number(id)
          );
          
          setIsEnrolled(enrolled);
        } catch (error) {
          console.error('Failed to check enrollment', error);
        } finally {
          setCheckingEnrollment(false);
        }
      }
    };
    
    checkEnrollment();
  }, [id, isAuthenticated]);

  if (loading || checkingEnrollment) {
    return (
      <div className="flex justify-center items-center h-64">
        <Spin size="large" />
      </div>
    );
  }

  if (!course) {
    return (
      <div className="text-center py-20 text-gray-500">
        Khóa học không tồn tại.
      </div>
    );
  }

  const isInCart = cartItems.some(item => item.id === Number(id));

  return (
    <div className="max-w-5xl mx-auto">
      {/* Course Header Banner */}
      <div className="bg-gradient-to-br from-blue-50 to-orange-50 dark:from-gray-900 dark:to-leetgray-800 text-gray-900 dark:text-white rounded-2xl p-8 mb-8 shadow-lg border border-gray-200 dark:border-gray-800 relative overflow-hidden">
        {/* Background Overlay if thumbnail exists */}
        {course.thumbnailUrl && (
          <div 
            className="absolute inset-0 opacity-5 dark:opacity-10 bg-cover bg-center" 
            style={{ backgroundImage: `url(${course.thumbnailUrl})` }}
          />
        )}
        
        <div className="relative z-10 grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="col-span-2">
            <div className="flex gap-2 mb-4">
              <Tag color="orange" className="border-0 bg-leetaccent/20 text-orange-600 dark:text-leetaccent font-bold">
                {course.category?.name || 'General'}
              </Tag>
              <Tag color="blue" className="border-0 bg-blue-500/20 text-blue-600 dark:text-blue-400 font-bold">
                {course.status}
              </Tag>
            </div>
            
            <Title level={1} className="!text-gray-900 dark:!text-white mb-4">
              {course.title}
            </Title>
            
            <Paragraph className="text-gray-700 dark:text-gray-300 text-lg mb-6 max-w-2xl">
              {course.description}
            </Paragraph>
            
            <div className="flex flex-wrap items-center gap-6 text-sm text-gray-700 dark:text-gray-300">
              <div className="flex items-center gap-2">
                <StarFilled className="text-yellow-500 dark:text-yellow-400 text-lg" />
                <span className="font-bold text-yellow-600 dark:text-yellow-400">{course.averageRating || 0}</span>
                <span>({course.totalReviews || 0} reviews)</span>
              </div>
              <div className="flex items-center gap-2">
                <UserOutlined className="text-lg" />
                <span>Instructor: <strong>{course.instructor?.fullName || 'Unknown'}</strong></span>
              </div>
            </div>
          </div>
          
          {/* Action Card inside Header */}
          <div className="bg-white/80 dark:bg-leetgray-900/80 backdrop-blur-md rounded-xl p-6 shadow-xl border border-gray-200 dark:border-gray-700 text-center lg:mt-0 mt-6 h-fit relative">
            <Title level={2} className="!text-gray-900 dark:!text-white !mb-6 !font-bold">
              {formatPrice(course.price)}
            </Title>
            
            {isEnrolled ? (
              <Button 
                type="primary" 
                size="large" 
                className="w-full h-12 text-lg font-bold shadow-md bg-green-500 hover:bg-green-400 border-0 mb-3"
                onClick={() => navigate('/my-courses')}
              >
                Vào học ngay
              </Button>
            ) : isInCart ? (
              <Button 
                type="primary" 
                size="large" 
                className="w-full h-12 text-lg font-bold shadow-md bg-gray-800 hover:bg-gray-700 dark:bg-gray-700 dark:hover:bg-gray-600 border-0 mb-3"
                onClick={() => navigate('/cart')}
              >
                Đã trong giỏ hàng (Đi tới giỏ)
              </Button>
            ) : (
              <>
                <Button 
                  type="primary" 
                  size="large" 
                  className="w-full h-12 text-lg font-bold shadow-md bg-leetaccent hover:bg-orange-400 border-0 mb-3"
                  onClick={() => {
                    if (!isAuthenticated) {
                      message.warning('Vui lòng đăng nhập để đăng ký!');
                      navigate('/login');
                    } else {
                      dispatch(addToCart(course));
                      message.success('Đã thêm vào giỏ hàng!');
                    }
                  }}
                >
                  Add to Cart
                </Button>
                <Button 
                  size="large" 
                  className="w-full h-12 text-lg font-bold text-gray-700 hover:text-leetaccent border-gray-300"
                  onClick={() => {
                    if (!isAuthenticated) {
                      navigate('/login');
                    } else {
                      dispatch(addToCart(course));
                      navigate('/checkout'); // Direct to checkout
                    }
                  }}
                >
                  Buy Now
                </Button>
              </>
            )}
            
            <Text className="text-gray-500 dark:text-gray-400 text-xs block mt-4">
              30-Day Money-Back Guarantee
            </Text>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Content */}
        <div className="col-span-2">
          <Title level={3} className="!text-gray-900 dark:!text-white mb-6">
            Course Curriculum
          </Title>
          
          {course.sections && course.sections.length > 0 ? (
            <Collapse 
              defaultActiveKey={['0']} 
              ghost 
              className="bg-white dark:bg-leetgray-800 rounded-lg border border-gray-200 dark:border-leetgray-700"
              expandIconPosition="end"
            >
              {[...course.sections].sort((a, b) => a.orderIndex - b.orderIndex).map((section, index) => (
                <Panel 
                  header={<span className="font-semibold text-base text-gray-800 dark:text-gray-200">{section.title}</span>} 
                  key={index.toString()}
                  className="border-b border-gray-100 dark:border-leetgray-700 last:border-0"
                >
                  <div className="flex flex-col gap-2">
                    {section.lessons && section.lessons.length > 0 ? (
                      [...section.lessons].sort((a, b) => a.orderIndex - b.orderIndex).map((lesson) => (
                        <div key={lesson.id} className="flex justify-between items-center py-2 px-4 hover:bg-gray-50 dark:hover:bg-leetgray-700 rounded transition-colors cursor-pointer group">
                          <div className="flex items-center gap-3">
                            <PlayCircleOutlined className="text-gray-400 group-hover:text-leetaccent transition-colors" />
                            <span className="text-gray-700 dark:text-gray-300 group-hover:text-leetaccent transition-colors">{lesson.title}</span>
                          </div>
                          {lesson.durationMinutes && (
                            <div className="flex items-center gap-1 text-xs text-gray-500">
                              <ClockCircleOutlined />
                              {lesson.durationMinutes} min
                            </div>
                          )}
                        </div>
                      ))
                    ) : (
                      <div className="text-gray-400 text-sm px-4 italic">No lessons in this section yet.</div>
                    )}
                  </div>
                </Panel>
              ))}
            </Collapse>
          ) : (
            <div className="bg-white dark:bg-leetgray-800 p-6 rounded-lg text-center text-gray-500 border border-gray-200 dark:border-leetgray-700">
              Curriculum is being updated. Please check back later.
            </div>
          )}
        </div>
        
        {/* Sidebar Info */}
        <div className="col-span-1 space-y-6">
          <div className="bg-white dark:bg-leetgray-800 p-6 rounded-lg shadow-sm border border-gray-100 dark:border-leetgray-700">
            <Title level={4} className="!text-gray-900 dark:!text-white mb-4">About the Instructor</Title>
            <div className="flex items-center gap-4 mb-4">
              <div className="w-12 h-12 bg-leetaccent/20 text-leetaccent rounded-full flex items-center justify-center text-xl font-bold">
                {course.instructor?.fullName?.charAt(0) || 'U'}
              </div>
              <div>
                <div className="font-bold text-gray-900 dark:text-white">{course.instructor?.fullName}</div>
                <div className="text-sm text-gray-500">{course.instructor?.email}</div>
              </div>
            </div>
            <Paragraph className="text-gray-600 dark:text-gray-400 text-sm m-0">
              Senior Software Engineer with over 10 years of experience building scalable applications.
            </Paragraph>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CourseDetailPage;
