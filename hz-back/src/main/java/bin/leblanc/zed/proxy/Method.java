package bin.leblanc.zed.proxy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.METHOD)
@Inherited
public @interface Method {
    String value();
}
