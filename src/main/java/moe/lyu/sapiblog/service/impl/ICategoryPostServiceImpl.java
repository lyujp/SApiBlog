package moe.lyu.sapiblog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import moe.lyu.sapiblog.entity.CategoryPost;
import moe.lyu.sapiblog.mapper.CategoryPostMapper;
import moe.lyu.sapiblog.service.ICategoryPostService;
import org.springframework.stereotype.Service;

@Service
public class ICategoryPostServiceImpl extends ServiceImpl<CategoryPostMapper, CategoryPost> implements ICategoryPostService {
}
