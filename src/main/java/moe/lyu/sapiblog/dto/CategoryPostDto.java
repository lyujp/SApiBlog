package moe.lyu.sapiblog.dto;

import lombok.Data;
import moe.lyu.sapiblog.entity.Category;
import moe.lyu.sapiblog.vo.PostWithoutContentVo;

import java.util.List;

@Data
public class CategoryPostDto {
    private Integer categoryId;
    private List<PostWithoutContentVo> postList;
    private Category category;
    private Long createTime;
    private Long updateTime;
}
