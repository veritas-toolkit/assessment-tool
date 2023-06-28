package org.veritas.assessment.biz.util;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
class HtmlCompareTest {

    private String html = HtmlUtilsTest.readFile(HtmlUtilsTest.HTML_FILE_SHORT);
    private String htmlDiff = HtmlUtilsTest.readFile(HtmlUtilsTest.HTML_FILE_SHORT_DIFF);
//        String html = HtmlUtilsTest.readFile("F9_0.html");
//        String htmlDiff = HtmlUtilsTest.readFile("F9_1.html");
//    String html = "<div>this is fair .</div>";
//    String htmlDiff = "<div>this is unfair .</div>";
    @Test
    void testCompare() {
        String html = HtmlUtilsTest.readFile(HtmlUtilsTest.HTML_FILE_SHORT);
        String htmlDiff = HtmlUtilsTest.readFile(HtmlUtilsTest.HTML_FILE_SHORT_DIFF);
//        String html = HtmlUtilsTest.readFile("html/F9_0.html");
//        String htmlDiff = HtmlUtilsTest.readFile("html/F9_1.html");
//        String html = "<div>this is fair .</div>";
//        String htmlDiff = "<div>this is unfair .</div>";

        HtmlCompare compare = new HtmlCompare(html, htmlDiff);

        Document document = compare.getBasedDocument();
        HtmlUtilsTest.print(document);
        HtmlUtilsTest.print(compare.getNewDocument());
    }

    @Test
    void test2() {
        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .ignoreWhiteSpaces(true)
                .lineNormalizer(s -> s)
                .oldTag((tag, f) -> f ? "<span class='diff_del'>" : "</span>")
//                .newTag((tag, f) -> f ? "<span class='diff_add'>" : "</span>")
                .newTag((tag, f) -> f ? "<span class='diff_add'>" : "</span>")
                .build();
        List<DiffRow> rows = generator.generateDiffRows(Arrays.asList(html), Arrays.asList(htmlDiff));
        for (DiffRow row : rows) {
            log.info("old: {}", row.getOldLine());
        }
        log.info("---------------------------------");
        for (DiffRow row : rows) {
            log.info("new: {}", row.getNewLine());
        }
    }

    @Test
    void test() {
        String str = "d;aflkdsjfa<del>aa<del>;lsdkfj<del>afjad;sifj[aa]a";
        String[] result = str.split("\\<del\\>");
        for (String s : result) {
            log.info("s: {}", s);
        }
        log.info("result: {}", result);
    }
}