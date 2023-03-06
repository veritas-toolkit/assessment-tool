/*
 * Copyright 2021 MAS Veritas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.veritas.assessment.biz.service.impl;

import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.pdfboxout.PdfBoxRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.slf4j.Slf4jLogger;
import com.openhtmltopdf.swing.NaiveUserAgent;
import com.openhtmltopdf.util.XRLog;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.ProjectReport;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.mapper.ProjectReportMapper;
import org.veritas.assessment.biz.model.ReportQuestionnaire;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ProjectReportService;
import org.veritas.assessment.biz.service.SystemService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.biz.util.Version;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.FileSystemException;
import org.veritas.assessment.common.exception.ReportGenerateException;
import org.veritas.assessment.system.config.VeritasProperties;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ProjectReportServiceImpl implements ProjectReportService {

    static {
        XRLog.setLoggerImpl(new Slf4jLogger());
    }

    private final String REPORT_FTL = "report.ftl";
    @Autowired
    SystemService systemService;
    @Autowired
    private ProjectReportMapper reportMapper;
    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private ModelArtifactService modelArtifactService;
    @Autowired
    private Configuration configuration;
    @Autowired
    private UserService userService;
    @Autowired
    private VeritasProperties veritasProperties;

    @Override
    @Transactional
    public String previewReport(Project project) throws IOException {
        QuestionnaireVersion questionnaire = questionnaireService.findLatestQuestionnaire(project.getId());
        ModelMap modelMap = modelMap(project, questionnaire);
        return generateReportHtml(modelMap);
    }

    @Override
    @Transactional
    public byte[] previewReportPdf(Project project) throws IOException {
        String html = previewReport(project);
        Document doc = Jsoup.parse(html);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        html = doc.toString();
        return generatePDF(html);
    }

    @Override
    @Transactional
    public ProjectReport createReport(User operator, Project project, String versionString, String message)
            throws ErrorParamException, IOException {
        Integer projectId = project.getId();

        Version version;

        try {
            version = new Version(versionString);
        } catch (Exception exception) {
            throw new ErrorParamException(
                    "Illegal version: " + versionString + ", should be X.Y.Z and XYZ must be integer",
                    exception);
        }

        ProjectReport latest = findLatestReport(projectId);
        if (latest != null) {
            Version latestVersion = null;
            try {
                latestVersion = new Version(latest.getVersion());
            } catch (Exception exception) {
                // Yes, do nothing
            }
            if (latestVersion != null) {
                if (version.compareTo(latestVersion) <= 0) {
                    throw new ErrorParamException(String.format(
                            "Latest version is [%s]. New version is [%s]. The version must be incremented.",
                            latestVersion, version));
                }
            }
        }


        ModelArtifact modelArtifact = modelArtifactService.findCurrent(projectId);
        QuestionnaireVersion questionnaire = questionnaireService.findLatestQuestionnaire(projectId);

        ProjectReport report = new ProjectReport();
        report.setProjectId(projectId);
        report.setCreatorUserId(operator.getId());
        report.setQuestionnaireVid(questionnaire.getVid());
        report.setModelArtifactVid(modelArtifact.getVersionId());
        report.setVersion(versionString);
        report.setMessage(message);
        report.setCreatedTime(new Date());

        ModelMap modelMap = modelMap(project, questionnaire, report);

        String html = generateReportHtml(modelMap);
        Document doc = Jsoup.parse(html);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        html = doc.toString();
        byte[] content = generatePDF(html);

        String filePath = savePdf(projectId, project.getName(), report.getVersion(), content);
        report.setPdfPath(filePath);

        reportMapper.add(report);
        questionnaireService.updateAsExported(questionnaire.getVid());

        return report;
    }

    private String savePdf(Integer projectId, String projectName, String reportVersion, byte[] content) throws IOException {
        Objects.requireNonNull(projectId, "Arg[projectId] should not be null.");
        Objects.requireNonNull(projectName, "Arg[projectName] should not be null.");
        Objects.requireNonNull(reportVersion, "Arg[reportVersion] should not be null.");
        Objects.requireNonNull(content, "Arg[content] should not be null.");
        if (content.length == 0) {
            throw new IllegalArgumentException("content is empty.");
        }

        File dir = veritasProperties.getPdfDirectory(projectId);
        final Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(StringUtils.lowerCase(projectName));
        String projectNameForPdf = matcher.replaceAll("_");
        if (StringUtils.isEmpty(projectNameForPdf)) {
            projectNameForPdf = "report";
        }
        String filename = String.format("%s_v%s.pdf", projectNameForPdf, reportVersion);
        File file = new File(dir, filename);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            IOUtils.write(content, outputStream);
        }
        return filename;
    }


    @Override
    @Transactional
    public ProjectReport findLatestReport(Integer projectId) {
        List<ProjectReport> projectReportList = findReportHistoryList(projectId);
        if (projectReportList == null || projectReportList.isEmpty()) {
            return null;
        }
        return projectReportList.get(0);
    }

    @Override
    public ProjectReport findReport(Integer projectId, Integer projectVersionId) {
        ProjectReport report = reportMapper.findPidAndVid(projectId, projectVersionId);
        if (report == null) {
            log.warn("Not found the report. project: {}, version: {}", projectId, projectVersionId);
        }
        return report;
    }

    @Override
    public byte[] loadReportPdf(ProjectReport report) throws FileSystemException {
        try {
            File file = new File(veritasProperties.getPdfDirectory(report.getProjectId()), report.getPdfPath());
            try (InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
                return IOUtils.toByteArray(stream);
            } catch (FileNotFoundException exception) {
                log.warn("Not found the file.", exception);
                throw new FileSystemException("Not found the report.");
            }
        } catch (IOException exception) {
            log.warn("Load file failed. report: {}", report, exception);
            throw new FileSystemException("");
        }
    }

    @Override
    @Transactional
    public List<ProjectReport> findReportHistoryList(Integer projectId) {
        return reportMapper.findAllByProjectId(projectId);
    }

    private ModelMap modelMap(Project project, QuestionnaireVersion questionnaire) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("project", project);
        modelMap.put("questionnaire", new ReportQuestionnaire(project, questionnaire));
        modelMap.put("versionHistoryList", versionHistoryList(project.getId(), null));

        BusinessScenarioEnum businessScenario = BusinessScenarioEnum.ofCode(project.getBusinessScenario());
        modelMap.put("businessScenario", businessScenario.getName());
        return modelMap;
    }

    private ModelMap modelMap(Project project, QuestionnaireVersion questionnaire,
                              ProjectReport current) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("project", project);
        modelMap.put("questionnaire", new ReportQuestionnaire(project, questionnaire));
        modelMap.put("versionHistoryList", versionHistoryList(project.getId(), current));

        BusinessScenarioEnum businessScenario = BusinessScenarioEnum.ofCode(project.getBusinessScenario());
        assert businessScenario != null;
        modelMap.put("businessScenario", businessScenario.getName());
        return modelMap;
    }

    private String generateReportHtml(ModelMap modelMap) throws IOException {
        try (StringWriter writer = new StringWriter()) {
            StopWatch stopWatch = StopWatch.createStarted();
            Template template = configuration.getTemplate(REPORT_FTL);
            template.process(modelMap, writer);
            String html = writer.toString();
            stopWatch.stop();
            log.info("Time of creating html: {}", stopWatch);
            return html;
        } catch (TemplateException exception) {
            log.error("Generate report html failed.", exception);
            throw new ReportGenerateException("Generate report html failed.", exception);
        } catch (IOException exception) {
            log.error("Generate report html failed.", exception);
            throw exception;
        }
    }

    // FIXME: 2021/9/9 config base document url
    public byte[] generatePDF(final String html) throws IOException {
        StopWatch stopWatch = StopWatch.createStarted();
//        byte[] bytes = generatePDF(builder -> builder.withHtmlContent(html, "/"));
        byte[] bytes = generatePDF(builder -> builder.withHtmlContent(html, Objects.requireNonNull(ProjectReportServiceImpl.class.getResource("/")).toExternalForm()));
        stopWatch.stop();
        log.info("Time of creating pdf: {}", stopWatch);
        return bytes;
//        return generatePDF(builder -> builder.withHtmlContent(html, "/ftl"));
    }

    public byte[] generatePDF(final File htmlFile) throws IOException {
        return generatePDF(builder -> builder.withFile(htmlFile));
    }

    private byte[] generatePDF(ICallableWithPdfBuilder callWithBuilder) throws IOException {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useUnicodeBidiSplitter(new ICUBidiSplitter.ICUBidiSplitterFactory());
        builder.useUnicodeBidiReorderer(new ICUBidiReorderer());
        builder.defaultTextDirection(PdfRendererBuilder.TextDirection.LTR);
        builder.usePDDocument(new PDDocument(MemoryUsageSetting.setupMixed(1000000)));

        builder.useFont(
                new FSSupplier<InputStream>() {
                    @Override
                    public InputStream supply() {
                        try {
                            return new ClassPathResource("font/RobotoSlab-VariableFont_wght.ttf").getInputStream();
                        } catch (IOException exception) {
                            throw new FileSystemException("Font file does not exist.", exception);
                        }
                    }
                },
                "Roboto Slab");
        builder.useFont(
                new FSSupplier<InputStream>() {
                    @Override
                    public InputStream supply() {
                        try {
                            return new ClassPathResource("font/Graphik-Regular.ttf").getInputStream();
                        } catch (IOException exception) {
                            throw new FileSystemException("Font file does not exist.", exception);
                        }
                    }
                },
                "Graphik");
        builder.useUriResolver(new NaiveUserAgent.DefaultUriResolver() {
            @Override
            public String resolveURI(String baseUri, String uri) {
                log.trace("baseUri: {}", baseUri);
                log.trace("uri: {}", uri);
                if (uri.startsWith("api/")) {
//                    StringUtils.removeStart()
//                    String u = "file/" + StringUtils.substringAfter(uri, "/");
                    String u = veritasProperties.getFilePath() + "/" +
                            StringUtils.removeStartIgnoreCase(uri, "api/project");
                    Path path = Paths.get(u);
                    if (Files.exists(path)) {
                        return path.toUri().toString();
                    } else {
                        log.warn("file not exist: {}", u);
                    }
                }
                if (!uri.startsWith("/")) {
                    // Classpath Resource
                    URL resource = getClass().getResource(uri);
                    if (resource != null) {
                        return resource.toString();
                    }
                    resource = getClass().getResource(baseUri + "/" + uri);
                    if (resource != null) {
                        return resource.toString();
                    }
                }
                return super.resolveURI(baseUri, uri);
            }
        });

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            builder.toStream(outputStream);
            callWithBuilder.apply(builder);

            try (PdfBoxRenderer pdfBoxRenderer = builder.buildPdfRenderer()) {
                pdfBoxRenderer.layout();
                pdfBoxRenderer.createPDF();
            }

            return outputStream.toByteArray();
        }
    }

    private List<VersionHistory> versionHistoryList(Integer projectId, ProjectReport current) {
        List<ProjectReport> projectReportList = findReportHistoryList(projectId);
        List<VersionHistory> versionHistoryList = new ArrayList<>(projectReportList.size() + 1);
        projectReportList.forEach(projectReport -> versionHistoryList.add(toVersionHistory(projectReport)));

        Collections.reverse(versionHistoryList);

        if (current != null) {
            versionHistoryList.add(toVersionHistory(current));
        } else {
            ProjectReport preview = new ProjectReport();
            preview.setVersion("-");
            preview.setMessage("Preview");
            preview.setCreatedTime(new Date());
            versionHistoryList.add(toVersionHistory(preview));
        }
        return Collections.unmodifiableList(versionHistoryList);
    }

    private VersionHistory toVersionHistory(ProjectReport projectReport) {
        VersionHistory versionHistory = new VersionHistory();
        versionHistory.setVersion(projectReport.getVersion());
        versionHistory.setMessage(projectReport.getMessage());

        final String DATE_FORMAT = "yyyy-MM-dd";
        Date date = projectReport.getCreatedTime();
        if (date != null) {
            versionHistory.setCreatedDate(DateFormatUtils.format(date, DATE_FORMAT));
        }

        if (projectReport.getCreatorUserId() != null) {
            User user = userService.findUserById(projectReport.getCreatorUserId());
            if (user != null) {
                versionHistory.setCreatedUser(user.getFullName());
            }
        }
        return versionHistory;
    }

    @FunctionalInterface
    interface ICallableWithPdfBuilder {
        void apply(PdfRendererBuilder builder);
    }

    @Data
    public static class VersionHistory {
        private String version;
        private String message;
        private String createdDate;
        private String createdUser;
    }

}
