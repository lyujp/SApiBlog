package moe.lyu.sapiblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@TableName("category_post")
public class CategoryPost {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer postId;
    private Integer categoryId;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

}
