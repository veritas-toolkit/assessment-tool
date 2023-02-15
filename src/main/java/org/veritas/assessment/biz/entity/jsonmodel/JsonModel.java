package org.veritas.assessment.biz.entity.jsonmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class JsonModel {
    @JsonProperty(value = "fairness")
    private Fairness fairness;

    @JsonProperty(value = "transparency")
    private Transparency transparency;
}
