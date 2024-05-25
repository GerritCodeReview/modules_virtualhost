// Copyright (C) 2024 GerritForge Ltd.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gerritforge.gerrit.modules.virtualhost;

import com.google.gerrit.server.config.SitePaths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.junit.Before;
import org.junit.Ignore;

@Ignore
public abstract class AbstractVirtualHostTest {
  public static final String TEST_VIRTUAL_HOST = "testhost";
  protected SitePaths testSitePaths;
  protected FileBasedConfig virtualHostFileConfig;

  @Before
  public void setup() throws IOException {
    Path tempPath = Files.createTempDirectory("virtualhost-test-" + System.nanoTime());
    testSitePaths = new SitePaths(tempPath);
    virtualHostFileConfig =
        new FileBasedConfig(
            testSitePaths.etc_dir.resolve("virtualhost.config").toFile(), FS.DETECTED);
  }

  protected void setVirtualHostConfig(String section, String subsection, String projectsValue)
      throws IOException {
    setVirtualHostConfig(section, subsection, List.of(projectsValue));
  }

  protected void setVirtualHostConfig(
      String section, String subsection, List<String> projectsValues) throws IOException {
    virtualHostFileConfig.setStringList(section, subsection, "projects", projectsValues);
    virtualHostFileConfig.save();
  }
}
