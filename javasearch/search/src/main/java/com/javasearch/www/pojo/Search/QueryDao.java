package com.javasearch.www.pojo.Search;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 用于sql查询文章时用的类
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryDao {
    public Long id;
    public Integer isDeleted;
}
