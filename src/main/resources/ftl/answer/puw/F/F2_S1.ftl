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
        <tr style="color:#ffffff">
            <td> </td>
            <td>Positive(Actual)</td>
            <td>Negative(Actual)</td>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>Positive(Predicted)</td>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td>Negative(Predicted)</td>
            <td></td>
            <td></td>
        </tr>
        </tbody>
    </table>
</div>

There are four decisions:
<ul>
<li>correct classification of eligible risk: insurer offers a good-risk customer auto approved life insurance;</li>
<li>correct classification of non-eligible risk: insurer does not offer bad-risk customer auto approved life insurance;</li>
<li>mis-classification of eligible risk: insurer does not offer good-risk customer auto approved life insurance;</li>
<li>mis-classification of non-eligible risk: insurer offers a bad-risk customer auto approved life insurance.</li>
</ul>

