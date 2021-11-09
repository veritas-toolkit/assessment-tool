<#--What are the other potential sources of bias in the data? -->
<div>
    In the case of credit scoring, ground truth labels of default/resolve are only available
    for customers that were approved for a loan. There are no ground truth labels for customers that were declined
    In the absence of true labels, we can create hypothetical labels for those whose ground truth labels are missing.\
    The process is called <b>reject inference</b>. The model for creating labels may be biased, which may influence
    the fairness of credit scoring model.
</div>

The data for the following tables are only available when some form of reject inference is performed.


<#list specialParameter.applicantMap as feature_name, applicant>

<div>
Data for <b>${feature_name}</b>
</div>
<div class="table_box">
    <table>
    <thead>
        <tr>
            <th>System Variables</th>
            <th>Privilege group</th>
            <th>Unprivilege group</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Number of applicant</td>
            <td>${applicant.numberList[0]}</td>
            <td>${applicant.numberList[1]}</td>
        </tr>
        <tr>
            <td>Base Default Rate</td>
            <td>${applicant.rateList[0]}</td>
            <td>${applicant.rateList[1]}</td>
        </tr>
    </tbody>
    </table>
</div>
<#sep><br/>
</#list>
