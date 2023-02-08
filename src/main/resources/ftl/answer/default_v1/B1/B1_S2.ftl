<#-- Is there measurement bias in the data?-->
<#if graphContainer.classDistributionPieChart??>
<div class="image_box">
    <div class="image_title">Class Distribution</div>
    <img class="pie" src="${graphContainer.classDistributionPieChart}" />
</div>
</#if>

<div>
    In the case of credit scoring, users who do not default will be assigned a positive label,
    while those who default will be assigned a negative label. If there is considerable
    class imbalance, it may lead to bias in the results so it needs to be addressed.
    The distribution pie chart for the classes is shown.
</div>

<div>
<#if jsonModel.minClassDistributionName()??>
    <#assign minDistribution = jsonModel.minClassDistributionName()>
    <#if jsonModel.classDistributionIsAverage()>
        The proportion of <b>${minDistribution.getKey()}</b> is approximately ${minDistribution.getValue()}, so the imbalance in distribution of labels is small.
    <#else>
        The proportion of <b>${minDistribution.getKey()}</b> is approximately ${minDistribution.getValue()}, which indicates an large imbalance in distribution of labels.
    </#if>
<#else>
    Unable describe this class distribution data.
</#if>
</div>

