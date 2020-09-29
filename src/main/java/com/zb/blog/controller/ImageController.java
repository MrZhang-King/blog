package com.zb.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

@Controller
public class ImageController {

    /**
     *通过IO流将头像图片写回
     * IO流读取图片
     * @param imgName 图片名字，即图片保存在服务器上的名称
     */
    @GetMapping("/showImage/{imgName}")
    public void showPhoto(@PathVariable("imgName") String imgName, HttpServletResponse response)
            throws Exception {
       String path = "/opt/duyi/images/userIcon/";
        this.imageIO(imgName,path,response);
    }

    /**
     * 通过IO流将博客图片写回
     * IO流读取图片
     * @param imgName 图片名字，即图片保存在服务器上的名称
     */
    @GetMapping("/showBlogImage/{imgName}")
    public void showBLogPhoto(@PathVariable("imgName") String imgName, HttpServletResponse response)
            throws Exception {

       String path = "opt/duyi/images/blogImg/";
       this.imageIO(imgName,path,response);
    }


    private void imageIO(String imgName, String path, HttpServletResponse response)
            throws Exception {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        // 获得的系统的根目录
        File fileParent = new File(File.separator);
        // 获得opt/duyi/images/userIcon/目录
        File file = new File(fileParent, path + imgName);
        BufferedImage bi = ImageIO.read(new FileInputStream(file));
        ServletOutputStream out = response.getOutputStream();
        if(imgName.endsWith("jpg")){
            ImageIO.write(bi, "jpg", out);
        }
        if(imgName.endsWith("png")){

            ImageIO.write(bi, "png", out);
        }
        try {
            out.flush();
        } finally {
            out.close();
        }
    }


    @RequestMapping("/showimage")
    public String showphoto(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws Exception {

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        // 获得的系统的根目录
        File fileParent = new File(File.separator);
        String photoName = (String) session.getAttribute("photoName");
        // 获得/usr/CBeann目录
        File file = new File(fileParent, "usr/CBeann/temp/" + photoName);


        BufferedImage bi = ImageIO.read(new FileInputStream(file));
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;

    }
}
