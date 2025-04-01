package OUA.OUA_V1.global.exception;

import OUA.OUA_V1.advice.InternalServerException;

public class GcpStorageServerException extends InternalServerException {
    
    private static final String MESSAGE = "이미지 파일 업로드 중 오류가 발생했습니다.";

    public GcpStorageServerException(){
        super(MESSAGE);
    }
}
