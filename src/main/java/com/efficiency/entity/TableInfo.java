package com.efficiency.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/7/18 16:57
 * @Version : 1.0
 */
@Data
public class TableInfo {
    private String table_name;            //表名
    private String table_type;            //表名
    private String remarks;               //注释

    private List<ColunmInfo> colunmInfos;   //列集合
    private List<IndexInfo> indexInfos;   //索引集合

}
