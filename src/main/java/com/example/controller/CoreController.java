package com.example.controller;

//import com.haihua.haihua.Utils.HttpRequestUtils;
//import org.json.JSONObject;

import com.alibaba.fastjson.JSONObject;
import com.example.util.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @Author zhangdj
 * @Date 2021/6/28:10:40
 */
@Controller
public class CoreController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${appid}")
    private String appid;

    @Value("${callBack}")
    private String callBack;

    @Value("${scope}")
    private String scope;

    @Value("${appsecret}")
    private String appsecret;

//    @RequestMapping("/1")
//    public String index1(Model model) throws UnsupportedEncodingException {
//        String redirect_uri = URLEncoder.encode(callBack, "utf-8");
//        ;
//        model.addAttribute("name", "liuzp");
//        model.addAttribute("appid", appid);
//        model.addAttribute("scope", scope);
//        model.addAttribute("redirect_uri", redirect_uri);
//        return "index1";
//    }


    @RequestMapping("/")
    public String index(Model model) throws UnsupportedEncodingException {
//        String oauthUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=snsapi_login&state=STATE#wechat_redirect";
////        String oauthUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
//        String redirect_uri = URLEncoder.encode(callBack, "utf-8");
//
//        oauthUrl = oauthUrl.replace("APPID", appid).replace("REDIRECT_URI", redirect_uri).replace("SCOPE", scope);
//        model.addAttribute("name", "liuzp");
//        model.addAttribute("oauthUrl", oauthUrl);

        String oauthUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        String redirect_uri = URLEncoder.encode("http://192.168.123.29:8080/login/callBack", "utf-8"); ;
        oauthUrl =  oauthUrl.replace("APPID","wxbf5e4cbd80f6c6f7").replace("REDIRECT_URI",redirect_uri).replace("SCOPE","snsapi_login");
        model.addAttribute("oauthUrl",oauthUrl);
        return "index2";
    }


    @RequestMapping("/callBack")
    public String callBack(String code, String state, Model model, HttpServletRequest request) throws Exception {
        logger.info("??????????????????,code:{},state:{}", code, state);

//        //1.??????code??????access_token
//        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
//        url = url.replace("APPID", appid).replace("SECRET", appsecret).replace("CODE", code);
//        String tokenInfoStr = HttpRequestUtils.httpGet(url, null, null);
//
//        JSONObject tokenInfoObject = JSONObject.parseObject(tokenInfoStr);
//        logger.info("tokenInfoObject:{}", tokenInfoObject);
//
//        //2.??????access_token???openid??????????????????
//        String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
//        userInfoUrl = userInfoUrl.replace("ACCESS_TOKEN", tokenInfoObject.getString("access_token")).replace("OPENID", tokenInfoObject.getString("openid"));
//        String userInfoStr = HttpRequestUtils.httpGet(userInfoUrl, null, null);
//        logger.info("userInfoObject:{}", userInfoStr);
//
//        model.addAttribute("tokenInfoObject", tokenInfoObject);
//        model.addAttribute("userInfoObject", userInfoStr);

        String returns="";
        HttpSession session = request.getSession();
        logger.info("??????????????????,code:{"+code+"},state:{"+state+"}");

        //1.??????code??????access_token
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        url = url.replace("APPID","wxbf5e4cbd80f6c6f7").replace("SECRET","7835d9ad2989573cfb3742edb5fed035").replace("CODE",code);
        String tokenInfoStr =  sendGet(url);

//        JSONObject tokenInfoObject = new JSONObject(tokenInfoStr);
        JSONObject tokenInfoObject = JSONObject.parseObject(tokenInfoStr);
        logger.info("tokenInfoObject:{"+tokenInfoObject+"}");

        //2.??????access_token???openid??????????????????
        String userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        userInfoUrl = userInfoUrl.replace("ACCESS_TOKEN",tokenInfoObject.getString("access_token")).replace("OPENID",tokenInfoObject.getString("openid"));
        String userInfoStr =  sendGet(userInfoUrl);
        logger.info("userInfoObject:{"+userInfoStr+"}");
        String openid = tokenInfoObject.getString("openid");
        if(openid!=null && openid!=""){

        }
        return "result";
    }

    public static String sendGet(String url) {
        String result = "";
        StringBuilder jsonStr = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // ?????????URL???????????????
            URLConnection connection = realUrl.openConnection();
            // ???????????????????????????
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // ?????????????????????
            connection.connect();
            // ???????????????????????????
            Map<String, List<String>> map = connection.getHeaderFields();
            // ??????????????????????????????
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }

            //ConstantUtil.UTF_CODE ????????????
            InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "utf-8");
            char[] buff = new char[1024];
            int length = 0;
            while ((length = reader.read(buff)) != -1) {
                result = new String(buff, 0, length);
                jsonStr.append(result);
            }

        } catch (Exception e) {
            System.out.println("??????GET?????????????????????" + e);
            e.printStackTrace();
        }
        // ??????finally?????????????????????
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return jsonStr.toString();
    }
}