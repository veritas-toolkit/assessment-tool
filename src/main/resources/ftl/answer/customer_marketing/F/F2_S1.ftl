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
<li>correct classification of customers who will respond to a call and acquire the product that they otherwise would have not acquired:
the FSI calls them for marketing unsecured loans;</li>
<li>correct classification of customers who are not influenced by a call whether they make a decision to buy or not, and who negatively respond to a call and do not acquire the product that they would have otherwise:
the FSI does not call them for marketing unsecured loans;</li>
<li>mis-classification of customers who will respond to a call and acquire the product that they otherwise would have not acquired:
the FSI does not call them for marketing unsecured loans;</li>
<li>mis-classification of customers who are not influenced by a call whether they make a decision to buy or not, and who negatively respond to a call and do not acquire the product that they would have otherwise:
the FSI calls them for marketing unsecured loans.</li>
</ul>

