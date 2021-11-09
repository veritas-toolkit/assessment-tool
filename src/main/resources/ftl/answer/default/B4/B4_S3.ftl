<#--Is the model well-calibrated?-->
<div>
In some situations we care about if the output represents real probabilities. In the reliability diagram, the x-axis
represents the predicted probabilities, which are divided to a number of bins; the y-axis represents the fraction of
    positives in each bin. When the reliability diagram is close to the diagonal, then the model is well calibrated.
    </div>

<#--jsonModel.calibration_curve 参考第二份白皮书p94 Figure 3.8-->

<#if graphContainer.getCalibrationCurveLineChart()??>
<div class="image_box">
    <div class="image_title">Calibration Curve</div>
    <img src="${graphContainer.getCalibrationCurveLineChart()}" />
</div>
    <div>
        The brier loss score is ${jsonModel.calibrationCurve.score}.
    </div>
<#else>
   Calibration curve does not apply to this project.
</#if>