
package com.gerritforge.gerrit.modules.virtualhost;

import org.eclipse.jgit.lib.Config;

import com.google.gerrit.httpd.HttpCanonicalWebUrlProvider;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.inject.Inject;

public class VritualHostHttpCanonicalWebUrlProvider extends HttpCanonicalWebUrlProvider {

  @Inject
  VritualHostHttpCanonicalWebUrlProvider(@GerritServerConfig Config config) {
    super(config);
  }

  @Override
  public String get() {
    return CurrentServerName.get()
        .map(serverName -> "https://" + serverName + "/")
        .orElse(super.get());
  }
}
