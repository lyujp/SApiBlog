package moe.lyu.sapiblog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Resp{
    private Integer code;
    private String msg;
    private Object data;

    public static Resp success() {
        return new Resp(200, null, null);
    }

    public static Resp success(String msg) {
        return new Resp(200, msg, null);
    }

    public static Resp success(Object data) {
        return new Resp(200, null, data);
    }

    public static Resp error(Integer code, String msg) {
        return new Resp(code, msg, null);
    }

    public static Resp error(Integer code, String msg, Object data) {
        return new Resp(code, msg, data);
    }

}
