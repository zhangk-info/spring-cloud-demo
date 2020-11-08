package com.zk.redis;

import cn.hutool.core.date.DateUtil;
import com.zk.configuration.redis.RedisService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;

/**
 * 用redis做日活用户及性能分析
 */
public class RedisForAnalysis {

	@Resource
	private RedisService redisService;

	/**
	 * 更新并获取累计用户数
	 *
	 * @param userId
	 * @param day
	 * @return
	 */
	@Transactional//开启redis事务，让这个方法的所有命令一个请求传输给redis,但是redis事务并不是原子性的
	public Long updateAndSetUser(Long userId, String day) {
		String key = "acc:users";
		//累计活跃
		redisService.bSet(key, userId);
		String activeKey = String.format("active:$s", (day == null || "".equals("day") ? DateUtil.format(new Date(), "yyyy-MM-dd") : day));
		if (!redisService.bGet(activeKey, userId)) {
			//首次访问，设置当日活跃
			redisService.bSet(activeKey, userId);
		}
		return redisService.bCount(key);
	}

	/**
	 * 获取指定天数内的活跃用户
	 * @param dayNum
	 * @return
	 */
	public Long getActiveUser(Integer dayNum) {
		ArrayList<String> days = new ArrayList<>();
		Date currentDate = new Date();
		for (int day = 1; day <= dayNum; day++) {
			days.add(String.format("active:$s", DateUtil.format(DateUtils.addDays(currentDate, -1), "yyyy-MM-dd")));
		}
		days.add(String.format("active:$s", DateUtil.format(currentDate, "yyyy-MM-dd")));
		String key = String.format("active:lastdays:$d:$s", dayNum, DateUtil.format(currentDate, "yyyy-MM-dd"));

		redisService.bOpWithAdd(key, days);

		return redisService.bCount(key);
	}
}
