package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuestionnaireVersion {
    @TableId(type = IdType.AUTO)
    private int vid;

    private int projectId;

    private Integer modelArtifactVid;

    private Integer creatorUserId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;

    private String message;

    @TableField(exist = false)
    private List<QuestionnaireVersionStructure> structureList;

    @TableField(exist = false)
    private List<QuestionMeta> questionMetaList;

    @TableField(exist = false)
    private List<QuestionVersion> questionVersionList;


    public void addContent(List<QuestionnaireVersionStructure> structureList,
                    List<QuestionMeta> questionMetaList,
                    List<QuestionVersion> questionVersionList) {
        this.structureList = structureList.stream().sorted().collect(Collectors.toList());
        this.questionMetaList = questionMetaList.stream().sorted().collect(Collectors.toList());
        this.questionVersionList = questionVersionList.stream().sorted().collect(Collectors.toList());
        for (QuestionnaireVersionStructure structure : structureList) {
            for(QuestionVersion questionVersion : questionVersionList) {
                if (structure.getQuestionVid() == questionVersion.getVid()) {
                    structure.setQuestionVersion(questionVersion);
                    for (QuestionMeta questionMeta : questionMetaList) {
                        if (questionVersion.getQuestionId() == questionMeta.getId()) {
                            questionVersion.setQuestionMeta(questionMeta);
                        }
                    }
                }
            }
        }

    }

    // 获取所有主问题 QV

    List<QuestionVersion> getMainQuestionList() {
        if (questionVersionList == null) {
            return Collections.emptyList();
        }
        return questionVersionList.stream()
                .filter(QuestionVersion::isMain)
                .sorted()
                .collect(Collectors.toList());
    }



}
