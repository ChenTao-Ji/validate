package com.example.validate.controller;

import com.alibaba.druid.util.Base64;
import com.example.validate.util.VerifyImageUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping(value = "/image")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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

            map.put("X", (int) (X * xPercent));
            map.put("Y", Y);

            String ImageId = UUID.randomUUID().toString();

            map.put("ImageId", ImageId);

            redisTemplate.opsForValue().set(ImageId, String.valueOf(X - X * xPercent),60);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @RequestMapping(value = "/checkImage", method = RequestMethod.GET)
    public Map checkImage(@RequestParam Integer width, @RequestParam String imageId) {

        Map<String, Object> map = new HashMap<>();

        String dataString = redisTemplate.opsForValue().get(imageId);
        logger.info("入参：imageId:{},width:{}",imageId,width);
        logger.info("根据imageId取值：imageId:{},width:{}",imageId,dataString);
        if (dataString==null){
            map.put("data", Boolean.FALSE);
            map.put("code", 300);
            map.put("message", "校验失败");
        }
        Double aDouble = new Double(dataString);

        double v = new Double(width).doubleValue() - aDouble.doubleValue();

        if (!(v > -5 && v < 5)){
            map.put("data", Boolean.FALSE);
            map.put("code", 300);
            map.put("message", "校验失败");
        }
        ///移动百分比
        map.put("data", Boolean.TRUE);
        map.put("code", 200);
        map.put("message", "校验图片成功");
        return map;

    }


}
