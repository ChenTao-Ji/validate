package com.example.validate.controller;

import com.alibaba.druid.util.Base64;
import com.example.validate.util.VerifyImageUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping(value = "/image")
public class ImageController {

    @RequestMapping(value = "/testImage", method = RequestMethod.GET)
    public void testImage() throws Exception {
        Map<String, byte[]> pictureMap;
        File templateFile;  //模板图片
        File targetFile;  //
        Random random = new Random();
        int templateNo = random.nextInt(4) + 1;
        int targetNo = random.nextInt(20) + 1;

        InputStream stream = getClass().getClassLoader().getResourceAsStream("static/templates/" + templateNo + ".png");
        templateFile = new File(templateNo + ".png");
        FileUtils.copyInputStreamToFile(stream, templateFile);

        stream = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");
        targetFile = new File(targetNo + ".jpg");
        FileUtils.copyInputStreamToFile(stream, targetFile);
        pictureMap = VerifyImageUtil.pictureTemplatesCut(templateFile, targetFile, "png", "jpg");
        byte[] oriCopyImages = pictureMap.get("oriCopyImage");
        byte[] newImages = pictureMap.get("newImage");

        FileOutputStream fout = new FileOutputStream("D:/oriCopyImage.jpg");
        //将字节写入文件
        try {
            fout.write(oriCopyImages);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fout.close();

        FileOutputStream newImageFout = new FileOutputStream("D:/newImage.jpg");
        //将字节写入文件
        newImageFout.write(newImages);
        newImageFout.close();
    }

    @RequestMapping(value = "/getImage", method = RequestMethod.GET)
    public Map getImage(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String, byte[]> pictureMap;
            File templateFile;  //模板图片
            File targetFile;  //
            Random random = new Random();
            int templateNo = random.nextInt(4) + 1;
            int targetNo = random.nextInt(20) + 1;

            InputStream stream = getClass().getClassLoader().getResourceAsStream("static/templates/" + templateNo + ".png");
            templateFile = new File(templateNo + ".png");
            FileUtils.copyInputStreamToFile(stream, templateFile);

            stream = getClass().getClassLoader().getResourceAsStream("static/targets/" + targetNo + ".jpg");
            targetFile = new File(targetNo + ".jpg");
            FileUtils.copyInputStreamToFile(stream, targetFile);
            pictureMap = VerifyImageUtil.pictureTemplatesCut(templateFile, targetFile, "png", "jpg");
            byte[] oriCopyImages = pictureMap.get("oriCopyImage");
            byte[] newImages = pictureMap.get("newImage");

            int X = Integer.parseInt(new String(pictureMap.get("X"),"UTF-8"));
            int Y = Integer.parseInt(new String(pictureMap.get("Y"),"UTF-8"));
            float xPercent = Float.parseFloat(new String(pictureMap.get("xPercent"), "UTF-8"));

            String base64OriCopyImages = Base64.byteArrayToBase64(oriCopyImages);
            String base64newImages = Base64.byteArrayToBase64(newImages);

            map.put("oriCopyImages", base64OriCopyImages);
            map.put("newImages", base64newImages);

            map.put("X", X);
            map.put("Y", Y);
            map.put("xPercent", xPercent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
