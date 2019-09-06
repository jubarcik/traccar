package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Position;

public class EelinkProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        EelinkProtocolDecoder decoder = new EelinkProtocolDecoder(null);

        // login (0x01)
        verifyNull(decoder, binary(
                "676701000c000103525440717505180104"));

        verifyAttributes(decoder, binary(
                "676707006502df5c89fde800bc3fa8030302005555045b555555057a5555550b225555550c105c55550d115555550e7e5555550f4555555510017b5555112b5555551f01ed5555208005b0012100005555407ad000004237f5555589000000498a0000aef78b00000000"));

        verifyAttribute(decoder, binary(
                "676712003400e45c5b0ade02012e03702d87064546aa24066a1086018a0000002dc1a0ffffffff0afd074d000000000000000000000000fce0"),
                Position.PREFIX_TEMP + 2, -50.0);

        verifyAttribute(decoder, binary(
                "6767120043000e5c37387c0304e4e1b4f8194fa800160013009408012e03702d8706453c6e5b066f115f05710000001b067f8d248d240313020500000000000000000000000001cc"),
                Position.PREFIX_TEMP + 2, 28.75);

        verifyPosition(decoder, binary(
                "676714002414B05AD43A7D03026B92B10C395499FFD7000000000701CC00002495000014203604067B"));

        verifyNotNull(decoder, binary(
                "676714004F14B0E68CAFE58AA8E68AA5E8ADA621E5B9BFE4B89CE79C81E6B7B1E59CB3E5B882E58D97E5B1B1E58CBAE696B0E8A5BFE8B7AF3138EFBC88E8B79DE5AE87E998B3E5A4A7E58EA63230E7B1B3EFBC89"));

        verifyPosition(decoder, binary(
                "676780005E5788014C754C754C61743A4E32332E3131313734330A4C6F6E3A453131342E3430393233380A436F757273653A302E30300A53706565643A302E31374B4D2F480A446174652054696D653A323031352D30392D31332032303A32313A3230"));


        // gps packet (0x02) with 10 bytes extra (device status/batt voltage/signal/adc1/adc2)
        verifyPosition(decoder, binary(
                "676702002500025868507603a1e92e03cf90fe000000019f000117ee00111e0120631145003101510000"));

        // terminal state (0x05)
        verifyPosition(decoder, binary(
                "6767050022000359643640000000000000000000000001CC0000249500142000015964A6C0006E"));

        verifyPosition(decoder, binary(
                "676702001c000459ae7387fcd360d6034332b2000000028f000a4f64002eb10101"));

        verifyPosition(decoder, binary(
                "6767050022000559643640000000000000000000000001CC0000249500142000015964A6C0006E"));

        // heartbeat (0x03)
        verifyAttributes(decoder, binary(
                "67670300040006006E"));

        // terminal state (0x05)
        verifyPosition(decoder, binary(
                "676705002200075964369D000000000000000000000001CC0000249500142000025964A71D006A"));

        // heartbeat (0x03)
        verifyAttributes(decoder, binary(
                "67670300040008006A"));

        // new proto - normal (0x12)
        verifyPosition(decoder, binary(
                "676712002d0009592cca6803002631a60b22127700240046005c08020d000301af000da0fd12007f11ce05820000001899c0"));

        // new proto - normal (0x12)
        verifyPosition(decoder, binary(
                "6767120034000a5784cc0b130246479b07d05a06001800000000070195039f046100002cc52e6466b391604a4900890e7c00000000000006ca"));

        //
        verifyAttributes(decoder, binary(
                "676712001e0092579714d60201f90001785003a301cd1a006a118504f2000000000000"));

        // new proto - warning (0x14)
        verifyPosition(decoder, binary(
                "676714002b000b5784cc24130246479b07d05a06001800010000060195039f046100002cc52f6466b391604a49020089"));
        // new proto - report (0x15)
        verifyAttributes(decoder, binary(
                "676715000a000c5685510b0002006a"));

        // login (0x01)
        verifyNull(decoder, binary(
                "676701000c001003541880486128290120"));

        // alarm (0x04)
        verifyPosition(decoder, binary(
                "676704001c0011569ff2dd0517a0f7020b0d9a06011000d8001e005b0004450183"));

        // terminal state (0x05)
        verifyPosition(decoder, binary(
                "67670500220012569fc3520517a0d8020b0f740f007100d8001e005b0004460101569fd162001f"));

        // gps packet (0x02) with 10 bytes (device status/batt voltage/signal/adc1/adc2)
        verifyPosition(decoder, binary(
                "67670200250013569fc3610517a091020b116000001900d8001e005b00044601001f1170003200000000"));

        // gps packet (0x02) only, no extra data
        verifyPosition(decoder, binary(
                "676702001b0014538086df0190c1790b3482df0f0157020800013beb00342401"));

        // alarm (0x04)
        verifyPosition(decoder, binary(
                "676704001c0015569fc3020517a2d7020b08e100000000d8001e005b0004460004"));

        // login (0x01)
        verifyNull(decoder, binary(
                "676701000b0016035418804661834901"));

        // heartbeat (0x03)
        verifyAttributes(decoder, binary(
                "676703000400170001"));

        // sms (0x06) PING? > PONG
        verifyPosition(decoder, binary(
                "6767060035001b598754d70585d9b3fffe5b4c00000000ea000a56d80078b00032302b34343739383736353433323100000000000050494e473f"));

        verifyAttributes(decoder, binary(
                "6767070088001050E2281400FFFFFFFF02334455660333445566043344556605AA00000007334455660A334455660B334455660C4E2000000DAA0000000E334455660F3344556610AAAA000011334455661C334455661F334455662133445566423344556646334455664D334455665C334455665E33445566880000000089000000008A000000008B00000000"));
    }

    @Test
    public void testDecodeUDP() throws Exception {
        EelinkProtocolDecoder decoder = new EelinkProtocolDecoder(null);

        // login (0x01) UDP
        verifyNull(decoder, binary(
            "454C0027E753035254407167747167670100180002035254407167747100200205020500010432000086BD"));

        verifyPosition(decoder, binary(
            "454C0050EAE2035254407167747167671200410021590BD93803026B940D0C3952AD0021000000000501CC0001A53F0170F0AB1305890F11000000000000C2D0001C001600000000000000000000000000000000"));

    }

    @Test
    public void testDecodeOBD() throws Exception {
        EelinkProtocolDecoder decoder = new EelinkProtocolDecoder(null);

        // login (0x01)
        verifyNull(decoder, binary(
            "676701000c000103525440717505180104"));

        // obd data (0x07)
        verifyAttributes(decoder, binary(
                "6767070088001050E2281400FFFFFFFF02334455660333445566043344556605AA00000007334455660A334455660B334455660C4E2000000DAA0000000E334455660F3344556610AAAA000011334455661C334455661F334455662133445566423344556646334455664D334455665C334455665E33445566880000000089000000008A000000008B00000000"));
    }

    @Test
    public void testDecodeDownlink() throws Exception {
        EelinkProtocolDecoder decoder = new EelinkProtocolDecoder(null);

        // login (0x01)
        verifyNull(decoder, binary(
            "676701000c000103525440717505180104"));

        // downlink data (0x80)
        verifyNotNull(decoder, binary(
            "676780005a00190100000000424154544552593a313030250a475052533a535543434553530a47534d3a4c4f570a4750533a434c4f5345442c300a4143433a4f46460a52454c41593a4f46460a504f5745523a4f4b0a4d533a4c4953334448"));

        verifyAttribute(decoder, binary(
            "676780005a001a0100000000424154544552593a313030250a475052533a535543434553530a47534d3a4c4f570a4750533a434c4f5345442c300a4143433a4f46460a52454c41593a4f46460a504f5745523a4f4b0a4d533a4c4953334448"),
            Position.KEY_RESULT,
            "BATTERY:100%\n" +
                    "GPRS:SUCCESS\n" +
                    "GSM:LOW\n" +
                    "GPS:CLOSED,0\n" +
                    "ACC:OFF\n" +
                    "RELAY:OFF\n" +
                    "POWER:OK\n" +
                    "MS:LIS3DH");

        verifyAttribute(decoder, binary(
            "676780005E5788014C754C754C61743A4E32332E3131313734330A4C6F6E3A453131342E3430393233380A436F757273653A302E30300A53706565643A302E31374B4D2F480A446174652054696D653A323031352D30392D31332032303A32313A3230"),
            Position.KEY_RESULT,
            "Lat:N23.111743\n" +
                    "Lon:E114.409238\n" +
                    "Course:0.00\n" +
                    "Speed:0.17KM/H\n" +
                    "Date Time:2015-09-13 20:21:20");

        verifyAttribute(decoder, binary(
            "676780005e001a01000000004c61743a4e32332e3131313734332c4c6f6e3a453131342e3430393233382c436f757273653a302e30302c53706565643a302e31374b4d2f482c446174652054696d653a323031352d30392d31332032303a32313a3230"),
            Position.KEY_RESULT,
            "Lat:N23.111743,Lon:E114.409238,Course:0.00,Speed:0.17KM/H,Date Time:2015-09-13 20:21:20");

        verifyAttribute(decoder, binary(
            "676780005d001a01000000004c61743a4e32332e3131313734332c4c6f6e3a453131342e3430393233382c436f757273653a302e30302c53706565643a302e31374b4d2f482c4461746554696d653a323031352d30392d31332032303a32313a3230"),
            Position.KEY_RESULT,
            "Lat:N23.111743,Lon:E114.409238,Course:0.00,Speed:0.17KM/H,DateTime:2015-09-13 20:21:20");
    }
}
