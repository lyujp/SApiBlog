package moe.lyu.sapiblog.dto;

import lombok.Data;

@Data
public class CategoryTreeDto {
    private Integer id;
    private String name;
    private CategoryTreeDto parentCategory;
    private String uniqName;
}
