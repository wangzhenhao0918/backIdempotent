package idempotent.controller;


import idempotent.annotation.RepeatSubmit;
import idempotent.api.RData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * redis 使用幂等性
 */
@RestController
@RequestMapping("/ide/mark")
public class BackIdeMarkController {

    /**
     * 测试幂等性
     * @return
     */
    @RepeatSubmit("#id")
    @GetMapping("/test/Idempotence")
    public Object testIdempotence(@RequestParam Long id) {
        String ideMark = "幂等测试成功";
        return RData.ok(ideMark) ;
    }
}
