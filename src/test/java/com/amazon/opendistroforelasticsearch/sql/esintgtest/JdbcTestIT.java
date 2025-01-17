/*
 *   Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package com.amazon.opendistroforelasticsearch.sql.esintgtest;

import org.json.JSONObject;

import static org.hamcrest.Matchers.equalTo;

public class JdbcTestIT extends SQLIntegTestCase {

    @Override
    protected void init() throws Exception {
        loadIndex(Index.ONLINE);
    }

    public void testDateTimeInQuery() {
        JSONObject response = executeJdbcRequest(
                "SELECT date_format(insert_time, 'dd-MM-YYYY') FROM elasticsearch-sql_test_index_online limit 1");

        assertThat(
                response.getJSONArray("datarows")
                        .getJSONArray(0)
                        .getString(0),
                equalTo("17-08-2014"));
    }

    public void testDivisionInQuery() {
        JSONObject response = executeJdbcRequest(
                "SELECT all_client/10 from elasticsearch-sql_test_index_online ORDER BY all_client/10 desc limit 1");

        assertThat(
                response.getJSONArray("datarows")
                        .getJSONArray(0)
                        .getDouble(0),
                equalTo(16827.0));
    }

    public void testGroupByInQuery() {
        JSONObject response = executeJdbcRequest(
                "SELECT date_format(insert_time, 'YYYY-MM-dd'), COUNT(*) " +
                        "FROM elasticsearch-sql_test_index_online " +
                        "GROUP BY date_format(insert_time, 'YYYY-MM-dd')"
        );

        assertThat(response.getJSONArray("schema").length(), equalTo(2));
        assertThat(response.getJSONArray("datarows").length(), equalTo(8));
    }

    private JSONObject executeJdbcRequest(String query){
        return new JSONObject(executeQuery(query, "jdbc"));
    }
}
