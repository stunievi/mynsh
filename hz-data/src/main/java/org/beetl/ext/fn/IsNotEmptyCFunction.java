package org.beetl.ext.fn;


import org.beetl.core.Context;
import org.beetl.core.Function;
import org.osgl.util.C;

import java.util.Map;

public final class IsNotEmptyCFunction implements Function {
    @Override
    public Boolean call(Object[] objects, Context context) {
        return objects[0] instanceof Map && C.notEmpty((Map) objects[0]);
    }
}
