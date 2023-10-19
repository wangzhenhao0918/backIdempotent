package idempotent.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 响应结果
 * @anthor theMeng
 * @date 2020/11/01
 */
public class RData<T> implements Serializable {
    public static Object ok() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", 0);
        obj.put("msg", 200);
        return obj;
    }

    public static Object ok(Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", 0);
        obj.put("msg", 200);
        obj.put("data", data);
        return obj;
    }

    public static Object fail() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", -1);
        obj.put("msg", "错误");
        return obj;
    }

    public static Object fail(int errno, String errmsg) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", errno);
        obj.put("msg", errmsg);
        return obj;
    }


}
