package org.example.thread.thread_design_pattern.future.spider;

import java.io.FileOutputStream;
import java.io.IOException;

// 采集数据、保存数据、分析数据
public class Spider {

    // 同步：采集数据
    public static Content grab(String url) {
        return new ContentImpl(url);
    }

    // 异步：采集数据
    public static Content asyncGrab(String url) {
        ContentAsyncImpl contentAsyncImpl = new ContentAsyncImpl();
        new Thread(()->{
            ContentImpl content = new ContentImpl(url);
            contentAsyncImpl.setContent(content.getContent());
        }).start();
        return contentAsyncImpl;
    }


    // 保存数据
    public static void save(String filePath, Content content) {
        byte[] bytes = content.getContent();
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
