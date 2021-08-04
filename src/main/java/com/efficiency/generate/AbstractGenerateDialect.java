package com.efficiency.generate;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.efficiency.entity.ColunmInfo;
import com.efficiency.entity.CompleteTable;
import com.efficiency.entity.IndexInfo;
import com.efficiency.entity.TableInfo;

import java.util.*;

/**
 * @author vincent.jiao
 */
public abstract class AbstractGenerateDialect implements GenerateDialect {
    /**
     * 生成完整表信息.
     * @param tableInfo
     * @return
     */
    @Override
    public CompleteTable generateCompleteTable(TableInfo tableInfo) {
        CompleteTable completeTable = new CompleteTable();

        completeTable.setTableSql(getCreateTableSql(tableInfo));
        completeTable.setCommonSqlList(generateCommon(tableInfo));
        completeTable.setIndexSqlList(generateIndex(tableInfo));
        completeTable.setExtendList(getExtendSqlList(tableInfo));

        return completeTable;
    }

    public List<String> getExtendSqlList (TableInfo tableInfo) {
        return Collections.emptyList();
    }

    /* 生成表索引
     * @param tableInfo
     * @return
     */
    @Override
    public List<String> generateIndex(TableInfo tableInfo) {
        List<String> list = new ArrayList<>();
        String indexSqlTemplate = " CREATE {unique} INDEX {indexName} ON {tableName} ({columnName}) ";

        //检查组合组建，存在就合并为一个
        Map<String, List<IndexInfo>> indexMap = new HashMap();
        for (IndexInfo item : tableInfo.getIndexInfos()) {
            List<IndexInfo> indexList = indexMap.get(item.getIndex_name());
            if (indexList == null) {
                indexList = new LinkedList<>();
                indexMap.put(item.getIndex_name(), indexList);
            }

            indexList.add(item);
        }

        for (String item : indexMap.keySet()) {
            List<IndexInfo> indexList = indexMap.get(item);
            String cols = "";

            if (CollectionUtil.isEmpty(indexList)) {
                break;
            }

            IndexInfo indexInfo = indexList.get(0);
            if (indexList.size() > 1) {
                for (int i = 0, size = indexList.size(); i < size; i++) {
                    cols += indexList.get(i).getColumn_name();
                    cols += i + 1 >= size ? "" : ", ";
                }
            } else {
                cols = indexInfo.getColumn_name();
            }

            Map<String, String> params = new HashMap<>();
            params.put("unique", "true".equals(indexInfo.getNon_unique()) ? "" : "unique");
            params.put("indexName", indexInfo.getIndex_name());
            params.put("tableName", tableInfo.getTable_name());
            params.put("columnName", cols);

            list.add(StrUtil.format(indexSqlTemplate, params));
        }

        return list;
    }

    /**
     * 生成表sql.
     * 返回sql 中会存在一些占位符，根据数据库不通特征去替换为特定语法。存在的占位符有：
     * autoKey: 自增长, 如果没有就设置 ""
     * @param tableInfo
     * @return
     */
    public String getCreateTableSql(TableInfo tableInfo) {
        Map<String, String> params = new HashMap<>();
        String createTable = " create table {table_name}  ( \n";
        params.put("table_name", tableInfo.getTable_name());

        if (CollectionUtil.isEmpty(tableInfo.getColunmInfos())) {
            return null;
        }

        String colunms = getColnumSql(tableInfo.getColunmInfos());

        createTable += colunms + " ) ";
        return StrUtil.format(createTable, params);
    }


    public String getColnumSql (List<ColunmInfo> colunmInfos) {
        Map<String, String> params = new HashMap<>();
        String createColumns = "";

        for (int i = 0, size = colunmInfos.size(); i < size; i++) {
            ColunmInfo info = colunmInfos.get(i);
            String colInfo = "\t";
            String tmp = "colName_" + i;
            params.put(tmp, info.getColumn_name());
            colInfo += "{"+tmp+"} ";

            tmp = "colType_" + i;
            params.put(tmp, getType(info.getType_name(), null));
            colInfo += "{"+tmp+"}";

            if (StrUtil.isNotEmpty(info.getColumn_size())) {
                tmp = "colSize_" + i;
                params.put(tmp, "(" + info.getColumn_size() + ")");
                colInfo += "{"+tmp+"} ";
            }

            if (StrUtil.isNotEmpty(info.getIs_autoincrement()) && "YES".equalsIgnoreCase(info.getIs_autoincrement())) {
                tmp = "colIsAuto_" + i;
                params.put(tmp, " {autoKey} ");     // 待二次替换
                colInfo += "{"+tmp+"} ";
            }

            if (StrUtil.isNotEmpty(info.getColumn_def())) {
                tmp = "colDefault_" + i;
                params.put(tmp, " default '" + (info.getColumn_def()) + "' ");
                colInfo += "{"+tmp+"} ";
            }



            if (StrUtil.isNotEmpty(info.getIs_nullable()) && "NO".equalsIgnoreCase(info.getIs_nullable())) {
                tmp = "colNotNull_" + i;
                params.put(tmp, " not null ");
                colInfo += "{"+tmp+"} ";
            }

            colInfo += i+1 == size ? "" : ",";
            createColumns += colInfo + "\n";
        }

        return StrUtil.format(createColumns, params);
    }

    /**
     * 返回类型映射 map.
     * @return
     */
    public abstract Map<String, String> getTypeMapper();

    /**
     * 根据类型返回指定数据库对应的数据类型.
     * @param type
     * @param size
     * @return
     */
    public String getType(String type, String size) {
        String result = getTypeMapper().get(type.toUpperCase());
        if (StrUtil.isEmpty(result)) {
            return "???";    //默认无法映射
        }

        return StrUtil.isEmpty(size) ? result : result + "(" + size + ")";
    }
}
