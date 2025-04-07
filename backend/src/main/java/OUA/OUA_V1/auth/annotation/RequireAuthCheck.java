package OUA.OUA_V1.auth.annotation;

import OUA.OUA_V1.auth.util.SecureResource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuthCheck {

    String targetId();  // 권한을 확인할 대상 리소스 (예: "productId", "orderId")

    Class<? extends SecureResource> targetDomain();
}
