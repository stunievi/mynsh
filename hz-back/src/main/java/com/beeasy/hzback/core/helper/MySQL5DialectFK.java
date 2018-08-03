package com.beeasy.hzback.core.helper;

import javassist.*;
import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.dialect.ColumnAliasExtractor;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.engine.jdbc.env.internal.DefaultSchemaNameResolver;
import org.hibernate.engine.jdbc.env.spi.SchemaNameResolver;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;
import org.hibernate.tool.schema.internal.StandardTableExporter;
import org.hibernate.tool.schema.spi.Exporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class MySQL5DialectFK extends MySQL5Dialect implements DialectFK{

    EntityManager entityManager;

    public MySQL5DialectFK() {
        super();
        byteCodes();
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        return " COMMENT ''" ;
//        return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey);
    }


    @Override
    public Exporter<ForeignKey> getForeignKeyExporter() {
        return new StandardForeignKeyExporter(this){
            @Override
            public String[] getSqlCreateStrings(ForeignKey foreignKey, Metadata metadata) {
                return new String[]{};
            }
        };
    }

    @Override
    public String convertColumn(String column) {
        switch (column){
            case JSONConverter.type:
                return "LONGTEXT";
            case JSONConverter.blobType:
                return "LONGBLOB";
        }
        return column;
    }
}
