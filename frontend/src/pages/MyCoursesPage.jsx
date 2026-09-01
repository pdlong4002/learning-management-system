import React, { useState, useEffect } from 'react';
import { Typography, Spin, Card, Progress, Button, Empty, message } from 'antd';
import { PlayCircleOutlined, BookOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { enrollmentService } from '../services/enrollmentService';

const { Title, Text } = Typography;

const MyCoursesPage = () => {
  const [enrollments, setEnrollments] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMyCourses = async () => {
      try {
        const response = await enrollmentService.getMyCourses();
        // Assuming response.data is an array of EnrollmentResponse
        setEnrollments(response.data || []);
      } catch (error) {
        console.error('Failed to fetch enrollments', error);
        message.error('Không thể tải danh sách khóa học của bạn');
      } finally {
        setLoading(false);
      }
    };

    fetchMyCourses();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto py-8 px-4">
      <div className="mb-8">
        <Title level={2} className="!text-gray-900 dark:!text-white !mb-2">Khóa học của tôi</Title>
        <Text className="text-gray-500 dark:text-gray-400">Tiếp tục hành trình học tập của bạn.</Text>
      </div>

      {enrollments.length === 0 ? (
        <Card className="bg-white dark:bg-leetgray-800 border-gray-100 dark:border-leetgray-700 py-16 text-center shadow-sm">
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description={<span className="text-gray-500 dark:text-gray-400">Bạn chưa tham gia khóa học nào.</span>}
          >
            <Button type="primary" size="large" className="mt-4 bg-leetaccent hover:bg-orange-400 border-0" onClick={() => navigate('/courses')}>
              Khám phá khóa học ngay
            </Button>
          </Empty>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {enrollments.map((enrollment) => {
            const course = enrollment.course;
            if (!course) return null;
            
            // Generate a random progress for demo since progress logic might not be fully implemented
            const progress = enrollment.progressPercentage || Math.floor(Math.random() * 60) + 10;
            
            return (
              <Card 
                key={enrollment.id}
                hoverable
                className="overflow-hidden border-gray-100 dark:border-leetgray-700 bg-white dark:bg-leetgray-800 shadow-sm hover:shadow-xl transition-shadow rounded-xl flex flex-col h-full body-no-padding"
                bodyStyle={{ padding: 0, display: 'flex', flexDirection: 'column', height: '100%' }}
                onClick={() => navigate(`/learn/${course.id}`)}
              >
                <div 
                  className="h-40 w-full bg-cover bg-center relative group"
                  style={{ backgroundImage: `url(${course.thumbnailUrl || '/default-course.png'})` }}
                >
                  <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                    <PlayCircleOutlined className="text-white text-5xl opacity-80" />
                  </div>
                </div>
                
                <div className="p-5 flex flex-col flex-grow">
                  <Text className="text-xs font-bold text-leetaccent uppercase tracking-wider mb-2 block">
                    {course.category?.name || 'Development'}
                  </Text>
                  
                  <Title level={4} className="!text-gray-900 dark:!text-white !text-lg !mb-4 line-clamp-2 leading-tight">
                    {course.title}
                  </Title>
                  
                  <div className="mt-auto">
                    <div className="flex justify-between items-center mb-1">
                      <Text className="text-xs text-gray-500 dark:text-gray-400">Tiến độ hoàn thành</Text>
                      <Text className="text-xs font-bold text-gray-700 dark:text-gray-300">{progress}%</Text>
                    </div>
                    <Progress percent={progress} showInfo={false} strokeColor="#FFA116" trailColor="rgba(0,0,0,0.1)" />
                  </div>
                </div>
              </Card>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default MyCoursesPage;
