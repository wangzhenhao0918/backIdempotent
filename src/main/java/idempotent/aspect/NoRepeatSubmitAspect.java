package idempotent.aspect;


import com.alibaba.fastjson.JSONObject;
import idempotent.annotation.RepeatSubmit;
import idempotent.constant.RedisKeyConstant;
import idempotent.exception.ApiCode;
import idempotent.exception.ApiException;
import idempotent.util.RedisUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 防止重复提交
 *
 * @author liuyanling
 * @date 2023年10月18日 20:46
 */
@Component
@Aspect
public class NoRepeatSubmitAspect {


    private ExpressionParser parser = new SpelExpressionParser();

    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Resource
    RedisUtil redisUtils;

    /**
     * 定义切点
     */
    @Pointcut("@annotation(repeatSubmit)")
    public void preventDuplication(RepeatSubmit repeatSubmit) {
    }


    @Around("preventDuplication(repeatSubmit)")
    public Object joinPoint(ProceedingJoinPoint pjp, RepeatSubmit repeatSubmit) throws Throwable {
        Method method = this.getMethod(pjp);
        //获取方法的参数值
        Object[] args = pjp.getArgs();
        EvaluationContext context = this.bindParam(method, args);
        /*
         * 获取请求信息
         */
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        String url = request.getRequestURI();
        // 获取注解中引用的参数，采用数字签名算法SHA1对方法签名字符串加签
        Object value = parser.parseExpression(repeatSubmit.value()).getValue(context);
        String valueSign = DigestUtils.sha1Hex(JSONObject.toJSONString(value));

        /*
         *  通过前缀 + url + 指定参数签名 来生成redis上的 key
         */
        String redisKey = RedisKeyConstant.PREVENT_DUPLICATION_PREFIX
                .concat(url)
                .concat(valueSign);


        if (!redisUtils.exists(redisKey)) {
            // 设置防重复操作限时标记
            redisUtils.setEx(redisKey, "submit_duplication", 10);
        } else {
            throw new ApiException(ApiCode.REPEAT_OPERATION);
        }
        return pjp.proceed();
    }

    /**
     * 获取当前执行的方法
     *
     * @param pjp
     * @return
     * @throws NoSuchMethodException
     */
    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Method targetMethod = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
        return targetMethod;
    }

    /**
     * 将方法的参数名和参数值绑定
     *
     * @param method 方法，根据方法获取参数名
     * @param args   方法的参数值
     * @return
     */
    private EvaluationContext bindParam(Method method, Object[] args) {
        //获取方法的参数名
        String[] params = discoverer.getParameterNames(method);
        //将参数名与参数值对应起来
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);
        }
        return context;
    }

}
