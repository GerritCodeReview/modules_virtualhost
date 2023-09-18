// Copyright (C) 2023 GerritForge Ltd.
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

import static com.gerritforge.gerrit.modules.virtualhost.VirtualHostFilter.FORWARDED_HOST_HEADER;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import com.google.gerrit.common.Nullable;

public class VirutalHostFilterTest {
  private static final String DOM0_HOST = "review.gerrithub.io";
  private static final String FORWARDED_HOST = "eclipse.gerrithub.io";
  private static final String CLOUD_HOST = "cloud-99.provider.com";

  @Test
  public void getServerNameFromForwardedHost() {
    HttpServletRequest requestMock = mockHttpServletRequest(CLOUD_HOST, FORWARDED_HOST);

    String actual = VirtualHostFilter.getServerNameFromRequest(requestMock);

    assertThat(actual).isEqualTo(FORWARDED_HOST);
  }

  @Test
  public void getServerNameFromRequest() {
    HttpServletRequest requestMock = mockHttpServletRequest(DOM0_HOST);

    String actual = VirtualHostFilter.getServerNameFromRequest(requestMock);

    assertThat(actual).isEqualTo(DOM0_HOST);
  }

  private HttpServletRequest mockHttpServletRequest(String serverName) {
    return mockHttpServletRequest(serverName, null);
  }

  private HttpServletRequest mockHttpServletRequest(
      String serverName, @Nullable String forwardedHost) {
    HttpServletRequest mock = mock(HttpServletRequest.class);

    when(mock.getHeader(FORWARDED_HOST_HEADER)).thenReturn(forwardedHost);
    when(mock.getServerName()).thenReturn(serverName);

    return mock;
  }
}
