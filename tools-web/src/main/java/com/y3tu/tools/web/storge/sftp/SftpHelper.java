package com.y3tu.tools.web.storge.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.y3tu.tools.kit.io.FileUtil;
import com.y3tu.tools.kit.pool.intf.BlockingPool;
import com.y3tu.tools.kit.system.SystemUtil;
import com.y3tu.tools.web.storge.RemoteFileHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Sftp服务
 *
 * @author y3tu
 */
@Slf4j
public class SftpHelper implements RemoteFileHelper {

    private BlockingPool<ChannelSftp> sftpPool;

    public SftpHelper(BlockingPool<ChannelSftp> sftpPool) {
        this.sftpPool = sftpPool;
    }

    @Override
    public boolean upload(String remotePath, String fileName, InputStream input) {
        ChannelSftp channelSftp = sftpPool.get();
        try {
            mkDirs(remotePath);
            channelSftp.cd(remotePath);
            channelSftp.put(input, remotePath + File.separator + fileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
        }
        return true;
    }

    /**
     * 上传文件
     *
     * @param remotePath 远程服务器上文件路径
     * @param fileName   文件名
     * @param file       浏览器上传的文件
     * @return 成功返回true，否则返回false
     */
    @Override
    public boolean upload(String remotePath, String fileName, MultipartFile file) {
        InputStream inputStream = null;
        ChannelSftp channelSftp = sftpPool.get();
        boolean result = false;
        try {
            inputStream = file.getInputStream();
            mkDirs(remotePath);
            channelSftp.put(inputStream, remotePath + File.separator + fileName, new SftpProgressMonitor());
            result = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * 上传文件
     *
     * @param remotePath 远程服务器上文件路径
     * @param fileName   保存文件名
     * @param srcFile    本地文件路径全路径
     * @return 成功返回true，否则返回false
     */
    @Override
    public boolean upload(String remotePath, final String fileName, String srcFile) {
        ChannelSftp channelSftp = sftpPool.get();
        try {
            mkDirs(remotePath);
            channelSftp.cd(remotePath);
            channelSftp.put(srcFile, remotePath + File.separator + fileName);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
        }
        return true;
    }


    /**
     * 下载文件
     *
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     * @return 成功返回true，否则返回false
     */
    @Override
    public boolean download(String downloadFile, String saveFile) {
        boolean result = false;
        ChannelSftp channelSftp = sftpPool.get();
        FileOutputStream os = null;
        File file = new File(saveFile);
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            os = new FileOutputStream(file);
            List<String> list = formatPath(downloadFile);
            channelSftp.get(list.get(0) + list.get(1), os);
            result = true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
            try {
                os.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }


    /**
     * 浏览器从远程服务器上下载文件
     *
     * @param remotePath   远程服务器上文件路径
     * @param fileName     文件名
     * @param deleteOnExit 退出时是否删除文件
     * @param request      请求
     * @param response     响应
     * @return
     */
    @Override
    public File download(String remotePath, String fileName, boolean deleteOnExit, HttpServletRequest request, HttpServletResponse response) {
        File destFile = null;
        ChannelSftp channelSftp = sftpPool.get();
        try {
            destFile = new File(SystemUtil.TMPDIR + File.separator + fileName);
            // 检测是否存在目录
            if (!destFile.getParentFile().exists()) {
                if (!destFile.getParentFile().mkdirs()) {
                    log.warn(" 创建文件夹失败：" + destFile.getParentFile().getAbsolutePath());
                }
            }

            if (!destFile.exists()) {
                channelSftp.get(remotePath, FileUtil.getAbsolutePath(destFile), new SftpProgressMonitor());
            }
            FileUtil.downloadFile(destFile, fileName, true, request, response);
            if (deleteOnExit) {
                channelSftp.rm(remotePath);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
        }
        return destFile;
    }

    /**
     * 浏览器从远程服务器上批量下载文件并压缩
     *
     * @param fileListSftpMap 远程文件名和路径
     * @param zipName         zip压缩包名
     * @param deleteOnExit    退出时是否删除文件
     * @param request         请求
     * @param response        响应
     */
    public void downloadBatch(Map<String, String> fileListSftpMap, String zipName, boolean deleteOnExit, HttpServletRequest request, HttpServletResponse response) {
        ChannelSftp channelSftp = sftpPool.get();
        Map<String, File> fileListMap = new HashMap<>();
        try {
            for (String fileName : fileListSftpMap.keySet()) {
                File destFile = new File(SystemUtil.TMPDIR);
                // 检测是否存在目录
                if (!destFile.getParentFile().exists()) {
                    if (!destFile.getParentFile().mkdirs()) {
                        log.warn(" 创建文件夹失败：" + destFile.getParentFile().getAbsolutePath());
                    }
                }
                if (!destFile.exists()) {
                    channelSftp.get(fileListSftpMap.get(fileName), FileUtil.getAbsolutePath(destFile), new SftpProgressMonitor());
                }
                fileListMap.put(fileName, destFile);
            }

            FileUtil.downloadFileBatch(fileListMap, zipName, true, request, response);

            if (deleteOnExit) {
                for (String fileName : fileListSftpMap.keySet()) {
                    channelSftp.rm(fileListSftpMap.get(fileName));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
        }
    }

    /**
     * 浏览器从远程服务器上批量下载文件并压缩
     *
     * @param fileListSftpMap  远程文件名和路径
     * @param fileListLocalMap 本地文件名和路径
     * @param zipName          zip压缩包名
     * @param deleteOnExit     退出时是否删除文件
     * @param request          请求
     * @param response         响应
     */
    public void downloadUnionBatch(Map<String, String> fileListSftpMap, Map<String, File> fileListLocalMap, String zipName, boolean deleteOnExit, HttpServletRequest request, HttpServletResponse response) {
        ChannelSftp channelSftp = sftpPool.get();
        Map<String, File> fileListUnionMap = new HashMap<>();
        try {

            for (String fileName : fileListSftpMap.keySet()) {
                File destFile = new File(SystemUtil.TMPDIR);
                // 检测是否存在目录
                if (!destFile.getParentFile().exists()) {
                    if (!destFile.getParentFile().mkdirs()) {
                        log.warn(" 创建文件夹失败：" + destFile.getParentFile().getAbsolutePath());
                    }
                }
                if (!destFile.exists()) {
                    channelSftp.get(fileListSftpMap.get(fileName), FileUtil.getAbsolutePath(destFile), new SftpProgressMonitor());
                }
                fileListUnionMap.put(fileName, destFile);
            }

            if (fileListLocalMap != null) {
                for (String fileName : fileListLocalMap.keySet()) {
                    File destFile = new File(SystemUtil.TMPDIR);
                    // 检测是否存在目录
                    if (!destFile.getParentFile().exists()) {
                        if (!destFile.getParentFile().mkdirs()) {
                            log.warn(" 创建文件夹失败：" + destFile.getParentFile().getAbsolutePath());
                        }
                    }
                    FileUtil.copyFile(fileListLocalMap.get(fileName).toPath(), destFile.toPath(), null);
                    fileListUnionMap.put(fileName, destFile);
                }
            }

            FileUtil.downloadFileBatch(fileListUnionMap, zipName, true, request, response);

            if (deleteOnExit) {
                for (String fileName : fileListSftpMap.keySet()) {
                    channelSftp.rm(fileListSftpMap.get(fileName));
                }

                for (String fileName : fileListLocalMap.keySet()) {
                    fileListLocalMap.get(fileName).delete();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
        }
    }

    /**
     * 删除服务器上文件
     *
     * @param path 远程服务器上文件路径
     * @return
     */
    @Override
    public boolean remove(String path) {
        ChannelSftp channelSftp = sftpPool.get();
        try {
            channelSftp.rm(path);
            return true;
        } catch (SftpException e) {
            if (e.getMessage().indexOf("No such file") >= 0) {
                //如果文件不存在 直接返回true
                return true;
            }
            return false;
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
        }
    }


    /**
     * 根据路径创建文件夹.
     *
     * @param dir 路径 必须是 /xxx/xxx/ 不能就单独一个/
     */
    @Override
    public boolean mkDir(String dir) {
        if (dir == null && dir.length() == 0) {
            return false;
        }
        String md = dir.replaceAll("\\\\", "/");
        if (md.indexOf("/") != 0 || md.length() == 1) {
            return false;
        }
        return mkDirs(md);
    }

    /**
     * 递归创建文件夹
     *
     * @param dir 路径
     * @return 是否创建成功
     */
    @Override
    public boolean mkDirs(String dir) {
        ChannelSftp channelSftp = sftpPool.get();
        try {
            String dirs = dir.substring(1, dir.length() - 1);
            String[] dirArr = dirs.split("/");
            String base = "";
            for (String d : dirArr) {
                base += "/" + d;
                if (dirExist(base + "/")) {
                    continue;
                } else {
                    channelSftp.mkdir(base + "/");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
        }
        return true;
    }

    /**
     * 判断文件夹是否存在.
     *
     * @param dir 文件夹路径， /xxx/xxx/
     * @return 是否存在
     */
    private boolean dirExist(String dir) {
        ChannelSftp channelSftp = sftpPool.get();
        try {
            Vector<?> vector = channelSftp.ls(dir);
            if (null == vector) {
                return false;
            } else {
                return true;
            }
        } catch (SftpException e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            if (channelSftp != null) {
                sftpPool.release(channelSftp);
            }
        }
    }


    /**
     * 格式化路径.
     *
     * @param srcPath 原路径. /xxx/xxx/xxx.yyy 或 X:/xxx/xxx/xxx.yy
     * @return list, 第一个是路径（/xxx/xxx/）,第二个是文件名（xxx.yy）
     */
    public List<String> formatPath(final String srcPath) {
        List<String> list = new ArrayList<String>(2);
        String repSrc = srcPath.replaceAll("\\\\", "/");
        int firstP = repSrc.indexOf("/");
        int lastP = repSrc.lastIndexOf("/");
        String fileName = lastP + 1 == repSrc.length() ? "" : repSrc.substring(lastP + 1);
        String dir = firstP == -1 ? "" : repSrc.substring(firstP, lastP);
        dir = (dir.length() == 1 ? dir : (dir + "/"));
        list.add(dir);
        list.add(fileName);
        return list;
    }

}
