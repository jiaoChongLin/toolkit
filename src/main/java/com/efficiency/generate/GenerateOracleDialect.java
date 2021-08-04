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
    Map<String, String> typeMapper = new HashMap<>();

    public GenerateOracleDialect () {
        typeMapper.put("TINYINT", "INT");
        typeMapper.put("SMALLINT", "INT");
        typeMapper.put("MEDIUMINT", "INT");
        typeMapper.put("INT", "INT");
        typeMapper.put("INTEGER", "INT");
        typeMapper.put("BIGINT", "INT");

        typeMapper.put("FLOAT", "FLOAT");
        typeMapper.put("DOUBLE", "FLOAT");
        typeMapper.put("DECIMAL", "NUMBER");

        typeMapper.put("DATE", "DATE");
//        typeMapper.put("TIME", "???");        //无法翻译
        typeMapper.put("YEAR", "INT");
        typeMapper.put("DATETIME", "DATE");
        typeMapper.put("TIMESTAMP", "TIMESTAMP");

        typeMapper.put("CHAR", "NVARCHAR2");
        typeMapper.put("VARCHAR", "NVARCHAR2");
        typeMapper.put("TINYBLOB", "NVARCHAR2");
        typeMapper.put("TINYTEXT", "NVARCHAR2");
        typeMapper.put("BLOB", "BLOB");
        typeMapper.put("TEXT", "NVARCHAR2");
        typeMapper.put("MEDIUMBLOB", "BLOB");
        typeMapper.put("MEDIUMTEXT", "NCLOB");
        typeMapper.put("LONGBLOB", "BLOB");
        typeMapper.put("LONGTEXT", "NCLOB");
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
    public List<String> getExtendSqlList (TableInfo tableInfo) {
        List<String> list = new LinkedList<>();

        String idCol = "???";

        //逻辑1：获取所有的唯一列，选第一个做自增
//        List<String> suspectedList = new LinkedList<>();
//        if (CollectionUtil.isEmpty(tableInfo.getIndexInfos())) {
//            for (IndexInfo item : tableInfo.getIndexInfos()) {
//                if (item.getNon_unique()) {
//
//                }
//            }
//        }

        //逻辑2：获取第一个列，做自增
        idCol = tableInfo.getColunmInfos().get(0).getColumn_name();

        //增加序列
        String suqName = tableInfo.getTable_name() + "_seq";
        list.add("create sequence " + suqName);
        list.add("CREATE OR REPLACE TRIGGER "+ (tableInfo.getTable_name()) +"_trigger BEFORE INSERT ON "+(tableInfo.getTable_name())+" \n" +
                "        FOR EACH ROW \n" +
                "        WHEN (new."+idCol+" IS NULL) \n" +
                "        BEGIN \n" +
                "        SELECT " + (suqName) + ".NEXTVAL INTO :new."+idCol+" FROM DUAL; \n" +
                "        END");

        return list;
    }

    @Override
    public List<String> generateCommon(TableInfo tableInfo) {
        List<String> list = new LinkedList<>();
        String tabName = tableInfo.getTable_name();
        list.add("comment on table " + tabName + " is '" + tableInfo.getRemarks() + "'");

        for (ColunmInfo colunmInfo : tableInfo.getColunmInfos()) {
            list.add("comment on column " + tabName + "." + colunmInfo.getColumn_name() + " is '" + colunmInfo.getRemarks() + "'");
        }

        return list;
    }
}
