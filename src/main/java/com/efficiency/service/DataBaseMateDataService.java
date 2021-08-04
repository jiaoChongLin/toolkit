package com.efficiency.service;

import cn.hutool.core.collection.CollectionUtil;
import com.efficiency.dao.DataBaseMateDataDao;
import com.efficiency.entity.ConnInfo;
import com.efficiency.entity.DataBaseDalect;
import com.efficiency.entity.DataBaseInfo;
import com.efficiency.entity.TableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * @author vincent.jiao
 */
@Service
public class DataBaseMateDataService {
    private Map<String, DataBaseInfo> dataBaseCacheMap = new HashMap<>();

    @Autowired
    DataBaseMateDataDao dataBaseMateDataDao;

    public Set<String> getAllConnName() {
        return dataBaseCacheMap.keySet();
    }

    public void refreshConnAndData(ConnInfo connInfo) throws SQLException, ClassNotFoundException {
        initConnAndData(connInfo, true);
    }

    public void initConnAndData(ConnInfo connInfo) throws SQLException, ClassNotFoundException {
        initConnAndData(connInfo, false);
    }

    public DataBaseInfo getDataBaseInfo (ConnInfo connInfo) {
        return dataBaseCacheMap.get(connInfo.getConnIdentifier());
    }

    public void initConnAndData(ConnInfo connInfo, boolean force) throws ClassNotFoundException, SQLException {
        DataBaseInfo dataBaseDalect = dataBaseCacheMap.get(connInfo.getConnIdentifier());

        if (dataBaseDalect != null && !force) {
            return;
        }

        switch (connInfo.getDataBaseDalect()) {
            case DM:
                Class.forName("dm.jdbc.driver.DmDriver");
//                Class.forName("org.hibernate.dialect.DmDialect");
                break;
            case ORACLE:
                Class.forName("oracle.jdbc.driver.OracleDriver");
                break;
            case MYSQL:
                Class.forName("com.mysql.cj.jdbc.Driver");
                break;
            case SQLSERVER:
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                break;
        }

        Connection connection = DriverManager.getConnection(connInfo.getUrl(), connInfo.getUsername(), connInfo.getPassword());
        List<TableInfo> list = dataBaseMateDataDao.getAllTableInfoDetail(connection);

        DataBaseInfo dataBaseInfo = new DataBaseInfo();
        dataBaseInfo.setConnInfo(connInfo);
        dataBaseInfo.setConnection(connection);
        dataBaseInfo.setTableInfoList(list);

        dataBaseCacheMap.put(connInfo.getConnIdentifier(), dataBaseInfo);
    }

    /**
     * 返回全部表名(小写).
     * 会去除系统表，但不一定去除的干净.
     * @param connInfo
     * @return
     */
    public Set<String> getAllTableName (ConnInfo connInfo) {
        List<TableInfo> list = getAllTableInfo(connInfo);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptySet();
        }

        Set<String> oracleSysTable = new HashSet<>();
        oracleSysTable.add("DBA_");
        oracleSysTable.add("DBMS_");
        oracleSysTable.add("ORDDCM_");
        oracleSysTable.add("WWV_");
        oracleSysTable.add("SYS_");
        oracleSysTable.add("SDO_");
        oracleSysTable.add("OLD_");
        oracleSysTable.add("OPATCH_");
        oracleSysTable.add("OLS_");

        Set<String> result = new HashSet<>();
        for (TableInfo item : list) {
            if (!"TABLE".equals(item.getTable_type())) {
                continue;
            }

            if (connInfo.getDataBaseDalect().equals(DataBaseDalect.ORACLE)) {
                if (item.getTable_name().indexOf("$") > -1) {
                    continue;
                }

                boolean isContinue = false;
                for (String key : oracleSysTable) {
                    if (item.getTable_name().startsWith(key)) {
                        isContinue = true;
                        break;
                    }
                }

                if (isContinue) {
                    continue;
                }
            }

            result.add(item.getTable_name().toLowerCase());
        }

        return result;
    }

    public List<TableInfo> getAllTableInfo(ConnInfo connInfo) {
        DataBaseInfo dataBaseInfo =  dataBaseCacheMap.get(connInfo.getConnIdentifier());
        if (dataBaseInfo == null) {
            return Collections.emptyList();
        }

        return dataBaseInfo.getTableInfoList();
    }
}
