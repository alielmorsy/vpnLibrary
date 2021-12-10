package aie.vpnLibrary.server.exceptions;

public class OfficeInUse extends Exception{
    public OfficeInUse(String officeName) {
        super("Office: "+officeName+" Currently in use");
    }
}
