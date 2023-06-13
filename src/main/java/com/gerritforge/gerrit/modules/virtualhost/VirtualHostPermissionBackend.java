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

import static java.util.Objects.requireNonNull;

import com.google.common.flogger.FluentLogger;
import com.google.gerrit.entities.Account;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.IdentifiedUser;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.util.Optional;

@Singleton
public class VirtualHostPermissionBackend extends PermissionBackend {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  private final Provider<CurrentUser> currentUser;
  private final IdentifiedUser.GenericFactory identifiedUserFactory;
  private final WithVirtualHostUser.Factory virtualDomainUserFactory;

  @Inject
  VirtualHostPermissionBackend(
      Provider<CurrentUser> currentUser,
      IdentifiedUser.GenericFactory identifiedUserFactory,
      WithVirtualHostUser.Factory virtualDomainUserFactory) {
    this.currentUser = currentUser;
    this.identifiedUserFactory = identifiedUserFactory;
    this.virtualDomainUserFactory = virtualDomainUserFactory;
  }

  @Override
  public boolean usesDefaultCapabilities() {
    return true;
  }

  @Override
  public WithUser user(CurrentUser user) {
    return virtualDomainUserFactory.get(user);
  }

  @Override
  public WithUser currentUser() {
    return virtualDomainUserFactory.get(currentUser.get());
  }

  @Override
  public WithUser absentUser(Account.Id id) {
    Optional<Account.Id> user = getAccountIdOfIdentifiedUser();
    if (user.isPresent() && id.equals(user.get())) {
      // What looked liked an absent user is actually the current caller. Use the per-request
      // singleton IdentifiedUser instead of constructing a new object to leverage caching in member
      // variables of IdentifiedUser.
      return virtualDomainUserFactory.get(currentUser.get().asIdentifiedUser());
    }
    return virtualDomainUserFactory.get(identifiedUserFactory.create(requireNonNull(id, "user")));
  }

  private Optional<Account.Id> getAccountIdOfIdentifiedUser() {
    try {
      return currentUser.get().isIdentifiedUser()
          ? Optional.of(currentUser.get().getAccountId())
          : Optional.empty();
    } catch (Exception e) {
      logger.atFine().withCause(e).log("Unable to get current user");
      return Optional.empty();
    }
  }
}
