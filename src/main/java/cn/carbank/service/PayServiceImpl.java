package cn.carbank.service;

import cn.carbank.idempotent.annotation.Idempotent;
import cn.carbank.idempotent.annotation.StorageParam;
import cn.carbank.idempotent.constant.StorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 支付实现
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月17日
 */
@Service
public class PayServiceImpl implements PayService {
    private final Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);

    @Idempotent(value = "#no + ':' + #amount", idempotentMethod = "payOrderIdempotent")
    @Override
    public String payOrder(String no, BigDecimal amount) {
        // @TODO 支付过程
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("{}支付 {}元 成功", no, amount);
        return "success";
    }

    @Idempotent(idempotentMethod = "addLocalHistoryIdempotent",
        storageParams = {@StorageParam(storage = StorageType.MEMORY, expireTime = 5000, timeUnit = TimeUnit.MILLISECONDS)})
    @Override
    public Boolean addLocalHistory(String no, BigDecimal amount, String payTimeStr) {

        logger.info("添加记录 {}", no);
        return true;
    }

    private Boolean addLocalHistoryIdempotent(String no, BigDecimal amount, String payTimeStr) {
        logger.info("{} 已记录", no);
        return true;
    }
    private String payOrderIdempotent(String no, BigDecimal amount) {
        logger.info("{}已经支付过啦", no);
        return "success";
    }
}
