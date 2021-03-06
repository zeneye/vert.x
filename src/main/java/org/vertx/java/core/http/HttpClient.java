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

package org.vertx.java.core.http;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.impl.DefaultHttpClient;

import java.util.Map;

/**
 * A pooling HTTP 1.1 client
 * <p>
 * Maintains a pool of connections to a specific host, at a specific port. The HTTP connections can act
 * as pipelines for HTTP requests.
 * <p>
 * It is used as a factory for {@link HttpClientRequest} instances which encapsulate the actual HTTP requests. It is also
 * used as a factory for HTML5 {@link WebSocket websockets}.
 * <p>
 * This class is a thread safe and can safely be used by different threads.
 * <p>
 * If an instance is instantiated from an event loop then the handlers
 * of the instance will always be called on that same event loop.
 * If an instance is instantiated from some other arbitrary Java thread then
 * and event loop will be assigned to the instance and used when any of its handlers
 * are called.
 * <p>
 * Instances cannot be used from worker verticles
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class HttpClient {

  private final DefaultHttpClient client = new DefaultHttpClient();
  
  /**
   * Create an {@code HttpClient} instance
   */
  public HttpClient() {
  }

  /**
   * Set an exception handler
   */
  public synchronized void exceptionHandler(Handler<Exception> handler) {
    client.exceptionHandler(handler);
  }

  /**
   * Set the maximum pool size<p>
   * The client will maintain up to {@code maxConnections} HTTP connections in an internal pool<p>
   * @return A reference to this, so multiple invocations can be chained together.
   */
  public synchronized HttpClient setMaxPoolSize(int maxConnections) {
    client.setMaxPoolSize(maxConnections);
    return this;
  }

  /**
   * Returns the maximum number of connections in the pool
   */
  public synchronized int getMaxPoolSize() {
    return client.getMaxPoolSize();
  }

  /**
   * If {@code keepAlive} is {@code true} then, after the request has ended the connection will be returned to the pool
   * where it can be used by another request. In this manner, many HTTP requests can be pipe-lined over an HTTP connection.
   * Keep alive connections will not be closed until the {@link #close() close()} method is invoked.<p>
   * If {@code keepAlive} is {@code false} then a new connection will be created for each request and it won't ever go in the pool,
   * the connection will closed after the response has been received. Even with no keep alive, the tcpHelper will not allow more
   * than {@link #getMaxPoolSize() getMaxPoolSize()} connections to be created at any one time. <p>
   * @return A reference to this, so multiple invocations can be chained together.
   */
  public synchronized HttpClient setKeepAlive(boolean keepAlive) {
    client.setKeepAlive(keepAlive);
    return this;
  }

  /**
   * Set the port that the tcpHelper will attempt to connect to on the server to {@code port}. The default value is {@code 80}<p>
   * @return A reference to this, so multiple invocations can be chained together.
   */
  public synchronized HttpClient setPort(int port) {
    client.setPort(port);
    return this;
  }

  /**
   * Set the host that the tcpHelper will attempt to connect to, to {@code host}. The default value is {@code localhost}<p>
   * @return A reference to this, so multiple invocations can be chained together.
   */
  public synchronized HttpClient setHost(String host) {
    client.setHost(host);
    return this;
  }

  /**
   * Attempt to connect an HTML5 websocket to the specified URI<p>
   * The connect is done asynchronously and {@code wsConnect} is called back with the result
   */
  public synchronized void connectWebsocket(final String uri, final Handler<WebSocket> wsConnect) {
    connectWebsocket(uri, WebSocketVersion.HYBI_17, wsConnect);
  }

  /**
   * Attempt to connect an HTML5 websocket to the specified URI<p>
   * This version of the method allows you to specify the websockets version using the {@code wsVersion parameter}
   * The connect is done asynchronously and {@code wsConnect} is called back with the result
   */
  public synchronized void connectWebsocket(final String uri, final WebSocketVersion wsVersion, final Handler<WebSocket> wsConnect) {
    client.connectWebsocket(uri, wsVersion, wsConnect);
  }

  /**
   * This is a quick version of the {@link #get(String, org.vertx.java.core.Handler) get()}
   * method where you do not want to do anything with the request before sending.<p>
   * Normally with any of the HTTP methods you create the request then when you are ready to send it you call
   * {@link HttpClientRequest#end()} on it. With this method the request is immediately sent.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized void getNow(String uri, Handler<HttpClientResponse> responseHandler) {
    client.getNow(uri, responseHandler);
  }

  /**
   * This method works in the same manner as {@link #getNow(String, org.vertx.java.core.Handler)},
   * except that it allows you specify a set of {@code headers} that will be sent with the request.
   */
  public synchronized void getNow(String uri, Map<String, ? extends Object> headers, Handler<HttpClientResponse> responseHandler) {
    client.getNow(uri, headers, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP OPTIONS request with the specified {@code uri}.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest options(String uri, Handler<HttpClientResponse> responseHandler) {
    return client.options(uri, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP GET request with the specified {@code uri}.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest get(String uri, Handler<HttpClientResponse> responseHandler) {
    return client.get(uri, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP HEAD request with the specified {@code uri}.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest head(String uri, Handler<HttpClientResponse> responseHandler) {
    return client.head(uri, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP POST request with the specified {@code uri}.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest post(String uri, Handler<HttpClientResponse> responseHandler) {
    return client.post(uri, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP PUT request with the specified {@code uri}.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest put(String uri, Handler<HttpClientResponse> responseHandler) {
    return client.put(uri, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP DELETE request with the specified {@code uri}.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest delete(String uri, Handler<HttpClientResponse> responseHandler) {
    return client.delete(uri, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP TRACE request with the specified {@code uri}.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest trace(String uri, Handler<HttpClientResponse> responseHandler) {
    return client.trace(uri, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP CONNECT request with the specified {@code uri}.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest connect(String uri, Handler<HttpClientResponse> responseHandler) {
    return client.connect(uri, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP PATCH request with the specified {@code uri}.<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest patch(String uri, Handler<HttpClientResponse> responseHandler) {
    return client.patch(uri, responseHandler);
  }

  /**
   * This method returns an {@link HttpClientRequest} instance which represents an HTTP request with the specified {@code uri}. The specific HTTP method
   * (e.g. GET, POST, PUT etc) is specified using the parameter {@code method}<p>
   * When an HTTP response is received from the server the {@code responseHandler} is called passing in the response.
   */
  public synchronized HttpClientRequest request(String method, String uri, Handler<HttpClientResponse> responseHandler) {
    return client.request(method, uri, responseHandler);
  }

  /**
   * Close the HTTP tcpHelper. This will cause any pooled HTTP connections to be closed.
   */
  public synchronized void close() {
    client.close();
  }

  // SSL and TCP attributes

  /**
   * If {@code ssl} is {@code true}, this signifies that any connections will be SSL connections.
   * @return A reference to this, so multiple invocations can be chained together.
   */
  public synchronized HttpClient setSSL(boolean ssl) {
    client.setSSL(ssl);
    return this;
  }

  /**
   * Set the path to the SSL key store. This method should only be used in SSL mode, i.e. after {@link #setSSL(boolean)}
   * has been set to {@code true}.<p>
   * The SSL key store is a standard Java Key Store, and will contain the tcpHelper certificate. Client certificates are only required if the server
   * requests tcpHelper authentication.<p>
   * @return A reference to this, so multiple invocations can be chained together.
   */
  public synchronized HttpClient setKeyStorePath(String path) {
    client.setKeyStorePath(path);
    return this;
  }

  /**
   * Set the password for the SSL key store. This method should only be used in SSL mode, i.e. after {@link #setSSL(boolean)}
   * has been set to {@code true}.<p>
   * @return A reference to this, so multiple invocations can be chained together.
   */
  public synchronized HttpClient setKeyStorePassword(String pwd) {
    client.setKeyStorePassword(pwd);
    return this;
  }

  /**
   * Set the path to the SSL trust store. This method should only be used in SSL mode, i.e. after {@link #setSSL(boolean)}
   * has been set to {@code true}.<p>
   * The trust store is a standard Java Key Store, and should contain the certificates of
   * any servers that the client trusts.
   * If you wish the client to trust all server certificates you can use the {@link #setTrustAll(boolean)} method.<p>
   * @return A reference to this, so multiple invocations can be chained together.
   */
  public synchronized HttpClient setTrustStorePath(String path) {
    client.setTrustStorePath(path);
    return this;
  }

  /**
   * Set the password for the SSL trust store. This method should only be used in SSL mode, i.e. after {@link #setSSL(boolean)}
   * has been set to {@code true}.<p>
   * @return A reference to this, so multiple invocations can be chained together.
   */
  public synchronized HttpClient setTrustStorePassword(String pwd) {
    client.setTrustStorePassword(pwd);
    return this;
  }

  /**
   * If you want an SSL client to trust *all* server certificates rather than match them
   * against those in its trust store. Set this to true.
   * Use this with caution as you may be exposed to "main in the middle" attacks
   * @param trustAll Set to true if you want to trust all server certificates
   */
  public synchronized HttpClient setTrustAll(boolean trustAll) {
    client.setTrustAll(trustAll);
    return this;
  }

  /**
   * If {@code tcpNoDelay} is set to {@code true} then <a href="http://en.wikipedia.org/wiki/Nagle's_algorithm">Nagle's algorithm</a>
   * will turned <b>off</b> for the TCP connections created by this instance.
   * @return a reference to this so multiple method calls can be chained together
   */
  public synchronized HttpClient setTCPNoDelay(boolean tcpNoDelay) {
    client.setTCPNoDelay(tcpNoDelay);
    return this;
  }

  /**
   * Set the TCP send buffer size for connections created by this instance to {@code size} in bytes.
   * @return a reference to this so multiple method calls can be chained together
   */
  public synchronized HttpClient setSendBufferSize(int size) {
    client.setSendBufferSize(size);
    return this;
  }

  /**
   * Set the TCP receive buffer size for connections created by this instance to {@code size} in bytes.
   * @return a reference to this so multiple method calls can be chained together
   */
  public synchronized HttpClient setReceiveBufferSize(int size) {
    client.setReceiveBufferSize(size);
    return this;
  }

  /**
   * Set the TCP keepAlive setting for connections created by this instance to {@code keepAlive}.
   * @return a reference to this so multiple method calls can be chained together
   */
  public synchronized HttpClient setTCPKeepAlive(boolean keepAlive) {
    client.setTCPKeepAlive(keepAlive);
    return this;
  }

  /**
   * Set the TCP reuseAddress setting for connections created by this instance to {@code reuse}.
   * @return a reference to this so multiple method calls can be chained together
   */
  public synchronized HttpClient setReuseAddress(boolean reuse) {
    client.setReuseAddress(reuse);
    return this;
  }

  /**
   * Set the TCP soLinger setting for connections created by this instance to {@code reuse}.
   * @return a reference to this so multiple method calls can be chained together
   */
  public synchronized HttpClient setSoLinger(boolean linger) {
    client.setSoLinger(linger);
    return this;
  }

  /**
   * Set the TCP trafficClass setting for connections created by this instance to {@code reuse}.
   * @return a reference to this so multiple method calls can be chained together
   */
  public synchronized HttpClient setTrafficClass(int trafficClass) {
    client.setTrafficClass(trafficClass);
    return this;
  }

  /**
   * @return true if Nagle's algorithm is disabled.
   */
  public synchronized Boolean isTCPNoDelay() {
    return client.isTCPNoDelay();
  }

  /**
   * @return The TCP send buffer size
   */
  public synchronized Integer getSendBufferSize() {
    return client.getSendBufferSize();
  }

  /**
   * @return The TCP receive buffer size
   */
  public synchronized Integer getReceiveBufferSize() {
    return client.getReceiveBufferSize();
  }

  /**
   *
   * @return true if TCP keep alive is enabled
   */
  public synchronized Boolean isTCPKeepAlive() {
    return client.isTCPKeepAlive();
  }

  /**
   *
   * @return The value of TCP reuse address
   */
  public synchronized Boolean isReuseAddress() {
    return client.isReuseAddress();
  }

  /**
   *
   * @return the value of TCP so linger
   */
  public synchronized Boolean isSoLinger() {
    return client.isSoLinger();
  }

  /**
   *
   * @return the value of TCP traffic class
   */
  public synchronized Integer getTrafficClass() {
    return client.getTrafficClass();
  }

  /**
   *
   * @return true if this client will make SSL connections
   */
  public synchronized boolean isSSL() {
    return client.isSSL();
  }

  /**
   *
   * @return true if this client will trust all server certificates.
   */
  public synchronized boolean isTrustAll() {
    return client.isTrustAll();
  }

  /**
   *
   * @return The path to the key store
   */
  public synchronized String getKeyStorePath() {
    return client.getKeyStorePath();
  }

  /**
   *
   * @return The keystore password
   */
  public synchronized String getKeyStorePassword() {
    return client.getKeyStorePassword();
  }

  /**
   *
   * @return The trust store path
   */
  public synchronized String getTrustStorePath() {
    return client.getTrustStorePath();
  }

  /**
   *
   * @return The trust store password
   */
  public synchronized String getTrustStorePassword() {
    return client.getTrustStorePassword();
  }

}
