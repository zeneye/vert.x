import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.streams.Pump
import org.vertx.groovy.core.file.FileSystem

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

server = new HttpServer().requestHandler { req ->
  req.pause()
  def filename = UUID.randomUUID().toString() + ".uploaded"
  FileSystem.instance.open(filename) { ares ->
    def file = ares.result
    def pump = new Pump(req, file.getWriteStream())
    req.endHandler {
      file.close {
        println "Uploaded ${pump.getBytesPumped()} bytes to $filename"
        req.response.end()
      }
    }
    pump.start()
    req.resume()
  }
}.listen(8080)

def vertxStop() {
  server.close()
}
