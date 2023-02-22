<!DOCTYPE html PUBLIC "-//OPENHTMLTOPDF//DOC XHTML Character Entities Only 1.0//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="UTF-8"/>
<style type="text/css">
@page {
    size: A4;
    margin: 2.5cm 1.8cm 2cm 1.9cm;
}

@page cover {
    margin-left: 0;
    margin-top: 0;
    background: no-repeat url("ftl/image/cover-background.jpeg");
    background-size: 100% 100%
}

@page toc {
    margin-left: 0;
    margin-top: 0;
}

@page report_content {

    background: no-repeat url("ftl/image/favicon.png");
    background-size: 34pt 20pt;
    background-position: 50% 98%;
    @top-left {
        content: element(header);
    }

    @bottom-left {
        content: element(footer);
    }
    @bottom-center {
        font-size: 6pt;
    }
    @bottom-right {
        content: element(page_counter);
    }
}

@media screen {
    #header {
        display: none;
    }
    #footer {
        display: none;
    }
    #footer_right {
        display: none;
    }
    #page_counter {
        display: none;
    }
    #toc {
        display: none;
    }
    .page_only {
        display: none;
    }
}

#header {
    position: running(header);
    border-bottom: 1px solid black;
    text-align: left;
    font-size: 6pt;
}
#footer {
    position: running(footer);
    border-top: 1px solid black;
    text-align: left;
    font-size: 6pt;
}
#footer_right {
    position: running(footer_right);
    border-top: 1px solid black;
    text-align: left;
    font-size: 6pt;
}

#page_counter {
    position: running(page_counter);
    border-top: 1px solid black;
    text-align: right;
    font-size: 6pt;
}

#page_num:before {
    content: counter(page);
}

.reset_page_count {
    page-break-before: always;
    -fs-page-sequence: start;
}



body {
    font-family: 'Graphik', 'Times New Roman', serif;
    font-size: 10.5pt;
    margin: 0;
}
<#-- ******************************************* cover *************************************** -->

#cover {
    page: cover;
}

#cover_title {
    left: 0;
    top: 0;
    width: 90%;
    height: 10%;
    background-color: rgb(215,  229, 237);
    font-family: 'Roboto Slab', serif;
    padding: 2em;
    border-bottom-right-radius: 2em;
}

#cover_logo_box {
    width: 170pt;
    height: 100pt;
    background: no-repeat url("ftl/image/favicon.png");
    background-size: 100% 100%;
    background-position: 4pt;
    padding-bottom: 0pt;
    margin-bottom: 0pt;
}

#cover_main_title {
    margin-top: -10pt;
    padding-top: 0pt;
    padding-left: 20pt;
    font-size: 32pt;
    color: rgb(2, 18, 77);
}
#cover_sub_title {
    font-size: 18pt;
    text-align: center;
    color: rgb(255, 255, 235);
    background-color: rgb(157, 133, 78);

    border-radius: 20pt / 20pt;
    padding: 10pt;
    margin-top: 20pt;
    margin-left: 20pt;
    margin-right: 20pt;
}

#cover_image {
}

#pagenum:before {
    content: counter(page);
}

#pagecount:before {
    content: counter(pages);
}

#introduction {
    page-break-after: avoid;
}

h1, h2, h3, h4 {
    -fs-page-break-min-height: 4cm;
}

h1 {
    font-size: 2em;
    text-align: center;
    margin-bottom: 1em;
}

<#-- ******************************************* revision history ****************************** -->
#revision_history {
    page-break-before: always;
    page-break-after: always;
}
#revision_history_title {
    width: auto;
    margin-left: 2rem;
    margin-bottom: 8pt;
    text-decoration: underline;
}
#revision_history table {
    margin-left: auto;
    margin-right: auto;
    width: 90%;
    page-break-inside: avoid;
    border-collapse: collapse;
    border: 1px solid #DDDDDD;
    <#--
    border-color: rgb(20, 150, 150);
    -->
}
#revision_history td, #revision_history th {
    /*
    */
    padding: 0.5rem;
    border: 1px solid #DDDDDD;
    <#--
    border: 1px solid rgb(250, 230, 210);
    -->
}

#revision_history tr:nth-child(even){
    <#--
    background-color: #f2f2f2;
    -->
}

#revision_history th {
    padding-top: 10px;
    padding-bottom: 10px;
    text-align: left;
    background-color: rgb(2, 18, 77);
    color: white;
}
#revision_history td {
    <#--
    border-bottom: 1px dashed rgb(250, 230, 210);
    -->
}

<#-- ******************************************* table of contents ****************************** -->
#toc {
    page: toc;
    page-break-before: always;
    page-break-after: always;
}
#toc a {
    text-decoration: none;
}
#toc_title {
    left: 0;
    top: 0;
    font: 40px 'Courier New' bold;
    width: 50%;
    padding: 60px 60px 50px 80px;
    padding-left: 80px;
    text-align: left;
    /*
    margin-bottom: 1em;
    */
    margin-bottom: 30px;
    background: rgb(215, 229,237);
    color: rgb(2, 18, 77);
    border-bottom-right-radius: 32px;
}
.toc_first_level {
    font-size: 1em;
    font-weight: bold;
    margin-top: 1.25em;
    margin-left: 80px;
}
.toc_first_level .toc_principle {
    color: rgb(161, 131, 68);
}
.toc_first_level a {
    color: rgb(2, 18, 77);
}
.toc_first_level a::after {
    content: leader(' .') target-counter(attr(href), page, decimal)
}
.toc_second_level {
    font-size: 0.75em;
    margin-top: 0.75em;
    margin-bottom: 0.75em;
    padding-left: 2.5em;
}
.toc_second_level a {
    color: black;
}
.toc_second_level a::after {
    content: leader('.') target-counter(attr(href), page, decimal)
}


<#-- ******************************************* content ****************************** -->

/** table */
.answer .table_box {
    margin-top: 10px;
    text-align: center;
    page-break-inside: avoid;
}

.answer table {
    margin-left: auto;
    margin-right: auto;
    margin-top: 10px;
    margin-bottom: 10px;
    page-break-inside: avoid;
    border-spacing: 0px;
}
kk
.answer table thead {
    color: white;
    background: rgb(2, 18, 77);
}
.answer th {
    padding: 5px 15px;
    border-top: 2pt solid rgb(2, 18, 77);
    border-bottom: 1pt solid rgb(2, 18, 77);
}
.answer td {
    border-bottom: 0.2pt dashed rgb(2, 18, 77);
    padding: 5px 15px;
    text-align: center;
}
/*
.answer table tbody tr td:first-child {
    color: white;
    background: grey;
}
*/

.answer table tbody tr.perf_metric_row {
    color: red;
}
.answer table tbody tr.fair_metric_row {
    color: red;
}

.answer table td.metric_name {
    text-align: left;
}
.answer table td.metric_value {
    text-align: right;
}

/** project title */

#project_name {
    font-size: 24px;
    text-align:center;
    margin: 20px;
}
#project_description {
    margin: 10px;
    padding: 5px;
    text-indent: 20px;
}

#report_content {
    page: report_content;
}
.chapter {
    page-break-before: always;
}

#introduction {
    font: 35px bold;
    margin-bottom: 0.5rem;
}
#introduction_content {
    font-size: 1rem;
    text-indent: 1.5rem;
}

#principle_G {
    margin-top: 2.0em;
    page-break-before: avoid;
}
.principle_title {
    font: 35px bold;
    margin-top: 1rem;
    margin-bottom: 1rem;
    font-family: 'Roboto Slab', serif;
}

/** main question title */
.main_question {
    color: black;
    background-color: rgb(215, 229, 237);
    page-break-inside: avoid;
    min-height: 4em;

    /*
    border-style: dashed;
    border-color: red;
    border-width: thin;
    */
}
.main_question_title_box {
    color: white;
    background-color: rgb(161, 131, 68);
    position: relative;

    padding: 0.75em;
    margin-left: 0.75em;
    margin-right: 0.75em;
    margin-top: 1.0em;
    #width: 1em;
    #height: 1em;
    border-radius: 50%;
    /*
    border-style: dashed;
    border-color: white;
    border-width: thin;
    */
    float: left;
}
.main_question_title {
    /*position: absolute;*/
    /*top: 50%;*/
    /*left: 50%;*/
    /*transform: translate(-50%, -50%);*/
    line-height: 1em;
    text-align: center;
}


.main_question_content {
    margin-top: 1.75em;
    margin-left: 3.25em;
    padding: 1.25em 0.25em 0.75em 0.25em;
    color: rgb(27, 69, 122);
    /*
    background-color: gray;
    border-style: dashed;
    border-color: black;
    border-width: thin;
    */
}
<#-- -------------------------------------------------------------------------- -->
.main_question_t {
    margin-top: 20pt;
    background-color: rgb(215, 229, 237);
    page-break-inside: avoid;
    border-radius: 5pt;
}
.main_question_title_box_t {
    width: 5%;
    font-size: 18px;
}

.main_question_title_t {
    color: white;
    background-color: rgb(161, 131, 68);
    padding: 0.25em;
    padding-left: 0.5em;
    margin-left: 0.75em;
    /*#margin-right: 0.75em;*/
    /*#margin-top: 1.0em;*/
    width: 1.5em;
    height: 1.5em;
    border-radius: 50%;
    font-size: 12pt;
}


.main_question_content_t {
    margin-top: 1.0em;
    margin-left: 16pt;
    padding: 1.25em 0.25em 1.25em 0.25em;
    color: rgb(27, 69, 122);
    font-size: 12pt;
}

.main_answer {
    color: black;
    margin: 10px;
    text-indent: 1.5em;
}

.sub_question {
    /*color: goldenrod;*/
    color: rgb(27, 69, 122);
    margin-top: 10px;
}

.sub_answer {
    color: black;
    margin: 10px;
    text-indent: 20px;
}
.none_answer {
    font-style: italic;
    color: gray;
    margin: 10px;
    text-indent: 20px;
}

.image_box {
    margin-top: 20px;
    margin-bottom: 20px;
    page-break-inside: avoid;
    /*
    display: inline-block;
    -fs-page-break-min-height: 4cm;
    break-inside: avoid-page;
    display: flex;
    */
}
.image_title {
    margin-top: auto;
    text-align: center;
}
img {
    width: 80%;
    display: block;
    margin-top: auto;
    margin-left: auto;
    margin-right: auto;
}

img.pie {
    width: 40%;
}



</style>
<title>FEAT Assessment Report</title>

<bookmarks>
    <bookmark name="Cover" href="#cover"></bookmark>
    <bookmark name="Revision History" href="#revision_history"></bookmark>
    <bookmark name="Table of Content" href="#toc"></bookmark>
    <bookmark name="Introduction" href="#introduction"/>

    <#list project.principles() as principle>
    <bookmark name="${principle.fullname} Principle" href="#principle_${principle.shortName}">
        <#list questionnaire.steps(principle) as step>
        <bookmark name="Step ${step.stepSerial}: ${step.description}" href="#principle_${principle.shortName}_step_${step.stepSerial}">
            <#list questionnaire.findQuestionList(principle, step) as question>
            <bookmark name="${question.serial()} ${question.content}" href="#${question.serial()}">
            </bookmark>
            </#list>
        </bookmark>
        </#list>
    </bookmark>
    </#list>
</bookmarks>
</head>

<body>
<div id="header">
	FEAT Assessment Report
</div>
<div id="footer">
    Generated by Veritas Toolkit
</div>
<div id="page_counter">
	<span id="page_num"></span>
</div>
<div id="footer_right"> &nbsp; </div>

<div id="cover" class="page_only">
    <div id="cover_title">
        <div id="cover_logo_box">
        </div>
        <div id="cover_main_title">
            FEAT Assessment Report
        </div>
        <div id="cover_sub_title">
            ${project.name} | ${businessScenario}
        </div>
    </div>
    <div>
    </div>
</div>

<div id="revision_history">
<div id="revision_history_title">Revision History</div>
<table>
    <thead>
        <tr>
            <th>Date</th>
            <th>Version</th>
            <th style="width: 50%">Description</th>
            <th>Author</th>
        </tr>
    </thead>
    <tbody>
        <#list versionHistoryList as versionHistory>
        <tr>
            <td>${versionHistory.createdDate!""}</td>
            <td>${versionHistory.version!""}</td>
            <td>${versionHistory.message!""}</td>
            <td>${versionHistory.createdUser!""}</td>
        </tr>
        </#list>
    </tbody>
</table>
</div>

<div id="toc">
    <div id="toc_title">Table of Contents</div>

    <div class="toc_first_level"><a href="#introduction">Introduction</a></div>

    <#list questionnaire.principles() as principle>
        <div class="toc_first_level">
            <a href="#principle_${principle.shortName}">
                <span class="toc_principle">${principle.fullname} Principle</span>
            </a>
            <#list questionnaire.steps(principle) as step>
                <#list questionnaire.findQuestionList(principle, step) as question>
                    <div class="toc_second_level">
                        <a href="#${question.serial()}">
                            ${question.serial()}. ${question.content}
                        </a>
                    </div>
                </#list>

            </#list>
        </div>

    </#list>

</div>


<div class="page_only reset_page_count"></div>

<div id="report_content">

<div id="introduction" class="chapter">
<div id="introduction">Introduction</div>
<div id="introduction_content">
   ${project.description}
</div>

</div>

<#list questionnaire.principles() as principle>
    <div class="question_principle chapter" id="principle_${principle.shortName}">
        <div class="principle_title" id="${principle.shortName}">
            ${principle.fullname} Principle
        </div>
        <#list questionnaire.steps(principle) as step>
            <div id="principle_${principle.shortName}_step_${step.stepSerial}" class="step_title">
                Step ${step.stepSerial}: ${step.description}
            </div>
            <div>
                <#list questionnaire.findQuestionList(principle, step) as question>
<#--                    <div class="main_question_t">-->
<#--                        <a href="#${question.serial()}">-->
<#--                            ${question.serial()}. ${question.content}-->
<#--                        </a>-->
<#--                    </div>-->
                    <div class="question" id="${question.serial()}">

                        <table class="main_question_t" width="100%">
                            <head>
                                <th class="main_question_title_box_t">
                                    <div class="main_question_title_t">${question.serial()}</div>
                                </th>
                                <th class="main_question_content_t">${question.content}</th>
                            </head>
                        </table>

                        <#if (question.answer)??>
                            <div class="answer main_answer">
                                ${question.answer}
                            </div>
                        </#if>

                        <#list question.subQuestionList as sub_question>
                            <div class="sub_question">
                                ${sub_question.content}
                            </div>

                            <#if (sub_question.answer)??>
                                <div class="answer sub_answer">
                                    ${sub_question.answer}
                                </div>
                            <#else>
                                <div class="none_answer">No specific considerations.</div>
                            </#if>
                        </#list>
                        <#-- if main question does not have answer and subs-->
                        <#if question.hasAnswer() == false && question.hasSubs() == false>
                            <div class="none_answer">No specific considerations.</div>
                        </#if>
                    </div>
                </#list>
            </div>
        </#list>
    </div>
</#list>
</div>

</body>


</html>