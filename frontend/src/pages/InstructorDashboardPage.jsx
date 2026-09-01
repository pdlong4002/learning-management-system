import React, { useState, useEffect } from 'react';
import { Typography, Button, Table, Space, Tag, Card, Row, Col, Statistic, Spin, message } from 'antd';
import { PlusOutlined, EditOutlined, BarChartOutlined, VideoCameraOutlined, UsergroupAddOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { courseService } from '../services/courseService';

const { Title } = Typography;

const InstructorDashboardPage = () => {
  const navigate = useNavigate();
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({ totalCourses: 0, totalStudents: 0, totalRevenue: 0 });

  const fetchCourses = async () => {
    try {
      setLoading(true);
      const response = await courseService.getMyCreatedCourses(0, 100); // Fetch up to 100 courses for dashboard
      const courseList = response.data?.content || [];
      setCourses(courseList);
      
      // Calculate dummy stats based on courses (In a real app, these come from backend)
      setStats({
        totalCourses: courseList.length,
        totalStudents: courseList.length * 42, // Dummy multiplier for demo
        totalRevenue: courseList.reduce((sum, c) => sum + (c.price || 0), 0) * 12 // Dummy multiplier
      });
    } catch (error) {
      console.error(error);
      message.error("Failed to fetch your courses");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  const columns = [
    {
      title: 'Course',
      dataIndex: 'title',
      key: 'title',
      render: (text, record) => (
        <Space>
          <div className="w-16 h-12 bg-gray-200 dark:bg-gray-700 rounded-md overflow-hidden">
            {record.thumbnailUrl ? (
              <img src={record.thumbnailUrl} alt={text} className="w-full h-full object-cover" />
            ) : (
              <div className="w-full h-full flex items-center justify-center text-gray-400"><VideoCameraOutlined /></div>
            )}
          </div>
          <span className="font-semibold text-gray-900 dark:text-gray-100">{text}</span>
        </Space>
      ),
    },
    {
      title: 'Price',
      dataIndex: 'price',
      key: 'price',
      render: (price) => <span className="font-medium">${price}</span>,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        const color = status === 'PUBLISHED' ? 'green' : 'orange';
        return <Tag color={color}>{status || 'DRAFT'}</Tag>;
      },
    },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => (
        <Button 
          type="primary" 
          ghost
          icon={<EditOutlined />} 
          onClick={() => navigate(`/instructor/course/${record.id}`)}
          className="hover:!bg-leetaccent hover:!text-white hover:!border-leetaccent"
        >
          Manage
        </Button>
      ),
    },
  ];

  if (loading) {
    return <div className="flex justify-center items-center h-screen"><Spin size="large" /></div>;
  }

  return (
    <div className="max-w-7xl mx-auto py-8">
      <div className="flex justify-between items-center mb-8">
        <Title level={2} className="!mb-0 !text-gray-900 dark:!text-white">Instructor Dashboard</Title>
        <Button 
          type="primary" 
          size="large" 
          icon={<PlusOutlined />} 
          onClick={() => navigate('/instructor/course/new')}
          className="bg-leetaccent hover:bg-orange-400 border-0 shadow-lg shadow-orange-500/30"
        >
          Create New Course
        </Button>
      </div>

      <Row gutter={[24, 24]} className="mb-8">
        <Col xs={24} md={8}>
          <Card className="rounded-2xl shadow-sm border-gray-100 dark:border-gray-800 dark:bg-[#1a1a1a]">
            <Statistic 
              title={<span className="text-gray-500 font-medium">Total Courses</span>} 
              value={stats.totalCourses} 
              prefix={<VideoCameraOutlined className="text-leetaccent mr-2" />} 
              valueStyle={{ fontWeight: 'bold' }}
            />
          </Card>
        </Col>
        <Col xs={24} md={8}>
          <Card className="rounded-2xl shadow-sm border-gray-100 dark:border-gray-800 dark:bg-[#1a1a1a]">
            <Statistic 
              title={<span className="text-gray-500 font-medium">Total Students (Est.)</span>} 
              value={stats.totalStudents} 
              prefix={<UsergroupAddOutlined className="text-blue-500 mr-2" />} 
              valueStyle={{ fontWeight: 'bold' }}
            />
          </Card>
        </Col>
        <Col xs={24} md={8}>
          <Card className="rounded-2xl shadow-sm border-gray-100 dark:border-gray-800 dark:bg-[#1a1a1a]">
            <Statistic 
              title={<span className="text-gray-500 font-medium">Revenue (Est.)</span>} 
              value={stats.totalRevenue} 
              prefix={<BarChartOutlined className="text-green-500 mr-2" />} 
              precision={2}
              valueStyle={{ fontWeight: 'bold' }}
            />
          </Card>
        </Col>
      </Row>

      <div className="bg-white dark:bg-[#111111] p-6 rounded-3xl shadow-sm border border-gray-100 dark:border-gray-800">
        <h3 className="text-xl font-bold mb-6 text-gray-900 dark:text-white">Your Courses</h3>
        <Table 
          columns={columns} 
          dataSource={courses} 
          rowKey="id" 
          pagination={false}
          className="custom-table"
        />
      </div>

      <style>{`
        .custom-table .ant-table {
          background: transparent !important;
        }
        .custom-table .ant-table-thead > tr > th {
          background: #f9fafb !important;
          color: #6b7280;
          font-weight: 600;
          border-bottom: 1px solid #f3f4f6;
        }
        .dark .custom-table .ant-table-thead > tr > th {
          background: #1a1a1a !important;
          color: #9ca3af;
          border-bottom: 1px solid #374151;
        }
        .custom-table .ant-table-tbody > tr > td {
          border-bottom: 1px solid #f3f4f6;
        }
        .dark .custom-table .ant-table-tbody > tr > td {
          border-bottom: 1px solid #374151;
        }
        .dark .custom-table .ant-table-tbody > tr.ant-table-row:hover > td {
          background: #1f1f1f !important;
        }
      `}</style>
    </div>
  );
};

export default InstructorDashboardPage;
