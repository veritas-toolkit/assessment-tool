package org.veritas.assessment.biz.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
class HtmlUtilsTest {

    public static final String HTML_FILE = "html/for_test.html";
    public static final String HTML_FILE_SHORT = "html/for_test_short.html";
    public static final String HTML_FILE_SHORT_DIFF = "html/for_test_short_diff.html";

    public static String readFile(String path) {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException("read failed.", exception);
        }
    }

    public static void print(Document document) {
        Objects.requireNonNull(document);
        Document.OutputSettings outputSettings = new Document.OutputSettings()
                .outline(false)
                .indentAmount(4)
                .maxPaddingWidth(30)
                .prettyPrint(true);
        document.outputSettings(outputSettings);
        log.info("html:\n{}", document.body().html());
    }

    @Test
    void testMinimize_success() {
        String html = readFile(HTML_FILE);
        log.info("start");
        StopWatch stopWatch = StopWatch.createStarted();
        String output = HtmlUtils.minimize(html);
        stopWatch.stop();
        log.info("end");
        log.info("time used: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
        log.info("output:\n{}", output);
    }


    @Test
    void a() throws Exception {
        Analyzer analyzer = new StandardAnalyzer();
//        Analyzer analyzer=new SimpleAnalyzer();
        //获得tokenStream对象
        //参数一：域名    参数二：要分析的文本内容
        TokenStream tokenStream = analyzer.tokenStream("test",
                "The Spring Framework provides a comprehensive programming and configuration model. " +
                        "He's finished his homework. " +
                        "It's cost 8.80$.Oh too expensive!");
        //添加一个引用，可以获得每个关键词
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //将指针调整到列表的头部
        tokenStream.reset();
        //遍历关键词列表，通过incrementToken方法判断列表是否结束
        while (tokenStream.incrementToken()) {
            //取关键词
//            log.info("{}", charTermAttribute.getClass().getCanonicalName());
            log.info("{}     - [{}, {})", charTermAttribute,
                    offsetAttribute.startOffset(), offsetAttribute.endOffset());
//            log.info("{}", offsetAttribute.startOffset());
        }
        //关闭资源
        tokenStream.close();
    }
}