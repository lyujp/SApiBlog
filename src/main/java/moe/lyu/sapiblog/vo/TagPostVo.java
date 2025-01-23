package moe.lyu.sapiblog.vo;

import lombok.Data;

@Data
public class TagPostVo {

    private Integer postId;
    private Integer tagId;
    private Long tagPostCreateTime;
    private Long tagPostUpdateTime;

    private String tagName;

    private Integer postAuthorId;
    private String postTitle;
    private Boolean postStatus;
    private String postCover;
    private String postSummary;
    private Boolean postPostType;
    private Long post_createTime;
    private Long postUpdateTime;
}
