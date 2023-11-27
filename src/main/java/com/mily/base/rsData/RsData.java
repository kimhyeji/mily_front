package com.mily.base.rsData;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RsData<T> {
    private String resultCode;
    private String msg;
    private T data;

    public boolean isSuccess() {
        return resultCode.startsWith("S-");
    }

    public static <T> RsData<T> of (String resultCode, String msg, T data) {
        return new RsData<>(resultCode, msg, data);
    }

    public static <T> RsData<T> of (String resultCode, String msg) {
        return of(resultCode, msg, null);
    }

    public boolean isFail() {
        return !isSuccess();
    }
}