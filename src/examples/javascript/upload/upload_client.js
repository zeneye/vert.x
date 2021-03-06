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

load('vertx.js')

var client = new vertx.HttpClient().setPort(8080);

var req = client.put("/someurl", function(resp) { stdout.println("Response " + resp.statusCode)});
var filename = "upload/upload.txt"
vertx.FileSystem.props(filename, function(err, props) {
  stdout.println("props is " + props)
  var size = props.size
  req.putHeader("Content-Length", '' + size)
  vertx.FileSystem.open(filename, function(err, file) {
    var rs = file.getReadStream()
    var pump = new vertx.Pump(rs, req)
    rs.endHandler(function() { req.end() });
    pump.start()
  });
});

function vertxStop() {
  client.close()
}