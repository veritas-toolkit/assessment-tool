<#-- F4.1 Are there representation bias, measurement bias on labelling and data pre-processing bias in the groups identified as at risk of disadvantage in F1? -->



<ul>
<li><b>Representation bias</b> occurs when certain groups are underrepresented in a data set which causes the effectiveness of model training to be hampered.</li>
<li><b>Measurement bias</b> arises when there is systematic or non-random error in the collection of data, and can occur on input variables and target labels on which the AIDA system operates.</li>
<li><b>Pre-processing bias</b> arises during data pre-processing in model development, when an operation (e.g., missing value treatment, data cleansing, outlier treatment, encoding, scaling or data transformations for unstructured data, etc.) causes or contributes to systematic disadvantage.</li>
</ul>

<h3>Target Label Distribution</h3>

The pie chart for target label distribution is shown.

<#if graphContainer.classDistributionPieChart??>
<div class="image_box">
    <div class="image_title">Class Distribution</div>
    <img id="classDistributionPieChart" class="pie" src="${graphContainer.classDistributionPieChart}" />
</div>
</#if>

<div>
    For marketing systems, many performance measures are computed with reference to a baseline system
    that customers may still acquire the product or services through their own volition or due to pre-existing marketing.
    Generally, customers are divided into control group(C) and treatment group(T), treated customers can choose whether to respond to the intervention,
    and controlled customers can choose whether to apply and acquire the product.
    There are four types of customers:
<ul>
<li>CN - controlled non-responder</li>
<li>TN - treatment non-responder</li>
<li>CR - control responder</li>
<li>TR - treatment responder</li>
</ul>
</div>


<h3>Group Distribution</h3>

<div>
    For each of the protected attribute, the group distribution pie chart is shown.
    The risk of representation bias depends on both absolute and relative amounts of training data.
    On a relative basis, less than 50 percent imbalance between classes is generally considered a relatively low level of imbalance.
</div>

<#list fairness.featureMap as feature_name, feature>
    <#if graphContainer.getFeatureDistributionPieChart(feature_name)??>
    <div class="image_box">
        <div class="image_title">Feature Distribution for ${feature_name}</div>
        <img id="FeatureDistributionPieChart_${feature_name}" class="pie" src="${graphContainer.getFeatureDistributionPieChart(feature_name)}" />
    </div>
    </#if>

    <div>
    <#if feature.isLow() == true >
        The ratio of the sample size of the two groups is ${feature.distributionRatio()}, which is lower than 2, so the risk of underrepresentation for <b>${feature_name}</b> is low.
    <#else>
        The ratio of the sample size of the two groups is ${feature.distributionRatio()}, which is larger than 2, so there exists underrepresentation for <b>${feature_name}.</b>
    </#if>
    </div>
</#list>