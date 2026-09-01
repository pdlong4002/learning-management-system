import React, { useState, useEffect } from 'react';
import { Typography, Form, Input, Button, Tabs, message, InputNumber, Select, Spin, Collapse, Modal, Space, Upload } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import { PlusOutlined, DeleteOutlined, EditOutlined, VideoCameraOutlined } from '@ant-design/icons';
import { courseService } from '../services/courseService';
import { useSelector } from 'react-redux';

const { Title, Text } = Typography;
const { Panel } = Collapse;
const { TextArea } = Input;

const CourseBuilderPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isNew = id === 'new';
  const { user } = useSelector(state => state.auth);
  const isAdmin = user?.role === 'ROLE_ADMIN' || user?.role === 'ADMIN';
  
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [course, setCourse] = useState(null);
  const [activeTab, setActiveTab] = useState('1');
  const [thumbnailBase64, setThumbnailBase64] = useState(null);

  // Modals state
  const [sectionModalVisible, setSectionModalVisible] = useState(false);
  const [lessonModalVisible, setLessonModalVisible] = useState(false);
  const [currentSection, setCurrentSection] = useState(null); // For edit/add lesson
  const [currentLesson, setCurrentLesson] = useState(null); // For edit lesson
  const [sectionForm] = Form.useForm();
  const [lessonForm] = Form.useForm();

  useEffect(() => {
    if (!isNew) {
      fetchCourseDetail();
    }
  }, [id]);

  const fetchCourseDetail = async () => {
    try {
      setLoading(true);
      const response = await courseService.getCourseDetail(id);
      const data = response.data;
      setCourse(data);
      setThumbnailBase64(data.thumbnailUrl);
      form.setFieldsValue({
        title: data.title,
        description: data.description,
        price: data.price,
        categoryId: data.category?.id || 1 // default category
      });
    } catch (error) {
      message.error("Failed to load course details");
    } finally {
      setLoading(false);
    }
  };

  const handleSaveBasicInfo = async (values) => {
    setLoading(true);
    try {
      if (isNew) {
        const payload = {
          title: values.title,
          description: values.description,
          price: values.price,
          thumbnailUrl: thumbnailBase64,
          categoryId: values.categoryId || 1
        };
        const res = await courseService.createCourse(payload);
        message.success("Course created successfully!");
        navigate(`/instructor/course/${res.data.id}`);
      } else {
        const payload = {
          title: values.title,
          description: values.description,
          price: values.price,
          thumbnailUrl: thumbnailBase64,
          categoryId: values.categoryId || 1
        };
        await courseService.updateCourse(id, payload);
        message.success("Course updated successfully!");
        fetchCourseDetail();
      }
    } catch (error) {
      message.error(error.response?.data?.message || "Failed to save course");
    } finally {
      setLoading(false);
    }
  };

  const handleChangeStatus = async (status) => {
    try {
      await courseService.changeCourseStatus(id, status);
      message.success(`Course status updated to ${status}`);
      fetchCourseDetail();
    } catch (error) {
      message.error(error.response?.data?.message || "Failed to update status");
    }
  };

  // --- SECTION HANDLERS ---
  const handleOpenSectionModal = (section = null) => {
    setCurrentSection(section);
    if (section) {
      sectionForm.setFieldsValue({ title: section.title, orderIndex: section.orderIndex });
    } else {
      sectionForm.resetFields();
    }
    setSectionModalVisible(true);
  };

  const handleSaveSection = async (values) => {
    try {
      if (currentSection) {
        await courseService.updateSection(currentSection.id, values);
        message.success("Section updated");
      } else {
        await courseService.createSection(id, values);
        message.success("Section added");
      }
      setSectionModalVisible(false);
      fetchCourseDetail();
    } catch (error) {
      message.error("Failed to save section");
    }
  };

  const handleDeleteSection = async (sectionId) => {
    try {
      await courseService.deleteSection(sectionId);
      message.success("Section deleted");
      fetchCourseDetail();
    } catch (error) {
      message.error("Failed to delete section");
    }
  };

  // --- LESSON HANDLERS ---
  const handleOpenLessonModal = (sectionId, lesson = null) => {
    setCurrentSection({ id: sectionId });
    setCurrentLesson(lesson);
    if (lesson) {
      lessonForm.setFieldsValue({ 
        title: lesson.title, 
        videoUrl: lesson.videoUrl, 
        duration: lesson.duration,
        orderIndex: lesson.orderIndex
      });
    } else {
      lessonForm.resetFields();
    }
    setLessonModalVisible(true);
  };

  const handleSaveLesson = async (values) => {
    try {
      if (currentLesson) {
        await courseService.updateLesson(currentLesson.id, values);
        message.success("Lesson updated");
      } else {
        await courseService.createLesson(currentSection.id, values);
        message.success("Lesson added");
      }
      setLessonModalVisible(false);
      fetchCourseDetail();
    } catch (error) {
      message.error("Failed to save lesson");
    }
  };

  const handleDeleteLesson = async (lessonId) => {
    try {
      await courseService.deleteLesson(lessonId);
      message.success("Lesson deleted");
      fetchCourseDetail();
    } catch (error) {
      message.error("Failed to delete lesson");
    }
  };

  if (loading && !isNew && !course) {
    return <div className="flex justify-center items-center h-screen"><Spin size="large" /></div>;
  }

  const basicInfoTab = (
    <div className="max-w-3xl py-4">
      <Form form={form} layout="vertical" onFinish={handleSaveBasicInfo}>
        <Form.Item name="title" label="Course Title" rules={[{ required: true, message: 'Please enter title' }]}>
          <Input size="large" className="rounded-lg" placeholder="e.g. Master React in 30 Days" />
        </Form.Item>
        <Form.Item name="description" label="Description" rules={[{ required: true, message: 'Please enter description' }]}>
          <TextArea rows={5} className="rounded-lg" placeholder="Describe your course..." />
        </Form.Item>
        <div className="flex gap-4">
          <Form.Item name="price" label="Price ($)" rules={[{ required: true, message: 'Please enter price' }]}>
            <InputNumber size="large" className="rounded-lg w-32" min={0} step={0.01} />
          </Form.Item>
          <Form.Item name="categoryId" label="Category" rules={[{ required: true, message: 'Please select category' }]} className="flex-1">
            <Select size="large" className="rounded-lg">
              <Select.Option value={1}>Programming</Select.Option>
              <Select.Option value={2}>Design</Select.Option>
              <Select.Option value={3}>Business</Select.Option>
            </Select>
          </Form.Item>
        </div>

        <Form.Item label="Course Thumbnail">
          <Upload
            name="thumbnail"
            listType="picture-card"
            className="thumbnail-uploader"
            showUploadList={false}
            beforeUpload={(file) => {
              const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
              if (!isJpgOrPng) message.error('You can only upload JPG/PNG file!');
              const isLt2M = file.size / 1024 / 1024 < 2;
              if (!isLt2M) message.error('Image must smaller than 2MB!');
              return isJpgOrPng && isLt2M;
            }}
            customRequest={({ file, onSuccess }) => {
              const reader = new FileReader();
              reader.addEventListener('load', () => {
                setThumbnailBase64(reader.result);
                onSuccess("ok");
              });
              reader.readAsDataURL(file);
            }}
          >
            {thumbnailBase64 ? (
              <img src={thumbnailBase64} alt="thumbnail" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
            ) : (
              <div>
                <PlusOutlined />
                <div style={{ marginTop: 8 }}>Upload</div>
              </div>
            )}
          </Upload>
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading} size="large" className="bg-leetaccent hover:bg-orange-400 border-0 h-12 px-8 font-bold rounded-lg shadow-lg">
            Save Basic Info
          </Button>
        </Form.Item>
      </Form>
    </div>
  );

  const curriculumTab = (
    <div className="py-4">
      {isNew ? (
        <div className="p-8 text-center bg-gray-50 dark:bg-[#1a1a1a] rounded-xl border border-dashed border-gray-300 dark:border-gray-700">
          <Text className="text-gray-500">Please save Basic Info first before building curriculum.</Text>
        </div>
      ) : (
        <>
          <div className="flex justify-between items-center mb-6">
            <Title level={4} className="!mb-0 dark:text-white">Curriculum Sections</Title>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => handleOpenSectionModal()}>
              Add Section
            </Button>
          </div>

          {course?.sections?.length === 0 ? (
            <div className="p-8 text-center bg-gray-50 dark:bg-[#1a1a1a] rounded-xl">
              <Text className="text-gray-500">No sections added yet. Start by adding a new section.</Text>
            </div>
          ) : (
            <Collapse className="bg-white dark:bg-[#111] border-gray-200 dark:border-gray-800 rounded-xl">
              {course?.sections?.sort((a,b)=>a.orderIndex - b.orderIndex).map(section => (
                <Panel 
                  header={<span className="font-bold">{section.title}</span>} 
                  key={section.id}
                  extra={
                    <Space>
                      <Button type="text" size="small" icon={<EditOutlined />} onClick={(e) => { e.stopPropagation(); handleOpenSectionModal(section); }} />
                      <Button type="text" size="small" danger icon={<DeleteOutlined />} onClick={(e) => { e.stopPropagation(); handleDeleteSection(section.id); }} />
                    </Space>
                  }
                >
                  <div className="mb-4">
                    {section.lessons?.sort((a,b)=>a.orderIndex - b.orderIndex).map((lesson, idx) => (
                      <div key={lesson.id} className="flex justify-between items-center p-3 mb-2 bg-gray-50 dark:bg-[#1a1a1a] border border-gray-100 dark:border-gray-800 rounded-lg group">
                        <div className="flex items-center gap-3">
                          <VideoCameraOutlined className="text-gray-400" />
                          <span className="font-medium text-gray-800 dark:text-gray-200">{idx + 1}. {lesson.title}</span>
                          <span className="text-xs text-gray-500 bg-gray-200 dark:bg-gray-700 px-2 py-1 rounded">{lesson.duration}m</span>
                        </div>
                        <Space className="opacity-0 group-hover:opacity-100 transition-opacity">
                          <Button type="text" size="small" icon={<EditOutlined />} onClick={() => handleOpenLessonModal(section.id, lesson)} />
                          <Button type="text" size="small" danger icon={<DeleteOutlined />} onClick={() => handleDeleteLesson(lesson.id)} />
                        </Space>
                      </div>
                    ))}
                    {section.lessons?.length === 0 && (
                      <div className="text-center p-4 text-sm text-gray-400">No lessons in this section.</div>
                    )}
                  </div>
                  <Button type="dashed" block icon={<PlusOutlined />} onClick={() => handleOpenLessonModal(section.id)}>
                    Add Lesson
                  </Button>
                </Panel>
              ))}
            </Collapse>
          )}
        </>
      )}
    </div>
  );

  return (
    <div className="max-w-5xl mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <Title level={2} className="!mb-0 dark:text-white flex items-center">
          {isNew ? 'Create New Course' : `Edit Course: ${course?.title}`}
          {!isNew && course && (
            <span className={`ml-4 text-sm px-3 py-1 rounded-full whitespace-nowrap ${course.status === 'DRAFT' ? 'bg-gray-200 text-gray-700' : course.status === 'PENDING' ? 'bg-yellow-200 text-yellow-800' : 'bg-green-200 text-green-800'}`}>
              {course.status}
            </span>
          )}
        </Title>
        <Space>
          {!isNew && course?.status === 'DRAFT' && (
            <Button type="primary" className="bg-yellow-500 hover:bg-yellow-400 border-0" onClick={() => handleChangeStatus('PENDING')}>
              Submit for Review
            </Button>
          )}
          {!isNew && course?.status === 'PENDING' && isAdmin && (
            <Button type="primary" className="bg-green-500 hover:bg-green-400 border-0" onClick={() => handleChangeStatus('PUBLISHED')}>
              Approve (Admin)
            </Button>
          )}
          <Button onClick={() => navigate('/instructor/dashboard')}>Back to Dashboard</Button>
        </Space>
      </div>

      <div className="bg-white dark:bg-[#111111] p-2 sm:p-6 rounded-3xl shadow-sm border border-gray-100 dark:border-gray-800">
        <Tabs activeKey={activeTab} onChange={setActiveTab} size="large">
          <Tabs.TabPane tab="Basic Info" key="1">
            {basicInfoTab}
          </Tabs.TabPane>
          <Tabs.TabPane tab="Curriculum" key="2" disabled={isNew}>
            {curriculumTab}
          </Tabs.TabPane>
        </Tabs>
      </div>

      {/* SECTION MODAL */}
      <Modal
        title={currentSection ? "Edit Section" : "Add New Section"}
        open={sectionModalVisible}
        onCancel={() => setSectionModalVisible(false)}
        footer={null}
      >
        <Form form={sectionForm} layout="vertical" onFinish={handleSaveSection}>
          <Form.Item name="title" label="Section Title" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="orderIndex" label="Order Index" rules={[{ required: true }]}>
            <InputNumber min={1} />
          </Form.Item>
          <Button type="primary" htmlType="submit" block>Save Section</Button>
        </Form>
      </Modal>

      {/* LESSON MODAL */}
      <Modal
        title={currentLesson ? "Edit Lesson" : "Add New Lesson"}
        open={lessonModalVisible}
        onCancel={() => setLessonModalVisible(false)}
        footer={null}
      >
        <Form form={lessonForm} layout="vertical" onFinish={handleSaveLesson}>
          <Form.Item name="title" label="Lesson Title" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="videoUrl" label="Video URL (YouTube/Vimeo)" rules={[{ required: true }]}>
            <Input placeholder="https://youtube.com/watch?v=..." />
          </Form.Item>
          <Form.Item name="duration" label="Duration (Minutes)">
            <InputNumber min={0} />
          </Form.Item>
          <Form.Item name="orderIndex" label="Order Index" rules={[{ required: true }]}>
            <InputNumber min={1} />
          </Form.Item>
          <Button type="primary" htmlType="submit" block>Save Lesson</Button>
        </Form>
      </Modal>
    </div>
  );
};

export default CourseBuilderPage;
