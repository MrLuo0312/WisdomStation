package com.winsion.dispatch.utils;

import android.os.Environment;
import android.support.annotation.IntDef;

import com.winsion.dispatch.media.constants.FileType;
import com.winsion.dispatch.utils.constants.DirName;
import com.winsion.dispatch.utils.constants.Formatter;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by 10295 on 2017/12/17 0017.
 * 获取目录/文件工具类
 */

public class DirAndFileUtils {
    private static StringBuilder stringBuilder = new StringBuilder();

    private static String getRootDir() throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DirName.ROOT;
        }
        throw new IOException("Please check SDCard state!");
    }

    /**
     * 获取版本更新包存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getUpdateDir() throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.UPDATE)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new NullPointerException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取发布命令/协作附件存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getLogDir() throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.LOG)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new NullPointerException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取任务执行人附件存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getPerformerDir(String userId, String jobOperatorId) throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.RECORD)
                .append(File.separator)
                .append(userId)
                .append(File.separator)
                .append(jobOperatorId)
                .append(File.separator)
                .append(DirName.PERFORMER)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new IOException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取任务监控人附件存储目录(主要指接收的命令/协作附带的附件)
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getMonitorDir(String userId, String jobsId) throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.RECORD)
                .append(File.separator)
                .append(userId)
                .append(File.separator)
                .append(jobsId)
                .append(File.separator)
                .append(DirName.MONITOR)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new IOException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取发布命令/协作附件存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getIssueDir() throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.ISSUE)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new NullPointerException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取失误招领附件存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getLostDir() throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.LOST)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new NullPointerException("Create folder failed!");
        }
        return file;
    }

    public static File getMediaFile(File file, @FileTypeLimit int type) {
        if (file.exists() || file.mkdirs()) {
            String timeStamp = Formatter.DATE_FORMAT11.format(new Date());
            File mediaFile;
            if (type == FileType.PICTURE) {
                mediaFile = new File(file.getPath() + File.separator
                        + "IMG_" + timeStamp + ".jpg");
            } else if (type == FileType.VIDEO) {
                mediaFile = new File(file.getPath() + File.separator
                        + "VID_" + timeStamp + ".mp4");
            } else if (type == FileType.AUDIO) {
                mediaFile = new File(file.getPath() + File.separator
                        + "VOI_" + timeStamp + ".aac");
            } else if (type == FileType.TEXT) {
                mediaFile = new File(file.getPath() + File.separator
                        + "TEXT_NOTE.txt");
            } else {
                return null;
            }
            return mediaFile;
        }
        return null;
    }

    /**
     * 文件类型
     */
    @IntDef({FileType.PICTURE, FileType.VIDEO, FileType.AUDIO, FileType.TEXT})
    @Retention(RetentionPolicy.SOURCE)
    @interface FileTypeLimit {
    }
}
