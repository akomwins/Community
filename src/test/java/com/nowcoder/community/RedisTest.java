package com.nowcoder.community;

import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.BitSet;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void testString(){
        String redisKey="test:count";
        redisTemplate.opsForValue().set(redisKey,1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }
    @Test
    public void testHash(){
        String redisKey="test:user";
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"name","yewenxuan");


        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"name"));

    }
    @Test
    public void testList(){
        String redisKey="test:ids";
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);



        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));



    }
    @Test
    public void testSet(){
        String redisKey="test:teachers";
        redisTemplate.opsForSet().add(redisKey,"yefei","yewenxuan","张开宙");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));//随机弹出
        System.out.println(redisTemplate.opsForSet().members(redisKey));


    }
    @Test
    public void testZSet(){
        String redisKey="test:students";
        redisTemplate.opsForZSet().add(redisKey,"yefei",80);
        redisTemplate.opsForZSet().add(redisKey,"张三",60);
        redisTemplate.opsForZSet().add(redisKey,"李四",70);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"yefei"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey,"yefei"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,2));



    }
    @Test
    public void testKeys(){
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));//查看是否还存在
        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);
    }
    //多次访问同一个key，绑定形式
    @Test
    public void testBoundOperations(){
        String redisKey="test:count";
        BoundValueOperations operations=redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        System.out.println(operations.get());

    }
    //事务，会把命令放入队列中存着，提交时批量执行，查询时不方便
    //编程式
    @Test
    public void testTrans(){
        Object object=redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey="test:tx";
                redisOperations.multi();//启用事务
                //逻辑处理
                redisOperations.opsForSet().add(redisKey,"zhangsan");
                redisOperations.opsForSet().add(redisKey,"zhang");
                redisOperations.opsForSet().add(redisKey,"san");
                System.out.println(redisOperations.opsForSet().members(redisKey));
                return redisOperations.exec();//提交事务
            }
        });
        //必须得提交事务后才能查看值
        System.out.println(object);
    }
    //统计20万个重复数据的独立总数
    @Test
    public void testHyperLogLog(){
        String redisKey="test:hll:01";
        for (int i = 1; i <=100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        for (int i = 1; i <=100000; i++) {
            int r=(int)(Math.random()*100000+1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);

        }
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));

    }
    //对数据进行合并，统计合并后的重复数据的独立总数
    @Test
    public void testHyperloglogUnion(){
        String redisKey="test:hll:02";
        for(int i=1;i<=10000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        String redisKey1="test:hll:03";
        for(int i=5001;i<=15000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey1,i);
        }
        String redisKey2="test:hll:04";
        for(int i=10001;i<=20000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey2,i);
        }
        String unionKey="test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey,redisKey,redisKey1,redisKey2);
        System.out.println(redisTemplate.opsForHyperLogLog().size(unionKey));
    }
    //统计一组数据布尔值
    @Test
    public void testBitmap(){
        String redisKey="test:bm:01";
        //记录
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        redisTemplate.opsForValue().setBit(redisKey,4,true);
        redisTemplate.opsForValue().setBit(redisKey,7,true);
        //查每一位的结果
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        //统计
        Object obj=redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {

                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

    }
    //统计三组数据的布尔值，并对or运算
    @Test
    public void testBitmapOperation(){
        String redisKey2="test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey2,0,true);
        redisTemplate.opsForValue().setBit(redisKey2,1,true);
        redisTemplate.opsForValue().setBit(redisKey2,2,true);
        String redisKey3="test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey3,2,true);
        redisTemplate.opsForValue().setBit(redisKey3,3,true);
        redisTemplate.opsForValue().setBit(redisKey3,4,true);
        String redisKey4="test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey4,4,true);
        redisTemplate.opsForValue().setBit(redisKey4,5,true);
        redisTemplate.opsForValue().setBit(redisKey4,6,true);
        String redisKey="test:bm:or";
        Object object=redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),redisKey2.getBytes(),redisKey3.getBytes(),redisKey4.getBytes());
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(object);
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,2));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,3));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,4));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,5));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey,6));



    }
}
