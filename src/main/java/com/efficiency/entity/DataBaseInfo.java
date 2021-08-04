package com.efficiency.entity;

import lombok.Data;

import java.sql.Connection;
import java.util.List;

/**
 * @author vincent.jiao
 */
@Data
public class DataBaseInfo {
    private ConnInfo connInfo;

    private Connection connection;

    private List<TableInfo> tableInfoList;
}
