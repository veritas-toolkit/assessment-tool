<#--What are the performance estimates of the AIDA model?-->



<#-- 仅画一遍 -->
<div class="table_box">
<table class="feature_table">
    <thead>
        <tr>
            <th>Performance Metric</th>
            <th>Value</th>
        </tr>
    </thead>
    <tbody>
    <#list jsonModel.perfMetricValues as metricName, valueList>
        <tr <#if jsonModel.isPerfMetric(metricName) == true> class="perf_metric_row"</#if>>
            <td>${metricName}</td>
            <td>${valueList[0]} +/- ${valueList[1]}</td>
        </tr>
    </#list>
    </tbody>
</table>
</div>

<#-- value写成+/-confidence interval的形式，比如"0.8 +/- 0.06" 参考p93表格-->
<div>
    The confidence interval indicates two standard deviations as computed via bootstrap resampling of the test set.
</div>


<#--
<#list jsonModel.featureMap as feature_name, feature>
    <div>
        <div>${feature.fairnessConclusion}</div>
        <div class="table_box">
        <table>
            <thead>
                <tr>
                    <th>Performance Metric</th>
                    <th>Value</th>
                    <th>Confidence Interval</th>
                </tr>
            </thead>
            <tbody>
                <#list feature.fairMetricValueListMap as metricName, metricValueList>
                <tr>
                    <td>${metricName}</td>
                    <#list metricValueList as metricValue>
                        <td>${metricValue}</td>
                    </#list>
                </tr>
                </#list>
            </tbody>
        </table>
        </div>
    </div>
    <#break>
</#list>
-->
    <#-- Yes, break, only output the first entry. -->

