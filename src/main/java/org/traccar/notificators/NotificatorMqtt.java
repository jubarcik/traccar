/*
 * Copyright 2020 Francesco Rega (francesco@francescorega.eu)
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
package org.traccar.notificators;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.Context;
import org.traccar.model.Event;
import org.traccar.model.Position;
import org.traccar.notification.NotificationFormatter;

import java.util.HashMap;
import java.util.Map;

public final class NotificatorMqtt extends Notificator {

    // Get Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificatorMqtt.class);

    // Initialize connection variables
    private String mqttHost;
    private String mqttUser;
    private String mqttPass;
    private String clientId;

    public NotificatorMqtt() {

        // Read connection configuration values
        mqttHost = Context.getConfig().getString("notificator.mqtt.hostname");
        mqttUser = Context.getConfig().getString("notificator.mqtt.username");
        mqttPass = Context.getConfig().getString("notificator.mqtt.password");
        clientId = (StringUtils.isNotBlank(Context.getConfig().getString("notificator.mqtt.clientid")))
                ? Context.getConfig().getString("notificator.mqtt.clientid")
                : MqttClient.generateClientId();
    }

    @Override
    public void sendSync(long userId, Event event, Position position) {

        try {

            // Create placeholders and substitutors map
            Map<String, String> values = new HashMap<String, String>();
            values.put("U", String.valueOf(userId));
            values.put("E", event.getType());
            values.put("D", String.valueOf(event.getDeviceId()));
            values.put("G", String.valueOf(event.getGeofenceId()));
            values.put("P", String.valueOf(event.getPositionId()));
            values.put("M", NotificationFormatter.formatShortMessage(userId, event, position));
            StrSubstitutor sub = new StrSubstitutor(values, "%", "%");

            // Read configured topic to publish to, if any, or set default
            String topic = (StringUtils.isNotBlank(Context.getConfig().getString("notificator.mqtt.topic")))
                    ? Context.getConfig().getString("notificator.mqtt.topic")
                    : "/Traccar/Notification/" + event.getType();

            // Read configured payload to publish, if any, or set default
            String payload = (StringUtils.isNotBlank(Context.getConfig().getString("notificator.mqtt.payload")))
                    ? Context.getConfig().getString("notificator.mqtt.payload")
                    : NotificationFormatter.formatShortMessage(userId, event, position);

            // Replace placeholders with real values
            topic = sub.replace(topic);
            payload = sub.replace(payload);

            // Create MQTT client
            IMqttClient client = new MqttClient(mqttHost, clientId);

            // Set connection options (username, password), if defined, and connect
            if ((StringUtils.isNotBlank(mqttUser)) && (StringUtils.isNotBlank(mqttPass))) {
                MqttConnectOptions connOpts = setUpConnectionOptions(mqttUser, mqttPass);
                client.connect(connOpts);
            } else {
                client.connect();
            }

            // Create and publish message
            MqttMessage msg = new MqttMessage();
            msg.setPayload(payload.getBytes());
            msg.setQos(0);
            msg.setRetained(false);
            client.publish(topic, msg);

            // Disconnect from the broker
            client.disconnect();

        } catch (MqttSecurityException e) {
            LOGGER.warn("MqttSecurityException", e);
        } catch (MqttException e) {
            LOGGER.warn("MqttException", e);
        }
    }

    @Override
    public void sendAsync(long userId, Event event, Position position) {
        sendSync(userId, event, position);
    }

    private static MqttConnectOptions setUpConnectionOptions(String username, String password) {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName(username);
        connOpts.setPassword(password.toCharArray());
        return connOpts;
    }

}
