package org.example.thread_design_pattern.future.spider;

public class SpiderTest {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        // 同步：采集数据
        // Content content1 = Spider.grab("http://www.baidu.com");
        // Content content2 = Spider.grab("http://www.taobao.com");
        // Content content3 = Spider.grab("http://www.jd.com");

        // 异步：采集数据
        Content content1 = Spider.asyncGrab("http://www.baidu.com");
        Content content2 = Spider.asyncGrab("http://www.taobao.com");
        Content content3 = Spider.asyncGrab("http://www.jd.com");

        // 保存数据
        Spider.save("baidu.html", content1);
        Spider.save("taobao.html", content2);
        Spider.save("jd.html", content3);

        long end = System.currentTimeMillis();
        System.out.println("spent time: " + (end - start) + " ms");

        // 同步：spent time: 443 ms
        // 异步：spent time: 82 ms

    }
}