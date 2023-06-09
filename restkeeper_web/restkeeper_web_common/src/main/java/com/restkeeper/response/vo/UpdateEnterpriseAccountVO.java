package com.restkeeper.response.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateEnterpriseAccountVO extends AddEnterpriseAccountVO {

    @ApiModelProperty("企业id")
    private String enterpriseId;
}
