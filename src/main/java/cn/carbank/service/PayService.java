package cn.carbank.service;

import java.math.BigDecimal;

/**
 * 支付
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月17日
 */
public interface PayService {

    /**
     * 支付
     * @param no
     * @param amount
     * @return
     */
    String payOrder(String no, BigDecimal amount);

    /**
     * 添加本地历史记录
     * @param no
     * @param amount
     * @param payTimeStr
     * @return
     */
    Boolean addLocalHistory(String no, BigDecimal amount, String payTimeStr);

}
