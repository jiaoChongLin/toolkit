package com.efficiency.generate;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.efficiency.entity.ColunmInfo;
import com.efficiency.entity.CompleteTable;
import com.efficiency.entity.TableInfo;
import com.efficiency.service.DataBaseService;

import java.sql.SQLException;
import java.util.*;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/7/18 20:21
 * @Version : 1.0
 */
public class GenerateSQLServerDialect extends AbstractGenerateDialect {
    /**
     * 类型映射.
     */
    Map<String, String> typeMapper = new HashMap<>();

    /**
     * 默认值映射.
     */
    Map<String, String> defaultMapper = new HashMap<>();

    public GenerateSQLServerDialect () {
        typeMapper.put("TINYINT", "tinyint");
        typeMapper.put("SMALLINT", "smallint");
        typeMapper.put("MEDIUMINT", "int{size}");
        typeMapper.put("INT", "int{size}");
        typeMapper.put("INTEGER", "int{size}");
        typeMapper.put("BIGINT", "int{size}");
        typeMapper.put("BIT", "bit");
        typeMapper.put("INT UNSIGNED", "int");

        typeMapper.put("FLOAT", "float");
        typeMapper.put("DOUBLE", "float");
        typeMapper.put("DECIMAL", "decimal{size}");

        typeMapper.put("DATE", "date");
        typeMapper.put("TIME", "time");
        typeMapper.put("YEAR", "smallint");
        typeMapper.put("DATETIME", "datetime");
        typeMapper.put("TIMESTAMP", "datetime");

        typeMapper.put("CHAR", "nchar{size}");
        typeMapper.put("VARCHAR", "nvarchar{size}");
        typeMapper.put("TINYBLOB", "nchar{size}");
        typeMapper.put("TINYTEXT", "nchar{size}");
        typeMapper.put("BLOB", "image");
        typeMapper.put("TEXT", "nvarchar{size}");
        typeMapper.put("MEDIUMBLOB", "image");
        typeMapper.put("MEDIUMTEXT", "ntext{size}");
        typeMapper.put("LONGBLOB", "image");
        typeMapper.put("LONGTEXT", "ntext{size}");

        //默认值映射
        defaultMapper.put("CURRENT_TIMESTAMP", "getdate()");
    }

    @Override
    public String getCreateTableSql(TableInfo tableInfo) {
        String sql = super.getCreateTableSql(tableInfo);
        //二次替换
        Map<String, String> params = new HashMap<>();
        params.put("autoKey", "identity");
        sql = StrUtil.format(sql, params);

        return sql;
    }

//    @Override
//    public String getType(String type, String size) {
//        String result = typeMapper.get(type.toUpperCase());
//        if (StrUtil.isEmpty(result)) {
//            return "???";    //默认无法映射
//        }
//
//        return StrUtil.isEmpty(size) ? result : result + "(" + size + ")";
//    }

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
        List<String> results = new LinkedList<>();
        String commonSql = "exec sp_addextendedproperty 'MS_Description', N'"+(tableInfo.getRemarks())+"', 'SCHEMA', 'dbo', 'TABLE', '"+(tableInfo.getTable_name())+"'; ";
        results.add(commonSql);

        List<ColunmInfo> colunmInfos = tableInfo.getColunmInfos();
        commonSql = "exec sp_addextendedproperty 'MS_Description', N'{commonText}', 'SCHEMA', 'dbo', 'TABLE', '{tableName}', 'COLUMN', '{colName}'; ";
        for (int i = 0; i < colunmInfos.size(); i++) {
            Map<String, String> params = new HashMap<>();
            params.put("commonText", colunmInfos.get(i).getRemarks());
            params.put("tableName", tableInfo.getTable_name());
            params.put("colName", colunmInfos.get(i).getColumn_name());

            results.add(StrUtil.format(commonSql, params));
        }

        return results;
    }

    public static void main(String args[]) throws SQLException, ClassNotFoundException {
        DataBaseService dataBaseService = new DataBaseService();
        dataBaseService.initConn("root", "123456", "jdbc:mysql://127.0.0.1:3306/wethink_sharding_dev?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&zeroDateTimeBehavior=convertToNull&allowPublicKeyRetrieval=true&autoReconnect=true");

        Map<String, TableInfo> map = dataBaseService.getAllTable();
        GenerateSQLServerDialect sqlServerDialect = new GenerateSQLServerDialect();

        for (String tableName : map.keySet()) {
            if ("aeitem".equalsIgnoreCase(tableName)) {
                CompleteTable completeTable = sqlServerDialect.generateCompleteTable(map.get(tableName));
                System.out.println(completeTable.getTableSql());
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println(CollectionUtil.join(completeTable.getCommonSqlList(), "\n"));
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println(CollectionUtil.join(completeTable.getIndexSqlList(), "\n"));
            }
        }
    }
}
