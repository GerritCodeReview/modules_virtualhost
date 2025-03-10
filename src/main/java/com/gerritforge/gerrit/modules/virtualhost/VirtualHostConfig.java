// Copyright (C) 2017 GerritForge Ltd.
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
import com.google.gerrit.server.project.RefPattern;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class VirtualHostConfig {
  private static final Logger log = LoggerFactory.getLogger(VirtualHostConfig.class);
  private static final String[] EMPTY_PROJECTS_ARRAY = new String[0];
  private final Config config;
  private final boolean enabled;

  public static final String ALL_PROJECTS_REGEX = "^.*";
  public final String[] defaultProjects;

  @Inject
  VirtualHostConfig(SitePaths sitePaths) {
    File configFile = sitePaths.etc_dir.resolve("virtualhost.config").toFile();
    FileBasedConfig fileConfig = new FileBasedConfig(configFile, FS.DETECTED);
    config = fileConfig;
    try {
      fileConfig.load();
    } catch (IOException | ConfigInvalidException e) {
      log.error("Unable to open or parse " + configFile + ": virtual domains are disabled", e);
      enabled = false;
      defaultProjects = new String[0];
      return;
    }
    defaultProjects = validateProjects(config.getStringList("default", null, "projects"));
    enabled = !config.getSubsections("server").isEmpty() || defaultProjects.length > 0;
  }

  public String[] getProjects(String hostname) {
    if (!enabled) {
      return EMPTY_PROJECTS_ARRAY;
    }

    String[] projects = validateProjects(config.getStringList("server", hostname, "projects"));
    if (projects.length > 0) {
      return projects;
    }

    return defaultProjects;
  }

  private String[] validateProjects(String[] projects) {
    for (String project : projects) {
      if (RefPattern.isRE(project) && !project.equals(ALL_PROJECTS_REGEX)) {
        throw new IllegalStateException(
            "Project regex '" + project + "' is not allowed in virtualhost.config");
      }
    }
    return projects;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
