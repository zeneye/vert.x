/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vertx.java.examples.httpperf;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.deploy.Verticle;

public class RateCounter implements Verticle, Handler<Message<Integer>> {

  private long start;

  private long count = 0;

  public void handle(Message<Integer> msg) {
    if (start == 0) {
      start = System.currentTimeMillis();
    }
    count += msg.body;
    if (count % 100000 == 0) {
      long now = System.currentTimeMillis();
      double rate = 1000 * (double)count / (now - start);
      System.out.println("Reqs/sec: " + rate);
    }
  }

  public void start() {
    EventBus.instance.registerHandler("rate-counter", this);
  }

  public void stop() {
    EventBus.instance.unregisterHandler("rate-counter", this);
  }


}
