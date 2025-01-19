package moe.lyu.sapiblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import moe.lyu.sapiblog.entity.TagPost;
import moe.lyu.sapiblog.mapper.TagPostMapper;
import moe.lyu.sapiblog.service.ITagPostService;
import org.springframework.stereotype.Service;

@Service
public class ITagPostServiceImpl extends ServiceImpl<TagPostMapper, TagPost> implements ITagPostService {
}
