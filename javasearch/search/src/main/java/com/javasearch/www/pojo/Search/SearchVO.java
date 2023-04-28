package com.javasearch.www.pojo.Search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 用于搜索时，记录词频和命中分词数量
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchVO {
    public Integer count;
    public Integer wordCount;
}
