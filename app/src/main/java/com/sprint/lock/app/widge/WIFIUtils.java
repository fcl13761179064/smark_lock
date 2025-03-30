package com.sprint.lock.app.widge;

public class WIFIUtils {

    //获取wifi 密码类型数值，对标WifiConfiguration  security types，需要自适配
    /**
     * Security type for an open network.
     */
    public static final int SECURITY_TYPE_OPEN = 0;
    /**
     * Security type for a WEP network.
     */
    public static final int SECURITY_TYPE_WEP = 1; //wep
    /**
     * Security type for a PSK network.
     */
    public static final int SECURITY_TYPE_PSK = 2; //wpa/wpa2
    /**
     * Security type for an EAP network.
     */
    public static final int SECURITY_TYPE_EAP = 3; //802.1x eap
    /**
     * Security type for an SAE network.
     */
    public static final int SECURITY_TYPE_SAE = 4; //wap3
    /**
     * Security type for an EAP Suite B network.
     */
    public static final int SECURITY_TYPE_EAP_SUITE_B = 5; //802.1x eap b

    /**
     * Security type for an OWE network.
     */
    public static final int SECURITY_TYPE_OWE = 6; //后面的情况不判断了，这里就是other！
    /**
     * Security type for a WAPI PSK network.
     */
    public static final int SECURITY_TYPE_WAPI_PSK = 7;
    /**
     * Security type for a WAPI Certificate network.
     */
    public static final int SECURITY_TYPE_WAPI_CERT = 8;

    //这里只适配0-5，加上第六种 Others，显示为OWE/WAPI，后面三种基本用不到！


    //显示给用户的wifi 加密类型
    public static final String NONE_STRING = "NONE";
    public static final String WEP_STRING = "WEP";
    public static final String PSK_STRING = "WPA/WPA2";
    public static final String EAP_STRING = "802.1X/EAP";
    public static final String SAE_STRING = "WPA3";
    public static final String EAP_B_STRING = "802.1X/EAP_B";
    public static final String OWE_WAPI = "OWE/WAPI"; //其他类型

    //关键字(内部使用判断协议包含关系),可以依据ScanResult的静态判断协议方法：
    public static final String CAPABILITIES_WEP = "WEP";
    public static final String CAPABILITIES_PKS = "PSK"; //wpa/wap2
    public static final String CAPABILITIES_EAP = "EAP";
    public static final String CAPABILITIES_SAE = "SAE"; //wpa3
    public static final String CAPABILITIES_EAP_B = "SUITE-B";
    public static final String CAPABILITIES_WAPI = "WAPI";
    public static final String CAPABILITIES_OWE = "OWE";


    //获取wifi 密码类型数值，参数为：ScanResult.capabilities
    public static int getWifiSecurityInt(String capabilities) { //扫描的ScanResult.capabilities
        // 参考 WifiConfiguration getSsidAndSecurityTypeString() 并不完全
        if (capabilities.contains(CAPABILITIES_SAE)) {
            return SECURITY_TYPE_SAE;
        } else if (capabilities.contains(CAPABILITIES_EAP_B)) {
            return SECURITY_TYPE_EAP_SUITE_B;
        } else if (capabilities.contains(CAPABILITIES_EAP)) {
            return SECURITY_TYPE_EAP;
        } else if (capabilities.contains(CAPABILITIES_PKS)) {
            return SECURITY_TYPE_PSK;
        } else if (capabilities.contains(CAPABILITIES_WEP)) {
            return SECURITY_TYPE_WEP;
        } else if (capabilities.contains(CAPABILITIES_WAPI) || capabilities.contains(CAPABILITIES_OWE)) { //其他类型判断
            return SECURITY_TYPE_OWE;
        }
        //最后返回无密码情况
        return SECURITY_TYPE_OPEN;
    }


    //获取wifi 密码类型字符，参数为：ScanResult.capabilities
    //判断顺序参考：ScanResultUtil.setAllowedKeyManagementFromScanResult
    // 先->后：WPA3->WAPI(常用的最后两种)-->PKS（WPA/WPA2）-->EAP_B-->EAP（802.1）-->WEP-->OWE（特殊的无密wifi）-->OPEN
    //不过可以参考AccessPoint，不判断 WAPI 和 OWE ，因为基本不用用到。
    public static String getWifiSecurityString(String capabilities) { //扫描的ScanResult.capabilities
        // 参考 WifiConfiguration getSsidAndSecurityTypeString() 并不完全
        if (capabilities.contains(CAPABILITIES_SAE)) {
            return SAE_STRING;
        } else if (capabilities.contains(CAPABILITIES_EAP_B)) {
            return EAP_B_STRING;
        } else if (capabilities.contains(CAPABILITIES_EAP)) {
            return EAP_STRING;
        } else if (capabilities.contains(CAPABILITIES_PKS)) {
            return PSK_STRING;
        } else if (capabilities.contains(CAPABILITIES_WEP)) {
            return WEP_STRING;
        } else if (capabilities.contains(CAPABILITIES_WAPI) || capabilities.contains(CAPABILITIES_OWE)) { //其他类型判断
            return SAE_STRING;
        }
        return NONE_STRING; //最后返回无密码情况
    }

    //wifi 信号数值转换 -88，-77，-66，-55 --> 0-4 ，5格信号,参数为参数为：ScanResult.rssi
    // frameworks\opt\net\wifi\service\java\com\android\server\wifi util\RssiUtil.java
    //RssiUtil.calculateSignalLevel(mContext, rssi);
    public static int calculateSignalLevel(int rssi) {
        int[] thresholds = {-88, -77, -66, -55};
        for (int level = 0; level < thresholds.length; level++) {
            if (rssi < thresholds[level]) {
                return level;
            }
        }
        return thresholds.length;
    }


    public static final int BAND_5_GHZ_START_FREQ_MHZ = 5160;
    public static final int BAND_5_GHZ_END_FREQ_MHZ = 5865;

    //是否是5G信号，参数为 ScanResult.frequency
    public static boolean is5GHz(int freqMhz) {
        return freqMhz >= BAND_5_GHZ_START_FREQ_MHZ && freqMhz <= BAND_5_GHZ_END_FREQ_MHZ;
    }
}
