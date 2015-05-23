package com.fourteencells.StudentAssociation.utils;

/**
 * 服务器接口类
 * 
 * @author 师春雷
 * 
 */
public class HttpUtils {

	/** 服务器地址 */
	public static final String ROOT_URL = "http://113.11.199.69:3000";
	/** 超时默认时间10秒 */
	public static final int TIME_OUT = 12 * 1000;

	/** 百度地图API服务器地址 */
	public static final String ROOT_BAIDUMAP_URL = "http://api.map.baidu.com/";

	/** 获得当前位置附近热点地域列表 */
	public static final String BAIDUMAP_LOCATION = "geocoder/v2/";
	/** 搜索地理位置 */
	public static final String BAIDUMAP_SEARCH_PLACE = "place/v2/search";
	/** 经纬度纠偏接口 */
	public static final String BAIDUMAP_CORRECT = "geoconv/v1/";

	/** 注册 */
	public static final String REGIST = "/api/users";
	/** 登录 */
	public static final String LOGIN = "/api/login";

	/** 获得社团列表 */
	public static final String GET_CLUBS_LIST = "/api/club";

	/** 获得某一社团的资料 */
	public static final String GET_CLUBS_DETAILS = "/api/clubs/";
	/** 获得某一社团成员列表 */
	public static final String GET_CLUBS_MEMBER_LIST = "/api/clubmember";
	/** 获得某一社团活动列表 */
	public static final String GET_CLUBS_EVENT_LIST = "/api/clubevent";
	/** 获得某一社团所有图片 */
	public static final String GET_CLUBS_ALBUM_LIST = "/api/clubalbum";

	/** 得到某一活动资料 */
	public static final String GET_EVENTS_DETAILS = "/api/events/";
	/** 获得某一活动的成员列表 */
	public static final String GET_EVENTS_MEMBERS_LIST = "/api/eventmember";
	/** 获得某一活动的所有图片 */
	public static final String GET_EVENTS_ALBUMS_LIST = "/api/eventalbum";

	/** 得到照片详情 */
	public static final String GET_ALBUMS_DETAILS = "/api/event_albums/";

	/** 创建社团 */
	public static final String CREATE_CLUB = "/api/clubs";
	/** 创建活动 */
	public static final String CREATE_EVENT = "/api/v2/events";
	/** 创建照片 */
	public static final String CREATE_PHOTO = "/api/event_albums";

	/** 申请加入社团 */
	public static final String JOIN_CLUB = "/api/club_members";
	/** 申请加入活动 */
	public static final String JOIN_EVENT = "/api/event_members";

	/** 退出社团 */
	public static final String EXIT_CLUB = "/api/club_members/";
	/** 退出活动 */
	public static final String EXIT_EVENT = "/api/v2/event_members/";

	/** 我参加的社团 */
	public static final String MY_PARTICIPATED_CLUB = "/api/myclub/";
	/** 我参加的活动 */
	public static final String MY_PARTICIPATED_EVENT = "/api/myevent/";
	/** 我发布的照片 */
	public static final String PUBLISH_PHOTO = "/api/myphoto/";

	/** 发现 */
	public static final String FIND = "/api/find";

	/** 修改个人信息 */
	public static final String UPDATE_USER_INFO = "/api/change_profile";
	/** 获取个人信息 */
	public static final String GET_USER_INFO = "/api/users/";

	/** 签到 */
	public static final String REGISTRATION = "/api/event_register";
	/** 显示签到情况 */
	public static final String STATISTIC = "/api/statistic";

	/** 修改密码 */
	public static final String CHANGE_PASSWORD = "/api/change_password";

	/** 搜索社团 */
	public static final String FIND_CLUB = "/api/findclub";

	/** 发表图片评论 */
	public static final String LEAVE_COMMENT = "/api/leave_comment";
	/** 赞 */
	public static final String LIKE = "/api/like";
	/** 取消赞 */
	public static final String DISLIKE = "/api/dislike";

	/** 获取消息 */
	public static final String GET_NEWS = "/api/messages";
	/** 标识消息已读 */
	public static final String NEWS_MESSAGE = "/api/messages/";

	/** 审核团员申请接口 */
	public static final String CHECK_NEW_MEMBER = "/api/club_application";

}
