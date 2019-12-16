package com.restkeeper.operator.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 运营端管理员
 * </p>
 */
@Data
@Accessors(chain = true)
public class OperatorUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    private String loginname;

    private String loginpass;


}
