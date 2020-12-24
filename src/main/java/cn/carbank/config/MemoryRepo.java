package cn.carbank.config;

import cn.carbank.repository.MemoryIdempotentRecordRepoImpl;
import org.springframework.stereotype.Component;

/**
 * 自定义存储-本地存储
 *
 * @author 周承钲(chengzheng.zhou @ ucarinc.com)
 * @since 2020年12月24日
 */
@Component
public class MemoryRepo extends MemoryIdempotentRecordRepoImpl {

}
