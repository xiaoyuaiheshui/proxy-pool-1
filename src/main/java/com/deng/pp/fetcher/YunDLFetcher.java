package com.deng.pp.fetcher;

import com.deng.pp.entity.ProxyEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: yusq
 * @Date: 2019/2/28 0028
 */
public class YunDLFetcher extends AbstractFetcher<List<ProxyEntity>> {

    private static final String BASE_URL = "http://www.ip3366.net/";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yy-MM-dd HH:mm");

    private static final Logger logger = LoggerFactory.getLogger(YunDLFetcher.class);


    public YunDLFetcher() {
        this(10, 1000);
    }

    public YunDLFetcher(int totalPage) {
        this(totalPage, 1000);
    }

    public YunDLFetcher(int totalPage, long interval) {
        super(totalPage, interval);
    }

    @Override
    protected String pageUrl() {
        //http://www.ip3366.net/?stype=1&page=2
        String url;
        if (pageIndex == 1) url = BASE_URL;
        else url = BASE_URL + "?stype=1&page=" + pageIndex;

        return url;
    }

    @Override
    protected List<ProxyEntity> parseHtml(String html) {
        /**
         *               <tr>
         *                 <td>103.102.73.34</td>
         *                 <td>37552</td>
         *                 <td>高匿代理IP</td>
         *                 <td>HTTPS</td>
         *                 <td>GET, POST</td>
         *                 <td>SSL高匿_亚太地区</td>
         *                 <td>4秒</td>
         *                 <td>2019/2/28 4:37:03</td>
         *             </tr>
         */
        List<ProxyEntity> res = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements tables = doc.select("tbody");
        if(tables == null)return res;
        for (Element table : tables) {
            Elements trs = table.select("tr");
            if(trs == null)continue;
            for (int i = 1; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                ProxyEntity enity = new ProxyEntity();
                enity.setIp(tds.get(0).text().trim());
                enity.setPort(Integer.parseInt(tds.get(1).text()));
                enity.setLocation(tds.get(2).text().trim());
                enity.setAgentType(tds.get(3).text().trim());

                logger.info("got an agent: " + enity);

                res.add(enity);
            }
        }
        return res;
    }
}
