import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

class Listner implements Runnable {
	private DatagramSocket socket;
	private Router local;
	public Listner(Router r) {
		local = r;
	}
	public void run() {
		try {
			socket = new DatagramSocket(local.getPort());
			local.setSocket(socket);
			execute();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	public void execute() {
		byte [] incomingData = new byte[1024*1024];
		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
		while (!Thread.interrupted()) {
			Arrays.fill(incomingData, (byte) 0);
			try {
				socket.receive(incomingPacket);
				String message = new String(incomingPacket.getData());
				System.out.println(message); 
				String ip = incomingPacket.getAddress().toString();
				String port = String.valueOf(incomingPacket.getPort());
				String name = ip + ":" + port;
				String[] m = message.split(" ");
				synchronized(local) {
					local.addVisitor(name); 
					local.Update(name, m);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}