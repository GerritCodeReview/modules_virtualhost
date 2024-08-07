# Gerrit VirtualHost

Gerrit lib module to split the projects' space into virtual hosts
similarly of what you would do with an HTTP Server and different
domain names.

## How to build

Build this module as it was a Gerrit plugin:

- Clone Gerrit source tree
- Clone the virtualhost source tree
- Link the ```virtualhost``` directory to Gerrit ```/plugins/virtualhost```
- From Gerrit source tree run ```bazel build plugins/virtualhost```
- The ```virtualhost.jar``` module is generated under ```/bazel-genfiles/plugins/virtualhost/```

## How install

Copy ```virtualhost.jar``` library to Gerrit ```/lib``` and add the following
two extra settings to ```gerrit.config```:

```
[gerrit]
  installModule = com.gerritforge.gerrit.modules.virtualhost.GuiceModule

[httpd]
  filterClass = com.gerritforge.gerrit.modules.virtualhost.VirtualHostFilter
```

## Propagation of the `X-Forwarded-Host` Header:

When Gerrit is hidden behind multiple service layers (eg. reverse-proxy and
load balancer), it is essential to ensure the propagation from the upstream
proxy of the header [X-Forwarded-Host](https://www.rfc-editor.org/rfc/rfc7239.html)
from the upstream proxy.

## How to define virtual hosts

/etc/virtualhost.config contains the definition of the virtual
hosts and the set of projects included.

Each ```server``` section defines a virtual host and contains a set of projects
included. Projects are defined using Gerrit ref-matching expressions and can
be repeated multiple times to include multiple matchers.

Example to include all the projects starting with ```team1/``` and the ones
starting with the username:

```
[server "team1.mycompany.com"]
  projects = team1/*
  projects = ${username}/*
```

## Default host

For all the other server names that are not defined and for SSH access, there
is a special default section that lists of visible projects.

Example to include all the projects by default:

```
[default]
  projects = ^.*
```

> **NOTE**: The `^.*` is the only regular expression supported by the virtualhost
> module because of the potential performance implication of a generic regular expression
> evaluation during the ACLs. Bear in mind that any possible action perform in Gerrit will
> go through the virtualhost module filtering and therefore it is paramount to minimize the
> potential performance impact.
