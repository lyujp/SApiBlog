package moe.lyu.sapiblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("setting")
public class Setting {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String k;
    private String v;
    private Boolean optionType;
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    public void setK(String k) {
        this.k = k.toLowerCase().trim();
    }

    public void setOptionType(Boolean optionType) {
        if (optionType == null) {
            this.optionType = false;
        }
        this.optionType = optionType;
    }
}
