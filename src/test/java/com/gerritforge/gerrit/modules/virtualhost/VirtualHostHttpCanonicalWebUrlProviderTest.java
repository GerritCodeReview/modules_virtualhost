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

import static com.google.common.truth.Truth.assertThat;

import java.util.Optional;

import org.junit.Test;

public class VirtualHostHttpCanonicalWebUrlProviderTest {
  @Test
  public void getWebProtocol() {
    assertThat(VirtualHostHttpCanonicalWebUrlProvider.getWebProtocol("")).isEqualTo("http://");
    assertThat(VirtualHostHttpCanonicalWebUrlProvider.getWebProtocol("https://localhost:8080/"))
        .isEqualTo("https://");
    assertThat(VirtualHostHttpCanonicalWebUrlProvider.getWebProtocol("http://localhost/"))
        .isEqualTo("http://");
  }

  @Test
  public void buildFromServerName() {
    String actual =
        VirtualHostHttpCanonicalWebUrlProvider.getVirtualHostHttpCanonicalWebUrl(
            "http://", Optional.of("gerrithub.io"), () -> "failure.com");

    assertThat(actual).isEqualTo("http://gerrithub.io/");
  }

  @Test
  public void fallbackToHttpCanonicalUrl() {
    String actual =
        VirtualHostHttpCanonicalWebUrlProvider.getVirtualHostHttpCanonicalWebUrl(
            "https://", Optional.empty(), () -> "https://dom0.com/");

    assertThat(actual).isEqualTo("https://dom0.com/");
  }
}
