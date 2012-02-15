package de.openCF.protocol;

public interface PacketHandler {

	public Packet handlePacket(Packet packet);

	public void handleClose();

	public void handleOpen();

}
