<#-- F2.1 What are the correct and wrong decisions that the system can make? Populating the confusion matrix may help. -->

For classification problem, populating the following confusion matrix may help. Confusion matrix is a way to measure the performance
of a classification model. Each row of the matrix represents a predicted class; and each column represents an actual class.
The following confusion matrix applies to binary classification.

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

