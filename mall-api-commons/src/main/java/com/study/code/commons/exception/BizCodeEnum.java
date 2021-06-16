package com.study.code.commons.exception;

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系统未知错误"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    BIZ_EXCEPTION(10002, "自定义异常"),
    STATUS_EXCEPTION(10003, "状态校验失败");

    private int code;
    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
