import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.sockjs.SockJSServer
import org.vertx.java.core.sockjs.AppConfig

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

server = new HttpServer()

// Serve the index page
server.requestHandler { req ->
  if (req.getUri() == "/") req.response.sendFile('sockjs/index.html')
}

// The handler for the SockJS app - we just echo data back
new SockJSServer(server).installApp(new AppConfig(prefix: '/testapp')) { sock ->
  sock.dataHandler { buff ->
    sock << buff
  }
}

server.listen(8080)

def vertxStop() {
  server.close()
}
