package de.openCF.protocol;

public class DefaultPacketHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet) {
		System.out.println(packet.dump());
	}

	@Override
	public void handleClose() {
	}

	@Override
	public void handleOpen() {
	}

}
