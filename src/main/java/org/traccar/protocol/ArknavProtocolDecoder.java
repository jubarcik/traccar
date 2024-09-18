/*
 * Copyright 2016 - 2018 Anton Tananaev (anton@traccar.org)
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
package org.traccar.protocol;

import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.session.DeviceSession;
import org.traccar.Protocol;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.util.regex.Pattern;

public class ArknavProtocolDecoder extends BaseProtocolDecoder {

    public ArknavProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .number("(d+),")                     // imei
            .expression(".{6},")                 // id code
            .number("(x{3}),")                   // status
            .expression("(.{4}),")               // version
            .expression("([AV]),")               // validity
            .number("(dd)(dd.d+),")              // latitude
            .expression("([NS]),")
            .number("(ddd)(dd.d+),")             // longitude
            .expression("([EW]),")
            .number("(d+.?d*),")                 // speed
            .number("(d+.?d*),")                 // course
            .number("(d+.?d*),")                 // hdop
            .number("(dd):(dd):(dd) ")           // time (hh:mm:ss)
            .number("(dd)-(dd)-(dd),")           // date (dd-mm-yy)
            .expression(".{4},")                 // Unit Version number
            .expression("(..)")                  // Battery level
            .any()
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        Parser parser = new Parser(PATTERN, (String) msg);
        if (!parser.matches()) {
            return null;
        }

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());
        long status = parser.nextHexLong();
        String model = parser.next();
        position.setValid(parser.next().equals("A"));
        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());
        position.setSpeed(UnitsConverter.kphFromKnots(parser.nextDouble(0)));
        position.setCourse(parser.nextDouble(0));
        position.set(Position.KEY_HDOP, parser.nextDouble(0));
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.HMS_DMY));
        if (model.equals("PT33")) {
            position.set(Position.KEY_BATTERY_LEVEL, parser.nextInt());
            if ((status & 0x100L) != 0) {
                position.addAlarm(Position.ALARM_LOW_BATTERY);
            }
            if ((status & 0x200L) != 0) {
                position.addAlarm(Position.ALARM_MOVEMENT);
            }
            if ((status & 0x400L) != 0) {
                position.addAlarm(Position.ALARM_GEOFENCE_EXIT);
            }
            if ((status & 0x010L) != 0) {
                position.addAlarm(Position.ALARM_SOS);
            }
            if ((status & 0x001L) != 0) {
                position.addAlarm(Position.ALARM_OVERSPEED);
            }
        }

        return position;
    }

}
