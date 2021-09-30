package com.y3tu.tools.kit.io;

import com.y3tu.tools.kit.exception.ToolException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * NIO中Path对象操作封装
 *
 * @author looly
 */
public class PathUtil {

    /**
     * 目录是否为空
     *
     * @param dirPath 目录
     * @return 是否为空
     */
    public static boolean isDirEmpty(Path dirPath) {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dirPath)) {
            return !dirStream.iterator().hasNext();
        } catch (IOException e) {
            throw new ToolException(e);
        }
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
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
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
     * 删除文件或者文件夹，不追踪软链<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param path 文件对象
     * @return 成功与否
     */
    public static boolean del(Path path) {
        if (Files.notExists(path)) {
            return true;
        }

        try {
            if (isDirectory(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    /**
                     * 访问目录结束后删除目录，当执行此方法时，子文件或目录都已访问（删除）完毕<br>
                     * 理论上当执行到此方法时，目录下已经被清空了
                     *
                     * @param dir 目录
                     * @param e   异常
                     * @return {@link FileVisitResult}
                     * @throws IOException IO异常
                     */
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                        if (e == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw e;
                        }
                    }

                });
            } else {
                delFile(path);
            }
        } catch (IOException e) {
            throw new ToolException(e);
        }
        return true;
    }

    /**
     * 判断是否为目录，如果file为null，则返回false<br>
     * 此方法不会追踪到软链对应的真实地址，即软链被当作文件
     *
     * @param path {@link Path}
     * @return 如果为目录true
     */
    public static boolean isDirectory(Path path) {
        return isDirectory(path, false);
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
     * 删除文件或空目录，不追踪软链
     *
     * @param path 文件对象
     */
    protected static void delFile(Path path) {
        try {
            Files.delete(path);
        } catch (Exception e) {
            // 可能遇到只读文件，无法删除.使用 file 方法删除
            if (!path.toFile().delete()) {
                throw new ToolException(e);
            }
        }
    }

}
