/*
 * Copyright 2019-2021 Micro Focus or one of its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cafapi.opensuse.elasticsearch7;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.xcontent.XContentType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public final class ContainerIT
{
    private final static RestHighLevelClient client = getElasticsearchClient();

    @BeforeClass
    public static void setup() throws IOException
    {
        final ClusterHealthResponse response = client.cluster().health(new ClusterHealthRequest(), RequestOptions.DEFAULT);
        final ClusterHealthStatus status = response.getStatus();
        assertEquals("Elasticsearch status not green", ClusterHealthStatus.GREEN, status);
    }

    @Test
    public void testIndexCreation() throws IOException
    {
        final CreateIndexRequest request = new CreateIndexRequest("container_test");
        request.source("{\n"
            + "    \"settings\" : {\n"
            + "        \"number_of_shards\" : 1,\n"
            + "        \"number_of_replicas\" : 0\n"
            + "    },\n"
            + "    \"mappings\" : {\n"
            + "        \"properties\" : {\n"
            + "            \"message\" : { \"type\" : \"text\" }\n"
            + "        }\n"
            + "    }"
            + "}", XContentType.JSON);
        final CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        assertTrue("Index response was not acknowledged", createIndexResponse.isAcknowledged());
        assertTrue("All shards were not copied", createIndexResponse.isShardsAcknowledged());
    }

    private static RestHighLevelClient getElasticsearchClient()
    {
        return new RestHighLevelClient(RestClient
            .builder(new HttpHost(System.getenv("ELASTICSEARCH_HOST"), Integer.parseInt(System.getenv("ELASTICSEARCH_PORT")), "http")));
    }
}
