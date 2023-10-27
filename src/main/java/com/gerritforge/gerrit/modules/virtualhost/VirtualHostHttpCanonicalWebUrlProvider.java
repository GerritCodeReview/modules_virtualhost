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

import com.google.common.annotations.VisibleForTesting;
import com.google.gerrit.httpd.HttpCanonicalWebUrlProvider;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;
import java.net.URI;
import java.util.Optional;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jgit.lib.Config;

public class VirtualHostHttpCanonicalWebUrlProvider extends HttpCanonicalWebUrlProvider {
  private final URI serverUri;

  @Inject
  VirtualHostHttpCanonicalWebUrlProvider(@GerritServerConfig Config config) {
    super(config);
    serverUri = URI.create(super.get());
  }

  @Override
  public String get() {
    return getVirtualHostHttpCanonicalWebUrl(serverUri, CurrentServerName.get());
  }

  @VisibleForTesting
  static String getVirtualHostHttpCanonicalWebUrl(URI baseUri, Optional<String> serverName) {
    return serverName
        .map(name -> new URIBuilder(baseUri).setHost(name).toString())
        .orElse(baseUri.toString());
  }
}
