import React, { useState, useEffect } from 'react';
import { Typography, Table, Tag, Button, message, Spin, Space } from 'antd';
import { Link } from 'react-router-dom';
import { courseService } from '../services/courseService';

const { Title } = Typography;

const AdminDashboardPage = () => {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });

  useEffect(() => {
    fetchPendingCourses(pagination.current - 1, pagination.pageSize);
  }, []);

  const fetchPendingCourses = async (page, size) => {
    try {
      setLoading(true);
      const response = await courseService.getPendingCourses(page, size);
      const data = response.data;
      setCourses(data.content);
      setPagination({
        ...pagination,
        current: data.number + 1,
        total: data.totalElements
      });
    } catch (error) {
      console.error(error);
      message.error('Failed to load pending courses');
    } finally {
      setLoading(false);
    }
  };

  const handleTableChange = (newPagination) => {
    fetchPendingCourses(newPagination.current - 1, newPagination.pageSize);
  };

  const handleApprove = async (id) => {
    try {
      setLoading(true);
      await courseService.changeCourseStatus(id, 'PUBLISHED');
      message.success('Course approved and published successfully');
      fetchPendingCourses(pagination.current - 1, pagination.pageSize);
    } catch (error) {
      console.error(error);
      message.error(error.response?.data?.message || 'Failed to approve course');
      setLoading(false);
    }
  };

  const handleReject = async (id) => {
    try {
      setLoading(true);
      await courseService.changeCourseStatus(id, 'DRAFT');
      message.success('Course rejected and sent back to draft');
      fetchPendingCourses(pagination.current - 1, pagination.pageSize);
    } catch (error) {
      console.error(error);
      message.error(error.response?.data?.message || 'Failed to reject course');
      setLoading(false);
    }
  };

  const columns = [
    {
      title: 'Course Title',
      dataIndex: 'title',
      key: 'title',
      render: (text, record) => <Link to={`/instructor/course/${record.id}`} className="font-semibold text-blue-600 hover:underline">{text}</Link>
    },
    {
      title: 'Instructor',
      dataIndex: ['instructor', 'firstName'],
      key: 'instructor',
      render: (text, record) => `${record.instructor?.firstName} ${record.instructor?.lastName}`
    },
    {
      title: 'Price',
      dataIndex: 'price',
      key: 'price',
      render: (price) => price === 0 ? 'Free' : `$${price}`
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status) => <Tag color="orange">{status}</Tag>
    },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Link to={`/instructor/course/${record.id}`}>
            <Button size="small">Review</Button>
          </Link>
          <Button 
            type="primary" 
            size="small" 
            className="bg-green-500 hover:bg-green-600 border-green-500"
            onClick={() => handleApprove(record.id)}
          >
            Approve
          </Button>
          <Button 
            danger 
            size="small"
            onClick={() => handleReject(record.id)}
          >
            Reject
          </Button>
        </Space>
      )
    }
  ];

  return (
    <div className="max-w-7xl mx-auto py-8 px-4">
      <div className="flex justify-between items-center mb-6">
        <Title level={2} className="!mb-0 dark:text-white">Admin Dashboard</Title>
      </div>

      <div className="bg-white dark:bg-[#111] p-6 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-800">
        <Title level={4} className="mb-4">Pending Courses Awaiting Approval</Title>
        <Table
          columns={columns}
          dataSource={courses}
          rowKey="id"
          pagination={pagination}
          loading={loading}
          onChange={handleTableChange}
        />
      </div>
    </div>
  );
};

export default AdminDashboardPage;
