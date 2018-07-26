package com.beeasy.hzback.core.helper;

import org.hibernate.dialect.DB2Dialect;

public class DB2DialectFK extends DB2Dialect{
    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        return " COMMENT ''" ;
//        return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey);
    }
}
