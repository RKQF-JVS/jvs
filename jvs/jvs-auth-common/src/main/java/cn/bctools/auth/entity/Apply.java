package cn.bctools.auth.entity;//package cn.bctools.auth.entity;
//
//import com.baomidou.mybatisplus.annotation.*;
//import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
//import cn.bctools.database.entity.po.BasalPo;
//import cn.bctools.database.entity.po.BasePo;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import lombok.experimental.Accessors;
//import org.hibernate.validator.constraints.Length;
//
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//import java.io.Serializable;
//import java.util.*;
//
///**
// * 应用表
// *
// * @author auto
// */
//@Data
//@Accessors(chain = true)
//@TableName(value = "sys_apply", autoResultMap = true)
//public class Apply extends BasalPo implements Serializable {
//
//    @TableId(value = "id", type = IdType.ASSIGN_UUID)
//    @ApiModelProperty(value = "id")
//    private String id;
//    @NotNull(message = "应用名不能为空")
//    @Size(max = 10, message = "应用名不能超过10个字符")
//    @ApiModelProperty(value = "应用名称")
//    private String name;
//    @ApiModelProperty(value = "描述")
//    private String describes;
//    @ApiModelProperty(value = "应用key")
//    private String appKey;
//    @ApiModelProperty(value = "应用凭证")
//    private String appSecret;
//    @TableLogic
//    @ApiModelProperty(value = "逻辑删除")
//    private Boolean delFlag;
//    /**
//     * 指定客户端支持的grant_type,可选值包括authorization_code,password,refresh_token,implicit,client_credentials, 若支持多个grant_type用逗号(,)分隔,如: "authorization_code,password".
//     * 在实际应用中,当注册时,该字段是一般由服务器端指定的,而不是由申请者去选择的,最常用的grant_type组合有: "authorization_code,refresh_token"(针对通过浏览器访问的客户端); "password,refresh_token"(针对移动设备的客户端).
//     * implicit与client_credentials在实际中很少使用.
//     */
//    @TableField(typeHandler = FastjsonTypeHandler.class)
//    private List<String> authorizedGrantTypes;
//    /**
//     * 客户端的重定向URI,可为空, 当grant_type为authorization_code或implicit时, 在Oauth的流程中会使用并检查与注册时填写的redirect_uri是否一致. 下面分别说明:
//     * 当grant_type=authorization_code时, 第一步 从 spring-oauth-server获取 'code'时客户端发起请求时必须有redirect_uri参数, 该参数的值必须与web_server_redirect_uri的值一致. 第二步 用 'code' 换取 'access_token' 时客户也必须传递相同的redirect_uri.
//     * 在实际应用中, web_server_redirect_uri在注册时是必须填写的, 一般用来处理服务器返回的code, 验证state是否合法与通过code去换取access_token值.
//     * 在spring-oauth-client项目中, 可具体参考AuthorizationCodeController.java中的authorizationCodeCallback方法.
//     * 当grant_type=implicit时通过redirect_uri的hash值来传递access_token值.如:
//     * http://localhost:7777/spring-oauth-client/implicit#access_token=dc891f4a-ac88-4ba6-8224-a2497e013865&token_type=bearer&expires_in=43199
//     * 然后客户端通过JS等从hash值中取到access_token值.
//     */
//    @TableField(typeHandler = FastjsonTypeHandler.class)
//    private List<String> registeredRedirectUris;
//    /**
//     * 设定客户端的access_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时).
//     * 在服务端获取的access_token JSON数据中的expires_in字段的值即为当前access_token的有效时间值.
//     * 在项目中, 可具体参考DefaultTokenServices.java中属性accessTokenValiditySeconds.
//     * 在实际应用中, 该值一般是由服务端处理的, 不需要客户端自定义.
//     */
//    private Integer accessTokenValiditySeconds;
//    /**
//     * 设定客户端的refresh_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天).
//     * 若客户端的grant_type不包括refresh_token,则不用关心该字段 在项目中, 可具体参考DefaultTokenServices.java中属性refreshTokenValiditySeconds.
//     * <p>
//     * 在实际应用中, 该值一般是由服务端处理的, 不需要客户端自定义.
//     * ————————————————
//     */
//    private Integer refreshTokenValiditySeconds;
//    /**
//     * 这是一个预留的字段,在Oauth的流程中没有实际的使用,可选,但若设置值,必须是JSON格式的数据,如:
//     * {"country":"CN","country_code":"086"}
//     * 按照spring-security-oauth项目中对该字段的描述
//     * Additional information for this client, not need by the vanilla OAuth protocol but might be useful, for example,for storing descriptive information.
//     * (详见ClientDetails.java的getAdditionalInformation()方法的注释)在实际应用中, 可以用该字段来存储关于客户端的一些其他信息,如客户端的国家,地区,注册时的IP地址等等.
//     * ————————————————
//     * 版权声明：本文为CSDN博主「Sunny3096」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//     */
//    @TableField(typeHandler = FastjsonTypeHandler.class)
//    private Map<String, Object> additionalInformation;
//    /**
//     * 设置用户是否自动Approval操作, 默认值为 'false', 可选值包括 'true','false', 'read','write'.
//     * 该字段只适用于grant_type="authorization_code"的情况,当用户登录成功后,若该值为'true'或支持的scope值,则会跳过用户Approve的页面, 直接授权.
//     * 该字段与 trusted 有类似的功能, 是 spring-security-oauth2 的 2.0 版本后添加的新属性.
//     * ————————————————
//     */
//    private String autoApproveScopes;
//
//    @ApiModelProperty(value = "ICON")
//    private String icon;
//    @ApiModelProperty(value = "logo")
//    private String logo;
//    @ApiModelProperty(value = "bgImg")
//    private String bgImg;
//    @ApiModelProperty(value = "登录域名映射")
//    private String loginDomain;
//}
