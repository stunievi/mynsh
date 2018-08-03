package com.beeasy.hzback.core.helper;

import javassist.*;

public interface DialectFK {

    default void byteCodes(){
        ClassPool pool = ClassPool.getDefault();
        try {
            pool.importPackage("org.hibernate.dialect");
            CtClass cc = pool.get("org.hibernate.mapping.Column");
            CtMethod method = cc.getDeclaredMethod("getSqlType",new CtClass[]{
                    pool.get("org.hibernate.dialect.Dialect"),
                    pool.get("org.hibernate.engine.spi.Mapping")
            });
//            method.setBody("{ " +
//                    "if ( sqlType == null ) { " +
//                    "   sqlType = $1.getTypeName( (int)getSqlTypeCode($2), (int)getLength(), (int)getPrecision(), (int)getScale() );" +
//                    "} " +
//                    "return sqlType; " +
//            "}");
            method.insertAfter("if($1 instanceof com.beeasy.hzback.core.helper.DialectFK){return ((com.beeasy.hzback.core.helper.DialectFK)$1).convertColumn(sqlType);}");
//            cc.addMethod(new CtMethod());
//            cc.setModifiers(cc.getModifiers() & ~Modifier.ABSTRACT);
            cc.toClass();
            cc.defrost();

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    default String convertColumn(String column){
        return column;
    }
}
