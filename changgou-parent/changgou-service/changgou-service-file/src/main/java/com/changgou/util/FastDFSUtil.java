package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 今何许
 * @date 2021-02-02 16:50
 */
public class FastDFSUtil {
    static {
        //从classpath下获取文件对象获取路径
        String path = new ClassPathResource("fdfs_client.conf").getPath();
        try {
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //图片上传
    public static String[] upload(FastDFSFile file) {
        try {
            //创建tracker
            StorageClient storageClient = getStorageClient();
            //参数1 字节数组
            //参数2 扩展名(不带点)
            //参数3 元数据( 文件的大小,文件的作者,文件的创建时间戳)
            NameValuePair[] meta_list = new NameValuePair[]{new NameValuePair(file.getAuthor()), new NameValuePair(file.getName())};
            String[] strings = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
            return strings;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //图片下载
    public static InputStream downFile(String groupName, String remoteFileName) {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            //3.创建trackerclient对象
            StorageClient storageClient = getStorageClient();
            //7.根据组名 和 文件名 下载图片

            //参数1:指定组名
            //参数2 :指定远程的文件名
            byte[] bytes = storageClient.download_file(groupName, remoteFileName);
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            return byteArrayInputStream;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //根据文件名和组名获取文件的信息
    public static FileInfo getFile(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getStorageClient();
            //参数1 指定组名
            //参数2 指定文件的路径
            FileInfo fileInfo = storageClient.get_file_info(groupName, remoteFileName);
            return fileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //图片删除
    public static void deleteFile(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getStorageClient();
            int i = storageClient.delete_file(groupName, remoteFileName);
            if (i == 0) {
                System.out.println("删除成功");
            } else {
                System.out.println("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据组名获取组的信息
    public static StorageServer getStorages(String groupName) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();
            //参数1 指定traqckerserver 对象
            //参数2 指定组名
            StorageServer group1 = trackerClient.getStoreStorage(trackerServer, groupName);
            return group1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    //根据文件名和组名 获取组信息的数组信息
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName) {
        try {
            //3.创建trackerclient对象
            TrackerClient trackerClient = new TrackerClient();
            //4.创建trackerserver 对象
            TrackerServer trackerServer = trackerClient.getConnection();

            ServerInfo[] group1s = trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
            return group1s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    //获取tracker 的ip和端口的信息
    //http://172.16.248.128:8888
    public static String getTrackerUrl() {
        try {
            TrackerServer trackerServer = getTrackerServer();
            //tracker 的ip的信息
            String ip = trackerServer.getInetSocketAddress().getHostString();

            //http://172.16.248.128:8888/group1/M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg img
            int port = ClientGlobal.getG_tracker_http_port();
            return "http://" + ip + ":" + port;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static TrackerServer getTrackerServer() throws IOException {
        //3.创建trackerclient对象
        TrackerClient trackerClient = new TrackerClient();
        //4.创建trackerserver 对象
        return trackerClient.getConnection();
    }
    private static StorageClient getStorageClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();

        return new StorageClient(trackerServer, null);
    }
}
