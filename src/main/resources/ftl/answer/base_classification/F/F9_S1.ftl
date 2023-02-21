<#--F9.1 What is the primary fairness metric for this use case?-->

<#assign fairnessInit=fairness.fairnessInit>
<#if fair_metric_name_input == 'auto'>
<div>
    To assess the fairness of ${businessScenario.name} model,our priority is to measure ${fairnessInit.fairImpact} ${fairnessInit.fairPriority!""} to the ${fairnessInit.fairConcernDisplay()}group,
    therefore we choose ${fairnessInit.fairMetricName} as primary fairness metric for the assessment.
<#else>
    The selected primary metric is ${fairnessInit.fairMetricName}.
</div>
</#if>