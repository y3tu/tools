package com.y3tu.tools.kit.io;

import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.lang.Assert;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * IO流工具类
 *
 * @author y3tu
 */
public class IoUtil {
    /**
     * 默认缓存大小 8192
     */
    public static final int DEFAULT_BUFFER_SIZE = 2 << 12;
    /**
     * 默认中等缓存大小 16384
     */
    public static final int DEFAULT_MIDDLE_BUFFER_SIZE = 2 << 13;
    /**
     * 默认大缓存大小 32768
     */
    public static final int DEFAULT_LARGE_BUFFER_SIZE = 2 << 14;

    /**
     * 关闭
     * 关闭失败不会抛出异常
     *
     * @param closeable 被关闭的对象
     */
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                //静默关闭
            }
        }
    }

    /**
     * 拷贝文件流 使用NIO
     *
     * @param in  输入
     * @param out 输出
     * @return 拷贝的字节数
     */
    public static long copy(FileInputStream in, FileOutputStream out) {
        Assert.notNull(in, "FileInputStream is null!");
        Assert.notNull(out, "FileOutputStream is null!");
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = in.getChannel();
            outChannel = out.getChannel();
            return inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            throw new ToolException("拷贝文件流异常", e);
        } finally {
            close(outChannel);
            close(inChannel);
        }
    }

    /**
     * 拷问流，拷贝后不关闭
     *
     * @param in       输入流
     * @param out      输出流
     * @param progress 进度条
     * @return 传输的byte数
     */
    public static long copy(InputStream in, OutputStream out, StreamProgress progress) {
        Assert.notNull(in, "InputStream is null!");
        Assert.notNull(out, "OutputStream is null!");
        if (null != progress) {
            progress.start();
        }
        long total = 0;
        try {
            int numToRead = DEFAULT_BUFFER_SIZE;
            int read;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while (numToRead > 0) {
                read = in.read(buffer, 0, numToRead);
                if (read < 0) {
                    // 提前读取到末尾
                    break;
                }
                out.write(buffer, 0, read);

                numToRead -= read;
                total += read;
                if (null != progress) {
                    progress.progress(total);
                }
            }
            out.flush();

        } catch (Exception e) {
            throw new ToolException("拷贝文件流异常", e);
        }
        if (null != progress) {
            progress.finish();
        }
        return total;
    }

    /**
     * 判断文件夹是否存在
     *
     * @param path 文件夹路径
     * @return 文件夹是否存在
     */
    public static boolean isDirExists(Path path) {
        return path != null && Files.exists(path) && Files.isDirectory(path);
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return 文件是否存在
     */
    public static boolean isFileExists(Path path) {
        if (path == null) {
            return false;
        }
        return Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * 在临时目录创建临时文件，命名为${prefix}${random.nextLong()}${suffix}
     *
     * @param prefix 文件名前缀
     * @param suffix 文件名后缀
     * @return 文件路径
     * @see Files#createTempFile
     */
    public static Path createTempFile(String prefix, String suffix) throws IOException {
        return Files.createTempFile(prefix, suffix);
    }


}
