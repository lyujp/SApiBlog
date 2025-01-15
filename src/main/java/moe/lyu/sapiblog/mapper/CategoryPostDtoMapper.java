package moe.lyu.sapiblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import moe.lyu.sapiblog.vo.CategoryPostVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CategoryPostDtoMapper extends BaseMapper<CategoryPostVo> {

    @Select("""
                    SELECT categoryPostTable.post_id AS post_id,
                   categoryPostTable.category_id AS category_id,
                   categoryPostTable.create_time AS category_post_create_time,
                   categoryPostTable.update_time AS category_post_update_time,
                   categoryTable.name AS category_name,
                   categoryTable.parent_id AS category_parent_id,
                   categoryTable.uniq_name AS category_uniq_name,
                   postTable.author_id AS post_author_id,
                   postTable.title AS post_title,
                   postTable.status AS post_status,
                   postTable.cover AS post_cover,
                   postTable.summary AS post_summary,
                   postTable.post_type AS post_post_type,
                   postTable.create_time AS post_create_time,
                   postTable.update_time AS post_update_time
                FROM category_post categoryPostTable
                LEFT JOIN category categoryTable ON categoryPostTable.category_id = categoryTable.id
                LEFT JOIN post postTable ON categoryPostTable.post_id = postTable.id
                WHERE categoryPostTable.category_id = #{category_id}
            """)
    List<CategoryPostVo> get(@Param("category_id") Integer category_id);
}
