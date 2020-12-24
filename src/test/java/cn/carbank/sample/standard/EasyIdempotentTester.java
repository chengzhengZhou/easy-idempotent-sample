package cn.carbank.sample.standard;

import cn.carbank.service.PayService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试用例
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月24日
 */

@RunWith(SpringRunner.class)
@SpringBootTest("standard")
public class EasyIdempotentTester {


    @Autowired
    private PayService payService;

    @Test
    public void testPayOrder() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                try {
                    countDownLatch.await();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String re = payService.payOrder("NO1001", BigDecimal.TEN);
                Assert.assertEquals("支付返回错误", re, "success");
            });
        }
        countDownLatch.countDown();
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddLocalHistory() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Boolean re = payService.addLocalHistory("NO1001", BigDecimal.TEN, "2020-12-24 12:00");
                Assert.assertEquals(re, true);
            });
        }
        countDownLatch.countDown();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
