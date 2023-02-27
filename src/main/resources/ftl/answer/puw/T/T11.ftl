
<h4>Permutation Importance</h4>
<div>
    The permutation feature importance measures the decrease in model score when a single feature is randomly shuffled
    while keeping other features constant. A large decrease in model score indicates a relative large contribution of the feature.
    The permutation importance plot below shows the top 10 highest contributing features and their relative percentage importance in descending order.
    The most important feature is assigned the highest importance (100%) and all other variables are measured relative to the most important feature.

</div>

<#if graphContainer.permutationImportancePlot??>
    <div class="image_box">
        <img id="permutationImportancePlot" src="${graphContainer.permutationImportancePlot}" class="permutationImportancePlot" alt="Permutation Importance Plot" />
    </div>
</#if>

<h4>Partial Dependence Plot</h4>
<div>
  Partial dependence plot shows how the predicted outcome changes with the selected feature.
</div>
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

<h4>Global Interpretability Based on SHAP</h4>

<div>
    At a global level, the collective SHAP values show how much each predictor contributes, either positively or negatively, to the target variable.
    In the summary plot below, variables are ranked in descending order. Each point represents an observation; the color indicates whether the value of
    a certain feature is high(in red) or low(in blue). A negative SHAP value indicates negative impact while a positive SHAP value indicates a positive impact
    on the predicted outcome.

</div>

<#--补充summary plot图-->
<#list graphContainer.getModelIdList() as modelId>
    <div class="image_box">
        <div class="image_title"> Summary Plot </div>
        <img id="summaryPlot_${modelId}" src="${graphContainer.getSummaryPlot(modelId)}" class="summary_plot" alt="summary plot" />
    </div>
</#list>

<h4>Local Interpretability Based on SHAP</h4>

<div>
    At a local level, each observation gets its own set of SHAP values. Shown in red are the variables that pushing the predictions higher,
    while shown in blue are the variables pushing the predictions lower. E[f(x)] is the baseline ratio for selection while f(x)
    is the sum of all SHAP values added to baseline for a particular customer.
</div>

<#--补充Water Fall图-->
<#list graphContainer.waterfallPlotList() as waterfallPlot>
    <div class="image_box">
<#--        <div class="image_title"> Summary Plot </div>-->
        <img id="waterfall" src="${waterfallPlot}" class="waterfall" alt="waterfall plot" />
    </div>
</#list>




