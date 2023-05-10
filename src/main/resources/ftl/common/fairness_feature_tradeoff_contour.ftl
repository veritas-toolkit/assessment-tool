<#if fairness.hasTradeoff() == true>
    <#list graphContainer.getFeatureTradeoffContourMap() as feature_name, chart>
        <div class="image_box">
            <div class="image_title">Fairness Tradeoff for ${feature_name}</div>
            <img id="feature_tradeoff_contour_${feature_name}"
                 src="${graphContainer.findFeatureTradeoffContour(feature_name)}"
                 alt="Fairness Tradeoff for ${feature_name}"/>
        </div>
    </#list>
</#if>
