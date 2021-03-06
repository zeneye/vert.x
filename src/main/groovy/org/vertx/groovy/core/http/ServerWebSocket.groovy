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


package org.vertx.groovy.core.http;

import org.vertx.java.core.http.ServerWebSocket as JServerWebSocket

/**
 * Represents a WebSocket that is passed into a the websocketHandler of an {@link HttpServer}
 * <p>
 * Instances of this class are not thread-safe
 * <p>
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ServerWebSocket extends WebSocket {

  private final JServerWebSocket jSWS;

  protected ServerWebSocket(JServerWebSocket jSWS) {
    super(jSWS);
    this.jSWS = jSWS;
  }

  /**
   * @return The path the websocket is attempting to connect at
   */
  String getPath() {
    jSWS.path
  }

  /**
   * Reject the WebSocket<p>
   * Calling this method from the websocketHandler gives you the opportunity to reject
   * the websocket, which will cause the websocket handshake to fail by returning
   * a 404 response code.<p>
   * You might use this method, if for example you only want to accept websockets
   * with a particular path.
   */
  void reject() {
    jSWS.reject()
  }
}