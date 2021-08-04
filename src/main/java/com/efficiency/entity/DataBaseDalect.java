package com.efficiency.entity;

/**
 * @author vincent.jiao
 */
public enum DataBaseDalect {
    SQLSERVER("SQLSERVER"),
    ORACLE("ORACLE"),
    MYSQL("MYSQL"),
    DM("DM");

    public String value;
    DataBaseDalect(String value) {
        this.value = value;
    }
}
