package com.it.orm.core;

import com.it.orm.utils.AnnotationUtil;
import com.it.orm.utils.Dom4jUtil;
import org.dom4j.Document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ORMConfig {
    private static String classpath;
    private static File cfgFile;
    private static Map<String, String> propConfig;
    private static Set<String> mappingSet;
    private static Set<String> entitySet;
    public static List<Mapper> mapperList;

    static {
        classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        cfgFile = new File(classpath + "MiniORM.cfg.xml");
        try {
            classpath = URLDecoder.decode(classpath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (cfgFile.exists()) {
            Document document = Dom4jUtil.getXMLByFilePath(cfgFile.getPath());
            propConfig = Dom4jUtil.Elements2Map(document, "property", "name");
            mappingSet = Dom4jUtil.Elements2Set(document, "mapping", "resource");
            entitySet = Dom4jUtil.Elements2Set(document, "entity", "package");
        } else {
            cfgFile = null;
            System.out.println("未找到MiniORM.cfg.xml");
        }
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        String url = propConfig.get("connection.url");
        String driverClass = propConfig.get("connection.driverClass");
        String username = propConfig.get("connection.username");
        String password = propConfig.get("connection.password");

        Class.forName(driverClass);
        Connection connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(true);
        return connection;
    }

    private void getMapping() throws ClassNotFoundException {
        mapperList = new ArrayList<>();
        for (String xmlPath : mappingSet) {
            Document document = Dom4jUtil.getXMLByFilePath(classpath + xmlPath);
            String className = Dom4jUtil.getPropValue(document, "class", "name");
            String tableName = Dom4jUtil.getPropValue(document, "class", "table");
            Map<String, String> id_id = Dom4jUtil.ElementsID2Map(document);
            Map<String, String> mapping = Dom4jUtil.Elements2Map(document);

            Mapper mapper = new Mapper();
            mapper.setTableName(tableName);
            mapper.setClassName(className);
            mapper.setIdMapper(id_id);
            mapper.setPropMapper(mapping);

            mapperList.add(mapper);
        }

        for (String packagePath : entitySet) {
            Set<String> nameSet = AnnotationUtil.getClassNameByPackage(packagePath);
            for (String name : nameSet) {
                Class clz = Class.forName(name);
                String className = AnnotationUtil.getClassName(clz);
                String tableName = AnnotationUtil.getTableName(clz);
                Map<String, String> id_id = AnnotationUtil.getIdMapper(clz);
                Map<String, String> mapping = AnnotationUtil.getPropMapping(clz);

                Mapper mapper = new Mapper();
                mapper.setTableName(tableName);
                mapper.setClassName(className);
                mapper.setIdMapper(id_id);
                mapper.setPropMapper(mapping);

                mapperList.add(mapper);
            }
        }
    }

    public ORMSession  buildORMSession() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        getMapping();
        return new ORMSession(connection);
    }
}
