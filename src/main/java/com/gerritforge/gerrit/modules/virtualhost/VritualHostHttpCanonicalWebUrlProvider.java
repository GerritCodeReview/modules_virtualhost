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

import org.eclipse.jgit.lib.Config;

import com.google.gerrit.httpd.HttpCanonicalWebUrlProvider;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;

public class VritualHostHttpCanonicalWebUrlProvider extends HttpCanonicalWebUrlProvider {
  private final String protocol;

  @Inject
  VritualHostHttpCanonicalWebUrlProvider(@GerritServerConfig Config config) {
    super(config);
    protocol = super.get().startsWith("https://") ? "https://" : "http://";
  }

  @Override
  public String get() {
    return CurrentServerName.get()
        .map(serverName -> appendTrailingSlash(protocol + serverName))
        .orElse(super.get());
  }

  private String appendTrailingSlash(String url) {
    if (url.endsWith("/")) {
      return url;
    }

    return url + "/";
  }
}
