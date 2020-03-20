package com.qhc.oa;

public class RespState {
    private int code;
    private String msg;
    private String data;

    public RespState() {
    }

    public RespState(int code, String msg, String data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    public static RespState build(int i){
        return new RespState(i,"ok","success");
    }

    public static RespState build(int code,String msg){
        return new RespState(code,msg,"failed");
    }
}
