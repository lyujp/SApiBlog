package moe.lyu.sapiblog.vo;

import lombok.Data;

@Data
public class CategoryPostVo {

    private Integer postId;
    private Integer categoryId;
    private Long categoryPostCreateTime;
    private Long categoryPostUpdateTime;

    private String categoryName;
    private Integer categoryParentId;
    private String categoryUniqName;

    private Integer postAuthorId;
    private String postTitle;
    private Boolean postStatus;
    private String postCover;
    private String postSummary;
    private Boolean postPostType;
    private Long post_createTime;
    private Long postUpdateTime;
}
