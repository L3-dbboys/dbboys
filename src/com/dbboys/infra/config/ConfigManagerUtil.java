package com.dbboys.infra.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class ConfigManagerUtil {
    private static final Logger log = LogManager.getLogger(ConfigManagerUtil.class);
    private static Properties properties = new Properties();
    private static String filePath="etc/config.properties";

    static{
        loadProperties();
    }

    // 读取配置文件
    private static void loadProperties() {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Operation failed", e);
        }
    }

    // 获取属性值
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    // 获取属性值（带默认值）
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    // 修改属性值
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveProperties();
    }

    // 保存修改到文件
    private static void saveProperties() {
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, "Updated configuration");
        } catch (IOException e) {
            log.error("Operation failed", e);
        }
    }

}
