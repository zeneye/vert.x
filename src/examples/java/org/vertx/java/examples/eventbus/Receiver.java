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

package org.vertx.java.examples.eventbus;

import org.vertx.java.core.Handler;
import org.vertx.java.deploy.Verticle;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Receiver implements Verticle, Handler<Message<String>> {

  String address = "example.address";
  String creditsAddress = "example.credits";

  EventBus eb = EventBus.instance;

  @Override
  public void start() throws Exception {
    eb.registerHandler(address, this);
  }

  int received;
  int count;

  public void handle(Message<String> message) {
    received++;
    if (received == 1000) {
      eb.send(creditsAddress, (String)null);
      received = 0;
    }
    count++;
    if (count % 10000 == 0) {
      System.out.println("Received messsage " + count);
    }
  }

  @Override
  public void stop() throws Exception {
    eb.unregisterHandler(address, this);
  }
}