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

var vertx = vertx || {};

if (!vertx.HttpServer) {

  (function() {

    function convertMap(j_map) {
      var map = {};
      var j_iter = j_map.entrySet().iterator();
      while (j_iter.hasNext()) {
        var entry = j_iter.next();
        map[entry.getKey()] = entry.getValue();
      }
      return map;
    }

    function wrappedRequestHandler(handler) {
      return function(req) {

        //We need to add some functions to the request and the response

        var reqHeaders = null;
        var reqParams = null;

        var reqProto = {

          headers: function() {
            if (!reqHeaders) {
              reqHeaders = convertMap(req.getAllHeaders());
            }
            return reqHeaders;
          },
          params: function() {
            if (!reqParams) {
              reqParams = convertMap(req.getAllParams());
            }
            return reqParams;
          }
        };

        var respProto = {
          putHeaders: function(hash) {
            for (k in hash) {
              req.response.putHeader(k, hash[k]);
            }
          },
          putTrailers: function(hash) {
            for (k in hash) {
              req.response.putTrailer(k, hash[k]);
            }
          }
        }

        req.__proto__ = reqProto;
        req.response.__proto__ = respProto;

        handler(req);
      }
    }

    vertx.HttpServer = function() {

      var j_server = new org.vertx.java.core.http.HttpServer();

      var that = this;

      this.requestHandler = function(handler) {

        if (handler) {

          if (typeof handler === 'function') {
            handler = wrappedRequestHandler(handler);
          } else {
            // It's a route matcher
            handler = handler._to_java_handler();
          }

          j_server.requestHandler(handler);
        }
        return that;
      };

      this.websocketHandler = function(handler) {
        if (handler) {
          j_server.websocketHandler(handler);
        }
        return that;
      };

      this.close = function(handler) {
        if (handler) {
          j_server.close(handler);
        } else {
          j_server.close();
        }
      };

      this.listen = function(port, host) {
        if (host) {
          j_server.listen(port, host);
        } else {
          j_server.listen(port);
        }
        return that;
      }

      that.setSSL = function(ssl) {
        j_server.setSSL(ssl);
        return that;
      }

      that.setKeyStorePath = function(path) {
        j_server.setKeyStorePath(path);
        return that;
      }

      that.setKeyStorePassword = function(password) {
        j_server.setKeyStorePassword(password);
        return that;
      }

      that.setTrustStorePath = function(path) {
        j_server.setTrustStorePath(path);
        return that;
      }

      that.setTrustStorePassword = function(password) {
        j_server.setTrustStorePassword(password);
        return that;
      }

      that.setClientAuthRequired = function(required) {
        j_server.setClientAuthRequired(required);
        return that;
      }

      that.setTCPNoDelay = function(tcpNoDelay) {
        j_server.setTCPNoDelay(tcpNoDelay);
        return that;
      }

      that.setSendBufferSize = function(size) {
        j_server.setSendBufferSize(size);
        return that;
      }

      that.setReceiveBufferSize = function(size) {
        j_server.setReceiveBufferSize(size);
        return that;
      }

      that.setTCPKeepAlive = function(keepAlive) {
        j_server.setTCPKeepAlive(keepAlive);
        return that;
      }

      that.setReuseAddress = function(reuse) {
        j_server.setReuseAddress(reuse);
        return that;
      }

      that.setSoLinger = function(linger) {
        j_server.setSoLinger(linger);
        return that;
      }

      that.setTrafficClass = function(class) {
        j_server.setTrafficClass(class);
        return that;
      }

      that.setClientAuthRequired = function(required) {
        j_server.setClientAuthRequired(required);
        return that;
      }

      that.isTCPNoDelay = function() {
        return j_server.isTCPNoDelay();
      }

      that.getSendBufferSize = function() {
        return j_server.getSendBufferSize();
      }

      that.getReceiveBufferSize = function() {
        return j_server.getReceiveBufferSize();
      }

      that.isSoLinger = function() {
        return j_server.isSoLinger();
      }

      that.getTrafficClass = function() {
        return j_server.getTrafficClass();
      }

      that.isSSL = function() {
        return j_server.isSSL();
      }

      that._to_java_server = function() {
        return j_server;
      }
    }

    vertx.HttpClient = function() {
      var j_client = new org.vertx.java.core.http.HttpClient();

      var that = this;

      function wrapResponseHandler(handler) {
        var wrapperHandler = function(resp) {

          var respHeaders = null;
          var respTrailers = null;

          var resp_proto = {

            headers: function() {
              if (!respHeaders) {
                respHeaders = convertMap(resp.getAllHeaders());
              }
              return respHeaders;
            },
            trailers: function() {
              if (!respTrailers) {
                respTrailers = convertMap(resp.getAllTrailers());
              }
              return respTrailers;
            }
          };

          resp.__proto__ = resp_proto;

          handler(resp);
        }
        return wrapperHandler;
      }

      that.exceptionHandler = function(handler) {
        j_client.exceptionHandler(handler);
        return that;
      }

      that.setMaxPoolSize = function(size) {
        j_client.setMaxPoolSize(size);
        return that;
      }

      that.getMaxPoolSize = function() {
        return j_client.getMaxPoolSize();
      }

      that.setKeepAlive = function(keepAlive) {
        j_client.setKeepAlive(keepAlive);
        return that;
      }

      that.setSSL = function(ssl) {
        j_client.setSSL(ssl);
        return that;
      }

      that.setKeyStorePath = function(path) {
        j_client.setKeyStorePath(path);
        return that;
      }

      that.setKeyStorePassword = function(password) {
        j_client.setKeyStorePassword(password);
        return that;
      }

      that.setTrustStorePath = function(path) {
        j_client.setTrustStorePath(path);
        return that;
      }

      that.setTrustStorePassword = function(password) {
        j_client.setTrustStorePassword(password);
        return that;
      }

      that.setTrustAll = function(trustAll) {
        j_client.setTrustAll(trustAll);
        return that;
      }

      that.setPort = function(port) {
        j_client.setPort(port);
        return that;
      }

      that.setHost = function(host) {
        j_client.setHost(host);
        return that;
      }

      that.connectWebsocket = function(uri, handler) {
        j_client.connectWebsocket(uri, handler);
      }

      that.getNow = function(uri, handler) {
        return j_client.getNow(uri, wrapResponseHandler(handler));
      }

      that.options = function(uri, handler) {
        return j_client.options(uri, wrapResponseHandler(handler));
      }

      that.get = function(uri, handler) {
        return j_client.get(uri, wrapResponseHandler(handler));
      }

      that.head = function(uri, handler) {
        return j_client.head(uri, wrapResponseHandler(handler));
      }

      that.post = function(uri, handler) {
        return j_client.post(uri, wrapResponseHandler(handler));
      }

      that.put = function(uri, handler) {
        return j_client.put(uri, wrapResponseHandler(handler));
      }

      that.delete = function(uri, handler) {
        return j_client.delete(uri, wrapResponseHandler(handler));
      }

      that.trace = function(uri, handler) {
        return j_client.trace(uri, wrapResponseHandler(handler));
      }

      that.connect = function(uri, handler) {
        return j_client.connect(uri, wrapResponseHandler(handler));
      }

      that.patch = function(uri, handler) {
        return j_client.patch(uri, wrapResponseHandler(handler));
      }

      that.request = function(method, uri, handler) {
        return j_client.request(method, uri, wrapResponseHandler(handler));
      }

      that.close = function() {
        j_client.close();
      }

      that.setTCPNoDelay = function(tcpNoDelay) {
        j_client.setTCPNoDelay(tcpNoDelay);
        return that;
      }

      that.setSendBufferSize = function(size) {
        j_client.setSendBufferSize(size);
        return that;
      }

      that.setReceiveBufferSize = function(size) {
        j_client.setReceiveBufferSize(size);
        return that;
      }

      that.setTCPKeepAlive = function(keepAlive) {
        j_client.setTCPKeepAlive(keepAlive);
        return that;
      }

      that.setReuseAddress = function(reuse) {
        j_client.setReuseAddress(reuse);
        return that;
      }

      that.setSoLinger = function(linger) {
        j_client.setSoLinger(linger);
        return that;
      }

      that.setTrafficClass = function(class) {
        j_client.setTrafficClass(class);
        return that;
      }

      that.isTCPNoDelay = function() {
        return j_client.isTCPNoDelay();
      }

      that.getSendBufferSize = function() {
        return j_client.getSendBufferSize();
      }

      that.getReceiveBufferSize = function() {
        return j_client.getReceiveBufferSize();
      }

      that.isSoLinger = function() {
        return j_client.isSoLinger();
      }

      that.getTrafficClass = function() {
        return j_client.getTrafficClass();
      }

      that.isSSL = function() {
        return j_client.isSSL();
      }

    }

    vertx.RouteMatcher = function() {

      var j_rm = new org.vertx.java.core.http.RouteMatcher();

      this.get = function(pattern, handler) {
        j_rm.get(pattern, wrappedRequestHandler(handler));
      }

      this.put = function(pattern, handler) {
        j_rm.put(pattern, wrappedRequestHandler(handler));
      }

      this.post = function(pattern, handler) {
        j_rm.post(pattern, wrappedRequestHandler(handler));
      }

      this.delete = function(pattern, handler) {
        j_rm.delete(pattern, wrappedRequestHandler(handler));
      }

      this.options = function(pattern, handler) {
        j_rm.options(pattern, wrappedRequestHandler(handler));
      }

      this.head = function(pattern, handler) {
        j_rm.head(pattern, wrappedRequestHandler(handler));
      }

      this.trace = function(pattern, handler) {
        j_rm.trace(pattern, wrappedRequestHandler(handler));
      }

      this.connect = function(pattern, handler) {
        j_rm.connect(pattern, wrappedRequestHandler(handler));
      }

      this.patch = function(pattern, handler) {
        j_rm.patch(pattern, wrappedRequestHandler(handler));
      }

      this.all = function(pattern, handler) {
        j_rm.all(pattern, wrappedRequestHandler(handler));
      }

      this.getWithRegEx = function(pattern, handler) {
        j_rm.getWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.putWithRegEx = function(pattern, handler) {
        j_rm.putWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.postWithRegEx = function(pattern, handler) {
        j_rm.postWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.deleteWithRegEx = function(pattern, handler) {
        j_rm.deleteWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.optionsWithRegEx = function(pattern, handler) {
        j_rm.optionsWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.headWithRegEx = function(pattern, handler) {
        j_rm.headWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.traceWithRegEx = function(pattern, handler) {
        j_rm.traceWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.connectWithRegEx = function(pattern, handler) {
        j_rm.connectWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.patchWithRegEx = function(pattern, handler) {
        j_rm.patchWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.allWithRegEx = function(pattern, handler) {
        j_rm.allWithRegEx(pattern, wrappedRequestHandler(handler));
      }

      this.noMatch = function(handler) {
        j_rm.noMatch(wrappedRequestHandler(handler));
      }

      this._to_java_handler = function() {
        return j_rm;
      }

    }
  })();
}
