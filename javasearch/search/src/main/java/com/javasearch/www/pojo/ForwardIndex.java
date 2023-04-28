package com.javasearch.www.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

// 倒排索引完整体
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForwardIndex {
    // 内容
    public Long word;

    public Integer isDelete;
    // 所属文档
    public ArrayList<String> idList;
}
