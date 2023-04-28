package com.javasearch.www.pojo.Search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用于词频排序
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdContextWordCountVO {
    public Long id;
    public String content;
    public Integer wordCount;
}
