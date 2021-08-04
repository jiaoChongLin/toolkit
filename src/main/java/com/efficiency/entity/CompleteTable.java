package com.efficiency.entity;

import lombok.Data;

import java.util.List;

/**
 * @author vincent.jiao
 */
@Data
public class CompleteTable {
    private String tableSql;
    private List<String> commonSqlList;
    private List<String> indexSqlList;
    private List<String> constraintSqlList;

    //扩展list
    private List<String> extendList;
}
