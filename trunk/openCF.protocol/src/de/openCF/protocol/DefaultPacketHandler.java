package de.openCF.protocol;

public class DefaultPacketHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet) {
		System.out.println("handlePacket");
		System.out.println(packet.dump());
	}

	@Override
	public void handleClose() {
		System.out.println("handleClose");
	}

	@Override
	public void handleOpen() {
		System.out.println("handleOpen");
	}

}
