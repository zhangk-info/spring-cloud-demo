package com.zk.importbigdata;

import cn.hutool.core.util.IdcardUtil;
import lombok.Data;

/**
 * 必须是可以通过反射创造的类
 */
@Data
public class Test {

    private Long id;
    private String name;
    private String idCard;

    public static void main(String[] args) {
        System.out.println(IdcardUtil.isValidCard18("51102119981010333X"));
    }
}
