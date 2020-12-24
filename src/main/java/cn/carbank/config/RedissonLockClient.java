package cn.carbank.config;

import cn.carbank.locksupport.Lock;
import cn.carbank.locksupport.LockClient;
import cn.carbank.locksupport.LockModel;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 自定义锁-基于Redisson
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月24日
 */
//@Component
public class RedissonLockClient implements LockClient, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonLockClient.class);

    private RedissonClient client;

    @Override
    public Lock getLock(String s, LockModel lockModel) {
        RLock lock = client.getLock(s);
        return new Lock() {
            @Override
            public void lock(long expireTime, TimeUnit timeUnit) {
                LOGGER.info("redisson lock {}", Thread.currentThread());
                lock.lock(expireTime, timeUnit);
            }

            @Override
            public boolean tryLock(long expireTime, long tryOutTime, TimeUnit timeUnit) throws InterruptedException {
                LOGGER.info("redisson try lock {},{}", expireTime, tryOutTime);
                return lock.tryLock(tryOutTime, expireTime, timeUnit);
            }

            @Override
            public boolean isLock() {
                return lock.isLocked();
            }

            @Override
            public void unlock() {
                LOGGER.info("redisson unlock {}", Thread.currentThread());
                lock.unlock();
            }
        };
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Config config = new Config();
        config.setLockWatchdogTimeout(1000L)
        .useSingleServer()
        .setTimeout(1000000)
        .setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create();
        this.client = redissonClient;
    }
}
