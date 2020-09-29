package com.zb.blog.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

@Component
public class Verify {

    //验证头像文件的格式
    private String[] fileSuffixes = new String[]{".jpg",".png"};
    public boolean verifyIcon(String fileName){
        //当文件名为null或""时，返回false
        if(StrUtil.isEmpty(fileName)){
            return false;
        }
        for(String fileSuffix : fileSuffixes){
            //当文件名以fileSuffixes数组中的格式结尾时，返回true
            if(fileName.endsWith(fileSuffix)){
                return true;
            }
        }
        return false;
    }
}
