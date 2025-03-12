package com.steam.steamapi.pogo;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public Result(int i, String success) {
    }

    public static <E> Result<E> success() {
        return new Result<>(0, "Success");
    }

    public static Result Success() {
        return new Result(0, "Success", null);
    }

    public static Result error(String message) {
        return new Result(1, message, null);
    }
}
