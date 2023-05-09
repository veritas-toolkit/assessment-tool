package org.veritas.assessment.biz.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DiffUtilsTest {

    @Test
    void testDiff() {
        DiffUtils.DiffResult diffResult = DiffUtils.diff(
                "this is an old line. add: del:",
                "This is a new line. add: del:");
        log.info("result:\n{}", diffResult);
        Set<String> deleteSet = new HashSet<>();
        deleteSet.add("this");
        deleteSet.add("an");
        deleteSet.add("old");
        log.info("based result:\n{}", diffResult.getBasedDiffList());
        for (DiffUtils.Diff diff : diffResult.getBasedDiffList()) {
            if (diff.getOperation() == DiffOperation.DELETE) {
                assertTrue(deleteSet.contains(diff.getText()));
                deleteSet.remove(diff.getText());
            }
        }
        assertTrue(deleteSet.isEmpty());


        Set<String> addSet = new HashSet<>();
        addSet.add("This");
        addSet.add("a");
        addSet.add("new");
        log.info("new result:\n{}", diffResult.getNewDiffList());
        for (DiffUtils.Diff diff : diffResult.getNewDiffList()) {
            if (diff.getOperation() == DiffOperation.ADD) {
                assertTrue(addSet.contains(diff.getText()));
                addSet.remove(diff.getText());
            }
        }
        assertTrue(addSet.isEmpty());
    }
}