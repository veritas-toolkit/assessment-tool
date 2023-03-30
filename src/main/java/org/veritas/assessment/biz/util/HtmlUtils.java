package org.veritas.assessment.biz.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Slf4j
public class HtmlUtils {

    public static String minimize(String html) {
        Document document = Jsoup.parseBodyFragment(html);

        Document.OutputSettings outputSettings = new Document.OutputSettings()
                .outline(false)
                .maxPaddingWidth(0)
                .prettyPrint(true);
        document.outputSettings(outputSettings);
        String output = document.body().html();
        return StringUtils.remove(output, "\n");
    }

}
