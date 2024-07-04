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

import static com.gerritforge.gerrit.modules.virtualhost.VirtualHostConfig.ALL_PROJECTS_REGEX;
import static com.google.common.truth.Truth.assertThat;
import static com.google.gerrit.testing.GerritJUnit.assertThrows;

import com.google.gerrit.server.config.SitePaths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.junit.Before;
import org.junit.Test;

public class VirtualHostConfigTest {

  public static final String TEST_VIRTUAL_HOST = "testhost";
  private SitePaths testSitePaths;
  private FileBasedConfig virtualHostFileConfig;

  @Before
  public void setup() throws IOException {
    Path tempPath = Files.createTempDirectory("virtualhost-test-" + System.nanoTime());
    testSitePaths = new SitePaths(tempPath);
    virtualHostFileConfig =
        new FileBasedConfig(
            testSitePaths.etc_dir.resolve("virtualhost.config").toFile(), FS.DETECTED);
  }

  @Test
  public void projectsAllowsCatchAllRegExOnDefaults() throws IOException {
    setVirtualHostConfig("default", null, ALL_PROJECTS_REGEX);
    assertThat(new VirtualHostConfig(testSitePaths).getProjects(TEST_VIRTUAL_HOST))
        .isEqualTo(new String[] {ALL_PROJECTS_REGEX});
  }

  @Test
  public void projectsAllowsCatchAllRegExOnSpecificHost() throws IOException {
    setVirtualHostConfig("server", TEST_VIRTUAL_HOST, ALL_PROJECTS_REGEX);
    assertThat(new VirtualHostConfig(testSitePaths).getProjects(TEST_VIRTUAL_HOST))
        .isEqualTo(new String[] {ALL_PROJECTS_REGEX});
  }

  @Test
  public void projectsGeneralRegExOnDefaultsShouldBeForbidden() throws IOException {
    setVirtualHostConfig("default", null, "^ageneralregex.*$");
    assertThrows(
        IllegalStateException.class,
        () -> new VirtualHostConfig(testSitePaths).getProjects(TEST_VIRTUAL_HOST));
  }

  @Test
  public void projectsGeneralRegExOnSpecificHostShouldBeForbidden() throws IOException {
    setVirtualHostConfig("server", TEST_VIRTUAL_HOST, "^ageneralregex.*$");
    assertThrows(
        IllegalStateException.class,
        () -> new VirtualHostConfig(testSitePaths).getProjects(TEST_VIRTUAL_HOST));
  }

  private void setVirtualHostConfig(String section, String subsection, String projectsValue)
      throws IOException {
    virtualHostFileConfig.setString(section, subsection, "projects", projectsValue);
    virtualHostFileConfig.save();
  }
}
