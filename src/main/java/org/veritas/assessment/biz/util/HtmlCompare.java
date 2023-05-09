package org.veritas.assessment.biz.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

@Slf4j
public class HtmlCompare {

    @Getter
    private final String basedHtml;
    @Getter
    private final String newHtml;

    @Getter
    private final Document basedDocument;
    @Getter
    private final Document newDocument;

    private final static String SPAN_DEL_WRAP = "<span class='diff_del'>";
    private final static String SPAN_ADD_WRAP = "<span class='diff_add'>";


    public HtmlCompare(String basedHtml, String newHtml) {
        this.basedHtml = basedHtml != null ? basedHtml : "";
        this.newHtml = newHtml != null ? newHtml : "";
        this.basedDocument = Jsoup.parseBodyFragment(this.basedHtml);
        this.newDocument = Jsoup.parseBodyFragment(this.newHtml);
        this.doCompare();
    }

    private void doCompare() {
        List<TextNode> basedSpanList = this.allWordTextNode(this.basedDocument);
        List<TextNode> newSpanList = this.allWordTextNode(this.newDocument);

        TextNodeManager basedTextNodeManager = new TextNodeManager(basedSpanList);
        TextNodeManager newTextNodeManager = new TextNodeManager(newSpanList);

        if (log.isTraceEnabled()) {
            log.trace("old text: {}", basedTextNodeManager.getText());
            log.trace("new text: {}", newTextNodeManager.getText());
        }

        DiffUtils.DiffResult diffResult =
                DiffUtils.diff(basedTextNodeManager.getText(),newTextNodeManager.getText());
        if (log.isTraceEnabled()) {
            StringBuilder builder = new StringBuilder();
            diffResult.getBasedDiffList().forEach(diff -> builder.append(diff.getText()));
            log.trace("old same? {}", StringUtils.equals(basedTextNodeManager.getText(), builder.toString()));

            StringBuilder builder2 = new StringBuilder();
            diffResult.getNewDiffList().forEach(diff -> builder2.append(diff.getText()));
            log.trace("new same? {}", StringUtils.equals(newTextNodeManager.getText(), builder2.toString()));
        }

        diffResult.getBasedDiffList().stream()
                .filter(diff -> diff.getOperation() == DiffOperation.DELETE)
                .forEach(diff -> {
                    basedTextNodeManager.wrapAsDelSpan(diff.getStart(), diff.getEnd());
                });

        diffResult.getNewDiffList().stream()
                .filter(diff -> diff.getOperation() == DiffOperation.ADD)
                .forEach(diff -> {
                    newTextNodeManager.wrapAsAddSpan(diff.getStart(), diff.getEnd());
                });
    }

    public String comparedBasedHtml() {
        Document.OutputSettings outputSettings = new Document.OutputSettings()
                .outline(false)
                .indentAmount(4)
                .maxPaddingWidth(30)
                .prettyPrint(true);
        this.basedDocument.outputSettings(outputSettings);
        return this.basedDocument.body().html();
    }

    public String comparedNewHtml() {
        Document.OutputSettings outputSettings = new Document.OutputSettings()
                .outline(false)
                .indentAmount(4)
                .maxPaddingWidth(30)
                .prettyPrint(true);
        this.newDocument.outputSettings(outputSettings);
        return this.newDocument.body().html();
    }


    private List<TextNode> allWordTextNode(Document document) {
        Objects.requireNonNull(document);
        List<TextNode> list = new ArrayList<>();
        document.textNodes();
        for (Element element : document.getAllElements()) {
            for (TextNode textNode : element.textNodes()) {
                String text = StringUtils.trimToNull(textNode.text());
                if (text == null) {
                    continue;
                }
                String allText = textNode.getWholeText();
                List<WordSegmentationUtils.Word> wordList = WordSegmentationUtils.split(allText);
                // cut the textNode from tail to head.
                int preStart = allText.length();
                Stack<TextNode> stack = new Stack<>();
                for (int i = wordList.size() - 1; i >= 0; --i) {
                    WordSegmentationUtils.Word word = wordList.get(i);
                    String gap = allText.substring(word.getEnd(), preStart);
                    if (StringUtils.trimToNull(gap) != null) {
                        TextNode gapTextNode = textNode.splitText(word.getEnd());
                        if (StringUtils.trimToNull(gapTextNode.text()) != null) {
                            stack.push(gapTextNode);
                        }
                    }
                    TextNode wordTextNode = textNode.splitText(word.getStart());
                    stack.push(wordTextNode);
                    preStart = word.getStart();
                }
                if (StringUtils.trimToNull(textNode.text()) != null) {
                    stack.push(textNode);
                }
                while (!stack.empty()) {
                    list.add(stack.pop());
                }
            }
        }
        return list;
    }

    private static class TextNodeManager {
        @Getter
        private final List<TextNode> spanList;

        @Getter
        private final String text;

        private final Map<Integer, TextNode> offsetMap = new LinkedHashMap<>();

        public TextNodeManager(List<TextNode> spanList) {
            this.spanList = spanList;
            StringBuilder stringBuilder = new StringBuilder();
            spanList.forEach(span -> {
                String spanText = span.text();
                int index = stringBuilder.length();
                for (int i = 0; i < spanText.length(); ++i) {
                    offsetMap.put(index, span);
                    ++index;
                }
                stringBuilder.append(spanText);
                stringBuilder.append(" ");
            });
            this.text = stringBuilder.toString();
        }

        private Set<TextNode> find(int startIndex, int endIndex) {
            Set<TextNode> set = new LinkedHashSet<>();
            for (int i = startIndex; i < endIndex; ++i) {
                TextNode e = offsetMap.get(i);
                if (e != null) {
                    set.add(e);
                }
            }
            return Collections.unmodifiableSet(set);
        }

        void wrapAsDelSpan(int startIndex, int endIndex) {
            Set<TextNode> set = find(startIndex, endIndex);
            set.forEach(e -> {
                log.debug("del: {}", e.text());
                e.wrap(SPAN_DEL_WRAP);
            });
        }

        void wrapAsAddSpan(int startIndex, int endIndex) {
            Set<TextNode> set = find(startIndex, endIndex);
            set.forEach(e -> {
                log.debug("add: {}", e.text());
                e.wrap(SPAN_ADD_WRAP);
            });
        }
    }

}
