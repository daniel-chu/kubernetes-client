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
package io.fabric8.kubernetes.client.mock;

import io.fabric8.kubernetes.api.model.rbac.KubernetesPolicyRuleBuilder;
import io.fabric8.kubernetes.api.model.rbac.KubernetesRole;
import io.fabric8.kubernetes.api.model.rbac.KubernetesRoleBuilder;
import io.fabric8.kubernetes.api.model.rbac.KubernetesRoleList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KubernetesRoleCrudTest {

  private static final Logger logger = LoggerFactory.getLogger(KubernetesRoleCrudTest.class);

  @Rule
  public KubernetesServer kubernetesServer = new KubernetesServer(true,true);

  @Test
  public void crudTest(){

    KubernetesClient client = kubernetesServer.getClient();

    KubernetesRole kubernetesRole = new KubernetesRoleBuilder()
      .withNewMetadata()
        .withName("job-reader")
      .endMetadata()
      .addToRules(0, new KubernetesPolicyRuleBuilder()
        .addToApiGroups(0,"batch")
        .addToNonResourceURLs(0,"/healthz")
        .addToResourceNames(0,"my-job")
        .addToResources(0,"jobs")
        .addToVerbs(0, "get")
        .addToVerbs(1, "watch")
        .addToVerbs(2, "list")
        .build()
      )
      .build();

    //test of creation
    kubernetesRole = client.rbac().kubernetesRoles().create(kubernetesRole);

    assertNotNull(kubernetesRole);
    assertEquals("Role", kubernetesRole.getKind());
    assertEquals("rbac.authorization.k8s.io/v1", kubernetesRole.getApiVersion());
    assertNotNull(kubernetesRole.getMetadata());
    assertEquals("job-reader", kubernetesRole.getMetadata().getName());
    assertNotNull(kubernetesRole.getRules());
    assertEquals(1, kubernetesRole.getRules().size());
    assertNotNull(kubernetesRole.getRules().get(0).getApiGroups());
    assertEquals(1, kubernetesRole.getRules().get(0).getApiGroups().size());
    assertEquals("batch", kubernetesRole.getRules().get(0).getApiGroups().get(0));
    assertNotNull(kubernetesRole.getRules().get(0).getNonResourceURLs());
    assertEquals(1, kubernetesRole.getRules().get(0).getNonResourceURLs().size());
    assertEquals("/healthz", kubernetesRole.getRules().get(0).getNonResourceURLs().get(0));
    assertNotNull(kubernetesRole.getRules().get(0).getResourceNames());
    assertEquals(1, kubernetesRole.getRules().get(0).getResourceNames().size());
    assertEquals("my-job", kubernetesRole.getRules().get(0).getResourceNames().get(0));
    assertNotNull(kubernetesRole.getRules().get(0).getResources());
    assertEquals(1, kubernetesRole.getRules().get(0).getResources().size());
    assertEquals("jobs", kubernetesRole.getRules().get(0).getResources().get(0));
    assertNotNull(kubernetesRole.getRules().get(0).getVerbs());
    assertEquals(3, kubernetesRole.getRules().get(0).getVerbs().size());
    assertEquals("get", kubernetesRole.getRules().get(0).getVerbs().get(0));
    assertEquals("watch", kubernetesRole.getRules().get(0).getVerbs().get(1));
    assertEquals("list", kubernetesRole.getRules().get(0).getVerbs().get(2));

    //test of list
    KubernetesRoleList kubernetesRoleList = client.rbac().kubernetesRoles().list();

    assertNotNull(kubernetesRoleList);
    assertNotNull(kubernetesRoleList.getItems());
    assertEquals(1, kubernetesRoleList.getItems().size());
    assertNotNull(kubernetesRoleList.getItems().get(0));
    assertEquals("Role", kubernetesRoleList.getItems().get(0).getKind());
    assertEquals("rbac.authorization.k8s.io/v1", kubernetesRoleList.getItems().get(0).getApiVersion());
    assertNotNull(kubernetesRoleList.getItems().get(0).getMetadata());
    assertEquals("job-reader", kubernetesRoleList.getItems().get(0).getMetadata().getName());
    assertNotNull(kubernetesRoleList.getItems().get(0).getRules());
    assertEquals(1, kubernetesRoleList.getItems().get(0).getRules().size());
    assertNotNull(kubernetesRoleList.getItems().get(0).getRules().get(0).getApiGroups());
    assertEquals(1, kubernetesRoleList.getItems().get(0).getRules().get(0).getApiGroups().size());
    assertEquals("batch", kubernetesRoleList.getItems().get(0).getRules().get(0).getApiGroups().get(0));
    assertNotNull(kubernetesRoleList.getItems().get(0).getRules().get(0).getNonResourceURLs());
    assertEquals(1, kubernetesRoleList.getItems().get(0).getRules().get(0).getNonResourceURLs().size());
    assertEquals("/healthz", kubernetesRoleList.getItems().get(0).getRules().get(0).getNonResourceURLs().get(0));
    assertNotNull(kubernetesRoleList.getItems().get(0).getRules().get(0).getResourceNames());
    assertEquals(1, kubernetesRoleList.getItems().get(0).getRules().get(0).getResourceNames().size());
    assertEquals("my-job", kubernetesRoleList.getItems().get(0).getRules().get(0).getResourceNames().get(0));
    assertNotNull(kubernetesRoleList.getItems().get(0).getRules().get(0).getResources());
    assertEquals(1, kubernetesRoleList.getItems().get(0).getRules().get(0).getResources().size());
    assertEquals("jobs", kubernetesRoleList.getItems().get(0).getRules().get(0).getResources().get(0));
    assertNotNull(kubernetesRoleList.getItems().get(0).getRules().get(0).getVerbs());
    assertEquals(3, kubernetesRoleList.getItems().get(0).getRules().get(0).getVerbs().size());
    assertEquals("get", kubernetesRoleList.getItems().get(0).getRules().get(0).getVerbs().get(0));
    assertEquals("watch", kubernetesRoleList.getItems().get(0).getRules().get(0).getVerbs().get(1));
    assertEquals("list", kubernetesRoleList.getItems().get(0).getRules().get(0).getVerbs().get(2));

    //test of updation

    kubernetesRole = client.rbac().kubernetesRoles().withName("job-reader").edit()
      .editRule(0).addToApiGroups(1, "extensions").endRule().done();

    assertNotNull(kubernetesRole);
    assertEquals("Role", kubernetesRole.getKind());
    assertEquals("rbac.authorization.k8s.io/v1", kubernetesRole.getApiVersion());
    assertNotNull(kubernetesRole.getMetadata());
    assertEquals("job-reader", kubernetesRole.getMetadata().getName());
    assertNotNull(kubernetesRole.getRules());
    assertEquals(1, kubernetesRole.getRules().size());
    assertNotNull(kubernetesRole.getRules().get(0).getApiGroups());
    assertEquals(2, kubernetesRole.getRules().get(0).getApiGroups().size());
    assertEquals("batch", kubernetesRole.getRules().get(0).getApiGroups().get(0));
    assertEquals("extensions", kubernetesRole.getRules().get(0).getApiGroups().get(1));
    assertNotNull(kubernetesRole.getRules().get(0).getNonResourceURLs());
    assertEquals(1, kubernetesRole.getRules().get(0).getNonResourceURLs().size());
    assertEquals("/healthz", kubernetesRole.getRules().get(0).getNonResourceURLs().get(0));
    assertNotNull(kubernetesRole.getRules().get(0).getResourceNames());
    assertEquals(1, kubernetesRole.getRules().get(0).getResourceNames().size());
    assertEquals("my-job", kubernetesRole.getRules().get(0).getResourceNames().get(0));
    assertNotNull(kubernetesRole.getRules().get(0).getResources());
    assertEquals(1, kubernetesRole.getRules().get(0).getResources().size());
    assertEquals("jobs", kubernetesRole.getRules().get(0).getResources().get(0));
    assertNotNull(kubernetesRole.getRules().get(0).getVerbs());
    assertEquals(3, kubernetesRole.getRules().get(0).getVerbs().size());
    assertEquals("get", kubernetesRole.getRules().get(0).getVerbs().get(0));
    assertEquals("watch", kubernetesRole.getRules().get(0).getVerbs().get(1));
    assertEquals("list", kubernetesRole.getRules().get(0).getVerbs().get(2));

    //test of deletion
    boolean deleted = client.rbac().kubernetesRoles().delete();

    assertTrue(deleted);
    kubernetesRoleList = client.rbac().kubernetesRoles().list();
    assertEquals(0,kubernetesRoleList.getItems().size());
  }
}
