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

package org.vertx.java.core.sockjs.impl;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.core.sockjs.AppConfig;
import org.vertx.java.core.sockjs.SockJSSocket;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
class JsonPTransport extends BaseTransport {

  private static final Logger log = LoggerFactory.getLogger(JsonPTransport.class);

  JsonPTransport(RouteMatcher rm, String basePath, final Map<String, Session> sessions, final AppConfig config,
            final Handler<SockJSSocket> sockHandler) {
    super(sessions, config);

    String jsonpRE = basePath + COMMON_PATH_ELEMENT_RE + "jsonp";

    rm.getWithRegEx(jsonpRE, new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {

        String callback = req.getAllParams().get("callback");
        if (callback == null) {
          callback = req.getAllParams().get("c");
          if (callback == null) {
            req.response.statusCode = 500;
            req.response.end("\"callback\" parameter required\n");
            return;
          }
        }

        String sessionID = req.getAllParams().get("param0");
        Session session = getSession(config.getSessionTimeout(), config.getHeartbeatPeriod(), sessionID, sockHandler);
        session.register(new JsonPListener(req, session, callback));
      }
    });

    String jsonpSendRE = basePath + COMMON_PATH_ELEMENT_RE + "jsonp_send";

    rm.postWithRegEx(jsonpSendRE, new Handler<HttpServerRequest>() {
      public void handle(final HttpServerRequest req) {
        String sessionID = req.getAllParams().get("param0");
        final Session session = sessions.get(sessionID);
        if (session != null) {
          handleSend(req, session);
        } else {
          req.response.statusCode = 404;
          setJSESSIONID(config, req);
          req.response.end();
        }
      }
    });
  }

  private void handleSend(final HttpServerRequest req, final Session session) {
    req.bodyHandler(new Handler<Buffer>() {

      public void handle(Buffer buff) {
        String body = buff.toString();

        boolean urlEncoded;
        String ct = req.getHeader("Content-Type");
        if (ct.equalsIgnoreCase("application/x-www-form-urlencoded")) {
          urlEncoded = true;
        } else if (ct.equals("text/plain")) {
          urlEncoded = false;
        } else {
          req.response.statusCode = 500;
          req.response.end("Invalid Content-Type");
          return;
        }

        if (body.equals("") || urlEncoded && (!body.startsWith("d=") || body.length() <= 2)) {
          req.response.statusCode = 500;
          req.response.end("Payload expected.");
          return;
        }

        if (urlEncoded) {
          try {
            body = URLDecoder.decode(body, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("No UTF-8!");
          }
          body = body.substring(2);
        }

        if (!session.handleMessages(body)) {
          sendInvalidJSON(req.response);
        } else {
          setJSESSIONID(config, req);
          req.response.putHeader("Content-Type", "text/plain; charset=UTF-8");
          req.response.end("ok");
        }
      }
    });
  }

  private class JsonPListener extends BaseListener {

    final HttpServerRequest req;
    final Session session;
    final String callback;
    boolean headersWritten;
    boolean closed;

    JsonPListener(HttpServerRequest req, Session session, String callback) {
      this.req = req;
      this.session = session;
      this.callback = callback;
      addCloseHandler(req.response, session, sessions);
    }


    public void sendFrame(String body) {

      if (!headersWritten) {
        req.response.setChunked(true);
        req.response.putHeader("Content-Type", "application/javascript; charset=UTF-8");
        req.response.putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        setJSESSIONID(config, req);
        headersWritten = true;
      }

      body = escapeForJavaScript(body);

      StringBuilder sb = new StringBuilder();
      sb.append(callback).append("(\"");
      sb.append(body);
      sb.append("\");\r\n");

      //End the response and close the HTTP connection

      req.response.write(sb.toString());
      close();
    }

    public void close() {
      if (!closed) {
        try {
          session.resetListener();
          req.response.end();
          req.response.close();
          closed = true;
        } catch (IllegalStateException e) {
          // Underlying connection might alreadu be closed - that's fine
        }
      }
    }
  }
}
