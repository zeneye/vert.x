import org.vertx.groovy.core.http.HttpClient
import org.vertx.groovy.core.file.FileSystem
import org.vertx.groovy.core.streams.Pump

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

def req = client.put("/someurl") { resp -> println "Response ${resp.statusCode}" }
def filename = "upload/upload.txt"
FileSystem.instance.props(filename) { ares ->
  def props = ares.result
  println "props is ${props}"
  def size = props.size
  req.putHeader("Content-Length", size)
  FileSystem.instance.open(filename) { ares2 ->
    def file = ares2.result
    def rs = file.getReadStream()
    def pump = new Pump(rs, req)
    rs.endHandler { req.end() }
    pump.start()
  }
}

def vertxStop() {
  client.close()
}
