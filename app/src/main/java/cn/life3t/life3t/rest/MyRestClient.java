package cn.life3t.life3t.rest;

import org.androidannotations.annotations.rest.Delete;
import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Put;
import org.androidannotations.annotations.rest.RequiresCookie;
import org.androidannotations.annotations.rest.Rest;
import org.androidannotations.annotations.rest.SetsCookie;
import org.androidannotations.api.rest.RestClientErrorHandling;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import cn.life3t.life3t.message.request.AddAddressRequest;
import cn.life3t.life3t.message.request.ChangePasswordRequest;
import cn.life3t.life3t.message.request.ChangeUserInfoRequest;
import cn.life3t.life3t.message.request.CheckUpdateRequest;
import cn.life3t.life3t.message.request.CreateFloorCleanOrderRequest;
import cn.life3t.life3t.message.request.CreateHoodCleanOrderRequest;
import cn.life3t.life3t.message.request.CreateNormalCleanOrderRequest;
import cn.life3t.life3t.message.request.CreateSofaCleanOrderRequest;
import cn.life3t.life3t.message.request.ExchangeCouponRequest;
import cn.life3t.life3t.message.request.FeedbackRequest;
import cn.life3t.life3t.message.request.LoginRequest;
import cn.life3t.life3t.message.request.ModifyAddressRequest;
import cn.life3t.life3t.message.request.OrderCommentRequest;
import cn.life3t.life3t.message.request.SetCouponRequest;
import cn.life3t.life3t.message.response.AddressListResponse;
import cn.life3t.life3t.message.response.BaseResponse;
import cn.life3t.life3t.message.response.CheckUpdateResponse;
import cn.life3t.life3t.message.response.CommentListResponse;
import cn.life3t.life3t.message.response.CreateOrderResponse;
import cn.life3t.life3t.message.response.GetActivityDetailResponse;
import cn.life3t.life3t.message.response.GetActivityListResponse;
import cn.life3t.life3t.message.response.GetAlipayResponse;
import cn.life3t.life3t.message.response.GetCouponListResponse;
import cn.life3t.life3t.message.response.GetInviteCodeResponse;
import cn.life3t.life3t.message.response.GetOrderCouponListResponse;
import cn.life3t.life3t.message.response.LoginResponse;
import cn.life3t.life3t.message.response.OrderDetailResponse;
import cn.life3t.life3t.message.response.OrderListResponse;
import cn.life3t.life3t.message.response.QuestionListResponse;
import cn.life3t.life3t.message.response.ScheduleResponse;
import cn.life3t.life3t.message.response.StaffDetailResponse;
import cn.life3t.life3t.message.response.StaffListResponse;
import cn.life3t.life3t.message.response.StaffScheduleResponse;
import cn.life3t.life3t.message.response.UserInfoResponse;

/**
 * Created by RexQian on 2014/12/9.
 */
//@Rest(rootUrl = "http://api.3tlife.cn",
@Rest(rootUrl = "http://open.3tlife.cn",
        converters = {MappingJackson2HttpMessageConverter.class,
                ByteArrayHttpMessageConverter.class,
                FormHttpMessageConverter.class,
                StringHttpMessageConverter.class})
public interface MyRestClient extends RestClientErrorHandling {
    public RestTemplate getRestTemplate();
    public void setRestTemplate(RestTemplate template);

    void setRootUrl(String rootUrl);
    String getRootUrl();

    void setCookie(String name, String value);
    String getCookie(String name);

    @Post("/version/CheckUpdate")
    CheckUpdateResponse checkUpdate(CheckUpdateRequest request);

    @Post("/member/register/getAuthCode?phone={phone}")
    BaseResponse getRegisterVerifyCode(String phone);

    @Post("/member/register/verifyAuthCode?code={code}")
    BaseResponse verifyRegisterAuthCode(String code);

    @Post("/member/register/setPassword?password={password}")
    @SetsCookie({"JSESSIONID"})
    LoginResponse register(String password);

    @Post("/member/login")
    @SetsCookie({"JSESSIONID"})
    LoginResponse login(LoginRequest request);

    @Post("/member/reset/getAuthCode?phone={phone}")
    BaseResponse getForgetPasswordVerifyCode(String phone);

    @Post("/member/reset/verifyAuthCode?code={code}")
    BaseResponse verifyForgetPasswordAuthCode(String code);

    @Post("/member/reset/setPassword?password={password}")
    BaseResponse setPassword(String password);

    @Post("/member/changePwd/getAuthCode")
    @RequiresCookie({"JSESSIONID"})
    BaseResponse getChangePasswordVerifyCode();

    @Post("/member/{id}/password")
    @RequiresCookie({"JSESSIONID"})
    BaseResponse changePassword(int id, ChangePasswordRequest request);

    @Get("/auntList/{staffId}")
    @RequiresCookie({"JSESSIONID"})
    StaffDetailResponse getStaffDetail(int staffId);

    @Get("/aunt/comments?auntId={staffId}&page={page}&pageSize={pageSize}")
    @RequiresCookie({"JSESSIONID"})
    CommentListResponse getStaffComments(int staffId, int page, int pageSize);

    @Get("/member/{id}")
    @RequiresCookie("JSESSIONID")
    UserInfoResponse getUserInfo(int id);

    @Post("/member/{id}")
    @RequiresCookie("JSESSIONID")
    BaseResponse changeUserInfo(int id, ChangeUserInfoRequest request);

    @Get("/addressList")
    @RequiresCookie("JSESSIONID")
    AddressListResponse getAddressList ();

    @Delete("/addressList/{id}")
    @RequiresCookie("JSESSIONID")
    BaseResponse deleteAddress (int id);

    @Put("/addressList")
    @RequiresCookie("JSESSIONID")
    BaseResponse addAddress(AddAddressRequest request);

    @Post("/addressList")
    @RequiresCookie("JSESSIONID")
    BaseResponse modifyAddress(ModifyAddressRequest request);

    @Get("/auntList?date={date}&startMin={startMin}&endMin={endMin}&businessId={businessId}&longitude={longitude}&latitude={latitude}")
    @RequiresCookie("JSESSIONID")
    StaffListResponse getStaffList(String date, int startMin, int endMin, int businessId, double longitude, double latitude);

    @Get("/schedule?date={date}&businessId={businessId}&longitude={longitude}&latitude={latitude}")
    @RequiresCookie("JSESSIONID")
    ScheduleResponse getSchedule(String date, int businessId, double longitude, double latitude);

    @Get("/auntList/{id}/schedule?date={date}")
    @RequiresCookie("JSESSIONID")
    StaffScheduleResponse getStaffSchedule(int id, String date);

    @Get("/orders/{orderId}/alipay")
    @RequiresCookie("JSESSIONID")
    GetAlipayResponse getAlipay(int orderId);

    @Put("/feedbackList")
    @RequiresCookie("JSESSIONID")
    BaseResponse sendFeedback(FeedbackRequest request);

    @Put("/houseClean/orders")
    @RequiresCookie("JSESSIONID")
    CreateOrderResponse createNormalCleanOrder(CreateNormalCleanOrderRequest request);

    @Put("/deeplyClean/orders")
    @RequiresCookie("JSESSIONID")
    CreateOrderResponse createDeepCleanOrder(CreateNormalCleanOrderRequest request);

    @Put("/floorWaxing/orders")
    @RequiresCookie("JSESSIONID")
    CreateOrderResponse createFloorCleanOrder(CreateFloorCleanOrderRequest request);

    @Put("/sofaClean/orders")
    @RequiresCookie("JSESSIONID")
    CreateOrderResponse createSofaCleanOrder(CreateSofaCleanOrderRequest request);

    @Put("/hoodsClean/orders")
    @RequiresCookie("JSESSIONID")
    CreateOrderResponse createHoodCleanOrder(CreateHoodCleanOrderRequest request);


    @Get("/memberOrders/completed?page={page}&pageSize={pageSize}")
    @RequiresCookie("JSESSIONID")
    OrderListResponse getFinishedOrders(int page, int pageSize);

    @Get("/memberOrders/inProgress?page={page}&pageSize={pageSize}")
    @RequiresCookie("JSESSIONID")
    OrderListResponse getProgressingOrders(int page, int pageSize);

    @Get("/orders/{orderId}")
    @RequiresCookie("JSESSIONID")
    OrderDetailResponse getOrderDetail(int orderId);

    @Post("/memberOrder/comment")
    @RequiresCookie("JSESSIONID")
    BaseResponse commentOrder(OrderCommentRequest request);

    @Post("/orders/{orderId}/cancel")
    @RequiresCookie("JSESSIONID")
    BaseResponse cancelOrder(int orderId);

    /** 获取活动列表 */
    @Get("/activityList")
    GetActivityListResponse getActivityList ();

    /** 获取活动详情 */
    @Get("/activityList/{id}")
    @RequiresCookie("JSESSIONID")
    GetActivityDetailResponse getActivityDetail (long id);


    /** 获取常见问题 */
    @Get("/questionAnswerList")
    QuestionListResponse getQuestionList();

    /** 获取邀请码 */
    @Get("/member/myInviteCode")
    @RequiresCookie("JSESSIONID")
    GetInviteCodeResponse getMyInvitationCode();

    /** 获取优惠券列表 */
    @Get("/couponList")
    @RequiresCookie("JSESSIONID")
    GetCouponListResponse getCouponList();

    /** 获取订单的优惠券列表 */
    @Get("/orders/{id}/couponList")
    @RequiresCookie("JSESSIONID")
    GetOrderCouponListResponse getOrderCouponList(int id);

    /** 使用优惠券 */
    @Post("/orders/{orderId}/coupon")
    @RequiresCookie("JSESSIONID")
    GetAlipayResponse payUseCoupon(int orderId, SetCouponRequest request);

    /** 优惠后支付金额为零 */
    @Post("/orders/{id}/payFree")
    @RequiresCookie("JSESSIONID")
    BaseResponse payFree(int id);

    /** 优惠码兑换 */
    @Post("/exchangeCoupon")
    @RequiresCookie("JSESSIONID")
    BaseResponse exchangeCoupon(ExchangeCouponRequest request);
}
