package com.y3tu.tools.kit.io;

import com.y3tu.tools.kit.collection.ArrayUtil;
import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.lang.Assert;
import com.y3tu.tools.kit.text.StrUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类
 *
 * @author y3tu
 */
@Slf4j
public class FileUtil extends PathUtil {
    /**
     * 系统临时目录
     * <br>
     * windows 包含路径分割符，但Linux 不包含,
     * 在windows \\==\ 前提下，
     * 为安全起见 同意拼装 路径分割符，
     * <pre>
     *       java.io.tmpdir
     *       windows : C:\Users/xxx\AppData\Local\Temp\
     *       linux: /temp
     * </pre>
     */
    public static final String SYS_TEM_DIR = System.getProperty("java.io.tmpdir");

    /**
     * 列出目录文件
     *
     * @param path 目录的绝对路径或者相对路径
     * @return 文件列表（包含目录）
     */
    public static File[] ls(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (file.isDirectory()) {
            return file.listFiles();
        }
        throw new ToolException(String.format("Path %s is not a directory", path));
    }

    /**
     * 文件是否为空
     * 目录：目录里面没有文件
     * 文件：文件大小为0
     *
     * @param file 文件
     * @return 是否为空，当提供非目录时，返回false
     */
    public static boolean isEmpty(File file) {
        if (file == null || !file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            //目录
            String[] subFiles = file.list();
            return ArrayUtil.isEmpty(subFiles);
        } else if (file.isFile()) {
            //文件
            return file.length() <= 0;
        }
        return false;
    }

    /**
     * 文件是否不为空
     *
     * @param file 文件
     * @return 文件是否不为空
     */
    public static boolean isNotEmpty(File file) {
        return !isEmpty(file);
    }


    /**
     * 判断路径是否是绝对路径
     *
     * @param path 路径
     * @return 是否绝对路径
     */
    public static boolean isAbsolutePath(String path) {
        if (StrUtil.isEmpty(path)) {
            return false;
        }
        // 给定的路径已经是绝对路径了
        return StrUtil.C_SLASH == path.charAt(0) || path.matches("^[a-zA-Z]:([/\\\\].*)?");
    }

    /**
     * 获取标准的绝对路径
     *
     * @param file 文件
     * @return 绝对路径
     */
    public static String getAbsolutePath(File file) {
        if (file == null) {
            return null;
        }

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * 创建文件夹
     *
     * @param dirPath 文件夹路径
     * @return 创建的目录
     */
    public static File mkdir(String dirPath) {
        if (dirPath == null) {
            return null;
        }
        final File dir = new File(dirPath);
        return mkdir(dir);
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br>
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        if (null == file) {
            return null;
        }
        return mkdir(file.getParentFile());
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     */
    public static File touch(File file) {
        if (null == file) {
            return null;
        }
        if (!file.exists()) {
            mkParentDirs(file);
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new ToolException(e);
            }
        }
        return file;
    }

    /**
     * 将流的内容写入文件
     *
     * @param destFile  目标文件
     * @param in        输入流
     * @param isCloseIn 是否关闭输入流
     * @return destFile
     */
    public static File writeFromStream(File destFile, InputStream in, boolean isCloseIn) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(touch(destFile));
            IoUtil.copy(in, out, null);

        } catch (Exception e) {
            throw new ToolException(e);
        } finally {
            IoUtil.close(out);
            if (isCloseIn) {
                IoUtil.close(in);
            }
        }
        return destFile;
    }

    /**
     * 通过JDK7+的 {@link Files#copy(Path, Path, CopyOption...)} 方法拷贝文件<br>
     * 此方法不支持递归拷贝目录，如果src传入是目录，只会在目标目录中创建空目录
     *
     * @param src     源文件路径，如果为目录只在目标中创建新目录
     * @param target  目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return Path
     */
    public static Path copyFile(Path src, Path target, CopyOption... options) {
        Assert.notNull(src, "Source File is null !");
        Assert.notNull(target, "Destination File or directiory is null !");

        final Path targetPath = isDirectory(target, false) ? target.resolve(src.getFileName()) : target;
        // 创建级联父目录
        mkParentDirs(targetPath.toFile());
        try {
            return Files.copy(src, targetPath, options);
        } catch (IOException e) {
            throw new ToolException(e);
        }
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     *
     * @param path          {@link Path}
     * @param isFollowLinks 是否追踪到软链对应的真实地址
     * @return 如果为目录true
     */
    public static boolean isDirectory(Path path, boolean isFollowLinks) {
        if (null == path) {
            return false;
        }
        final LinkOption[] options = isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
        return Files.isDirectory(path, options);
    }

    /**
     * 下载文件
     *
     * @param request  /
     * @param response /
     * @param file     /
     */
    public static void downloadFile(File file, String fileName, boolean deleteOnExit, HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding(request.getCharacterEncoding());
        response.setContentType("application/octet-stream");
        InputStream fis = null;

        if (StrUtil.isEmpty(fileName)) {
            fileName = file.getName();
        }
        try {
            fis = new FileInputStream(file);
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            IoUtil.copy(fis, response.getOutputStream(), null);
            response.flushBuffer();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    if (deleteOnExit) {
                        if (response.isCommitted()) {
                            file.deleteOnExit();
                        }
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void downloadFileBatch(Map<String, File> fileListMap, String zipName, boolean deleteOnExit, HttpServletRequest request, HttpServletResponse response) {
        //响应头的设置
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        String downloadName = zipName + ".zip";
        //返回客户端浏览器的版本号、类型
        String agent = request.getHeader("USER-AGENT");
        try {
            //针对IE或者以IE为内核的浏览器：
            if (agent.contains("MSIE") || agent.contains("Trident")) {
                downloadName = java.net.URLEncoder.encode(downloadName, "UTF-8");
            } else {
                //非IE浏览器的处理：
                downloadName = new String(downloadName.getBytes("UTF-8"), "ISO-8859-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setHeader("Content-Disposition", "attachment;fileName=\"" + downloadName + "\"");

        //设置压缩流：直接写入response，实现边压缩边下载
        ZipOutputStream zipStream = null;
        //循环将文件写入压缩流
        FileInputStream zipSource = null;
        BufferedInputStream bufferStream = null;
        try {
            zipStream = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            //设置压缩方法
            zipStream.setMethod(ZipOutputStream.DEFLATED);
            for (String fileName : fileListMap.keySet()) {

                File file = fileListMap.get(fileName);
                if (file.exists()) {
                    //将需要压缩的文件格式化为输入流
                    zipSource = new FileInputStream(file);
                    //在压缩目录中文件的名字
                    ZipEntry zipEntry = new ZipEntry(fileName);
                    //定位该压缩条目位置，开始写入文件到压缩包中
                    zipStream.putNextEntry(zipEntry);
                    bufferStream = new BufferedInputStream(zipSource, 1024 * 10);
                    int read = 0;
                    byte[] buf = new byte[1024 * 10];
                    while ((read = bufferStream.read(buf, 0, 1024 * 10)) != -1) {
                        zipStream.write(buf, 0, read);
                    }
                    zipStream.closeEntry();
                }

            }

            response.flushBuffer();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (zipStream != null) {
                    zipStream.close();
                }

                if (zipSource != null) {
                    zipSource.close();
                }

                if (bufferStream != null) {
                    bufferStream.close();
                }
                if (deleteOnExit) {
                    if (response.isCommitted()) {
                        for (String fileName : fileListMap.keySet()) {
                            File file = fileListMap.get(fileName);
                            if (deleteOnExit) {
                                file.deleteOnExit();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 获取文件扩展名，不带 .
     *
     * @param fileName 文件名
     * @return 文件扩展名，不带 .
     */
    public static String getExtensionName(String fileName) {
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length() - 1))) {
                return fileName.substring(dot + 1);
            }
        }
        return fileName;
    }

    /**
     * 判断文件是否存在，如果path为null，则返回false
     *
     * @param path 文件路径
     * @return 如果存在返回true
     */
    public static boolean exist(String path) {
        return exist(new File(path));
    }

    /**
     * 判断文件是否存在，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果存在返回true
     */
    public static boolean exist(File file) {
        return (null != file) && file.exists();
    }

    /**
     * 删除文件或者文件夹<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param file 文件对象
     * @return 成功与否
     * @see Files#delete(Path)
     */
    public static boolean del(File file) {
        if (file == null || !file.exists()) {
            // 如果文件不存在或已被删除，此处返回true表示删除成功
            return true;
        }

        if (file.isDirectory()) {
            // 清空目录下所有文件和目录
            boolean isOk = clean(file);
            if (false == isOk) {
                return false;
            }
        }

        // 删除文件或清空后的目录
        final Path path = file.toPath();
        try {
            delFile(path);
        } catch (Exception e) {
            // 遍历清空目录没有成功，此时补充删除一次（可能存在部分软链）
            try {
                del(path);
            } catch (Exception ee) {
                throw new ToolException("文件删除异常！", e.getMessage());
            }
        }

        return true;
    }

    /**
     * 清空文件夹<br>
     * 注意：清空文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param directory 文件夹
     * @return 成功与否
     */
    public static boolean clean(File directory) {
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return true;
        }

        final File[] files = directory.listFiles();
        if (null != files) {
            for (File childFile : files) {
                if (!del(childFile)) {
                    // 删除一个出错则本次删除任务失败
                    return false;
                }
            }
        }
        return true;
    }

}
