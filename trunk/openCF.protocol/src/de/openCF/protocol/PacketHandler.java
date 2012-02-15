package de.openCF.protocol;

public interface PacketHandler {

	public void handlePacket(Packet packet);

	public void handleClose();

	public void handleOpen();

}
