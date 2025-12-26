package com.filemanagement.Model.Response;


import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseModel<T> {

    private String message;

    private String status;

    private int statusCode;

    private T data;

    public ResponseModel(T data, String message, String status, int statusCode) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
    }

    public ResponseModel(String message, String status, int statusCode) {
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
    }
}