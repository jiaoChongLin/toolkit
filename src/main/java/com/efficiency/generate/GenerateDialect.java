package com.efficiency.generate;

import com.efficiency.entity.CompleteTable;
import com.efficiency.entity.TableInfo;

import java.util.List;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/7/18 20:21
 * @Version : 1.0
 */
public interface GenerateDialect {
    /**
     * 生成表sql
     * @param tableInfo
     * @return
     */
    public String getCreateTableSql(TableInfo tableInfo);

    /**
     * 生成表注释
     * @param tableInfo
     * @return
     */
    public List<String> generateCommon(TableInfo tableInfo);

     /* 生成表索引
     * @param tableInfo
     * @return
     */
    public List<String> generateIndex(TableInfo tableInfo);

    /**
     * 生成完整表信息.
     * @param tableInfo
     * @return
     */
    public CompleteTable generateCompleteTable(TableInfo tableInfo);
}
