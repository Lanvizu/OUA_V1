package OUA.OUA_V1.global.util;

import OUA.OUA_V1.advice.OUACustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ProblemDetail;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionLogger {

    public static void info(HttpServletRequest request, OUACustomException exception) {
        setMDC(request, exception);
        log.info("handle info level exception");
        clearMDC();
    }

    // MDC에 메타데이터 설정
    private static void setMDC(HttpServletRequest request, OUACustomException exception) {
        StackTraceElement origin = exception.getStackTrace()[0];

        MDC.put("httpMethod", request.getMethod());
        MDC.put("requestUri", request.getRequestURI());
        MDC.put("statusCode", exception.statusCode());
        MDC.put("sourceClass", origin.getClassName());
        MDC.put("sourceMethod", origin.getMethodName());
        MDC.put("exceptionClass", exception.getClass().getSimpleName());
        MDC.put("exceptionMessage", exception.getMessage());
    }

    private static void clearMDC() {
        MDC.clear();
    }

    // MDC 초기화
    public static void info(ProblemDetail problemDetail) {
        setMDC(problemDetail);
        log.info("handle info level exception");
        clearMDC();
    }

    private static void setMDC(ProblemDetail problemDetail) {
        Map<String, Object> details = problemDetail.getProperties();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, Object> stringObjectEntry : details.entrySet()) {
            map.put(stringObjectEntry.getKey(), java.lang.String.valueOf(stringObjectEntry.getValue()));
        }
        MDC.setContextMap(map);
    }

    public static void warn(ProblemDetail problemDetail) {
        setMDC(problemDetail);
        log.warn("handle warn level exception");
        clearMDC();
    }

    public static void error(ProblemDetail problemDetail) {
        setMDC(problemDetail);
        log.error("handle error level exception");
        clearMDC();
    }
}
