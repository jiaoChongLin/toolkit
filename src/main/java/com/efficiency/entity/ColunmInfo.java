package com.efficiency.entity;

import lombok.Data;

/**
 * @Author : Vincent.jiao
 * @Date : 2021/7/18 16:58
 * @Version : 1.0
 */
@Data
public class ColunmInfo {
    private String remarks;             //注释
    private String column_name;            //列名
    private String column_size;            //列大小
    private String type_name;            //类型
    private String is_nullable;            //是不是null
    private String decimal_digits;            //小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null
    private String column_def;            //该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
    private String is_autoincrement;            //  指示此列是否自动增加 YES --- 如果该列自动增加  NO --- 如果该列不自动增加
}
