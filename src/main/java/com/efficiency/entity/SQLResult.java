package com.efficiency.entity;

import lombok.Data;

/**
 * @author vincent.jiao
 */
@Data
public class SQLResult {
    /**
     * 1: DML 查询
     * 2: DML 删
     * 3: DML 改
     * 4: DML 增
     * 5: DDL
     */
    public Integer type;

    public Object data;
}
