package com.ramennsama.springboot.lms.dto.event;

import com.ramennsama.springboot.lms.entity.Order;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentSuccessEvent extends ApplicationEvent {
    
    private final Long orderId;

    public PaymentSuccessEvent(Object source, Long orderId) {
        super(source);
        this.orderId = orderId;
    }
}
