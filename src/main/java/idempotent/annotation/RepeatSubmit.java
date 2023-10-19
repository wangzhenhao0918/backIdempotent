package idempotent.annotation;

import java.lang.annotation.*;

/**
 * 实现幂等性注解
 * @Author wzh
 * @Date 2020/10/29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
    /**
     * 防重复操作限时标记数值(存储redis限时标记数值)
     */
    String value();

    /**
     * 防重复操作过期时间(借助redis实现限时控制)
     */
    long expireSeconds() default 10;

}
