package com.efficiency.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.efficiency.entity.ColunmInfo;
import com.efficiency.entity.IndexInfo;
import com.efficiency.entity.TableInfo;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据库元数据操作.
 * @author vincent.jiao
 */
@Component
public class DataBaseMateDataDao {
    /**
     * 根据表名返回表信息.
     * @param conn
     * @param tableName
     * @return
     */
    public TableInfo getTableInfoByTableName(Connection conn, String tableName) throws SQLException {
        ResultSet rs = getDatabaseMetaData(conn).getTables(null, null, tableName, null);
        List<TableInfo> list = getTableInfo(rs);

        rs.close();
        return CollectionUtil.isEmpty(list) ? null : list.get(0);
    }

    /**
     * @param conn
     * @return
     */
    public List<TableInfo> getAllTableInfo(Connection conn) throws SQLException {
        ResultSet rs = getDatabaseMetaData(conn).getTables(null, null, "%", null);
        List<TableInfo> list = getTableInfo(rs);

        rs.close();
        return list;
    }

    public TableInfo getTableInfoDetailByTableName(Connection conn, String tableName) throws SQLException {
        TableInfo tableInfo = getTableInfoByTableName(conn, tableName);
        if (tableInfo != null) {
            setTableInfoDetail(conn, tableInfo);
        }

        return tableInfo;
    }

    public List<TableInfo> getAllTableInfoDetail(Connection conn) throws SQLException {
        List<TableInfo> list = getAllTableInfo(conn);
        if (CollectionUtil.isNotEmpty(list)) {
            for (TableInfo item : list) {
                if ("TABLE".equals(item.getTable_type())) {
                    setTableInfoDetail(conn, item);
                }
            }
        }

        return list;
    }


    private void setTableInfoDetail (Connection conn, TableInfo tableInfo) throws SQLException {
        if (tableInfo != null) {
            try {
                tableInfo.setColunmInfos(getColunmInfoByTableName(conn, tableInfo.getTable_name()));
                tableInfo.setIndexInfos(getIndexInfoByTableName(conn, tableInfo.getTable_name()));
            } catch (Exception e){}
        }
    }

    private List<TableInfo> getTableInfo(ResultSet rs) throws SQLException {
        List<TableInfo> list = new LinkedList<>();

        //获取表
        while(rs.next()){
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTable_name(rs.getString("TABLE_NAME"));
            tableInfo.setRemarks(rs.getString("REMARKS"));
            tableInfo.setTable_type(rs.getString("TABLE_TYPE"));

            list.add(tableInfo);
        }

        return list;
    }

    public List<IndexInfo> getIndexInfoByTableName(Connection conn, String tableName) throws SQLException {
        ResultSet rs = getDatabaseMetaData(conn).getIndexInfo(null, null, tableName, false, false);
        List<IndexInfo> list = getIndexInfo(rs);
        rs.close();

        return list;
    }


    private List<IndexInfo> getIndexInfo(ResultSet rs) throws SQLException {
        List<IndexInfo> list = new LinkedList<>();

        while(rs.next()){
            IndexInfo info = new IndexInfo();
            info.setColumn_name(rs.getString("COLUMN_NAME"));
            info.setIndex_name(rs.getString("INDEX_NAME"));
            info.setAsc_or_desc(rs.getString("ASC_OR_DESC"));
            info.setType(rs.getString("TYPE"));
            info.setNon_unique(rs.getString("NON_UNIQUE"));

            list.add(info);
        }

        return list;
    }

    public List<ColunmInfo> getColunmInfoByTableName (Connection conn, String tableName) throws SQLException {
        ResultSet rs = getDatabaseMetaData(conn).getColumns(null, null, tableName, null);
        List<ColunmInfo> list = getColunmInfo(rs);

        rs.close();
        return list;
    }

    private List<ColunmInfo> getColunmInfo (ResultSet rs) throws SQLException {
        List<ColunmInfo> list = new LinkedList<>();

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
            list.add(colunmInfo);
        }

        return list;
    }


    private DatabaseMetaData getDatabaseMetaData(Connection conn) throws SQLException {
        return conn.getMetaData();
    }
}
