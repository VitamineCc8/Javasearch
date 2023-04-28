package com.javasearch.www.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 返回给前端用的结果类
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> {
    // 4000 ok 4002 error 4005 server_error
    public int code;
    public String msg;
    public T data;

    public ResultVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultVO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
