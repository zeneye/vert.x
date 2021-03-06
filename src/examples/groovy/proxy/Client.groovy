import org.vertx.groovy.core.http.HttpClient

/*
* Copyright 2011-2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

client = new HttpClient(port: 8080)
def request = client.put('/') { resp ->
  println "Got response ${resp.getStatusCode()}"
  resp.bodyHandler { body -> println "Got data ${body}" }
}

request.setChunked(true)

10.times { request << "client-chunk-" + it }
request.end()

def vertxStop() {
  client.close()
}