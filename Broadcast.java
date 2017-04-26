
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