import java.util.HashSet;
import java.util.Iterator;

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