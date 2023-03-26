<#list graphContainer.getModelIdList() as modelId>
    <#list graphContainer.getPartialDependenceFeatureList(modelId) as feature>
        <div class="image_box partial_dependence_plot_box">
            <div class="image_title">PDP for ${feature}</div>
            <img id="PartialDependence_${modelId}_${feature}" class="partial_dependence_plot"
                 src="${graphContainer.getPartialDependencePlot(modelId, feature)}"
                 alt="partial dependence plot"/>
        </div>
    </#list>
</#list>