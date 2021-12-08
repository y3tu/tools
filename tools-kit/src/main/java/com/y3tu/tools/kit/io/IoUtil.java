package com.y3tu.tools.kit.io;

import com.y3tu.tools.kit.exception.ToolException;
import com.y3tu.tools.kit.lang.Assert;
import com.y3tu.tools.kit.text.StrUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
    public static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                //静默关闭
            }
        }
    }

    /**
     * 从缓存中刷出数据
     *
     * @param flushable {@link Flushable}
     */
    public static void flush(Flushable flushable) {
        if (null != flushable) {
            try {
                flushable.flush();
            } catch (Exception e) {
                // 静默刷出
            }
        }
    }

    /**
     * 获得一个Reader
     *
     * @param in      输入流
     * @param charset 字符集
     * @return BufferedReader对象
     */
    public static BufferedReader getReader(InputStream in, Charset charset) {
        if (null == in) {
            return null;
        }

        InputStreamReader reader;
        if (null == charset) {
            reader = new InputStreamReader(in);
        } else {
            reader = new InputStreamReader(in, charset);
        }

        return new BufferedReader(reader);
    }

    /**
     * 获得一个Writer
     *
     * @param out     输入流
     * @param charset 字符集
     * @return OutputStreamWriter对象
     */
    public static OutputStreamWriter getWriter(OutputStream out, Charset charset) {
        if (null == out) {
            return null;
        }

        if (null == charset) {
            return new OutputStreamWriter(out);
        } else {
            return new OutputStreamWriter(out, charset);
        }
    }

    /**
     * 将多部分内容写到流中
     *
     * @param out        输出流
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容
     */
    public static void writeObjects(OutputStream out, boolean isCloseOut, Serializable... contents) {
        ObjectOutputStream osw = null;
        try {
            osw = out instanceof ObjectOutputStream ? (ObjectOutputStream) out : new ObjectOutputStream(out);
            for (Object content : contents) {
                if (content != null) {
                    osw.writeObject(content);
                }
            }
            osw.flush();
        } catch (IOException e) {
            throw new ToolException(e);
        } finally {
            if (isCloseOut) {
                close(osw);
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

    /**
     * 从流中读取bytes
     *
     * @param in      {@link InputStream}
     * @param isClose 是否关闭输入流
     * @return bytes
     */
    public static byte[] readBytes(InputStream in, boolean isClose) {
        if (in instanceof FileInputStream) {
            // 文件流的长度是可预见的，此时直接读取效率更高
            final byte[] result;
            try {
                final int available = in.available();
                result = new byte[available];
                final int readLength = in.read(result);
                if (readLength != available) {
                    throw new IOException(StrUtil.format("File length is [{}] but read [{}]!", available, readLength));
                }
            } catch (IOException e) {
                throw new ToolException(e);
            } finally {
                if (isClose) {
                    close(in);
                }
            }
            return result;
        }

        // 未知bytes总量的流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out, null);

        byte[] bytes = out.toByteArray();
        if (isClose) {
            close(in);
            close(out);
        }
        return bytes;
    }

    /**
     * 从FileChannel中读取UTF-8编码内容
     *
     * @param fileChannel 文件管道
     * @return 内容
     */
    public static String readUtf8(FileChannel fileChannel) {
        return read(fileChannel, StandardCharsets.UTF_8);
    }

    /**
     * 从流中读取UTF-8编码内容
     *
     * @param in      {@link InputStream}
     * @param isClose 是否关闭输入流
     * @return 内容
     */
    public static String readUtf8(InputStream in, boolean isClose) {
        byte[] bytes = readBytes(in, isClose);
        return StrUtil.str(bytes, StandardCharsets.UTF_8);
    }
}
