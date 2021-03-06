# Copyright 2011 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

require "vertx"
include Vertx

@server = HttpServer.new

# Serve the static resources
@server.request_handler do |req|
  req.response.send_file("eventbusbridge/index.html") if req.uri == "/"
  req.response.send_file("eventbusbridge/vertxbus.js") if req.uri == "/vertxbus.js"
end

# Create a bridge that lets everything through (be careful!)
SockJSBridge.new(@server, {"prefix" => "/eventbus"}, [{}])

@server.listen(8080)

def vertx_stop
  @server.close
end