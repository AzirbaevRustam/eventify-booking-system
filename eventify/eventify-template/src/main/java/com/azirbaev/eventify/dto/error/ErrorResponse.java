package com.azirbaev.eventify.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String level;
    private String message;
    private List<ErrorDetail> details;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.level = "error";
        this.message = message;
        this.details = null;
    }
}
