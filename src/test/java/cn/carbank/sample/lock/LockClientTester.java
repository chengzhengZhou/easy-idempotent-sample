package cn.carbank.sample.lock;

import cn.carbank.exception.IdempotentRuntimeException;
import cn.carbank.locksupport.Lock;
import cn.carbank.locksupport.LockClient;
import cn.carbank.locksupport.LockModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 请填写类注释
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月22日
 */
public class LockClientTester implements LockClient {

    public static void main(String[] args) throws InterruptedException {
        LockClientTester tester = new LockClientTester();
        tester.setTemplate(new MyTemplateTester());
        Lock lock = tester.getLock("no1001", LockModel.REENTRANT);
        lock.lock(1000, TimeUnit.MILLISECONDS);
        lock.unlock();
    }

    private MyTemplateTester stringRedisTemplate;

    @Autowired
    public void setTemplate(MyTemplateTester stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Lock getLock(String lock, LockModel lockModel) {
        Assert.notNull(lock, "lock key is required.");
        return new RedisLock(lock, stringRedisTemplate);
    }

    static class RedisLock implements Lock{

        private MyTemplateTester template;
        private String lock;
        private UUID uuid;

        public RedisLock(String lock, MyTemplateTester template) {
            this.template = template;
            this.lock = lock;
            this.uuid = UUID.randomUUID();
        }

        public String getLockVal() {
            return String.format("%s:%s", uuid.toString(), Thread.currentThread().getId());
        }

        @Override
        public void lock(long timeout, TimeUnit timeUnit) {
            // 均设置超时，等待两倍的过期时间
            int interruptedTimes = 0;
            boolean isLock = false;
            while(interruptedTimes < 10) {
                try {
                    isLock = tryLock(timeout, timeout * 2, timeUnit);
                    break;
                } catch (InterruptedException e) {
                    interruptedTimes++;
                }
            }
            if (!isLock) {
                throw new IdempotentRuntimeException("get lock " + lock + " fail.");
            }
        }

        @Override
        public boolean tryLock(long timeout, long tryTimeout, TimeUnit timeUnit) throws InterruptedException {
            long curr = System.currentTimeMillis();
            boolean isLock = false;
            long time = timeUnit.toMillis(tryTimeout);
            long current = System.currentTimeMillis();
            long rest = 0;
            int shortRetryCount = 0;
            int retryCount = 0;
            for(;;) {
                isLock = template.setIfAbsent(lock, getLockVal(), timeout, timeUnit);
                if (isLock) {
                    System.out.println("结束啦" + (System.currentTimeMillis() - curr));
                    return true;
                } else {
                    rest = time - (System.currentTimeMillis() - current);
                    if(rest < 0) {
                        System.out.println(rest + " < 0T结束啦" + (System.currentTimeMillis() - curr));
                        return false;
                    }

                    String redisVal = template.get(lock);
                    if (redisVal == null) {
                        rest = time - (System.currentTimeMillis() - current);
                        shortRetryCount++;
                        retryCount++;

                        // 快速失败，可能是由于key的过期时间过短
                        if (rest < 0 || shortRetryCount >= 3) {
                            System.out.println("快速失败");
                            System.out.println("结束啦" + (System.currentTimeMillis() - curr));
                            return false;
                        }
                        continue;
                    } else {
                        if (getLockVal().equals(redisVal)) {
                            System.out.println("结束啦" + (System.currentTimeMillis() - curr));
                            return true;
                        } else {
                            rest = time - (System.currentTimeMillis() - current);
                            if (rest < 0) {
                                System.out.println(rest + " < 0结束啦" + (System.currentTimeMillis() - curr));
                                return false;
                            }
                            retryCount++;

                            autoSleep(retryCount, rest);
                        }
                    }
                }
            }
        }

        /**
         * 前5次快速尝试，后面固定折减，最长1000毫秒，最短50毫秒
         * 折减速率递减，初期等待时间相对长些
         * @param retryCount
         * @param time 毫秒值
         */
        private void autoSleep(int retryCount, long time) throws InterruptedException {
            long l;
            if (retryCount <=5 ) {
                l = System.currentTimeMillis();
                System.out.println("均衡的睡了");
                Thread.sleep(Math.min(time, 10));
                System.out.println("睡醒啦" + (System.currentTimeMillis() - l));
            } else {
                if (time < 50) {
                    l = System.currentTimeMillis();
                    System.out.println("最后一睡");
                    Thread.sleep(50);
                    System.out.println("睡醒啦" + (System.currentTimeMillis() - l));
                } else {
                    long cutTime = (long) time >>> 1;
                    l = System.currentTimeMillis();
                    System.out.println("随机睡");
                    Thread.sleep(Math.min(1000, Math.max(50, cutTime)));
                    System.out.println("睡醒啦" + (System.currentTimeMillis() - l));
                }
            }
        }

        @Override
        public boolean isLock() {
            String redisVal = template.get(lock);
            if (redisVal == null) {
                return false;
            }
            if (getLockVal().equals(redisVal)) {
                return true;
            }
            return false;
        }

        /**
         * 该解锁流程非完美方案
         * 极端情况出现误删非当前客户端设置的Key
         * @throws IllegalMonitorStateException
         *          锁超时或非当前线程设置的锁
         */
        @Override
        public void unlock() {
            if (isLock()) {
                template.delete(lock);
            } else {
                throw new IllegalMonitorStateException("attempt to unlock lock, not locked by current thread by id：" + getLockVal());
            }
        }
    }

}
