/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fabric8.openshift;

import io.fabric8.openshift.api.model.*;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BuildConfigTest {
  public static OpenShiftClient client;

  public static String currentNamespace;

  @BeforeClass
  public static void init() {
    client = new DefaultOpenShiftClient();
    currentNamespace = "rt-" + RandomStringUtils.randomAlphanumeric(6).toLowerCase();
    ProjectRequest proj = new ProjectRequestBuilder().withNewMetadata().withName(currentNamespace).endMetadata().build();
    client.projectrequests().create(proj);
  }

  @AfterClass
  public static void cleanup() {
    client.projects().withName(currentNamespace).delete();
    client.close();
  }

  @Test
  public void testLoad() {
    BuildConfig aBuildConfig = client.buildConfigs().inNamespace(currentNamespace)
      .load(getClass().getResourceAsStream("/test-buildconfig.yml")).get();
    assertThat(aBuildConfig).isNotNull();
    assertEquals("ruby-sample-build", aBuildConfig.getMetadata().getName());
  }

  @Test
  public void testCrud() throws InterruptedException {
    BuildConfig buildConfig1 = new BuildConfigBuilder()
      .withNewMetadata().withName("bc1").endMetadata()
      .withNewSpec()
      .addNewTrigger()
      .withType("GitHub")
      .withNewGithub()
      .withSecret("secret101")
      .endGithub()
      .endTrigger()
      .addNewTrigger()
      .withType("Generic")
      .withNewGeneric()
      .withSecret("secret101")
      .endGeneric()
      .endTrigger()
      .addNewTrigger()
      .withType("ImageChange")
      .endTrigger()
      .withNewSource()
      .withType("Git")
      .withNewGit()
      .withUri("https://github.com/openshift/ruby-hello-world")
      .endGit()
      .withDockerfile("FROM openshift/ruby-22-centos7\\nUSER example")
      .endSource()
      .withNewStrategy()
      .withType("Source")
      .withNewSourceStrategy()
      .withNewFrom()
      .withKind("ImageStreamTag")
      .withName("origin-ruby-sample:latest")
      .endFrom()
      .endSourceStrategy()
      .endStrategy()
      .withNewOutput()
      .withNewTo()
      .withKind("ImageStreamTag")
      .withName("origin-ruby-sample:latest")
      .endTo()
      .endOutput()
      .withNewPostCommit()
      .withScript("bundle exec rake test")
      .endPostCommit()
      .endSpec()
      .build();

    BuildConfig buildConfig2 = new BuildConfigBuilder()
      .withNewMetadata().withName("bc2").endMetadata()
      .withNewSpec()
      .withNewOutput()
      .withNewTo()
      .withKind("ImageStreamTag")
      .withName("ruby-hello-world:latest")
      .endTo()
      .endOutput()
      .withNewSource()
      .withNewGit()
      .withUri("https://github.com/openshift/ruby-hello-world")
      .endGit()
      .withType("Git")
      .endSource()
      .withNewStrategy()
      .withType("Docker")
      .withNewDockerStrategy()
      .withNewFrom()
      .withKind("ImageStreamTag")
      .withName("ruby-22-centos7:latest")
      .endFrom()
      .endDockerStrategy()
      .endStrategy()
      .addNewTrigger()
      .withType("Github")
      .withNewGithub()
      .withSecret("R4c5loDOcu7qJ1XZYDXE")
      .endGithub()
      .endTrigger()
      .addNewTrigger()
      .withType("Generic")
      .withNewGeneric()
      .withSecret("tx_Ppqs7ghQa0-sHC7Uq")
      .endGeneric()
      .endTrigger()
      .addNewTrigger()
      .withType("ImageChange")
      .endTrigger()
      .endSpec()
      .build();

    client.buildConfigs().inNamespace(currentNamespace).create(buildConfig1);
    client.buildConfigs().inNamespace(currentNamespace).create(buildConfig2);

    BuildConfigList bcList = client.buildConfigs().inNamespace(currentNamespace).list();
    assertThat(bcList).isNotNull();
    assertEquals(2, bcList.getItems().size());

    buildConfig1 = client.buildConfigs().inNamespace(currentNamespace).withName("bc1").edit()
      .editSpec().withFailedBuildsHistoryLimit(5).endSpec().done();
    assertEquals(5, buildConfig1.getSpec().getFailedBuildsHistoryLimit().intValue());

    boolean bDeleted = client.buildConfigs().inNamespace(currentNamespace).withName("bc2").delete();
    assertTrue(bDeleted);
  }
}
