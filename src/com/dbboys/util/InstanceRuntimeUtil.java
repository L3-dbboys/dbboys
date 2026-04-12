package com.dbboys.util;

import com.dbboys.api.InstanceTabCapability;
import com.dbboys.vo.Connect;
import com.jcraft.jsch.Session;

import java.util.ArrayList;
import java.util.List;

public final class InstanceRuntimeUtil {
    private InstanceRuntimeUtil() {
    }

    public static String loadInformixStyleRuntimeLog(Connect connect) throws Exception {
        Session session = JschUtil.getConnect(connect);
        try {
            return JschUtil.executeCommand(
                    session,
                    JschUtil.extractEnvValue(connect.getInfo()) + "onstat -c |awk '/^MSGPATH/ {print \"tail -1000 \"$2}' |sh"
            );
        } finally {
            JschUtil.disConnect(session);
        }
    }

    public static List<InstanceTabCapability.ConfigEntry> loadInformixStyleConfigEntries(Connect connect) throws Exception {
        Session session = JschUtil.getConnect(connect);
        try {
            String config = JschUtil.executeCommand(
                    session,
                    JschUtil.extractEnvValue(connect.getInfo()) + "onstat -c |grep -v '^$' |grep -v '^#' |sed '1,2d'"
            );
            return parseConfigEntries(config);
        } finally {
            JschUtil.disConnect(session);
        }
    }

    public static boolean isInformixStyleInstanceOnline(Connect connect) throws Exception {
        Session session = JschUtil.getConnect(connect);
        try {
            String instanceStatus = JschUtil.executeCommand(session, JschUtil.extractEnvValue(connect.getInfo()) + "onstat -");
            return instanceStatus.contains("On-Line") || instanceStatus.contains("Read-Only");
        } finally {
            JschUtil.disConnect(session);
        }
    }

    private static List<InstanceTabCapability.ConfigEntry> parseConfigEntries(String rawData) {
        List<InstanceTabCapability.ConfigEntry> result = new ArrayList<>();
        String[] lines = rawData.replaceAll("\r\n", "\n").split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }
            String[] parts = trimmedLine.split("\\s+", 2);
            String paramName = parts[0];
            String paramValue = parts.length > 1 ? parts[1] : "";
            result.add(new InstanceTabCapability.ConfigEntry(paramName, paramValue));
        }
        return result;
    }
}
