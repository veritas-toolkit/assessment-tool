<#--How are the features correlated with each other?-->

Some of the non-personal attributes may act as proxies for personal attributes. So we would like to check if there
are strong correlations for the features. The correlation matrix heatmap (if applicable) of top 20 most important features are shown
as follows.

<#-- fairnessInit.corr_values 画相关性矩阵热力图-->



<#if graphContainer.getCorrelationHeatMapChart()??>
<div class="image_box">
    <div class="image_title">Correlation Heatmap</div>
    <img src="${graphContainer.getCorrelationHeatMapChart()}" />
</div>
<#else>
   <div>Correlation matrix heatmap does not apply to this project.</div>
</#if>