package com.efficiency.service;

import com.efficiency.entity.ColunmInfo;
import com.efficiency.entity.ConnInfo;
import com.efficiency.entity.IndexInfo;
import com.efficiency.entity.TableInfo;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/7/18 16:52
 * @Version : 1.0
 */
@Service
public class DataBaseService {
    public static ConnInfo connInfo = new ConnInfo();
    public static Connection connection;
    private Map<String, TableInfo> tablsMap = new ConcurrentHashMap<>();

    public void initConn(String username, String password, String url) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
//        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, username, password);
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet rs = databaseMetaData.getTables(null, null, "%", null);
        ResultSetMetaData metaData = null;

        //获取表
        while(rs.next()){
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTable_name(rs.getString("TABLE_NAME"));
            tableInfo.setRemarks(rs.getString("REMARKS"));
            tableInfo.setTable_type(rs.getString("TABLE_TYPE"));

            tablsMap.put(tableInfo.getTable_name(), tableInfo);
        }

        rs.close();

        //读取所有列
        rs = databaseMetaData.getColumns(null, null, "%", null);
        while(rs.next()){
            ColunmInfo colunmInfo = new ColunmInfo();
            colunmInfo.setRemarks(rs.getString("REMARKS"));
            colunmInfo.setType_name(rs.getString("TYPE_NAME"));
            colunmInfo.setColumn_name(rs.getString("COLUMN_NAME"));
            colunmInfo.setColumn_size(rs.getString("COLUMN_SIZE"));
            colunmInfo.setIs_nullable(rs.getString("IS_NULLABLE"));
            colunmInfo.setDecimal_digits(rs.getString("DECIMAL_DIGITS"));
            colunmInfo.setColumn_def(rs.getString("COLUMN_DEF"));
            colunmInfo.setIs_autoincrement(rs.getString("IS_AUTOINCREMENT"));

            TableInfo tableInfo = tablsMap.get(rs.getString("TABLE_NAME"));
            if (tableInfo == null) {
                continue;
            }

            if (tableInfo.getColunmInfos() == null) {
                tableInfo.setColunmInfos(new LinkedList<>());
            }

            tableInfo.getColunmInfos().add(colunmInfo);
        }

        rs.close();

        //读取所有索引
        for (String tableName : tablsMap.keySet()) {
            rs = databaseMetaData.getIndexInfo(null, null, tableName, false, false);
            List<IndexInfo> indexs = new ArrayList<>();

            while(rs.next()){
                IndexInfo info = new IndexInfo();
                info.setColumn_name(rs.getString("COLUMN_NAME"));
                info.setIndex_name(rs.getString("INDEX_NAME"));
                info.setAsc_or_desc(rs.getString("ASC_OR_DESC"));
                info.setType(rs.getString("TYPE"));
                info.setNon_unique(rs.getString("NON_UNIQUE"));

                indexs.add(info);
            }

            rs.close();
            tablsMap.get(tableName).setIndexInfos(indexs);

            //获取主键
//            rs =  databaseMetaData.getPrimaryKeys(null, null, tableName);
//            metaData = rs.getMetaData();
//
//            while (rs.next()){
//                for (int i = 1; i <= metaData.getColumnCount(); i++) {
//                    System.out.println(metaData.getColumnName(i) +"---->"+ rs.getString(i));
//                }
//
//                System.out.println();
//                System.out.println();
//                System.out.println();
//            }
//
//            rs.close();
        }
    }

    public Map<String, TableInfo> getAllTable(String username, String password, String url){
        return getAllTable();
    }

    public Map<String, TableInfo> getAllTable(){
        return tablsMap;
    }

    public ConnInfo getDataBaseInfo() {
        return connInfo;
    }


}
