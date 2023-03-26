<#if graphContainer.getCorrelationHeatMapChart()??>
    <div class="image_box">
        <div class="image_title">Correlation Heatmap</div>
        <img id="correlation_heatmap"
             src="${graphContainer.getCorrelationHeatMapChart()}"
             alt="Correlation Heatmap"/>
    </div>
<#else>
    <div>Correlation matrix heatmap does not apply to this project.</div>
</#if>