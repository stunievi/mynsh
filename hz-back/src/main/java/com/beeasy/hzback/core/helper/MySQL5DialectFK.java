package com.beeasy.hzback.core.helper;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.internal.StandardTableExporter;
import org.hibernate.tool.schema.spi.Exporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class MySQL5DialectFK extends MySQL5Dialect {

    @Autowired
    EntityManager entityManager;

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        return " COMMENT ''" ;
//        return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey);
    }

//    @Override
//    public Exporter<Table> getTableExporter() {
//        return new StandardTableExporter( this ){
//
//        };
//    }
}
