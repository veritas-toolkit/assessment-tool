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
<li>correct classification of eligible risk: insurer offers a good-risk customer auto approved life insurance;</li>
<li>correct classification of non-eligible risk: insurer does not offer bad-risk customer auto approved life insurance;</li>
<li>mis-classification of eligible risk: insurer does not offer good-risk customer auto approved life insurance;</li>
<li>mis-classification of non-eligible risk: insurer offers a bad-risk customer auto approved life insurance.</li>
</ul>

