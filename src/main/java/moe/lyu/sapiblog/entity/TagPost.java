package moe.lyu.sapiblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@TableName("tag_post")
public class TagPost {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer postId;
    private Integer tagId;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

}
