package com.efficiency.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/8/3 22:09
 * @Version : 1.0
 */
@Data
public class ExecuteSQL {
    @JsonProperty("sql")
    private String sql;
}
