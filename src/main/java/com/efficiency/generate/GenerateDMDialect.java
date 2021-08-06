package com.efficiency.generate;

import cn.hutool.core.util.StrUtil;
import com.efficiency.entity.ColunmInfo;
import com.efficiency.entity.TableInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/7/18 20:23
 * @Version : 1.0
 */
public class GenerateDMDialect extends AbstractGenerateDialect {
    /**
     * 类型映射.
     */
    Map<String, String> typeMapper = new HashMap<>();

    /**
     * 默认值映射.
     */
    Map<String, String> defaultMapper = new HashMap<>();

    public GenerateDMDialect () {
        typeMapper.put("TINYINT", "TINYINT");
        typeMapper.put("SMALLINT", "SMALLINT");
        typeMapper.put("MEDIUMINT", "INT{size}");
        typeMapper.put("INT", "INT");
        typeMapper.put("INTEGER", "INTEGER{size}");
        typeMapper.put("BIGINT", "BIGINT");
        typeMapper.put("BIT", "INTEGER{size}");
        typeMapper.put("INT UNSIGNED", "INT");

        typeMapper.put("FLOAT", "FLOAT");
        typeMapper.put("DOUBLE", "DOUBLE");
        typeMapper.put("DECIMAL", "DECIMAL{size}");

        typeMapper.put("DATE", "DATE");
        typeMapper.put("TIME", "TIME");
        typeMapper.put("YEAR", "???"); // 这个不知道
        typeMapper.put("DATETIME", "DATETIME");
        typeMapper.put("TIMESTAMP", "TIMESTAMP");

        typeMapper.put("CHAR", "CHAR{size}");
        typeMapper.put("VARCHAR", "VARCHAR{size}");
        typeMapper.put("TINYBLOB", "BLOB");
        typeMapper.put("TINYTEXT", "TEXT{size}");
        typeMapper.put("BLOB", "BLOB");
        typeMapper.put("TEXT", "TEXT{size}");
        typeMapper.put("MEDIUMBLOB", "BLOB");
        typeMapper.put("MEDIUMTEXT", "TEXT{size}");
        typeMapper.put("LONGBLOB", "BLOB");
        typeMapper.put("LONGTEXT", "TEXT{size}");

        //默认值映射
        defaultMapper.put("CURRENT_TIMESTAMP", "curdate()");
    }

    @Override
    public String getCreateTableSql(TableInfo tableInfo) {
        String sql = super.getCreateTableSql(tableInfo);
        //二次替换
        Map<String, String> params = new HashMap<>();
        params.put("autoKey", " IDENTITY ");
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
    public List<String> generateCommon(TableInfo tableInfo) {
//        COMMENT ON TABLE "SYSDBA"."PLK_CHANNEL" IS 'plk渠道表';
//        COMMENT ON COLUMN "SYSDBA"."PLK_CHANNEL"."PC_CREATE_ENT_ID" IS '创建人';

        List<String> list = new LinkedList<>();
        String tabName = tableInfo.getTable_name();
        list.add("comment on table \"" + tabName + "\" is '" + tableInfo.getRemarks() + "' ;");

        for (ColunmInfo colunmInfo : tableInfo.getColunmInfos()) {
            list.add("comment on column \"" + tabName + "\".\"" + colunmInfo.getColumn_name() + "\" is '" + colunmInfo.getRemarks() + "';");
        }

        return list;
    }
}
