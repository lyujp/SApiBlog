package moe.lyu.sapiblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer authorId;
    private String title;
    private String content;
    private Boolean status;
    private String cover;
    private Boolean postType;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    public void setStatus(Boolean status) {
        if (this.status == null) {
            this.status = false;
        }
        this.status = status;
    }

    public void setPostType(Boolean postType) {
        if (this.postType == null) {
            this.postType = false;
        }
        this.postType = postType;
    }
}
