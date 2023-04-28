package com.javasearch.www.pojo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//插在节点后面的东西
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    // 文档id
    public Long docId;
    // 词频
    public Integer wordCount;
    //伪删除
    public Integer isDelete;
}
