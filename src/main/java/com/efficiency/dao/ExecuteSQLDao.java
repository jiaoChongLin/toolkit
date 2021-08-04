package com.efficiency.dao;

import com.efficiency.entity.TableInfo;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author vincent.jiao
 */
@Component
public class ExecuteSQLDao {
    public List<List<Object>> select (String sql, Connection conn) throws SQLException {
        List<List<Object>> dataResult = new LinkedList<>();

        PreparedStatement pstmt = getPreparedStatement(conn, sql);
        ResultSet resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        List<Object> list = new LinkedList<>();
        int colSize = metaData.getColumnCount();
        for (int i = 1; i <= colSize; i++) {
            list.add(metaData.getColumnName(i));
        }
        dataResult.add(list);

        while (resultSet.next()) {
            list = new LinkedList<>();
            for (int i = 1; i <= colSize; i++) {
                list.add(resultSet.getObject(i));
            }
            dataResult.add(list);
        }

        return dataResult;
    }

    public Object executeUpdate (String sql, Connection conn) throws SQLException {
        PreparedStatement pstmt = getPreparedStatement(conn, sql);
        return pstmt.executeUpdate();
    }

    private PreparedStatement getPreparedStatement(Connection conn, String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }
}
