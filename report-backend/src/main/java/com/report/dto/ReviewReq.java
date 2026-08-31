package com.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 导师批阅请求：填写评语。
 */
public class ReviewReq {

    @NotBlank(message = "评语不能为空")
    @Size(max = 2000, message = "评语长度不能超过2000字")
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
