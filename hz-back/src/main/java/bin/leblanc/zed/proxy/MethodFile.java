package bin.leblanc.zed.proxy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Inherited
public @interface MethodFile {
    String value();
}
