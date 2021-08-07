package com.efficiency.generate;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.efficiency.entity.ColunmInfo;
import com.efficiency.entity.IndexInfo;
import com.efficiency.entity.TableInfo;

import java.util.*;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/7/18 20:23
 * @Version : 1.0
 */
public class GenerateOracleDialect extends AbstractGenerateDialect{
    /**
     * 类型映射.
     */
    Map<String, String> typeMapper = new HashMap<>();

    /**
     * 默认值映射.
     */
    Map<String, String> defaultMapper = new HashMap<>();

    public GenerateOracleDialect () {
        typeMapper.put("TINYINT", "INT");
        typeMapper.put("SMALLINT", "INT");
        typeMapper.put("MEDIUMINT", "INT");
        typeMapper.put("INT", "INT");
        typeMapper.put("INTEGER", "INT");
        typeMapper.put("BIGINT", "INT");
        typeMapper.put("BIT", "INTEGER{size}");
        typeMapper.put("INT UNSIGNED", "INT");

        typeMapper.put("FLOAT", "FLOAT");
        typeMapper.put("DOUBLE", "FLOAT");
        typeMapper.put("DECIMAL", "NUMBER{size}");

        typeMapper.put("DATE", "DATE");
//        typeMapper.put("TIME", "???");        //无法翻译
        typeMapper.put("YEAR", "INT");
        typeMapper.put("DATETIME", "DATE");
        typeMapper.put("TIMESTAMP", "TIMESTAMP");

        typeMapper.put("CHAR", "NVARCHAR2{size}");
        typeMapper.put("VARCHAR", "NVARCHAR2{size}");
        typeMapper.put("TINYBLOB", "NVARCHAR2{size}");
        typeMapper.put("TINYTEXT", "NVARCHAR2{size}");
        typeMapper.put("BLOB", "BLOB");
        typeMapper.put("TEXT", "NVARCHAR2{size}");
        typeMapper.put("MEDIUMBLOB", "BLOB");
        typeMapper.put("MEDIUMTEXT", "NCLOB");
        typeMapper.put("LONGBLOB", "BLOB");
        typeMapper.put("LONGTEXT", "NCLOB");

        //默认值映射
        defaultMapper.put("CURRENT_TIMESTAMP", "CURRENT_TIMESTAMP");
    }

    @Override
    public String getCreateTableSql(TableInfo tableInfo) {
        String sql = super.getCreateTableSql(tableInfo);
        //二次替换
        Map<String, String> params = new HashMap<>();
        params.put("autoKey", "");
        sql = StrUtil.format(sql, params);

        return sql;
    }

    @Override
    public Map<String, String> getTypeMapper() {
        return typeMapper;
    }

    @Override
    public Map<String, String> getDefaultMapper() {
        return defaultMapper;
    }

    @Override
    public List<String> getExtendSqlList (TableInfo tableInfo) {
        String idCol = null;
        for (ColunmInfo info : tableInfo.getColunmInfos()) {
            if ("YES".equalsIgnoreCase(info.getIs_autoincrement())) {
                idCol = info.getColumn_name();
                break;
            }
        }

        if (idCol == null) {
            return Collections.emptyList();
        }

        List<String> list = new LinkedList<>();
        //增加序列
        String suqName = tableInfo.getTable_name() + "_seq";
        list.add("create sequence " + suqName + "; \n");
        list.add("CREATE OR REPLACE TRIGGER "+ (tableInfo.getTable_name()) +"_trigger BEFORE INSERT ON "+(tableInfo.getTable_name())+" \n" +
                "        FOR EACH ROW \n" +
                "        WHEN (new."+idCol+" IS NULL) \n" +
                "        BEGIN \n" +
                "        SELECT " + (suqName) + ".NEXTVAL INTO :new."+idCol+" FROM DUAL; \n" +
                "        END ;");

        return list;
    }

    @Override
    public List<String> generateCommon(TableInfo tableInfo) {
        List<String> list = new LinkedList<>();
        String tabName = tableInfo.getTable_name();
        list.add("comment on table " + tabName + " is '" + tableInfo.getRemarks() + "' ;");

        for (ColunmInfo colunmInfo : tableInfo.getColunmInfos()) {
            list.add("comment on column " + tabName + "." + colunmInfo.getColumn_name() + " is '" + colunmInfo.getRemarks() + "' ;");
        }

        return list;
    }
}
