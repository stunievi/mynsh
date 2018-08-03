package com.beeasy.hzback.core.helper;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;
import org.hibernate.tool.schema.spi.Exporter;

public class DB2DialectFK extends DB2Dialect implements DialectFK {

    public DB2DialectFK() {
        super();
        byteCodes();
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        return " COMMENT ''";
//        return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey);
    }


    /**
     * kill foreign key !!!!!
     *
     * @return
     */
    @Override
    public Exporter<ForeignKey> getForeignKeyExporter() {
        return new StandardForeignKeyExporter(this) {
            @Override
            public String[] getSqlCreateStrings(ForeignKey foreignKey, Metadata metadata) {
                return new String[]{};
            }
        };
    }

    @Override
    public String convertColumn(String column) {
        switch (column) {
            case JSONConverter.type:
                return "VARCHAR(32672)";
            case JSONConverter.blobType:
                return "BLOB(16M)";
        }
        return column;
    }
}
