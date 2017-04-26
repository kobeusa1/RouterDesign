import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;

public class Router {
	private int port; 
	private int timeOut; //in second
	DatagramSocket socket;
	private HashMap<String, Node> table; //store path and costs to other router
	private HashSet<String> neighbors; //neighbors list
	private HashSet<String> visited = new HashSet<>(); //store receiver with time out
	private HashMap<String, Integer> copy = new HashMap<>();
	public Router(String[] input) {
		this.port = Integer.valueOf(input[0]);
		this.timeOut = Integer.valueOf(input[1]);
		for (int i = 2; i < input.length - 3; i+= 3) {
			String s = input[i] + ":" + input[i + 1];
			int cost = Integer.valueOf(input[i + 2]);
			table.put(s,  new Node(cost, s));
			copy.put(s, cost);
		}
	}
	protected void setSocket(DatagramSocket s) {
		this.socket = s;
	}
	protected void addVisitor(String n) {
		visited.add(n);
	}
	protected HashSet<String> getNeighbor() {
		return neighbors;
	}
	protected void clearVisitor() {
		visited.clear();
	}
	protected HashSet<String> getVisitedList() {
		return visited;
	}
	protected HashMap<String, Node> getTable() {
		return table;
	}
	protected int getPort() {
		return port;
	}
	protected int timeOut() {
		return timeOut;
	}
	//Broadcast table to its neighbors;
	protected void Broadcast() {
		String message = "";
		for (String t : table.keySet()) {
			Node n = table.get(t);
			message += t + " " + String.valueOf(n.cost);
		}
		for (String str : neighbors) {
			Solocast(str, message);
		}
	}
	protected void Solocast(String str, String message) {
		String[] in = str.split(":");
		String ip = in[0], port = in[1];
		try {
			InetAddress i= InetAddress.getByName(ip);
			DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), i,Integer.valueOf(port));
			socket.send(dp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}			
	}
	protected void Update(String neighbor, String[] message) {
		if (message.length == 1) {
			if (message[0].compareTo("Linkup") == 0) {
				Linkup(neighbor, false);
			} else {
				LinkDown(neighbor, false);
			}
			return;
		}
		boolean converge = true; 
		int cost = table.get(neighbor).cost;
		for (int i = 0; i < message.length - 1; i+= 2) {
			String name = message[i];
			int costTo = Integer.valueOf(message[i + 1]);
			table.putIfAbsent(name, new Node(Integer.MAX_VALUE, "Unreachable"));
			if (costTo == Integer.MAX_VALUE) continue;
			Node n = table.get(name);
			if (n.cost > cost + costTo) {
				n.cost = cost + costTo;
				n.path = neighbor;
				converge = false;
			}
		}
		if (!converge) Broadcast(); //not converge, send update information.
	}
	protected void Linkup(String s, boolean toSend) {
		neighbors.add(s);
		table.get(s).cost = copy.get(s);
		if (toSend) Solocast(s, "Linkup");
		//wait some time for its neighbor deal with link up
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Broadcast();
	}
	protected void LinkDown(String s, boolean toSend) {
		neighbors.remove(s);
		table.get(s).cost = Integer.MAX_VALUE;
		if (toSend) Solocast(s, "LinkDown");
		//wait some time for its neighbor deal with link down
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Broadcast();
	}
}
	