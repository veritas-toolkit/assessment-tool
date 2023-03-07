<#list fairness.featureMap as feature_name, feature>
    <div>
        <div class="table_box">
            <div class="table_title">
                Fairness metrics for <b>${feature_name}</b>
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
                    <tr <#if fairness.isFairMetric(metricName)> class="fair_metric_row"</#if>>
                        <td class="metric_name">${metricName}</td>
                        <td class="metric_value">${feature.faireMetricValueFormat(valueList)}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
        <div>Fairness Threshold Input: ${fairness.fairnessInit.fairThresholdInput}%</div>
        <div>Fairness Threshold: ${feature.fairThreshold}</div>
        <div>Fairness Conclusion: ${feature.fairnessConclusion}</div>
    </div>
</#list>