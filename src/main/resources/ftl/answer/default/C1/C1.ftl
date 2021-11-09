<div>
Disadvantage is defined as a significant difference in the rates of occurrence of harms and benefits between
groups. For each of the attribute, the value and confidence interval of fairness metrics are shown below.
The primary fairness metric is marked in red.
</div>

<#-- 添加说明 -->
<#assign fairnessInit=jsonModel.fairnessInit>
<#if fairnessInit.fairMetricNameInput?? && fairnessInit.fairMetricNameInput == "auto">
<div>
To assess the fairness of this ${businessScenario.name} model,
our priority is to measure <#if fairnessInit.fairImpact != "normal">${fairnessInit.fairImpact}</#if>
${fairnessInit.fairPriority!""} to the
${fairnessInit.fairConcernDisplay()}
cohort as the result of the model,
therefore we chose ${fairnessInit.fairMetricName} as primary fairness metric for the assessment.
</div>
</#if>


<#list jsonModel.featureMap as feature_name, feature>
    <div>
        <div class="table_box">
        <div>
        <big>
        Fairness metrics for <b>${feature_name}</b>
        </big>
        </div>
        <table>
            <thead>
            <tr>
                <th>Fairness Metric</th>
                <th>Value</th>
            </tr>
            </thead>
            <tbody>
            <#list feature.fairMetricValueListMap as metricName, valueList>
                <tr <#if jsonModel.isFairMetric(metricName)> class="fair_metric_row"</#if>>
                    <td class="metric_name">${metricName}</td>
                    <td class="metric_value">${feature.faireMetricValueFormat(valueList)}</td>
                </tr>
            </#list>
            </tbody>
        </table>
        </div>

        <div>Fairness Threshold Input: <b>${jsonModel.fairnessInit.fairThresholdInput}%</b></div>
        <div>Fairness Threshold: <b>${feature.fairThreshold}</b></div>
        <div>Fairness Conclusion: <b>${feature.fairnessConclusion}</b></div>

    </div>
</#list>



