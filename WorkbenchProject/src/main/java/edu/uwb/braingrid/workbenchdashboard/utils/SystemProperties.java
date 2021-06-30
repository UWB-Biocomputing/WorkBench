package edu.uwb.braingrid.workbenchdashboard.utils;

import java.util.logging.Logger;

public final class SystemProperties {

    enum OSType {
        Windows,
        Linux,
        Mac,
        SunOS,
        FreeBSD,
        UNKNOWN
    }

    enum OS {
        Windows_XP,
        Windows_2003,
        Linux,
        Windows_2000,
        Mac_OS_X,
        Windows_98,
        SunOS,
        FreeBSD,
        Windows_NT,
        Windows_Me,
        Windows_10,
        UNKNOWN
    }

    private static final Logger LOG = Logger.getLogger(SystemProperties.class.getName());

    private static OS os = OS.UNKNOWN;
    private static OSType osType = OSType.UNKNOWN;
    private static OS osUserDef =  OS.UNKNOWN;
    private static OSType osTypeUserDef = OSType.UNKNOWN;
    private static SystemProperties sysProperties = null;

    private SystemProperties() {
        LOG.info("new SystemProperties");
//        System.getProperties().list(System.out);
        initOSTypeByString(System.getProperty("os.name"));
    }

    public static SystemProperties getInstance() {
        if (sysProperties == null) {
            sysProperties = new SystemProperties();
        }
        return sysProperties;
    }

    private static void initOSTypeByString(String sysOS) {
        switch (sysOS) {
            case "Windows 10":
                informOSType("Windows 10", OS.Windows_10, OSType.Windows);
                break;
            case "Windows XP":
                informOSType("Windows XP", OS.Windows_XP, OSType.Windows);
                break;
            case "Windows 2003":
                informOSType("Windows 2003", OS.Windows_2003, OSType.Windows);
                break;
            case "Windows NT":
                informOSType("Windows NT", OS.Windows_NT, OSType.Windows);
                break;
            case "Windows Me":
                informOSType("Windows NT", OS.Windows_Me, OSType.Windows);
                break;
            case "Linux":
                informOSType("Linux", OS.Linux, OSType.Linux);
                break;
            case "Mac OS X":
                informOSType("Mac OS X", OS.Mac_OS_X, OSType.Mac);
                break;
            case "SunOS":
                informOSType("SunOS", OS.SunOS, OSType.SunOS);
                break;
            case "FreeBSD":
                informOSType("FreeBSD", OS.FreeBSD, OSType.FreeBSD);
                break;
            default:
                informOSType("UNKNOWN", OS.UNKNOWN, OSType.UNKNOWN);
        }
    }

    private static void informOSType(String name, OS sysOS, OSType sysOSType) {
        LOG.info("OS found - " + name);
        SystemProperties.os = sysOS;
        SystemProperties.osType = sysOSType;
    }

    public static OS getOS() {
        if (os == OS.UNKNOWN) {
            return osUserDef;
        }
        return os;
    }

    public static OSType getOSType() {
        if (osType == OSType.UNKNOWN) {
            return osTypeUserDef;
        }
        return osType;
    }

    public static void setOS(OS sysOS) {
        osUserDef = sysOS;
    }

    public static void setOSType(OSType sysOSType) {
        osTypeUserDef = sysOSType;
    }
}
