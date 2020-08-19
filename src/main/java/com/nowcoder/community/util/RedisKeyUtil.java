package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static  final String SPLIT=":";
    private static  final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PREFIX_USER_LIKE="like:user";
    //用户关注key,两份数据便于交互
    private static final String PREFIX_FOLLOWEE="followee";//被关注者
    private static final String PREFIX_FOLLOWER="follower";
    //验证码
    private static final String PREFIX_KAPTCHA="kaptcha";
    //登陆凭证
    private static final String PREFIX_TICKET="ticket";
    //用户
    private static final String PREFIX_USER="user";
    //uv
    private static final String PREFIX_UV="uv";
    private static final String PREFIX_DAU="dau";
    private static final String PREFIX_POST="post";

    //某个实体的赞
    //like：entity：entityType:entityId->set(userId)
    public static String getEntityLikeKey(int entityType,int entityId){

        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;

    }
    //某个用户的赞
    //like:user:userId->int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;

    }
    //某个用户关注的实体
    //followee:userId:entityType->zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }
    //某个实体拥有的粉丝
    //follower:entityType:entityId->zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
    //登陆验证码
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;

    }
    //登陆凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }
    //用户
    public static String getUserKey(int userId){

        return PREFIX_USER+SPLIT+userId;
    }
    //单日uv
    public static String getUVKey(String date){
        return PREFIX_UV+SPLIT+date;
    }
    //区间UV
    public static String getUKKey(String startDate,String endDate){
        return PREFIX_UV+SPLIT+startDate+SPLIT+endDate;
    }
    //单日dau
    public static String getDauKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }
    //区间dau
    public static String getDauKey(String startDate,String endDate) {
        return PREFIX_DAU + SPLIT + startDate+SPLIT+endDate;
    }
    //帖子分数
    public static String getPostScoreKey(){
        return PREFIX_POST+SPLIT+"score";
    }
}
