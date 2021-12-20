package aie.vpnLibrary.server;

import aie.vpnLibrary.messages.BaseMessage;
import aie.vpnLibrary.messages.RequestMessage;
import aie.vpnLibrary.messages.ResponseMessage;
import aie.vpnLibrary.messages.enums.MethodType;
import aie.vpnLibrary.messages.enums.PostType;
import aie.vpnLibrary.server.connnectionManager.ConnectionManager;
import aie.vpnLibrary.server.exceptions.OfficeInUse;
import aie.vpnLibrary.server.exceptions.RequestException;
import aie.vpnLibrary.server.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Test {
    public static void main(String[] args) {
        VPNServer server = new VPNServer(new ServerConfiguration().setClientAuth(false).setPort(9999).setNumberOfPools(6));
        server.bind();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    String a= Arrays.toString(new ConnectionManager("a") {
                        @Override
                        public void editRequest(RequestMessage requestMessage) {
                            requestMessage.setHeader("Accept", "image/jpeg  application/x-ms-application \0 image/gif\0  application/xaml+xml  image/pjpeg  application/x-ms-xbap \0 application/msword\0 application/vnd.ms-powerpoint\0 application/vnd.ms-excel\0 */*");
                            if (requestMessage.getMethod() == MethodType.POST && requestMessage.getPostType() == PostType.XML) {
                                requestMessage.setHeader("SOAPAction", "TP.SED.SIMActivationServices/BioMetrixVerfication");
                            }else{
                                requestMessage.setUrl("http://bvs.telenor.com.pk/fca-789/" + requestMessage.getUrl());
                            }
                            requestMessage.setHeader("Refer", requestMessage.getUrl());
                        }
                    }.requestGET("http://www.google.com"));
                    System.out.println(a);
                } catch (UserNotFoundException | OfficeInUse | RequestException e) {
                    e.printStackTrace();
                }
            }
        },20000);

    }
}
