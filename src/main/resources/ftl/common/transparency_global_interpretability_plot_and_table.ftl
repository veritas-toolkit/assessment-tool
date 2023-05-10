<#list transparency.modelList as modelInfo>
    <#assign modelId = modelInfo.id>
    <#if modelInfo.summaryPlotFeatureList??>
        <div class="table_box">
            <div class="table_title">Global Interpretability Values for Model[${modelId}] </div>
            <table id="table-summary-plot-${modelId}" class="table-summary-plot">
                <thead>
                <tr>
                    <th>Feature_name</th>
                    <th>Mean|shap|</th>
                </tr>
                </thead>
                <tbody>
                <#list modelInfo.summaryPlotFeatureList as summaryPlotFeature>
                    <tr>
                        <td>${summaryPlotFeature.feature}</td>
                        <td>${summaryPlotFeature.meanOfAbsoluteValueOfShap}</td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    <#elseif modelInfo.summaryPlotBase64??>
        <div class="image_box">
            <div class="image_title"> Summary Plot for Model[${modelId}] </div>
            <img id="img-summary-plot-${modelId}" src="${graphContainer.getSummaryPlot(modelId?c)}" class="img-summary_plot" alt="summary plot" />
        </div>
    </#if>
</#list>