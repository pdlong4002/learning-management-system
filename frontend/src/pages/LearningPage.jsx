import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Spin, message, Collapse, Menu, Tabs, Button, Drawer, Empty } from 'antd';
import { PlayCircleFilled, CheckCircleFilled, MenuOutlined, CheckCircleOutlined, BookOutlined } from '@ant-design/icons';
import ReactPlayer from 'react-player';
import { courseService } from '../services/courseService';

const { Title, Text, Paragraph } = Typography;
const { Panel } = Collapse;

const LearningPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [currentLesson, setCurrentLesson] = useState(null);
  const [completedLessons, setCompletedLessons] = useState({});
  const [sidebarVisible, setSidebarVisible] = useState(window.innerWidth > 1024);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth > 1024) {
        setSidebarVisible(true);
      }
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  useEffect(() => {
    const fetchCourseData = async () => {
      try {
        const response = await courseService.getCourseById(id);
        const fetchedCourse = response.data;
        setCourse(fetchedCourse);
        
        // Find first lesson to play if exists
        if (fetchedCourse.sections && fetchedCourse.sections.length > 0) {
          const sortedSections = [...fetchedCourse.sections].sort((a, b) => a.orderIndex - b.orderIndex);
          for (const section of sortedSections) {
            if (section.lessons && section.lessons.length > 0) {
              const sortedLessons = [...section.lessons].sort((a, b) => a.orderIndex - b.orderIndex);
              setCurrentLesson(sortedLessons[0]);
              break;
            }
          }
        }
      } catch (error) {
        console.error('Failed to fetch course', error);
        message.error('Không thể tải dữ liệu khóa học!');
        navigate('/my-courses');
      } finally {
        setLoading(false);
      }
    };
    
    if (id) {
      fetchCourseData();
    }
  }, [id, navigate]);

  const handleLessonEnded = () => {
    if (currentLesson) {
      setCompletedLessons(prev => ({...prev, [currentLesson.id]: true}));
      message.success(`Đã hoàn thành: ${currentLesson.title}`);
      // In a real app, you would call progressService.markLessonCompleted(currentLesson.id)
    }
  };

  const handleSelectLesson = (lesson) => {
    setCurrentLesson(lesson);
    if (window.innerWidth <= 1024) {
      setSidebarVisible(false); // Auto close sidebar on mobile after selecting
    }
  };

  const renderCurriculum = () => {
    if (!course?.sections?.length) {
      return (
        <div className="p-8 flex justify-center">
          <Empty description={<span className="text-gray-500">Chưa có bài học</span>} image={Empty.PRESENTED_IMAGE_SIMPLE} />
        </div>
      );
    }

    const sortedSections = [...course.sections].sort((a, b) => a.orderIndex - b.orderIndex);
    
    return (
      <Collapse 
        defaultActiveKey={sortedSections.map(s => s.id.toString())} 
        ghost 
        expandIconPosition="end"
        className="learning-collapse"
      >
        {sortedSections.map(section => (
          <Panel 
            key={section.id.toString()}
            header={
              <div className="font-bold text-gray-800 dark:text-gray-200 text-sm">
                Section {section.orderIndex}: {section.title}
              </div>
            }
            className="border-b border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-[#1a1a1a]"
          >
            <div className="flex flex-col">
              {section.lessons && [...section.lessons].sort((a, b) => a.orderIndex - b.orderIndex).map((lesson, idx) => {
                const isActive = currentLesson?.id === lesson.id;
                const isCompleted = completedLessons[lesson.id];
                
                return (
                  <div 
                    key={lesson.id}
                    onClick={() => handleSelectLesson(lesson)}
                    className={`flex items-start gap-3 p-3 cursor-pointer transition-colors ${
                      isActive 
                        ? 'bg-orange-50 dark:bg-leetaccent/20 border-l-4 border-leetaccent' 
                        : 'hover:bg-gray-100 dark:hover:bg-gray-800 border-l-4 border-transparent'
                    }`}
                  >
                    <div className="mt-0.5">
                      {isCompleted ? (
                        <CheckCircleFilled className="text-green-500 text-base" />
                      ) : isActive ? (
                        <PlayCircleFilled className="text-leetaccent text-base" />
                      ) : (
                        <CheckCircleOutlined className="text-gray-400 dark:text-gray-600 text-base" />
                      )}
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className={`text-sm line-clamp-2 leading-snug ${
                        isActive ? 'font-bold text-gray-900 dark:text-white' : 'text-gray-700 dark:text-gray-300'
                      }`}>
                        {idx + 1}. {lesson.title}
                      </div>
                      <div className="text-xs text-gray-500 mt-1 flex items-center gap-2">
                        <span><PlayCircleFilled className="text-xs" /> Video</span>
                        {lesson.durationMinutes && <span>• {lesson.durationMinutes} min</span>}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </Panel>
        ))}
      </Collapse>
    );
  };

  const tabItems = [
    {
      key: 'overview',
      label: 'Tổng quan',
      children: (
        <div className="py-4">
          <Title level={4} className="!text-gray-900 dark:!text-white">Về khóa học này</Title>
          <Paragraph className="text-gray-700 dark:text-gray-300 whitespace-pre-line text-base leading-relaxed">
            {course?.description}
          </Paragraph>
          
          <Title level={4} className="!text-gray-900 dark:!text-white mt-8">Giảng viên</Title>
          <div className="flex items-center gap-4 mt-4 bg-gray-50 dark:bg-[#1a1a1a] p-4 rounded-xl border border-gray-100 dark:border-gray-800">
            <div className="w-14 h-14 bg-leetaccent/20 text-leetaccent rounded-full flex items-center justify-center text-xl font-bold">
              {course?.instructor?.fullName?.charAt(0) || 'U'}
            </div>
            <div>
              <div className="font-bold text-lg text-gray-900 dark:text-white">{course?.instructor?.fullName || 'Unknown Instructor'}</div>
              <div className="text-sm text-gray-500">{course?.instructor?.email}</div>
            </div>
          </div>
        </div>
      ),
    },
    {
      key: 'notes',
      label: 'Ghi chú',
      children: (
        <div className="py-8 text-center text-gray-500">
          Tính năng ghi chú đang được phát triển.
        </div>
      ),
    },
    {
      key: 'qa',
      label: 'Hỏi đáp (Q&A)',
      children: (
        <div className="py-8 text-center text-gray-500">
          Chưa có câu hỏi nào. Hãy là người đầu tiên đặt câu hỏi!
        </div>
      ),
    }
  ];

  const getVideoUrl = (url) => {
    if (!url) return 'https://www.youtube.com/watch?v=LXb3EKWsInQ';
    if (url.includes('youtube.com') || url.includes('youtu.be')) return url;
    // Always return a working YouTube video if the DB contains fake/broken links
    return 'https://www.youtube.com/watch?v=LXb3EKWsInQ';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-full w-full bg-white dark:bg-[#0a0a0a]">
        <Spin size="large" tip="Đang tải dữ liệu học tập..." />
      </div>
    );
  }

  return (
    <div className="flex flex-col lg:flex-row h-full w-full bg-white dark:bg-[#0a0a0a]">
      {/* Main Content Area (Video & Info) */}
      <div className="flex-1 flex flex-col min-w-0 overflow-y-auto custom-scrollbar">
        {/* Video Player Section */}
        <div className="w-full bg-black flex justify-center relative shadow-xl">
          <div className="w-full max-w-6xl aspect-video relative">
            {!course?.sections?.length ? (
              <div className="absolute inset-0 flex flex-col items-center justify-center text-gray-400 bg-gray-900 border border-gray-800">
                <PlayCircleFilled className="text-6xl mb-4 opacity-30" />
                <h3 className="text-2xl font-bold text-white mb-2">Khóa học đang cập nhật</h3>
                <p>Giảng viên chưa tải lên bài giảng nào cho khóa học này.</p>
              </div>
            ) : currentLesson ? (
              <ReactPlayer
                url={getVideoUrl(currentLesson.videoUrl)}
                width="100%"
                height="100%"
                controls={true}
                playing={true}
                onEnded={handleLessonEnded}
                config={{
                  youtube: {
                    playerVars: { showinfo: 1, modestbranding: 1 }
                  }
                }}
              />
            ) : (
              <div className="absolute inset-0 flex flex-col items-center justify-center text-gray-500 bg-gray-900 border border-gray-800">
                <PlayCircleFilled className="text-6xl mb-4 opacity-50" />
                <p>Chưa chọn bài học</p>
              </div>
            )}
          </div>
        </div>

        {/* Course Info & Tabs */}
        <div className="max-w-6xl w-full mx-auto p-4 sm:p-8 flex-1">
          <div className="flex justify-between items-start mb-6">
            <div>
              <Title level={3} className="!text-gray-900 dark:!text-white !mb-2 !font-bold">
                {currentLesson?.title || 'Chưa chọn bài học'}
              </Title>
              <Text className="text-leetaccent font-semibold flex items-center gap-2">
                <BookOutlined /> {course?.title}
              </Text>
            </div>
            <Button 
              type="text" 
              icon={<MenuOutlined />} 
              onClick={() => setSidebarVisible(true)}
              className="lg:hidden flex items-center bg-gray-100 dark:bg-gray-800"
            >
              Nội dung
            </Button>
          </div>
          
          <Tabs 
            defaultActiveKey="overview" 
            items={tabItems} 
            className="learning-tabs"
            tabBarStyle={{ marginBottom: 0, borderBottom: '1px solid #e5e7eb' }}
          />
        </div>
      </div>

      {/* Curriculum Sidebar (Desktop) */}
      <div className={`hidden lg:flex flex-col w-96 border-l border-gray-200 dark:border-gray-800 bg-white dark:bg-[#0f0f0f] flex-shrink-0 transition-all duration-300 shadow-[-10px_0_15px_-3px_rgba(0,0,0,0.1)] z-10 overflow-hidden`}>
        <div className="p-4 border-b border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-[#1a1a1a]">
          <Title level={5} className="!text-gray-900 dark:!text-white !m-0 !font-bold">
            Nội dung khóa học
          </Title>
        </div>
        <div className="flex-1 overflow-y-auto custom-scrollbar">
          {renderCurriculum()}
        </div>
      </div>

      {/* Curriculum Drawer (Mobile/Tablet) */}
      <Drawer
        title="Nội dung khóa học"
        placement="right"
        onClose={() => setSidebarVisible(false)}
        open={sidebarVisible && window.innerWidth <= 1024}
        width={320}
        styles={{
           body: { padding: 0, backgroundColor: isDarkMode() ? '#0f0f0f' : '#fff' },
           header: { backgroundColor: isDarkMode() ? '#1a1a1a' : '#f9fafb', borderBottomColor: isDarkMode() ? '#1f2937' : '#e5e7eb' }
        }}
        className={isDarkMode() ? 'dark-drawer' : ''}
      >
        <div className="overflow-y-auto h-full bg-white dark:bg-[#0f0f0f]">
           {renderCurriculum()}
        </div>
      </Drawer>
      
      {/* Helper to check dark mode for Drawer inline styles */}
      <span className="hidden" id="dark-mode-helper"></span>
    </div>
  );
};

// Quick helper to read body class for Drawer styling
const isDarkMode = () => document.documentElement.classList.contains('dark');

export default LearningPage;
