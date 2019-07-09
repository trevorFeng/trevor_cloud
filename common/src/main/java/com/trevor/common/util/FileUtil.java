package com.trevor.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author trevor
 * @date 2019/3/4 12:56
 */
@Slf4j
public class FileUtil {

    /**
     *
     * @param directoryPath 目录url
     * @param fileName 文件名
     * @param in 文件流
     * @return
     */
    public static Boolean saveFileToDirectory(String directoryPath , String fileName , InputStream in){
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            try {
                FileUtils.copyInputStreamToFile(in, new File(
                        directoryPath, fileName));
            } catch (IOException e) {
                log.error("保存文件错误" ,e);
                return false;
            }
        }else {
            directory.mkdir();
            try {
                FileUtils.copyInputStreamToFile(in, new File(
                        directoryPath, fileName));
            } catch (IOException e) {
                log.error("保存文件错误");
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param directoryPath 目录url
     * @param fileName 文件名
     * @return
     */
    public static Boolean delFile(String directoryPath , String fileName ) {
        File file = new File(directoryPath+"///"+fileName);
        if (!file.exists()) {
            return false;
        }
        FileUtils.deleteQuietly(file);
        return true;
    }
}
