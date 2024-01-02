package top.ticho.trace.spring.aop;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * xxl-job链路处理
 *
 * @author zhajianjun
 * @date 2023-06-09 11:35
 */
@Aspect
@Component
@ConditionalOnClass({Aspect.class, XxlJob.class})
public class XxlJobAspect extends AbstractAspect {

    @Pointcut("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        return trace(joinPoint, "XxlJob定时任务", null);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

}
