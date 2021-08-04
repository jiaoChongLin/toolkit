package com.efficiency.entity;

import lombok.Data;

/**
 * @author vincent.jiao
 */
@Data
public class IndexInfo {
    //索引名称；null当 TYPE 是 tableIndexStatistic
    private String index_name;

    /**
     * TYPE短=>索引类型：
     * tableIndexStatistic - 标识与表的索引描述结合返回的表统计信息
     * tableIndexClustered - 这是一个聚集索引
     * tableIndexHashed - 这是一个散列索引
     * tableIndexOther - 这是一些其他样式的索引
     */
    private String type;

    //COLUMN_NAME字符串=>列名；null当 TYPE 是 tableIndexStatistic
    private String column_name;

    //ASC_OR_DESC String=>列排序顺序，“A”=>升序，“D”=>降序，null如果不支持排序顺序可能是； null当 TYPE 是 tableIndexStatistic
    private String asc_or_desc;

    //如果为真则说明索引值不唯一，为假则说明索引值必须唯一
    private String non_unique;

}
