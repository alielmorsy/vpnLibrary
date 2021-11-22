import aie.vpnLibrary.messages.BaseMessage;
import aie.vpnLibrary.messages.RequestMessage;
import aie.vpnLibrary.messages.ResponseMessage;
import aie.vpnLibrary.messages.enums.MethodType;

import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        ResponseMessage message = new ResponseMessage();

        System.out.println(new String(message.buildMessage().array()));
    }
}
