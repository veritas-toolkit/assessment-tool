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

package org.veritas.assessment.biz.service;

import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModel;
import org.veritas.assessment.common.exception.FileSystemException;

import java.io.IOException;

public interface ModelArtifactService {

    JsonModel parser(String json);

    ModelArtifact create(Integer projectId, String json, String filename);

    void saveJsonFile(ModelArtifact modelArtifact) throws FileSystemException;

    void upload(ModelArtifact modelArtifact);

    ModelArtifact findCurrent(Integer projectId);

    ModelArtifact findByVersionId(Integer versionId);

    void loadContent(ModelArtifact modelArtifact) throws IOException;

    Object findPlotData(ModelArtifact modelArtifact, String imgId, String imgClass, String imgSrc);


}
