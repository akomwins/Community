package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {
    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    private static final Logger logger= LoggerFactory.getLogger(DiscussPostService.class);
    @Value("${caffeine.posts.max_size}")
    private int maxSize;
    @Value("${caffeine.posts.expires_seconds}")
    private int expiredSeconds;
    //Caffeine核心接口：Cache,LoadingCache同步缓存,AsyncLoadingCache支持并发
    //帖子列表缓存
    private LoadingCache<String,List<DiscussPost>>postListCache;
    //帖子总数的缓存
    private LoadingCache<Integer,Integer>postRowsCache;
    @PostConstruct
    public void init(){
        //初始化帖子列表缓存
        postListCache= Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expiredSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        //数据没用进行db查询
                        if(key==null||key.length()==0){
                            throw new IllegalArgumentException("参数错误");

                        }
                        else{
                            String[] params=key.split(":");
                            if(params==null||params.length!=2){
                                throw new IllegalArgumentException("参数错误");
                            }
                            int offset=Integer.valueOf(params[0]);
                            int limit=Integer.valueOf(params[1]);
                            //二级缓存:Redis->mysql

                            logger.debug("load post list from db");

                            return discussPostMapper.selectDiscussPosts(0,offset,limit,0);

                        }

                    }
                });
        //初始化帖子总数缓存
        postRowsCache=Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expiredSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {

                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });

    }
    public List<DiscussPost>findDiscussPosts(int userId,int offset,int limit,int orderMode){
         if(userId==0&&orderMode==0){
             return postListCache.get(offset+":"+limit);
         }
         logger.debug("load post list from db");
        return discussPostMapper.selectDiscussPosts(userId,offset,limit,orderMode);

    }
    public int findDiscussPostRows(int userId){
        if(userId==0)
            return postRowsCache.get(userId);
        logger.debug("load post rows from db");
        return discussPostMapper.selectDiscussPostRows(userId);
    }
    //增加帖子
    public int addDiscussPost(DiscussPost post){
        if(post==null){
            throw new IllegalArgumentException("参数不能为空");

        }
        //对标签做处理,转义html标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertDiscussPost(post);
    }
    //根据id查询帖子
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }
    //更新评论数量
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }
    //
    public int updateType(int id,int type){
        return discussPostMapper.updateType(id,type);
    }
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id,status);
    }
    public int updateScore(int id,double score){
        return discussPostMapper.updateScore(id,score);
    }
}
