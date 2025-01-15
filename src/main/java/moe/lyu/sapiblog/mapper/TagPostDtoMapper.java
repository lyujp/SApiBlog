package moe.lyu.sapiblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import moe.lyu.sapiblog.vo.TagPostVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TagPostDtoMapper extends BaseMapper<TagPostVo> {

    @Select("""
                    SELECT tagPostTable.post_id AS post_id,
                   tagPostTable.tag_id AS tag_id,
                   tagPostTable.create_time AS tag_post_create_time,
                   tagPostTable.update_time AS tag_post_update_time,
                   tagTable.name AS tag_name,
                   tagTable.parent_id AS tag_parent_id,
                   tagTable.uniq_name AS tag_uniq_name,
                   postTable.author_id AS post_author_id,
                   postTable.title AS post_title,
                   postTable.status AS post_status,
                   postTable.cover AS post_cover,
                   postTable.summary AS post_summary,
                   postTable.post_type AS post_post_type,
                   postTable.create_time AS post_create_time,
                   postTable.update_time AS post_update_time
                FROM tag_post tagPostTable
                LEFT JOIN tag tagTable ON tagPostTable.tag_id = tagTable.id
                LEFT JOIN post postTable ON tagPostTable.post_id = postTable.id
                WHERE tagPostTable.tag_id = #{tag_id}
            """)
    List<TagPostVo> get(@Param("tag_id") Integer tag_id);
}
