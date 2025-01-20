package moe.lyu.sapiblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("tag")
public class Tag {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}
