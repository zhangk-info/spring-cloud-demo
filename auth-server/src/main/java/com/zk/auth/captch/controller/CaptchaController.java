package com.zk.auth.captch.controller;

import com.zk.auth.captch.services.CaptchaStore;
import com.zk.auth.captch.util.Captchas;
import com.zk.auth.captch.util.ClientUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 图片验证码
 */
@Api(tags = "验证码 API后台管理接口")
@RestController
@RequestMapping
public class CaptchaController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private CaptchaStore captchaStore;

    @GetMapping(value = "captchas")
    public String getCaptcha() {
        String signature = Base64Utils.encode(UUID.randomUUID().toString().getBytes()).toString();
        captchaStore.set(signature, null);
        return signature;
    }

    /**
     * 生成带验证码的图片
     *
     * @param response
     * @param signature
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "captchas.jpg", method = RequestMethod.GET)
    public void getCaptchaImage(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam(value = "signature", required = false) String signature) throws IOException {
        if (captchaStore.hasSignature(signature)) {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");
            String verifyCode = Captchas.generateVerifyCode(4);
            captchaStore.set(signature, verifyCode);
            int w = 120, h = 40;
            Captchas.outputImage(w, h, response.getOutputStream(), verifyCode);
        } else {
            logger.error("非法访问[{}]", ClientUtils.getClientIp(request));
        }
    }

    /**
     * @param signature
     * @param code
     * @return true或fasle, ture表示验证成功, false表示验证失败
     */
//    @PostMapping("/captchas/validate")
//    @ResponseBody
    public Boolean checkCaptcha(@RequestParam(value = "signature", required = false) String signature,
                                @RequestParam(value = "code", required = false) String code) {
        captchaStore.validated(signature, code);
        return true;
    }
}
