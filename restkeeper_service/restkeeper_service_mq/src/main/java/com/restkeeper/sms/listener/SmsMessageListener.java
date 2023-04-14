package com.restkeeper.sms.listener;

import com.alibaba.alicloud.sms.ISmsService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.sms.SmsObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsMessageListener {

    @Autowired
    private ISmsService smsService;

    /**
     * 监听指定队列
     * @param msg  就是进行JSON转换之后的msgObject类
     */
    @RabbitListener(queues = SystemCode.SMS_ACCOUNT_QUEUE)
    public void getAccountMessage(String msg){
        log.info("发送短信的监听类接收到了消息："+msg);

        //转换参数
        SmsObject smsObject = JSON.parseObject(msg, SmsObject.class);

        //基于sms组件进行短信的发送
//        SendSmsResponse sendSmsResponse = this.sendSms(smsObject.getPhoneNumber(),smsObject.getSignName(),smsObject.getTemplateCode(),smsObject.getTemplateJsonParam());
        SendSmsResponse sendSmsResponse = this.sendSms(smsObject.getPhoneNumber(),smsObject.getSignName(),smsObject.getTemplateCode(),smsObject.getTemplateJsonParam());


        //打印结果
        log.info(JSON.toJSONString(sendSmsResponse));
    }

    //发送手机短信
    private SendSmsResponse sendSms(String phoneNumber, String signName, String templateCode, String templateJsonParam) {
        //创建请求对象
        SendSmsRequest request = new SendSmsRequest();
        //参数设置
        request.setPhoneNumbers(phoneNumber);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "6666");
        log.info(jsonObject.toJSONString());

//        request.setTemplateParam(templateJsonParam);

        request.setTemplateParam(jsonObject.toJSONString());

        //创建相应对象
        SendSmsResponse sendSmsResponse;
        try {
            //使用alibaba提供的组件进行发送
            //返回的数据就是Response
            sendSmsResponse = smsService.sendSmsRequest(request);

        } catch (ClientException e) {
            e.printStackTrace();
            sendSmsResponse = new SendSmsResponse();
        }


        return sendSmsResponse;
    }

}
