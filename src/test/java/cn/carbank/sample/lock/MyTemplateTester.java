package cn.carbank.sample.lock;

import java.util.concurrent.TimeUnit;

/**
 * 请填写类注释
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月22日
 */
public class MyTemplateTester {

    private String val;
    private long curr;

    public boolean setIfAbsent(String lock, String val, long timeOut, TimeUnit unit) {
        curr = System.currentTimeMillis();
        int random = (int) (Math.random() * 50);
        System.out.println(random);
        try {
            Thread.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (random > 25) {
            this.val = val;
            //System.out.println(System.currentTimeMillis() + "赋值成功" + (System.currentTimeMillis() - curr));
            return true;
        } else {
            //System.out.println(System.currentTimeMillis() + "赋值失败" + (System.currentTimeMillis() - curr));
            return false;
        }
    }

    public String get(String lock) {
        int random = (int) (Math.random() * 50);
        System.out.println(random);
        try {
            Thread.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println(System.currentTimeMillis() + "查询成功" + (System.currentTimeMillis() - curr));
        if (random > 25) {
            return val;
        } else {
            return val + random;
        }
    }

    public void delete(String lock) {
        int random = (int) (Math.random() * 50);
        System.out.println(random);
        try {
            Thread.sleep(random);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println(System.currentTimeMillis() + "删除成功" + (System.currentTimeMillis() - curr));
    }
}
