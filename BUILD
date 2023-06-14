load("//tools/bzl:junit.bzl", "junit_tests")
load("//tools/bzl:plugin.bzl", "PLUGIN_DEPS", "PLUGIN_TEST_DEPS", "gerrit_plugin")

gerrit_plugin(
    name = "virtualhost",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Implementation-Title: Gerrit Virtual Host lib module",
        "Implementation-URL: https://gerrit.googlesource.com/modules/virtualhost",
    ],
    resources = glob(["src/main/resources/**/*"]),
)

junit_tests(
    name = "tests",
    srcs = glob(["src/test/java/**/*Test.java"]),
    visibility = ["//visibility:public"],
    deps = PLUGIN_TEST_DEPS + PLUGIN_DEPS + [
        ":virtualhost__plugin",
    ],
)
