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

import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.permissions.DefaultPermissionBackend;
import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QueryChangesFilterTest extends AbstractVirtualHostTest {
  private static final String TEST_PROJECT1 = "testproject-1";
  private static final String TEST_PROJECT2 = "testproject-2";

  @Mock private CurrentUser currentUserMock;

  @Mock private DefaultPermissionBackend permissionBackendMock;

  @Test
  public void shouldBeNullWithEmtpyConfig() {
    assertThat(newWithVirtualHostUser().filterQueryChanges()).isNull();
  }

  @Test
  public void shouldBeNullWithDefaultCatchAllConfig() throws IOException {
    setVirtualHostConfig("default", null, ALL_PROJECTS_REGEX);
    assertThat(newWithVirtualHostUser().filterQueryChanges()).isNull();
  }

  @Test
  public void shouldBeNullWithSpecificCatchAllConfig() throws IOException {
    setVirtualHostConfig("server", TEST_VIRTUAL_HOST, ALL_PROJECTS_REGEX);
    CurrentServerName.set(TEST_VIRTUAL_HOST);
    assertThat(newWithVirtualHostUser().filterQueryChanges()).isNull();
  }

  @Test
  public void shouldBeSingleProjectConditionWhenFilteringBySingleProjectName() throws IOException {
    setVirtualHostConfig("server", TEST_VIRTUAL_HOST, TEST_PROJECT1);
    CurrentServerName.set(TEST_VIRTUAL_HOST);
    assertThat(newWithVirtualHostUser().filterQueryChanges())
        .isEqualTo("(project:" + TEST_PROJECT1 + ")");
  }

  @Test
  public void shouldBeSingleProjectConditionWhenFilteringByMultipleProjectNames()
      throws IOException {
    setVirtualHostConfig("server", TEST_VIRTUAL_HOST, List.of(TEST_PROJECT1, TEST_PROJECT2));
    CurrentServerName.set(TEST_VIRTUAL_HOST);
    assertThat(newWithVirtualHostUser().filterQueryChanges())
        .isEqualTo("(project:" + TEST_PROJECT1 + " OR project:" + TEST_PROJECT2 + ")");
  }

  @Test
  public void shouldBeSingleProjectConditionWhenFilteringByProjectNameWildcard()
      throws IOException {
    setVirtualHostConfig("server", TEST_VIRTUAL_HOST, TEST_PROJECT1 + "*");
    CurrentServerName.set(TEST_VIRTUAL_HOST);
    assertThat(newWithVirtualHostUser().filterQueryChanges())
        .isEqualTo("(projects:" + TEST_PROJECT1 + ")");
  }

  @Test
  public void shouldBeNullWhenFilteringBySingleProjectNameDoesNotMatchHost() throws IOException {
    setVirtualHostConfig("server", TEST_VIRTUAL_HOST, TEST_PROJECT1);
    CurrentServerName.set(TEST_VIRTUAL_HOST + "_unmatched");
    WithVirtualHostUser virtualHostPermission = newWithVirtualHostUser();
    assertThat(virtualHostPermission.filterQueryChanges()).isNull();
  }

  private WithVirtualHostUser newWithVirtualHostUser() {
    return new WithVirtualHostUser(
        new VirtualHostConfig(testSitePaths), permissionBackendMock, currentUserMock);
  }
}
