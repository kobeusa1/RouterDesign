import java.util.HashMap;
import java.util.Scanner;
public class Main {
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



	

