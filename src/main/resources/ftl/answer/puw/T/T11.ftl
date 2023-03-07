
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

<#--summary plot or table-->
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

<h4>Local Interpretability Based on SHAP</h4>

<div>
    At a local level, each observation gets its own set of SHAP values. Shown in red are the variables that pushing the predictions higher,
    while shown in blue are the variables pushing the predictions lower. E[f(x)] is the baseline ratio for selection while f(x)
    is the sum of all SHAP values added to baseline for a particular customer.
</div>


<#-- Local interpretability plot or table -->
<#list transparency.modelList as modelInfo>
    <#assign modelId = modelInfo.id>
    <#if modelInfo.localInterpretabilityList??>
        <#list modelInfo.localInterpretabilityList as localInterpretability>
            <#assign localInterpretabilityId = localInterpretability.id>
            <#if localInterpretability.plotDisplay != true>
                <div class="table_box">
                    <div class="table_title">
                        Local interpretability for Index [${localInterpretabilityId}]
                        <br/>
                        efx: ${localInterpretability.efx}, fx: ${localInterpretability.fx}
                    </div>
                    <table id="table_local_interpretability_${modelId}_${localInterpretabilityId}" class="table_local_interpretability">
                        <thead>
                        <tr>
                            <th>Feature Name</th>
                            <th>Value</th>
                            <th>Shap</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list localInterpretability.featureInfoList as featureInfo>
                            <tr>
                                <td>${featureInfo.name}</td>
                                <td>${featureInfo.value}</td>
                                <td>${featureInfo.shap}</td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            <#else>
                <div class="image_box">
                    <div class="image_title"> Local interpretability plot for index: ${localInterpretabilityId}</div>
                    <img id="local_interpretability__${modelId}_${localInterpretabilityId}"
                         src="${graphContainer.getLocalInterpretability(modelId?c, localInterpretabilityId?c)}"
                         class="img_local_interpretability"
                         alt="Local interpretability plot" />
                </div>
            </#if>
        </#list>
    </#if>
</#list>