package com.beeasy.hzback.core.helper;

import org.hibernate.dialect.MySQL5Dialect;

public class MySQL5DialectFK extends MySQL5Dialect {
    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        return " COMMENT ''" ;
//        return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey);
    }
}
