import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public class Processor {
	static Router local = null;	
	static Thread t_timer = null;
	static Thread t_listner = null;
	static Thread t_broadcast = null;
	public static void main(String[] agrs) {
		local = new Router(agrs);
		t_listner = new Thread(new Listner(local));
		t_timer = new Thread(new Timer(local));
		t_broadcast = new Thread(new Broadcast(local));
		t_timer.start();
		t_listner.start();
		t_broadcast.start();
		UserInterface();
	}
	public static void UserInterface() {
		Scanner scanner;
		while (true) {
			scanner = new Scanner(System.in);
			String[] command = scanner.next().split(" ");
			if (command.length == 0) continue;
			String order = command[0];
			if (order.compareTo("CLOSE") == 0) break;
			synchronized(local) {
				if (order.compareTo("LINKUP") == 0) {
					local.Linkup(command[1], true);
				} else if (order.compareTo("LINKDOWN") == 0) {
					local.LinkDown(command[1], true);
				} else {	
					HashMap<String, Node> cost = local.getTable();
					for (String key : cost.keySet()) {
						Node n = cost.get(key);
						String output = "Destination : " + key + ',' + "Cost = " + String.valueOf(n.cost) + ',' 
									 	+ "Link = " + n.path;
						System.out.println(output);
					}
				}
			}
		}
		t_timer.interrupt();
		t_listner.interrupt();
		t_broadcast.interrupt();
		scanner.close();
	}
	
}
//After time out, send table to its neighbors
class Broadcast implements Runnable {
	private Router local;
	public Broadcast(Router r) {
		local = r;
	}
	public void run() {
		broadcast();
	}
	public void broadcast() {
		while (!Thread.interrupted()) {
			try {
				Thread.sleep(local.timeOut() * 1000);
				local.Broadcast();
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
//detect dead neighbors
class Timer implements Runnable {
	private Router local;
	public Timer (Router r) {
		local = r;
	}
	public void run() {
		exec();
	}
	public void exec() {
		while (!Thread.interrupted()) {
			local.clearVisitor();
			try {
				Thread.sleep(3 * local.timeOut() * 1000);
				synchronized(local) {
					HashSet<String> recentUpload = local.getVisitedList();
					HashSet<String> neighbors = local.getNeighbor();
					Iterator<String> iterator = neighbors.iterator();
					while (iterator.hasNext()) {
						String n = iterator.next();
						if (!recentUpload.contains(n)) {
							local.LinkDown(n, false);
						}
					}
				}
			} catch (InterruptedException e) {
				System.out.println("logOut");
				break;
			}	
		}
	}
}
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
	

