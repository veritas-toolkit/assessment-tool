<#-- F2.1 What are the correct and wrong decisions that the system can make? Populating the confusion matrix may help.>


<div>
    <div class="table_box">
    <div>
    <big>
    Fairness metrics for <b>Confusion Matrix</b>
    </big>
    </div>
    <table>
        <thead>
        <tr>
            <td> </td>
            <td>Positive(Actual)</td>
            <td>Negative(Actual)</td>
        </tr>
        <tr>
            <td>Positive(Predicted)</td>
            <td> </td>
        </tr>
        <tr>
            <td>Negative(Predicted)</td>
            <td> </td>
        </tr>
        </thead>

    </table>
    </div>

There are four decisions:
<ul>
<li>TRUE POSITIVE：an applicant is approved, the customer subsequently paid back the loan</li>
<li>TRUE NEGATIVE：an applicant that the customer would not have paid back the loan is denied</li>
<li>FALSE POSITIVE：an applicant is approved, and the customer subsequently defaulted on the loan</li>
<li>FALSE NEGATIVE: an applicant that the customer would have paid back the loan is denied.</li>
</ul>

