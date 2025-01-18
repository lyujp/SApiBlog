package moe.lyu.sapiblog.dto;

import lombok.Data;

@Data
public class PostWithoutContentDto {
    private Integer id;
    private Integer authorId;
    private String title;
    private Boolean status;
    private String cover;
    private String summary;
    private Boolean postType;
    private Long createTime;
    private Long updateTime;
    private String jwt;

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
