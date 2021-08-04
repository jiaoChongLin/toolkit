package com.efficiency.controller;

import com.efficiency.entity.*;
import com.efficiency.service.DataBaseMateDataService;
import com.efficiency.service.DataBaseToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author vincent.jiao
 */
@RestController
@RequestMapping("/db")
public class DataBaseToolController {
    @Autowired
    DataBaseMateDataService mateDataService;

    @Autowired
    DataBaseToolService toolService;

    /**
     * 返回所有表名
     * @param connInfo
     * @return
     */
    @RequestMapping("getAllTable")
    public Object getAllTable(ConnInfo connInfo) {
        return mateDataService.getAllTableName(connInfo);
    }

    /**
     * 初始化链接
     * @param connInfo
     * @return
     */
    @RequestMapping("initConnInfo")
    public void initConnInfo(ConnInfo connInfo) throws SQLException, ClassNotFoundException {
        mateDataService.initConnAndData(connInfo);
    }

    /**
     * 返回所有链接
     * @return
     */
    @RequestMapping("getAllConnInfo")
    public Object getAllConnInfo() {
        return mateDataService.getAllConnName();
    }

    /**
     * 刷新链接
     * @param connInfo
     * @return
     */
    @RequestMapping("refreshConnInfo")
    public void refreshConnInfo(ConnInfo connInfo) throws SQLException, ClassNotFoundException {
        mateDataService.refreshConnAndData(connInfo);
    }

    /**
     * 翻译sql
     * @param connInfo
     * @return
     */
    @PostMapping("translateSql")
    public Object translateSql(ConnInfo connInfo,  String[] tables) {
        Object result = null;

        if (tables == null || tables.length == 0) {
            return R.fail("请选择表");
        }

        return toolService.translate(connInfo, Arrays.asList(tables));
    }

    /**
     * 差异对比
     * @return
     */
    @PostMapping("getTableDifference")
    public Object getTableDifference (@RequestBody DifferenceSourceDTO sourceDto) {
        return toolService.getTableDifference(sourceDto.getConnInfo1(), sourceDto.getConnInfo2());
    }

    @PostMapping("sqlExecute")
    public Object executeSQL (@RequestBody ExecuteSQL executeSQL, ConnInfo connInfo) throws Exception {
        String sql = URLDecoder.decode(URLDecoder.decode(executeSQL.getSql(), "UTF-8"), "UTF-8");
        return toolService.executeSQL(sql, connInfo);
    }
}
