package moe.lyu.sapiblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@TableName("tag")
public class Tag {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String uniqName;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    public void setUniqName(String uniqName) {
        this.uniqName = uniqName.toLowerCase().trim();
    }
}
