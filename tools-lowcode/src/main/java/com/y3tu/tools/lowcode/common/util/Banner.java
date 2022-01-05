package com.y3tu.tools.lowcode.common.util;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * 控制台横幅工具
 * 生成banner地址：http://patorjk.com/software/taag
 *
 * @author y3tu
 */
public class Banner {

    private static final String name = "Tools";
    private static final String defaultBanner = "  ______            __    \n" +
            " /_  __/___  ____  / /____\n" +
            "  / / / __ \\/ __ \\/ / ___/\n" +
            " / / / /_/ / /_/ / (__  ) \n" +
            "/_/  \\____/\\____/_/____/  \n" +
            "                          ";

    public static void print() {
        printVersion();
    }

    /**
     * 打印版本号
     */
    private static void printVersion() {
        PrintStream printStream = System.out;
        String version = Banner.getVersion();
        version = version != null ? " (v" + version + ")" : "";
        StringBuilder padding = new StringBuilder();

        while (padding.length() < 42 - (version.length() + name.length())) {
            padding.append(" ");
        }

        printStream.println(AnsiOutput.toString(new Object[]{AnsiColor.BRIGHT_RED, defaultBanner, AnsiColor.DEFAULT, padding.toString(), AnsiStyle.FAINT}));
        printStream.println();
        printStream.println(AnsiOutput.toString(new Object[]{AnsiColor.GREEN, " :: Tools-Low-Code :: ", AnsiColor.DEFAULT, padding.toString(), AnsiStyle.FAINT, version}));
        printStream.println();
    }

    /**
     * 获取组件版本号
     *
     * @return String
     */
    private static String getVersion() {
        String implementationVersion = Banner.class.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        } else {
            CodeSource codeSource = Banner.class.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                return null;
            } else {
                URL codeSourceLocation = codeSource.getLocation();

                try {
                    URLConnection connection = codeSourceLocation.openConnection();
                    if (connection instanceof JarURLConnection) {
                        return getImplementationVersion(((JarURLConnection) connection).getJarFile());
                    } else {
                        JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()));
                        Throwable var5 = null;

                        String var6;
                        try {
                            var6 = getImplementationVersion(jarFile);
                        } catch (Throwable var16) {
                            throw var16;
                        } finally {
                            if (jarFile != null) {
                                if (var5 != null) {
                                    try {
                                        jarFile.close();
                                    } catch (Throwable var15) {
                                        var5.addSuppressed(var15);
                                    }
                                } else {
                                    jarFile.close();
                                }
                            }

                        }

                        return var6;
                    }
                } catch (Exception var18) {
                    return null;
                }
            }
        }
    }

    /**
     * 获取包文件对应的版本用于banner的打印
     *
     * @param jarFile 文件
     * @return String 版本号
     */
    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }
}
