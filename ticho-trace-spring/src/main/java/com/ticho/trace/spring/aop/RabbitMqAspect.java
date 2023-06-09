package com.ticho.trace.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * mq链路处理
 *
 * @author zhajianjun
 * @date 2023-06-09 11:35
 */
@Aspect
@Component
@ConditionalOnClass({Aspect.class, RabbitListener.class})
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class RabbitMqAspect extends AbstractAspect {

    @Pointcut("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        return trace(joinPoint, "RabbitMQ消息队列", null);
    }

}
