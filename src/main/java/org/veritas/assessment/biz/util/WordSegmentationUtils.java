package org.veritas.assessment.biz.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class WordSegmentationUtils {
    public static List<Word> split(String text) {
        List<Word> list = new ArrayList<>();

        try (Analyzer analyzer = new StandardAnalyzer();
             TokenStream tokenStream = analyzer.tokenStream("prod", text)) {

            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                list.add(new Word(offsetAttribute.startOffset(), offsetAttribute.endOffset(), text));
            }
        } catch (IOException exception) {
            log.error("word segmentation failed.", exception);
        }
        return list;
    }

    public static class Word {
        @Getter
        private final int start;
        @Getter
        private final int end;

        @Getter
        private final String word;

        @Getter
        private final String text;

        public Word(int start, int end, String text) {
            Objects.requireNonNull(text);
            if (text.length() < end) {
                throw new IllegalArgumentException();
            }
            this.start = start;
            this.end = end;
            this.word = text.substring(start, end);
            this.text = text;
        }
    }
}
