import React, { useState, useEffect } from 'react';
import { Typography, Spin, message, Input, Select, Pagination, Button } from 'antd';
import { SearchOutlined, FilterOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { courseService } from '../services/courseService';
import { categoryService } from '../services/categoryService';

const { Title, Text } = Typography;
const { Option } = Select;

const CoursesPage = () => {
  const navigate = useNavigate();
  const [courses, setCourses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Filters & Pagination state
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(12);
  const [totalElements, setTotalElements] = useState(0);

  const fetchCategories = async () => {
    try {
      const response = await categoryService.getCategories();
      setCategories(response.data || []);
    } catch (error) {
      console.error('Failed to fetch categories');
    }
  };

  const fetchCourses = async (currentPage = page, currentSize = pageSize, search = searchTerm, category = selectedCategory) => {
    setLoading(true);
    try {
      // Adjusted based on current backend API assuming it takes page & size
      // We might need to adjust search/category params if backend supports them.
      const response = await courseService.getCourses(currentPage - 1, currentSize);
      
      let fetchedCourses = response.data?.content || [];
      
      // Temporary frontend filtering if backend doesn't support query params yet
      if (search) {
        fetchedCourses = fetchedCourses.filter(c => c.title.toLowerCase().includes(search.toLowerCase()));
      }
      if (category) {
        fetchedCourses = fetchedCourses.filter(c => c.category?.id === category);
      }
      
      setCourses(fetchedCourses);
      setTotalElements(response.data?.totalElements || fetchedCourses.length);
    } catch (error) {
      console.error('Failed to fetch courses', error);
      message.error('Không thể tải danh sách khóa học!');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchCourses(page, pageSize, searchTerm, selectedCategory);
  }, [page, pageSize, selectedCategory]); // Removed searchTerm to avoid calling on every keystroke, handle it via Search button

  const onSearch = (value) => {
    setSearchTerm(value);
    setPage(1);
    fetchCourses(1, pageSize, value, selectedCategory);
  };

  const formatPrice = (price) => {
    if (price === 0) return 'Free';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  };

  return (
    <div className="max-w-7xl mx-auto py-8 px-4">
      <div className="mb-8">
        <Title level={2} className="!text-gray-900 dark:!text-white !mb-2">Khám phá Khóa học</Title>
        <Text className="text-gray-500 dark:text-gray-400">Nâng cao kỹ năng lập trình của bạn với các khóa học chất lượng cao.</Text>
      </div>

      {/* Filter Bar */}
      <div className="flex flex-col md:flex-row gap-4 mb-8 bg-white dark:bg-leetgray-800 p-4 rounded-xl shadow-sm border border-gray-100 dark:border-leetgray-700">
        <Input.Search
          placeholder="Tìm kiếm khóa học..."
          allowClear
          enterButton={<Button type="primary" icon={<SearchOutlined />}>Tìm kiếm</Button>}
          size="large"
          onSearch={onSearch}
          className="flex-grow"
        />
        
        <Select
          showSearch
          placeholder={<><FilterOutlined /> Lọc theo danh mục</>}
          size="large"
          allowClear
          className="w-full md:w-64"
          onChange={(value) => {
            setSelectedCategory(value);
            setPage(1);
          }}
        >
          {categories.map(cat => (
            <Option key={cat.id} value={cat.id}>{cat.name}</Option>
          ))}
        </Select>
      </div>

      {/* Course List */}
      {loading ? (
        <div className="flex justify-center items-center h-64">
          <Spin size="large" />
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8 mb-10">
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
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1} d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <Text className="text-gray-500 dark:text-gray-400 text-lg">Không tìm thấy khóa học nào phù hợp.</Text>
              </div>
            )}
          </div>
          
          {totalElements > 0 && (
            <div className="flex justify-center">
              <Pagination 
                current={page} 
                pageSize={pageSize} 
                total={totalElements} 
                onChange={(page, pageSize) => {
                  setPage(page);
                  setPageSize(pageSize);
                }}
                showSizeChanger
              />
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default CoursesPage;
