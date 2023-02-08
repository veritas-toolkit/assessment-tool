<#--What is the confusion matrix for the model?-->

<#-- 最新一版的json文件里 用weighted_confusion_matrix画热力图，参考第二份白皮书p94 Figure 3.7-->


<div>
    The number of true positives (correct acceptances), true negatives (correct declines), false positives (incorrect
    acceptances) and false negatives (incorrect declines) are summarized in the heatmap.
    in the heatmap.
</div>


<#if graphContainer.getWeightedConfusionHeatMapChart()??>
<div class="image_box">
    <div class="image_title">Weighted Confusion Matrix Heatmap</div>
    <img src="${graphContainer.getWeightedConfusionHeatMapChart()}" />
</div>
<#else>
   Weighted confusion matrix does not apply to this project.
</#if>

