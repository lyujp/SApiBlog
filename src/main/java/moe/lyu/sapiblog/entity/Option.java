package moe.lyu.sapiblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@TableName("option")
public class Option {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String key;
    private String val;
    private Boolean optionType;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    public void setKey(String key) {
        this.key = key.toLowerCase().trim();
    }

    public void setOptionType(Boolean optionType) {
        if (optionType == null) {
            this.optionType = false;
        }
        this.optionType = optionType;
    }
}
