package com.y3tu.tools.kit.io;

import com.y3tu.tools.kit.exception.ToolException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Zip工具类
 *
 * @author y3tu
 */
@Slf4j
public class ZipUtil {

    /**
     * 缓存大小
     */
    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     * 压缩文件
     *
     * @param sourceFilePath   源文件路径
     * @param targetFilePath   目标文件路径
     * @param keepDirStructure
     */
    public static void toZip(String sourceFilePath, String targetFilePath, boolean keepDirStructure) {
        try {
            File file = new File(targetFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            toZip(sourceFilePath, fileOutputStream, keepDirStructure);
        } catch (Exception e) {
            throw new ToolException("文件压缩失败！" + e.getMessage());
        }
    }

    /**
     * 文件压缩
     *
     * @param srcDir           压缩文件路径
     * @param out              压缩文件输出流
     * @param keepDirStructure 是否保留原来的目录结构
     */
    public static void toZip(String srcDir, OutputStream out, boolean keepDirStructure) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), keepDirStructure);
        } catch (Exception e) {
            throw new ToolException("文件压缩失败！" + e.getMessage());
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    /**
     * 多文件压缩
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     */
    public static void toZip(List<File> srcFiles, OutputStream out) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new ToolException("文件压缩失败！" + e.getMessage());
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    /**
     * 文件解压
     *
     * @param srcFile     源文件
     * @param destDirPath 文件解压路基
     * @param charset     编码格式
     */
    public static void unZip(File srcFile, String destDirPath, Charset charset) {
        //判断源文件是否存在
        if (!srcFile.exists()) {
            throw new ToolException(srcFile.getPath() + ":指定文件不存在！");
        }
        //开始解压
        ZipFile zipFile = null;

        try {
            File file;
            //指定编码格式
            zipFile = new ZipFile(srcFile, charset);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                log.info("开始解压：" + entry.getName());
                if (entry.isDirectory()) {
                    //如果是文件夹就创建文件夹
                    String dirPath = destDirPath + File.separator + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    //如果是文件，就先创建一个文件，然后用IO流把内存copy过去
                    File targetFile = new File(destDirPath + File.separator + entry.getName());
                    //保证这个文件的父文件夹必须存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    //将压缩文件的内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[BUFFER_SIZE];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    //先闭流，先打开的后关闭
                    fos.close();
                    is.close();

                }
            }
        } catch (Exception e) {
            throw new ToolException("文件解压失败！" + e.getMessage());
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception e) {
                    //ignore
                }
            }
        }
    }

    /**
     * 递归压缩
     *
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的文件名
     * @param keepDirStructure 是否保留原来的目录结构
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            //文件
            //向zip输出流中添加一个zip实体，构造器中的name为zip实体文件的名称
            zos.putNextEntry(new ZipEntry(name));
            //copy文件到zip输出流中
            int len;
            File file;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
            in.close();
        } else {
            //文件夹
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                if (keepDirStructure) {
                    //需要保留原来的文件结构
                    //空文件夹处理
                    zos.putNextEntry(new ZipEntry(name + File.separator));
                    //没有文件，不需要copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    if (keepDirStructure) {
                        compress(file, zos, name + File.separator + file.getName(), true);
                    } else {
                        compress(file, zos, file.getName(), false);
                    }
                }
            }
        }
    }

}
