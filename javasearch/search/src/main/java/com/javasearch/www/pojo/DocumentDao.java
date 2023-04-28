package com.javasearch.www.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//映射数据库
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDao {
    public Long id;
    public String content;
    public Integer isDeleted;
    public Long timeAt;
    public String deletedTime;
}
