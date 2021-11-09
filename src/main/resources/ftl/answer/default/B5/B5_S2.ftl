<#--What is the relation between the score threshold and primary performance indicator?-->

<#--jsonModel.perf_dynamic 参考p96 Figure3.10-->

<div>
    The following line chart illustrates how selection rate and ${jsonModel.fairnessInit.perfMetricName}
    change with risk threshold.
</div>

<#if graphContainer.getPerformanceLineChart()??>
<div class="image_box">
    <div class="image_title">Performance</div>
    <img src="${graphContainer.getPerformanceLineChart()}" />
</div>
<#else>
   Performance dynamics does not apply to this project.
</#if>