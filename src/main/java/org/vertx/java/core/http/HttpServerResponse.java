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

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.core.streams.WriteStream;

import java.util.Map;

/**
 * Represents a server-side HTTP response.
 * <p>
 * An instance of this class is created and associated to every instance of
 * {@link HttpServerRequest} that is created.<p>
 * It allows the developer to control the HTTP response that is sent back to the
 * client for the corresponding HTTP request. It contains methods that allow HTTP
 * headers and trailers to be set, and for a body to be written outto the response.
 * <p>
 * It also allows files to be streamed by the kernel directly from disk to the
 * outgoing HTTP connection, bypassing user space altogether (where supported by
 * the underlying operating system). This is a very efficient way of
 * serving files from the server since buffers do not have to be read one by one
 * from the file and written to the outgoing socket.
 * <p>
 * Instances of this class are not thread-safe
 * <p>
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public abstract class HttpServerResponse implements WriteStream {

  private static final Logger log = LoggerFactory.getLogger(HttpServerResponse.class);

  protected HttpServerResponse() {
  }

  /**
   * The HTTP status code of the response. The default is {@code 200} representing {@code OK}.
   */
  public int statusCode = HttpResponseStatus.OK.getCode();

  /**
   * The HTTP status message of the response. If this is not specified a default value will be used depending on what
   * {@link #statusCode} has been set to.
   */
  public String statusMessage = null;

  /**
   * If {@code chunked} is {@code true}, this response will use HTTP chunked encoding, and each call to write to the body
   * will correspond to a new HTTP chunk sent on the wire. If chunked encoding is used the HTTP header
   * {@code Transfer-Encoding} with a value of {@code Chunked} will be automatically inserted in the response.<p>
   * If {@code chunked} is {@code false}, this response will not use HTTP chunked encoding, and therefore if any data is written the
   * body of the response, the total size of that data must be set in the {@code Content-Length} header <b>before</b> any
   * data is written to the response body. If no data is written, then a {@code Content-Length} header with a value of {@code 0}
   * will be automatically inserted when the response is sent.<p>
   * An HTTP chunked response is typically used when you do not know the total size of the request body up front.
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse setChunked(boolean chunked);

  /**
   * Inserts a header into the response. The {@link Object#toString()} method will be called on {@code value} to determine
   * the String value to actually use for the header value.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse putHeader(String key, Object value);

  /**
   * Inserts all the specified headers into the response.
   * The {@link Object#toString()} method will be called on the header values {@code value} to determine
   * the String value to actually use for the header value.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse putAllHeaders(Map<String, ? extends Object> m);

  /**
   * Inserts a trailer into the response. The {@link Object#toString()} method
   * will be called on {@code value} to determine
   * the String value to actually use for the trailer value.<p>
   * Trailers are only sent if you are using a HTTP chunked response.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse putTrailer(String key, Object value);

  /**
   * Inserts all the specified trailers into the response.
   * The {@link Object#toString()} method will be called on {@code value} to determine
   * the String value to actually use for the trailer value.<p>
   * Trailers are only sent if you are using a HTTP chunked response.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse putAllTrailers(Map<String, ? extends Object> m);

  /**
   * Set a close handler for the response. This will be called if the underlying connection closes before the response
   * is complete.
   * @param handler
   */
  public abstract void closeHandler(Handler<Void> handler);

  /**
   * Write a {@link Buffer} to the response body.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse write(Buffer chunk);

  /**
   * Write a {@link String} to the response body, encoded using the encoding {@code enc}.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse write(String chunk, String enc);

  /**
   * Write a {@link String} to the response body, encoded in UTF-8.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse write(String chunk);

  /**
   * Write a {@link Buffer} to the response body. The {@code doneHandler} is called after the buffer is actually written to the wire.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse write(Buffer chunk, Handler<Void> doneHandler);

  /**
   * Write a {@link String} to the response body, encoded with encoding {@code enc}. The {@code doneHandler} is called after the buffer is actually written to the wire.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse write(String chunk, String enc, Handler<Void> doneHandler);

  /**
   * Write a {@link String} to the response body, encoded in UTF-8. The {@code doneHandler} is called after the buffer is actually written to the wire.<p>
   *
   * @return A reference to this, so multiple method calls can be chained.
   */
  public abstract HttpServerResponse write(String chunk, Handler<Void> doneHandler);

  /**
   * Same as {@link #end(Buffer)} but writes a String with the default encoding
   */
  public abstract void end(String chunk);

  /**
   * Same as {@link #end(Buffer)} but writes a String with the specified encoding
   */
  public abstract void end(String chunk, String enc);

  /**
   * Same as {@link #end()} but writes some data to the response body before ending. If the response is not chunked and
   * no other data has been written then the Content-Length header will be automatically set
   */
  public abstract void end(Buffer chunk);

  /**
   * Ends the response. If no data has been written to the response body,
   * the actual response won't get written until this method gets called.<p>
   * Once the response has ended, it cannot be used any more.
   */
  public abstract void end();

  /**
   * Tell the kernel to stream a file as specified by {@code filename} directly
   * from disk to the outgoing connection, bypassing userspace altogether
   * (where supported by the underlying operating system.
   * This is a very efficient way to serve files.<p>
   */
  public abstract HttpServerResponse sendFile(String filename);

  /**
   * Close the underlying TCP connection
   */
  public abstract void close();

}
