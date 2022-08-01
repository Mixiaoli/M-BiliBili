package com.Mixiao.bilibili.domain.exception;
//条件异常 继承的就是在运行过程中抛出异常
public class ConditionException extends RuntimeException{
    //序列化
    private static final long serialVersionUID = 1L;

    private String code; //返回值code

    public ConditionException(String code, String name){
        super(name);
        this.code = code;
    }

    public ConditionException(String name){
        super(name);
        code = "500";//通用返回错误代码
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
