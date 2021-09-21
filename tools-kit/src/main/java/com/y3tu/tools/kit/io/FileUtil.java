package com.y3tu.tools.kit.io;

import com.y3tu.tools.kit.collection.ArrayUtil;
import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.text.StrUtil;

import java.io.*;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * 文件工具类
 *
 * @author y3tu
 */
public class FileUtil {

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
     * 递归遍历目录以及子目录中的所有文件
     * 如果提供path为文件，直接返回过滤结果
     *
     * @param path       当前遍历文件或目录
     * @param maxDepth   遍历最大深度，-1表示遍历到没有目录为止
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录，null表示接收全部文件
     * @return 文件列表
     */
    public static List<File> loopFiles(Path path, int maxDepth, FileFilter fileFilter) {
        final List<File> fileList = new ArrayList<>();
        if (path == null || !Files.exists(path)) {
            return fileList;
        } else if (!Files.isDirectory(path)) {
            //文件
            final File file = path.toFile();
            if (fileFilter == null || fileFilter.accept(file)) {
                fileList.add(file);
            }
            return fileList;
        }

        walkFiles(path, maxDepth, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                final File file = path.toFile();
                if (null == fileFilter || fileFilter.accept(file)) {
                    fileList.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return fileList;
    }

    /**
     * 遍历指定path下的文件并处理
     *
     * @param path     起始路径，必须是目录
     * @param maxDepth 最大遍历深度，-1表示不限深度
     * @param visitor  用于自定义在访问文件时，访问目录前后等节点做的操作
     */
    public static void walkFiles(Path path, int maxDepth, FileVisitor<? super Path> visitor) {
        if (maxDepth < 0) {
            // < 0 表示遍历到最底层
            maxDepth = Integer.MAX_VALUE;
        }

        try {
            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), maxDepth, visitor);
        } catch (Exception e) {
            throw new ToolException(e);
        }
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
}
