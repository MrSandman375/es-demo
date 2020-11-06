package com.renjie.utils;

import com.renjie.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Fan
 * @Date 2020/11/5
 * @Description: 用于获取搜索结果的工具类
 */
@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {

        new HtmlParseUtil().parseJD("java").forEach(System.out::println);
    }
    public List<Content> parseJD(String keywords) throws IOException {
        //获取请求 https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword="+keywords;
        //解析网页(document对象)
        Document document = Jsoup.parse(new URL(url), 3000);
        //js中用法一致
        Element element = document.getElementById("J_goodsList");
//        System.out.println(element.html());
        //获取所有的li元素
        Elements lis = element.getElementsByTag("li");

        //ArrayList
        ArrayList<Content> goodsList = new ArrayList<>();
        //获取元素中的内容
        for (Element el : lis){
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodsList.add(content);

        }
        return goodsList;
    }
}
