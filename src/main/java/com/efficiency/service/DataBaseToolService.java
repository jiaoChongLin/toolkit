package com.efficiency.service;

import cn.hutool.core.collection.CollectionUtil;
import com.efficiency.dao.ExecuteSQLDao;
import com.efficiency.entity.*;
import com.efficiency.generate.GenerateDMDialect;
import com.efficiency.generate.GenerateDialect;
import com.efficiency.generate.GenerateOracleDialect;
import com.efficiency.generate.GenerateSQLServerDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 数据库操作工具类.
 * @author vincent.jiao
 */
@Service
public class DataBaseToolService {
    @Autowired
    DataBaseMateDataService dataBaseMateDataService;

    @Autowired
    ExecuteSQLDao executeSQLDao;

    public Map<String, Set<String>> getTableDifference (ConnInfo connInfo1, ConnInfo connInfo2) {
        Map<String, Set<String>> resultMap = new HashMap<>();

        Set<String> allTable1 = dataBaseMateDataService.getAllTableName(connInfo1);
        Set<String> allTable2 = dataBaseMateDataService.getAllTableName(connInfo2);

        Set<String> tmpSet = new HashSet<>(allTable1);
        tmpSet.removeAll(allTable2);
        resultMap.put(connInfo1.getConnIdentifier(), tmpSet);

        tmpSet = new HashSet<>(allTable2);
        tmpSet.removeAll(allTable1);
        resultMap.put(connInfo2.getConnIdentifier(), tmpSet);

        return resultMap;
    }

    public Map<String, List<CompleteTable>> translate (ConnInfo connInfo) {
        List<TableInfo> list = dataBaseMateDataService.getAllTableInfo(connInfo);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }

        return translate(list);
    }

    public Map<String, List<CompleteTable>> translate (ConnInfo connInfo, List<String> tablelNameList) {
        List<TableInfo> list = dataBaseMateDataService.getAllTableInfo(connInfo);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }

        List<TableInfo> tranList = new LinkedList<>();

        if (!CollectionUtil.isEmpty(tablelNameList)) {
            for (TableInfo item : list) {
                if (tablelNameList.contains(item.getTable_name())) {
                    tranList.add(item);
                }
            }

        } else {
            tranList = list;
        }

        return translate(tranList);
    }

    public Map<String, List<CompleteTable>> translate (List<TableInfo> list) {
        Map<String, List<CompleteTable>> map = new HashMap<>();
        map.put(DataBaseDalect.SQLSERVER.value, new LinkedList<>());
        map.put(DataBaseDalect.ORACLE.value, new LinkedList<>());
        map.put(DataBaseDalect.DM.value, new LinkedList<>());

        for (TableInfo item : list) {
            if (!"TABLE".equals(item.getTable_type())) {
                continue;
            }

            GenerateDialect generateDialect = new GenerateSQLServerDialect();
            CompleteTable completeTable = generateDialect.generateCompleteTable(item);
            map.get(DataBaseDalect.SQLSERVER.value).add(completeTable);

            generateDialect = new GenerateOracleDialect();
            completeTable = generateDialect.generateCompleteTable(item);
            map.get(DataBaseDalect.ORACLE.value).add(completeTable);

            generateDialect = new GenerateDMDialect();
            completeTable = generateDialect.generateCompleteTable(item);
            map.get(DataBaseDalect.DM.value).add(completeTable);
        }

        return map;
    }

    public SQLResult executeSQL (String sql, ConnInfo connInfo) throws Exception {
        DataBaseInfo dataBaseInfo = dataBaseMateDataService.getDataBaseInfo(connInfo);
        if (dataBaseInfo == null) {
            throw new Exception("此链接未初始化");
        }

        Integer type = 1;
        sql = sql.trim().toLowerCase();
        if (sql.startsWith("select")) {
            type = 1;
        } else if (sql.startsWith("delete")) {
            type = 2;
        } else if (sql.startsWith("update")) {
            type = 3;
        } else if (sql.startsWith("insert")) {
            type = 4;
        } else {
            type = 5;
        }

        Object result = null;
        if (type == 1) {
            result = executeSQLDao.select(sql, dataBaseInfo.getConnection());

        } else {
            result = executeSQLDao.executeUpdate(sql, dataBaseInfo.getConnection());
        }

        SQLResult sqlResult = new SQLResult();
        sqlResult.setType(type);
        sqlResult.setData(result);

        return sqlResult;
    }
}
