package Markov.Crawler;

import Markov.Objects.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static Markov.Scroller.*;

public class Crawler_DC extends Crawler {

    @Override
    public void scrollMain() {
        if (major) {
            mainPage = "http://gall.dcinside.com/board/lists/?id=" + gallID;
        } else {
            mainPage = "http://gall.dcinside.com/mgallery/board/lists/?id=" + gallID;
        }
        try {
            Document doc = Jsoup.connect(mainPage).get();
            Elements posts = doc.select("tbody").first().select("tr[class=ub-content],tr[class=ub-content us-post]");

            int nCount = 0;
            for (Element post : posts) {
                //  Elements id = post.select("td[class=gall_subject]");
                String type;
                if (major) {
                    type = post.select("td[class=gall_num]").first().text();
                } else {
                    type = post.select("td[class=gall_subject]").first().text();
                }

                if (isValidIndex(type)) {
                    //  System.out.println(post.html());
                    try {
                        String writer = post.select("td[class=gall_writer ub-writer]").first().attr("data-nick");
                        String url = post.select("td[class=gall_tit ub-word]").select("a").first().attr("href");
                        String mobileURL = "http://gall.dcinside.com" + url;
                        String title = post.select("td[class=gall_tit ub-word]").select("a").first().text();

                        if (emergency) {
                            lookedID.add(mobileURL);
                            System.out.println(mobileURL);
                        } else {
                            if (!lookedID.contains(mobileURL)) {
                                Page haruhi = new Page(title, mobileURL);
                                haruhi.writer = writer;
                                instances.add(haruhi);
                                nCount++;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("------------");
                        e.printStackTrace();
                        System.out.println(post.html());
                    }
                }
            }

            if (emergency) emergency = false;
            if (nCount > 0)
                System.out.println("[페이지 로드] 축적된 ID: " + lookedID.size() + "  새로운 링크: " + nCount);

            if (lookedID.size() > 100) {
                lookedID.remove(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
