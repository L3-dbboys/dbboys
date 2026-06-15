package com.dbboys.core;

import com.dbboys.model.Connect;

/**
 * 实例管理页签所需的平台能力。
 * 用于把 UI 层依赖的 OS 用户、安装目录环境变量、巡检文案等差异从控件类中抽离。
 */
public interface InstanceManagerCapability {

    boolean supportsInstanceManager(Connect connect);

    String installDirEnvName();

    String adminOsUser(Connect connect);

    default String runtimeLogCommand() {
        return "onstat -m";
    }

    default String versionExpectation() {
        return "";
    }
}
