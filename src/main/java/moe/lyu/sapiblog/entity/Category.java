package moe.lyu.sapiblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("category")
public class Category {


    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer parentId;
    private String uniqName;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    public void setParentId(Integer parentId) {
        if (parentId == null) {
            parentId = 0;
        }
        this.parentId = parentId;
    }

    public void setUniqName(String uniqName) {
        this.uniqName = uniqName.toLowerCase().trim();
    }
}
