package com.ramennsama.springboot.lms.mapper;

import com.ramennsama.springboot.lms.dto.response.OrderItemResponse;
import com.ramennsama.springboot.lms.dto.response.OrderResponse;
import com.ramennsama.springboot.lms.entity.Order;
import com.ramennsama.springboot.lms.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface OrderMapper {

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    OrderResponse toOrderResponse(Order order);
}
