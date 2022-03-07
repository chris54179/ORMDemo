package com.it.orm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ORMSession {
    private Connection connection;

    public ORMSession(Connection connection) {
        this.connection = connection;
    }

    public void save(Object entity) throws IllegalAccessException, SQLException {
        String insertSQL = "";

        List<Mapper> mapperList = ORMConfig.mapperList;

        for (Mapper mapper : mapperList) {

            if (mapper.getClassName().equals(entity.getClass().getName())) {
                String tableName = mapper.getTableName();
                String insertSQL1 = "insert into " + tableName + "( ";
                String insertSQL2 = " ) values ( ";
                Field[] fields = entity.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    String columnName = mapper.getPropMapper().get(field.getName());
                    String columnValue = field.get(entity).toString();
                    insertSQL1 += columnName + ",";
                    insertSQL2 += "'" + columnValue + "',";
                }
                insertSQL = insertSQL1.substring(0, insertSQL1.length() - 1) + insertSQL2.substring(0, insertSQL2.length() - 1) + " )";
                break;
            }
        }
        System.out.println("MiniORM-save: " + insertSQL);

        PreparedStatement ps = connection.prepareStatement(insertSQL);
        ps.executeUpdate();
        ps.close();
    }

    public void delete(Object entity) throws NoSuchFieldException, IllegalAccessException, SQLException {
        String delSQL = "delete from ";
        List<Mapper> mapperList = ORMConfig.mapperList;

        for (Mapper mapper : mapperList) {
            if (mapper.getClassName().equals(entity.getClass().getName())) {
                String tableName = mapper.getTableName();
                delSQL += tableName + " where ";
                Object[] idProp = mapper.getIdMapper().keySet().toArray();
                Object[] idColumn = mapper.getIdMapper().values().toArray();

                Field field = entity.getClass().getDeclaredField(idProp[0].toString());
                field.setAccessible(true);

                String idVal = field.get(entity).toString();
                delSQL += idColumn[0].toString() + " = " + idVal;

                break;
            }
        }
        PreparedStatement ps = connection.prepareStatement(delSQL);
        ps.executeUpdate();
        ps.close();
    }

    public Object findOne(Class clz, Object id) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        String querySQL = " select * from ";
        List<Mapper> mapperList = ORMConfig.mapperList;
        for (Mapper mapper : mapperList) {
            if (mapper.getClassName().equals(clz.getName())) {
                String tableName = mapper.getTableName();
                Object[] idColumn = mapper.getIdMapper().values().toArray();

                querySQL += tableName + " where " + idColumn[0].toString() + " = " + id;
                break;
            }
        }

        System.out.println("MiniORM-findOne:" + querySQL);
        PreparedStatement ps = connection.prepareStatement(querySQL);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Object obj = clz.newInstance();
            for (Mapper mapper : mapperList) {
                if (mapper.getClassName().equals(clz.getName())) {
                    Map<String, String> propMap = mapper.getPropMapper();
                    Set<String> keySet = propMap.keySet();
                    for (String prop : keySet) {
                        String column = propMap.get(prop);
                        Field field = clz.getDeclaredField(prop);
                        field.setAccessible(true);
                        field.set(obj, rs.getObject(column));
                    }
                    break;
                }
            }
            ps.close();
            rs.close();
            return obj;
        } else {
            return null;
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
}
