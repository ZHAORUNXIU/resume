package com.x.resume.common.manager.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.x.resume.common.constant.Code;
import com.x.resume.common.constant.UserCode;
import com.x.resume.common.model.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import static com.x.resume.common.util.Log.format;

public class AliyunClient {

    private static final Logger LOG = LoggerFactory.getLogger(AliyunClient.class);

    private String regionId;

    private String accessKeyId;

    private String accessKeySecret;

    private String signName;

    private String templateCode;

    private IAcsClient client;

    public AliyunClient() {
    }

    public AliyunClient(String regionId, String accessKeyId, String accessKeySecret) {
        this.regionId = regionId;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    @PostConstruct
    void init() throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);

        this.client = new DefaultAcsClient(profile);

    }

    /**
     * 发送短信
     *
     * @param phone
     * @param code
     */
    public Result<Boolean> sendMessage(String phone, String code) {

        // 构建请求
        CommonRequest request = new CommonRequest();

        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");

        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);

        HashMap<String, Object> map = new HashMap<>();
        map.put("code", code);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(map));

        try {
            CommonResponse response = client.getCommonResponse(request);
            return Result.success(response.getHttpResponse().isSuccess());
        } catch (ServerException e) {
            LOG.warn(format("由于系统维护，信息发送失败"), e);
            return Result.failure(Code.SYSTEM_ERROR);
        } catch (ClientException e) {
            LOG.warn(format("由于系统维护，信息发送失败"), e);
            return Result.failure(Code.SYSTEM_ERROR);
        }
    }


    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
}
