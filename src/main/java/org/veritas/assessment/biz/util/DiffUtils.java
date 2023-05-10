package org.veritas.assessment.biz.util;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class DiffUtils {

    private static final String DEFAULT_DEL_PREFIX = "del:";
    private static final String DEFAULT_ADD_PREFIX = "add:";



    public static DiffResult diff(String basedText, String newText) {
        basedText = basedText != null ? basedText : "";
        newText = newText != null ? newText : "";

        String delPrefix = prefix(DEFAULT_DEL_PREFIX, basedText, newText);
        String addPrefix = prefix(DEFAULT_ADD_PREFIX, basedText, newText);

        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .ignoreWhiteSpaces(true)
                .lineNormalizer(s -> s)
                .oldTag((tag, f) -> f ? "\n" + delPrefix : "\n")
                .newTag((tag, f) -> f ? "\n" + addPrefix : "\n")
                .build();
        List<DiffRow> diffRowList = generator.generateDiffRows(Arrays.asList(basedText), Arrays.asList(newText));
        return convert(diffRowList, delPrefix, addPrefix);
    }

    private static String prefix(String defaultPrefix, String... searchTextArray) {
        Objects.requireNonNull(defaultPrefix);
        if (searchTextArray == null || searchTextArray.length == 0) {
            return defaultPrefix;
        }
        String prefix = defaultPrefix;
        boolean contains = false;
        do {
            contains = false;
            for (String s : searchTextArray) {
                if (StringUtils.contains(s, prefix)) {
                    contains = true;
                    break;
                }
            }
            if (contains) {
                prefix = RandomStringUtils.randomAlphanumeric(8) + "-" + defaultPrefix;
            }
        } while (contains);
        if (log.isDebugEnabled()) {
            log.debug("compare prefix: {}", prefix);
        }
        return prefix;
    }

    private static DiffResult convert(List<DiffRow> diffRowList, String delPrefix, String addPrefix) {
        List<Diff> basedDiffList = new ArrayList<>();
        int basedStartIndex = 0;
        for (DiffRow diffRow : diffRowList) {
            String[] oldSplit = StringUtils.split(diffRow.getOldLine(), "\n");
            for (String s : oldSplit) {
                Diff diff = null;
                if (StringUtils.startsWith(s, delPrefix)) {
                    String diffWord = StringUtils.substring(s, StringUtils.length(delPrefix));
                    diff = new Diff(DiffOperation.DELETE, diffWord, basedStartIndex);
                    if (log.isDebugEnabled()) {
                        log.debug("---- {}", s);
                    }
                } else {
                    diff = new Diff(DiffOperation.EQUAL, s, basedStartIndex);
                }
                basedDiffList.add(diff);
                basedStartIndex = diff.getEnd();
            }
        }
        List<Diff> newDiffList = new ArrayList<>();
        int newStartIndex = 0;
        for (DiffRow diffRow : diffRowList) {
            String[] newSplit = StringUtils.split(diffRow.getNewLine(), "\n");
            for (String s : newSplit) {
                Diff diff = null;
                if (StringUtils.startsWith(s, addPrefix)) {
                    String diffWord = StringUtils.substring(s, StringUtils.length(addPrefix));
                    diff = new Diff(DiffOperation.ADD, diffWord, newStartIndex);
                    if (log.isDebugEnabled()) {
                        log.debug("---- {}", s);
                    }
                } else {
                    diff = new Diff(DiffOperation.EQUAL, s, newStartIndex);
                }
                newDiffList.add(diff);
                newStartIndex = diff.getEnd();
            }
        }
        return new DiffResult(basedDiffList, newDiffList);
    }


    @Getter
    @ToString
    public static class DiffResult {
        private final List<Diff> basedDiffList;
        private final List<Diff> newDiffList;

        private DiffResult(List<Diff> basedDiffList, List<Diff> newDiffList) {
            this.basedDiffList = basedDiffList;
            this.newDiffList = newDiffList;
        }

    }

    @Getter
    @ToString
    public static class Diff {
        private final DiffOperation operation;
        private final String text;
        private final int start;
        private final int end;

        public Diff(DiffOperation operation, String text, int start) {
            this.operation = operation;
            this.text = text;
            this.start = start;
            this.end = start + StringUtils.length(text);
        }


    }
}
