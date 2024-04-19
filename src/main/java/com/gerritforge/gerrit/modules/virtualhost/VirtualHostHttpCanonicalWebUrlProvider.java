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
import com.google.common.base.Predicates;
import com.google.common.flogger.FluentLogger;
import com.google.gerrit.httpd.HttpCanonicalWebUrlProvider;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.net.URI;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jgit.lib.Config;

@Singleton
public class VirtualHostHttpCanonicalWebUrlProvider extends HttpCanonicalWebUrlProvider {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private final URI serverUri;
  private Provider<HttpServletRequest> requestProvider;

  @Inject
  VirtualHostHttpCanonicalWebUrlProvider(@GerritServerConfig Config config) {
    super(config);
    serverUri = URI.create(super.get());
  }

  @Inject(optional = true)
  public void setHttpServletRequest(Provider<HttpServletRequest> hsr) {
    super.setHttpServletRequest(hsr);
    requestProvider = hsr;
  }

  @Override
  public String get() {
    return getVirtualHostHttpCanonicalWebUrl(serverUri, getServerName());
  }

  @VisibleForTesting
  static String getVirtualHostHttpCanonicalWebUrl(URI baseUri, Optional<String> serverName) {
    return serverName
        .map(name -> new URIBuilder(baseUri).setHost(name).toString())
        .orElse(baseUri.toString());
  }

  private Optional<String> getServerName() {
    return CurrentServerName.get()
        .or(
            () ->
                Optional.ofNullable(requestProvider)
                    .map(
                        provider -> {
                          try {
                            return provider.get().getServerName();
                          } catch (OutOfScopeException e) {
                            logger.atWarning().withCause(e).log(
                                "Unable to determine the virtual-host servername: current thread out of an HTTP request scope or outside a call stack coming from a GuiceServlet filter");
                            return null;
                          }
                        })
                    .filter(Predicates.notNull()));
  }
}
