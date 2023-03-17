package org.veritas.assessment.biz.action;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;

@Data
@NoArgsConstructor
public class QueryProjectPageableAction {
    @Setter(AccessLevel.NONE)
    private String[] keywords;

    private Integer creatorUserId;

    private Integer groupOwnerId;

    private BusinessScenarioEnum businessScenario;

    private Boolean archived;

    private int page = 1;

    private int pageSize = 20;

    public void setKeyWordsString(String string) {
        if (StringUtils.isEmpty(string)) {
            return;
        }
        this.keywords = string.split("\\s");
    }

    public boolean hasKeyWords() {
        return ArrayUtils.isNotEmpty(this.keywords);
    }

}
