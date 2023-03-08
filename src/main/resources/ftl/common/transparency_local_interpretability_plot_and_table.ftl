<#list transparency.modelList as modelInfo>
    <#assign modelId = modelInfo.id>
    <#if modelInfo.localInterpretabilityList??>
        <div>Local interpretability for model: ${modelId}</div>
        <#list modelInfo.localInterpretabilityList as localInterpretability>
            <#assign localInterpretabilityId = localInterpretability.id>
            <#if localInterpretability.plotDisplay != true>
                <div class="table_box">
                    <div class="table_title">
                        Local interpretability for Index [${localInterpretabilityId}]
                        <br/>
                        efx: ${localInterpretability.efx}, fx: ${localInterpretability.fx}
                    </div>
                    <table id="table_local_interpretability_${modelId}_${localInterpretabilityId}"
                           class="table_local_interpretability">
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
                    <img id="local_interpretability_${modelId}_${localInterpretabilityId}"
                         src="${graphContainer.getLocalInterpretability(modelId?c, localInterpretabilityId?c)}"
                         class="img_local_interpretability"
                         alt="Local interpretability plot"/>
                </div>
            </#if>
        </#list>
    </#if>
</#list>