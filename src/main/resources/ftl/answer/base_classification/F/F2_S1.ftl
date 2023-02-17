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
<li>TRUE POSITIVE：the real value of the sample target variable is positive, and the predicted value of the classification is also positive</li>
<li>TRUE NEGATIVE：the real value of the sample target variable is negative, and the predicted value of the classification is also negative</li>
<li>FALSE POSITIVE：the real value of the sample target variable is negative, but the predicted target value of the classification is positive</li>
<li>FALSE NEGATIVE: the real value of the sample target variable is positive, but the predicted target value of the classification is negative</li>


</ul>

