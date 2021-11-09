<#--Are there any graphical or numerical outputs to visualize the tradeoff?-->
<#if jsonModel.hasTradeoff() == true>

<#list graphContainer.getFeatureTradeoffContourMap() as feature_name, chart>
    <#if chart??>
        <br/>
        <div class="image_box">
            <div class="image_title">Fairness Tradeoff for ${feature_name}</div>
            <img src="${graphContainer.findFeatureTradeoffContour(feature_name)}" />
        </div>
        <br/>
    </#if>
</#list>


<div>
    The x-axis and y-axis show a range of possible lending risk thresholds for two groups, respectively.
    The heatmap indicates the model’s expected performance when operated at each pair of risk thresholds.
    The white contour lines indicate the fairness metric value with respect to selected feature.
</div>

<div>
    For example, equal opportunity measures the difference in the true positive rates between two groups of individuals.
    Thus it is optimal when equal to zero (0).
</div>
<div>
    The <b>blue diamond</b> maximizes the unconstrained model performance.
</div>

<div>
    The <b>red X</b> maximizes model performance while keeping the same risk threshold for both men and women.
</div>
<div>
    The <b>purple star</b> maximizes the model performance while ensuring optimal fairness as measured via the selected fairness metric.
</div>
</#if>