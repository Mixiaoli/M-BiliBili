package com.Mixiao.bilibili.service.handler;


import com.Mixiao.bilibili.domain.JsonResponse;
import com.Mixiao.bilibili.domain.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice//预设全局数据
@Order(Ordered.HIGHEST_PRECEDENCE) //这个全局异常处理器优先级最高
public class CommonGlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)//标识这是一个哪个类型的异常 用全局的exc
    @ResponseBody//封装参数传递
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e){
        String errorMsg = e.getMessage();
        if(e instanceof ConditionException){//左边是否是右边的实例
            String errorCode = ((ConditionException)e).getCode();//获得相关错误状态码
            return new JsonResponse<>(errorCode, errorMsg);//状态码和信息的返回
        }else{
            return new JsonResponse<>("500",errorMsg);
        }
    }
}
