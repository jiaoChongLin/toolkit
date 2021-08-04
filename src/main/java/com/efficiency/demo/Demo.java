package com.efficiency.demo;

import java.sql.*;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/7/18 16:59
 * @Version : 1.0
 */
public class Demo {
    //输出表名
    public static void printTableNames(DatabaseMetaData databaseMetaData)throws Exception{
        //获取表名的结果集
        ResultSet rs = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
        while(rs.next()){
            String tableName = rs.getString("TABLE_NAME");
            System.out.println(tableName);
        }
    }

    //输出列名、类型、注释
    public static void printColumnInfo(DatabaseMetaData databaseMetaData)throws Exception{
        ResultSet rs = databaseMetaData.getColumns(null, null, "%", null);

        ResultSetMetaData metaData = rs.getMetaData();

        while(rs.next()){
            for (int i = 1; i < metaData.getColumnCount(); i++) {
                System.out.println(metaData.getColumnName(i) +":"+ rs.getObject(i));
            }

            System.out.println();

            /**
             * REMARKS:注释
             * TABLE_NAME:表名
             * COLUMN_NAME:列名
             * COLUMN_SIZE ：列大小
             * TYPE_NAME:类型
             * IS_NULLABLE:是不是null
             * DECIMAL_DIGITS : 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null
             * COLUMN_DEF String : 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
             * IS_AUTOINCREMENT String => 指示此列是否自动增加
             *      YES --- 如果该列自动增加
             *      NO --- 如果该列不自动增加
             *
             * */

            //列名
//            String columnName = rs.getString("COLUMN_NAME");
//            //类型
//            String typeName = rs.getString("TYPE_NAME");
//            //注释
//            String remarks = rs.getString("REMARKS");
//            System.out.println(columnName + "--" + typeName + "--" + remarks);
        }
    }

    public static void printTableInfo(DatabaseMetaData databaseMetaData)throws Exception{
        ResultSet rs = databaseMetaData.getTables(null, null, "%", null);
        ResultSetMetaData metaData = rs.getMetaData();

        while(rs.next()){
            for (int i = 1; i < metaData.getColumnCount(); i++) {
                System.out.println(metaData.getColumnName(i) +":"+ rs.getObject(i));
            }

            /**
             * TABLE_NAME : 表名
             * TABLE_TYPE : 类型 TABLE表   VIEW 视图
             * REMARKS : 注释
             * */

            System.out.println();

//            String columnName = rs.getString("COLUMN_NAME");
//            //类型
//            String typeName = rs.getString("TYPE_NAME");
//            //注释
//            String remarks = rs.getString("REMARKS");
//            System.out.println(columnName + "--" + typeName + "--" + remarks);
        }
    }

    public static void main(String[] args) throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://127.0.0.1:3306/wethink_sharding_dev?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&zeroDateTimeBehavior=convertToNull&allowPublicKeyRetrieval=true&autoReconnect=true";
        String user = "root";
        String password = "123456";
        Connection connection = DriverManager.getConnection(url, user, password);
        DatabaseMetaData databaseMetaData = connection.getMetaData();

        printColumnInfo(databaseMetaData);
//        printTableNames(databaseMetaData);
//        printTableInfo(databaseMetaData);
    }
}
