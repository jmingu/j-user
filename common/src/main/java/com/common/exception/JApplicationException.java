package com.common.exception;


import com.common.enums.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JApplicationException extends RuntimeException {

    private int resultCode;
    private String resultMessage;

    public JApplicationException(CodeEnum codeEnum) {
        this.resultCode = codeEnum.getCode();
        this.resultMessage = null;
    }

    public JApplicationException(String resultMessage) {
        super(resultMessage);
        this.resultCode = CodeEnum.ERROR.getCode();
        this.resultMessage = resultMessage;
    }

    public JApplicationException(CodeEnum codeEnum, String resultMessage) {
        super(resultMessage);
        this.resultCode = codeEnum.getCode();
        this.resultMessage = resultMessage;
    }

}
