<#-- Is there representation bias in the data?-->

The risk of representation bias also depends on both absolute and relative amounts of training data.
On a relative basis, less than 50 percent imbalance between classes is generally considered a relatively low level of imbalance.
For each of the protected feature, the distribution pie chart is shown.


<#list jsonModel.featureMap as feature_name, feature>
    <#if graphContainer.getFeatureDistributionPieChart(feature_name)??>
    <div class="image_box">
        <div class="image_title">Feature Distribution for ${feature_name}</div>
        <img class="pie" src="${graphContainer.getFeatureDistributionPieChart(feature_name)}" />
    </div>
    </#if>

    <div>
    <#if feature.isLow() == true >
        The ratio of the sample size of the two groups is ${feature.distributionRatio()}, so the risk of underrepresentation for <b>${feature_name}</b> is low.
    <#else>
        The ratio of the sample size of the two groups is ${feature.distributionRatio()}, so there exists underrepresentation for <b>${feature_name}.</b>
    </#if>
    </div>
</#list>