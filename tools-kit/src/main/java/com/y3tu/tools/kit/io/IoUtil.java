package com.y3tu.tools.kit.io;

import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.lang.Assert;
import com.y3tu.tools.kit.text.StrUtil;
import lombok.NonNull;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * IO流工具类
 * NIO相关工具封装，主要针对Channel读写、拷贝等封装
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
     * 数据流末尾
     */
    public static final int EOF = -1;

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
        return copy(Channels.newChannel(in), Channels.newChannel(out), -1, -1, progress);
    }

    /**
     * 拷贝流，使用NIO，不会关闭channel
     *
     * @param source     {@link ReadableByteChannel}
     * @param target     {@link WritableByteChannel}
     * @param bufferSize 缓冲大小，如果小于等于0，使用默认
     * @param count      读取总长度
     * @param progress   {@link StreamProgress}进度处理器
     * @return 拷贝的字节数
     */
    public static long copy(ReadableByteChannel source, WritableByteChannel target, int bufferSize, long count, StreamProgress progress) {
        Assert.notNull(source, "InputStream is null !");
        Assert.notNull(target, "OutputStream is null !");
        if (null != progress) {
            progress.start();
        }
        long total = 0;
        try {
            //如果限制最大长度，则按照最大长度读取，否则一直读取直到遇到-1
            long numToRead = count > 0 ? count : Long.MAX_VALUE;
            int read;
            //缓存大小，如果小于等于0，使用默认
            bufferSize = bufferSize > 0 ? bufferSize : DEFAULT_BUFFER_SIZE;
            //取缓存和目标长度最小值
            ByteBuffer buffer = ByteBuffer.allocate((int) Math.min(bufferSize, count));
            while (numToRead > 0) {
                read = source.read(buffer);
                if (read < 0) {
                    // 提前读取到末尾
                    break;
                }
                // 写转读
                buffer.flip();
                target.write(buffer);
                buffer.clear();

                numToRead -= read;
                total += read;
                if (null != progress) {
                    progress.progress(total);
                }
            }
        } catch (Exception e) {
            throw new ToolException(e, "拷贝流发生异常：{}", e.getMessage());
        }

        return total;
    }

    /**
     * 从FileChannel中读取内容
     *
     * @param fileChannel 文件管道
     * @param charset     字符集
     * @return 内容
     */
    public static String read(FileChannel fileChannel, Charset charset) {
        MappedByteBuffer buffer;
        try {
            buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size()).load();
        } catch (IOException e) {
            throw new ToolException(e);
        }
        return StrUtil.str(buffer, charset);
    }

    public static String read(InputStream in, Charset charset) {
        return read(, charset);
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
     * @throws IOException io异常
     * @see Files#createTempFile
     */
    public static Path createTempFile(String prefix, String suffix) throws IOException {
        return Files.createTempFile(prefix, suffix);
    }

    /**
     * 文件转为{@link FileInputStream}
     *
     * @param file 文件
     * @return {@link FileInputStream}
     */
    public static FileInputStream toStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (Exception e) {
            throw new ToolException(e);
        }
    }

}
