package run.download.av.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import run.download.av.frame.download.Table;
import run.download.av.response.episodeinfo.Data;
import run.download.av.response.episodeinfo.Durl;
import run.download.av.response.episodeinfo.EpisodeInfo;
import run.download.av.response.episodelist.Pages;
import run.download.av.util.DownloadManager;
import util.FileUtil;
import util.MergeFlvFiles;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 单p处理器
 */
public class PageHandler {
    private long aid;
    private Pages page;
    private File folder;
    private Table table;
    private int rowIndex;
    //视频格式
    private String format;

    public PageHandler(long aid, Pages page, File folder, Table table, int rowIndex) {
        this.aid = aid;
        this.page = page;
        this.folder = folder;
        this.table = table;
        this.rowIndex = rowIndex;
    }

    /**
     * 返回单durl的下载地址
     *
     * @param durl
     * @return
     */
    private static String getSingleDurlDownloadUrl(Durl durl) {
        //视频下载地址
        String downloadUrl = null;
        String url = durl.getUrl();
        List<String> backupUrlList = durl.getBackup_url();
        //如果有url
        if (StringUtils.isNotEmpty(url)) {
            downloadUrl = url;
            //如果没有url，看有没有backupUrl
        } else if (CollectionUtils.isNotEmpty(backupUrlList)) {
            downloadUrl = backupUrlList.get(0);
            //如果backupUrl也没有
        } else {
            throw new RuntimeException("没找到下载url");
        }
        return downloadUrl;
    }

    /**
     * 下载单p
     */
    public void downloadPage() {
        //设置ui状态为准备中
        table.updateState(rowIndex, "preparing");
        long cid = page.getCid();
        //单p名
        String pageName = page.getPart();
        //获取单p信息
        EpisodeInfo episodeInfo = BilibiliHandler.getSingleEpisodeInfo(aid, cid);
        Data data = episodeInfo.getData();
        //视频格式
        format = data.getFormat();
        //去掉数字
        format = format.replaceAll("\\d+", "");
        //如果是非flv格式视频，并且有多个碎片
        if (format.equals("flv") == false) {
            System.err.println("发现非flv格式视频：aid = " + aid + ", cid = " + cid + ", pageName = " + pageName);
            System.err.println("发现非flv格式视频：aid = " + aid + ", cid = " + cid + ", pageName = " + pageName);
            System.err.println("发现非flv格式视频：aid = " + aid + ", cid = " + cid + ", pageName = " + pageName);
            System.err.println("发现非flv格式视频：aid = " + aid + ", cid = " + cid + ", pageName = " + pageName);
            System.err.println("发现非flv格式视频：aid = " + aid + ", cid = " + cid + ", pageName = " + pageName);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //下载地址列表
        List<Durl> durlList = data.getDurl();
        //列表大小
        int durlListSize = durlList.size();
        //初始化碎片的，已下载字节数的数组
        partDownloadBytes = new long[durlListSize];
        //初始化下载进度
        downloadStateArray = new Boolean[durlListSize];
        //初始化碎片文件
        pieces = new File[durlListSize];
        //遍历url列表，下载所有碎片
        for (int i = 0; i < durlListSize; i++) {
            Durl durl = durlList.get(i);
            //文件大小
            long fileSize = durl.getSize();
            //加到总大小里
            totalPageBytes += fileSize;
            //序号
            int order = durl.getOrder();
            //TODO 补零
            String no = page.getPage() + "";
            //替换非法字符
            pageName = FileUtil.replaceIllegalFileName(pageName, " ");
            //文件名
            String fileName = no + "_" + pageName + "." + format;
            //如果有多个碎片，追加.part1
            if (durlListSize > 1) {
                fileName += ".part" + order;
            }
            //下载地址
            String url = getSingleDurlDownloadUrl(durl);
            //碎片文件
            File file = new File(folder, fileName);
            //添加碎片文件到数组，以备回调
            pieces[i] = file;
            //提交下载任务，更新ui状态为waiting
            table.updateState(rowIndex, "waiting");
            //更新ui中的文件大小
            table.updateSize(rowIndex, FileUtil.getSizeString(totalPageBytes));
            //下载碎片
            DownloadManager.downloadFile(url, fileSize, file, aid, i, this);
        }
    }

    /**
     * 在下载任务得到执行，正式开始下载时回调
     */
    public void onStartDownload() {
        //更新ui状态为downloading
        table.updateState(rowIndex, "downloading");
    }

    //单p文件碎片总大小，当然是flv合并之前的
    private long totalPageBytes;
    //各碎片已下载字节数
    private long[] partDownloadBytes;

    /**
     * 在下载时
     *
     * @param partIndex     碎片索引
     * @param finishedBytes 已下载字节数
     */
    public void onDownloading(int partIndex, long finishedBytes) {
        partDownloadBytes[partIndex] = finishedBytes;
        //计算所有碎片已下载字节数
        long totalDownloadBytes = 0;
        for (long partBytes : partDownloadBytes) {
            totalDownloadBytes += partBytes;
        }
        //更新进度条
        table.updateProgress(rowIndex, totalDownloadBytes, totalPageBytes);
        //更新文件大小
        table.updateSize(rowIndex, FileUtil.getSizeString(totalDownloadBytes)
                + " / " + FileUtil.getSizeString(totalPageBytes));
    }

    /**
     * 碎片下载进度状态
     * 默认都是null，代表还没提交下载任务
     * true代表已经下载成功
     * false代表已经下载失败
     */
    private Boolean[] downloadStateArray;
    //文件碎片数组
    private File[] pieces;

    /**
     * 当单p，或单个碎片，下载完成时回调
     *
     * @param partIndex 碎片序号
     * @param isSucceed 下载是否成功
     */
    public void downloadPartFinishCallback(int partIndex, boolean isSucceed) {
        //碎片数量
        int length = downloadStateArray.length;
        //如果单p不分碎片，则返回
        if (length == 1) {
            //更新ui状态为finished
            table.updateState(rowIndex, "finished");
            return;
        }
        //设置下载状态
        downloadStateArray[partIndex] = isSucceed;
        //检查是不是所有的碎片下载任务都有结果了
        for (Boolean downloadState : downloadStateArray) {
            //如果有没结果的任务，则什么都不做
            if (downloadState == null) {
                return;
            }
        }
        //到这里说明所有任务都有结果了
        //检查是否所有碎片都下载成功
        boolean isAllSucceed = true;
        for (int i = 0; i < length; i++) {
            for (Boolean downloadState : downloadStateArray) {
                //只要有没下载成功的，就跳出
                if (downloadState == false) {
                    isAllSucceed = false;
                    System.err.println("有下载失败碎片: index = " + i + " file = " + pieces[i]);
                }
            }
        }
        //如果有失败的
        if (isAllSucceed == false) {
            System.err.println("单p下载结束，存在下载失败的碎片");
            return;
        }
        //如果都成功
        //获得文件名
        File pieceFile = pieces[0];
        String fileName = pieceFile.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        //合并碎片
        File desc = new File(pieceFile.getParent(), fileName);
        //设置ui状态为合成中
        table.updateState(rowIndex, "merging");
        //如果是flv格式
        if (format.equals("flv")) {
            MergeFlvFiles mergeFlvFiles = new MergeFlvFiles();
            try {
                mergeFlvFiles.merge(pieces, desc);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("合并flv文件发生异常：" + e.getMessage() + " fileName = " + fileName);
            }
        } else {
            //如果是非flv格式，使用FFmpeg
            System.out.println("非flv格式合并");
        }
        //删除所有碎片
        for (File file : pieces) {
            System.out.println("删除碎片：" + file.getPath());
            file.delete();
        }
        //更新ui状态为finished
        table.updateState(rowIndex, "finished");
    }

}
