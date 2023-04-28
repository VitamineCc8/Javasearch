package com.javasearch.www.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 倒排索引完整体
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvertedIndex {
    // 内容
    public String word;
    // 所属文档
    public Document document;
}
