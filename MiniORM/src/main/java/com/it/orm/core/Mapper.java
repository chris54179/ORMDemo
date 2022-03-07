package com.it.orm.core;

import java.util.HashMap;
import java.util.Map;

public class Mapper {
    private String className;
    private String tableName;
    private Map<String, String> idMapper = new HashMap<>();
    private Map<String, String> propMapper = new HashMap<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getIdMapper() {
        return idMapper;
    }

    public void setIdMapper(Map<String, String> idMapper) {
        this.idMapper = idMapper;
    }

    public Map<String, String> getPropMapper() {
        return propMapper;
    }

    public void setPropMapper(Map<String, String> propMapper) {
        this.propMapper = propMapper;
    }

    @Override
    public String toString() {
        return "Mapper{" +
                "className='" + className + '\'' +
                ", tableName='" + tableName + '\'' +
                ", idMapper=" + idMapper +
                ", propMapper=" + propMapper +
                '}';
    }
}
