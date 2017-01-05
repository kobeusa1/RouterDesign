
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


public class bfclient{
    
    static String port_update;
    static int flag_block =0;
    static String ip_addr;
    static String cost;
    static int flag_update_information;
    static int flag_link=0;
    static int time_out;
    static int flag_work_done = 1;
    static int flag_userinput = 0;
    static int flag_usercommand;
    static int stop = 0;
    static int time_out_message;
    static String ip_addr_received;
    static int port_received;
    static String ip_change = null;
    static int flag_transmit_done = 1;
    static String port_change = null;
    static ArrayList<String>list_of_received = new ArrayList<String>();
    static ArrayList<String>list_of_remote = new ArrayList<String>();
    static ArrayList<String>list_of_nebighor = new ArrayList<String>();
    static ArrayList<String>list_of_recovery = new ArrayList<String>();
    static 	int flag_transport = 1;
    static int flag_relog= 0;
    static int flag_relog_confirm = 0;
    static String ip_host;
    static int port;
    
    public static void main(String args[]) throws IOException {
        
        DatagramSocket socket = null;
        port = Integer.parseInt(args[0]);
        //int cc = 0;
        
        String ip_hoste =InetAddress.getLocalHost().toString();
        int x= ip_hoste.indexOf('/');
        ip_host = ip_hoste.substring(x+1);
        time_out_message = Integer.parseInt(args[1]);
        socket = new DatagramSocket(port);
        listen server = new listen(socket);
        server.start();
        Update_information send = new Update_information(socket);
        send.start();
        Time_out thread = new Time_out();
        thread.start();
        User_input input = new User_input();
        input.start();
        Timer timer = new Timer();
        timer.start();
        list_of_nebighor.add(ip_host);
        list_of_nebighor.add(String.valueOf(port));
        list_of_nebighor.add(String.valueOf(0));
        list_of_nebighor.add(ip_host);
        list_of_nebighor.add(String.valueOf(port));
        
        int index;
        for(index = 2;index<=args.length-3;index+=3){
            list_of_nebighor.add(args[index]);
            list_of_nebighor.add(args[index+1]);
            list_of_nebighor.add(args[index+2]);
            list_of_nebighor.add(args[index]);
            list_of_nebighor.add(args[index+1]);
            
        }
        
        //   System.out.println(list_of_nebighor);
        
        // System.out.println("Received nebighor is ");
        //System.out.println(list_of_nebighor);
        
        list_of_recovery.addAll(list_of_nebighor);
        int cost_to_local=0;
        String link_ip = " ";
        String link_port = " ";
        int flag = 0 ;
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(flag_link ==1){
                flag_link = 0;
                while(flag_transmit_done == 0){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                
                for (int d = 5; d<= list_of_nebighor.size()-5;d+=5){
                    String link_donw = list_of_nebighor.get(d);
                    int port_down = Integer.parseInt(list_of_nebighor.get(d+1));
                    if(link_donw.compareTo(ip_addr_received)==0 && port_down == port_received){
                        int co = 0;
                        while(co<=4){
                            co++;
                            list_of_nebighor.remove(d);
                        }
                        break;
                        
                    }
                }
                
                flag_work_done = 1;
                //flag_transport = 1;
                
            }
            if(flag_link == 2){
                flag_link = 0;
                //flag_work_done = 0;
                list_of_nebighor.add(ip_addr_received);
                list_of_nebighor.add(String.valueOf(port_received));
                list_of_nebighor.add(String.valueOf(1000));
                list_of_nebighor.add(ip_addr_received);
                list_of_nebighor.add(String.valueOf(port_received));
                while(flag_transmit_done == 0){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                for(int xx = 0;xx<= list_of_remote.size()-5; xx+=5){
                    String ipc = list_of_remote.get(xx);
                    String portc = list_of_remote.get(xx+1);
                    if(ipc.compareTo(ip_addr_received) ==0 && portc.compareTo(String.valueOf(port_received))==0){
                        int co = 0;
                        while(co<=4){
                            co++;
                            list_of_remote.remove(xx);
                        }
                        break;
                        
                    }
                    
                }
                
                flag_work_done = 1;
                flag_transport =1;
                
            }
            
            
            if(flag_update_information ==1){
                // thread.interrupt();
                timer.interrupt();
                flag_update_information = 0;
                flag = 0;
                for(int xx = 5 ; xx<=list_of_nebighor.size()-5;xx+=5){
                    int p = Integer.parseInt(list_of_nebighor.get(xx+4));
                    int p1 = Integer.parseInt(list_of_nebighor.get(xx+1));
                    if(ip_addr_received.compareTo(list_of_nebighor.get(xx+3))==0 && port_received == p ){
                        if(ip_addr_received.compareTo(list_of_nebighor.get(xx)) != 0 || p1!= port_received ){
                            //System.out.println("The change port is "  + list_of_nebighor.get(xx+1)) ;
                            //System.out.println("cost changed by " +port_received);
                            list_of_nebighor.set(xx+2, String.valueOf(1000));
                        }
                    }
                }
                if(!list_of_remote.isEmpty()){
                    for(int xx = 0 ; xx<=list_of_remote.size()-5;xx+=5){
                        int p = Integer.parseInt(list_of_remote.get(xx+4));
                        int p1 = Integer.parseInt(list_of_remote.get(xx+1));
                        if(ip_addr_received.compareTo(list_of_remote.get(xx+3))==0 && port_received == p ){
                            if(ip_addr_received.compareTo(list_of_remote.get(xx)) != 0 || p1!= port_received ){
                                list_of_remote.set(xx+2, String.valueOf(1000));
                                //  System.out.println("The change port is "  + list_of_remote.get(xx+1)) ;
                                //  System.out.println("cost changed by " +port_received);
                            }
                            
                        }
                        
                    }
                    
                }
                int flag_neibhor = 0;
                int flag_source = 0;
                String ip_r;
                String port_r;
                
                //identify the source and cost and link to that source
                
                for(int i = 0;i<=list_of_nebighor.size()-5;i+=5){
                    String ip_n = list_of_nebighor.get(i);
                    int portnum = Integer.parseInt(list_of_nebighor.get(i+1));
                    if(ip_n.compareTo(ip_addr_received)==0 && portnum==port_received){
                        cost_to_local = Integer.parseInt(list_of_nebighor.get(i+2));
                        link_ip = list_of_nebighor.get(i+3);
                        link_port = list_of_nebighor.get(i+4);
                        flag_source = 1;
                        break;
                    }
                }
                
                if(flag_source ==0){
                    flag_source = 0;
                    System.out.println("Coming from " + port_received);
                    System.out.println(list_of_received);
                    System.out.println("Undefined Neibhor Router");
                    //flag_transport = 1;
                    flag_work_done = 1;
                    continue;
                }
                
                
                for(int i = 0;i<=list_of_received.size()-3;i+=3){
                    // counter ++;
                    //System.out.println(counter);
                    int pointer = -1;
                    String router_ip = list_of_received.get(i);
                    String router_port = list_of_received.get(i+1);
                    String inf = "INF";
                    if(inf.compareTo(list_of_received.get(i+2))==0){
                        
                        continue;
                    }
                    int cost_received = Integer.parseInt(list_of_received.get(i+2));
                    int router_cost = cost_to_local+ cost_received;
                    //System.out.println("Cost _to_local is " + cost_to_local);
                    //System.out.println("Cost received is " +cost_received);
                    
                    for(int d = 0;d<=list_of_nebighor.size()-5;d+=5){
                        String ip_n = list_of_nebighor.get(d);
                        String portnum = list_of_nebighor.get(d+1);
                        int cost_n = Integer.parseInt(list_of_nebighor.get(d+2));
                        //System.out.println("cost_n is " + cost_n);
                        
                        if(ip_n.compareTo(router_ip)==0 && portnum.compareTo(router_port)==0){
                            if(cost_n>router_cost){
                                list_of_nebighor.set(d+2,String.valueOf(router_cost));
                                list_of_nebighor.set(d+3,ip_addr_received);
                                list_of_nebighor.set(d+4,String.valueOf(port_received));
                                flag = 1;
                            }
                            flag_neibhor = 1;
                            break;
                        }
                    }
                    if(flag_neibhor ==1){
                        flag_neibhor = 0;
                        continue;
                    }
                    //if it is not neighbor router and is new,get pointer
                    if(!list_of_remote.isEmpty()){
                        for(int c= 0;c<=list_of_remote.size()-5;c+=5){
                            ip_r = list_of_remote.get(c);
                            port_r = list_of_remote.get(c+1);
                            //System.out.println(ip_r);
                            //System.out.println(port_r);
                            if(ip_r.compareTo(router_ip)==0 && port_r.compareTo(router_port)==0){
                                
                                pointer = c;
                                //  System.out.println("the pointer is " +pointer);
                                break;
                            }
                        }
                        
                    }
                    // add the new router into Arraylist
                    if(pointer ==-1){
                        list_of_remote.add(router_ip);
                        list_of_remote.add(router_port);
                        list_of_remote.add(String.valueOf(router_cost));
                        list_of_remote.add(link_ip);
                        list_of_remote.add(String.valueOf(link_port));
                        flag = 1;
                        // System.out.println("NEW ROUTER FOUND");
                        
                    }else{
                        //not new, check for update
                        int cost_saved =Integer.parseInt(list_of_remote.get(pointer+2));
                        if(cost_saved > router_cost){
                            list_of_remote.set(pointer+2,String.valueOf(router_cost));
                            list_of_remote.set(pointer+3,link_ip);
                            list_of_remote.set(pointer+4,String.valueOf(link_port));
                            flag = 1;
                        }
                        
                    }
                    
                }
                for(int a = 5; a<= list_of_recovery.size()-5;a+=5){
                    String ip_recovery = list_of_recovery.get(a);
                    int port_recovery = Integer.parseInt(list_of_recovery.get(a+1));
                    int cost_recovery = Integer.parseInt(list_of_recovery.get(a+2));
                    for (int aa = 5; aa<=list_of_nebighor.size()-5;aa+=5){
                        String ip_record = list_of_nebighor.get(aa);
                        int port_record = Integer.parseInt(list_of_nebighor.get(aa+1));
                        int cost_record = Integer.parseInt(list_of_nebighor.get(aa+2));
                        if(ip_record.compareTo(ip_recovery)== 0 && port_recovery == port_record){
                            if(cost_recovery<cost_record)
                            {
                                list_of_nebighor.set(aa+2, String.valueOf(cost_recovery));
                                list_of_nebighor.set(aa+3,ip_recovery);
                                list_of_nebighor.set(aa+4, String.valueOf(port_recovery));
                            }
                        }
                    }
                    
                }
                
                while(flag_transmit_done == 0){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if(flag == 1){
                    flag_transport = 1;
                }
                flag_work_done = 1;
            }
            if(flag_userinput ==1){
                flag_userinput =0;
                if (flag_usercommand ==2){
                    System.out.println("<Current Time>Distance Vector list is");
                    int size = list_of_nebighor.size()/5 - 1;
                    int size_remote = list_of_remote.size()/5;
                    int i;
                    int ctb= 0;
                    int ctc = 0;
                    String cost = null;
                    String output = " ";
                    String link = null;
                    for (i =0;i<size;i++){
                        ctb +=5;
                        if(Integer.valueOf(list_of_nebighor.get(2+ctb)) >=1000){
                            cost = "Unreachable";
                            link = "No Such Path";
                            
                        }else{
                            cost = list_of_nebighor.get(2+ctb);
                            link = list_of_nebighor.get(ctc+3) + ":"+list_of_nebighor.get(ctc+4);
                        }
                        
                        output = "Destination =" + list_of_nebighor.get(ctb)+":"+list_of_nebighor.get(1+ctb)+ ",  "+ "Cost = "+cost;
                        output +=", "+ "LINK = " +link;
                        System.out.println(output);
                    }
                    for (i =0;i<size_remote;i++){
                        if(Integer.valueOf(list_of_remote.get(2+ctc)) >=1000){
                            cost = "Unreachable";
                            link = "No Such Path";
                            
                        }else{
                            cost = list_of_remote.get(2+ctc);
                            link = list_of_remote.get(ctc+3) + ":"+list_of_remote.get(ctc+4);
                        }
                        
                        output = "Destination =" + list_of_remote.get(ctc)+":"+list_of_remote.get(1+ctc)+ ", "+ "Cost = "+cost;
                        output +=", "+ "LINK = " + link;
                        System.out.println(output);
                        ctc+= 5;
                        
                    }
                    
                }
                if(flag_usercommand==0){
                    String message = "LINKDOWN";
                    InetAddress ip1 = InetAddress.getByName(ip_change);
                    DatagramPacket dp = new DatagramPacket(message.getBytes(),message.length(),ip1,Integer.parseInt(port_change));
                    socket.send(dp);
                    flag_block = 1;
                    
                    for(int xx = 5 ; xx<=list_of_nebighor.size()-5;xx+=5){
                        int p = Integer.parseInt(list_of_nebighor.get(xx+4));
                        int p1 = Integer.parseInt(list_of_nebighor.get(xx+1));
                        if(ip_change.compareTo(list_of_nebighor.get(xx+3))==0 && Integer.parseInt(port_change) == p ){
                            if(ip_change.compareTo(list_of_nebighor.get(xx)) != 0 || p1!= Integer.parseInt(port_change) ){
                                // System.out.println("The change port is "  + list_of_nebighor.get(xx+1)) ;
                                //System.out.println("cost changed by " +port_received);
                                list_of_nebighor.set(xx+2, String.valueOf(1000));
                            }
                        }
                    }
                    if(!list_of_remote.isEmpty()){
                        for(int xx = 0 ; xx<=list_of_remote.size()-5;xx+=5){
                            int p = Integer.parseInt(list_of_remote.get(xx+4));
                            int p1 = Integer.parseInt(list_of_remote.get(xx+1));
                            if(ip_change.compareTo(list_of_remote.get(xx+3))==0 && Integer.parseInt(port_change) == p ){
                                if(ip_addr_received.compareTo(list_of_remote.get(xx)) != 0 || p1!= Integer.parseInt(port_change) ){
                                    list_of_remote.set(xx+2, String.valueOf(1000));
                                    // System.out.println("The change port is "  + list_of_remote.get(xx+1)) ;
                                    //System.out.println("cost changed by " +port_received);
                                }
                                
                            }
                            
                        }
                        
                    }
                    for (int d = 5; d<= list_of_nebighor.size()-5;d+=5){
                        String link_down = list_of_nebighor.get(d);
                        int port_down = Integer.parseInt(list_of_nebighor.get(d+1));
                        
                        if(link_down.compareTo(ip_change)==0 && port_down == Integer.parseInt(port_change)){
                            int co = 0;
                            while(co<=4){
                                co++;
                                list_of_nebighor.remove(d);
                            }
                            break;
                            
                        }
                    }
                    flag_work_done = 1;
                    flag_transport = 1;
                    if(flag_relog ==1){
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        flag_relog = 0;
                        flag_relog_confirm = 1;
                    }
                    
                    
                }
                if(flag_usercommand == 1){
                    String message = "LINKUP";
                    flag_block = 0;
                    list_of_nebighor.add(ip_change);
                    list_of_nebighor.add(port_change);
                    list_of_nebighor.add(String.valueOf(1000));
                    list_of_nebighor.add(ip_change);
                    list_of_nebighor.add(port_change);
                    for(int xx = 0;xx<= list_of_remote.size()-5; xx+=5){
                        String ipc = list_of_remote.get(xx);
                        String portc = list_of_remote.get(xx+1);
                        if(ipc.compareTo(ip_change) ==0 && portc.compareTo(port_change)==0){
                            int co = 0;
                            while(co<=4){
                                co++;
                                list_of_remote.remove(xx);
                            }
                            break;
                            
                        }
                        
                    }
                    InetAddress ip1 = InetAddress.getByName(ip_change);
                    DatagramPacket dp = new DatagramPacket(message.getBytes(),message.length(),ip1,Integer.parseInt(port_change));
                    socket.send(dp);
                    
                    
                }
                if (flag_usercommand == 3){
                    stop = 1;
                    thread.interrupt();
                    break;
                }
                
            }
            
            
        }
        System.out.println("LINK BREAK");
        System.exit(0);
        
    }
}
class listen extends Thread{
    private DatagramSocket socket;
    public listen(DatagramSocket sock) {
        socket = sock;
    }
    public void run() {
        byte [] incomingData = new byte[1024*1024];
        
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
        while (bfclient.stop == 0){
            try {
                for(int x = 0;x< incomingData.length; x++){
                    incomingData[x] = (byte) 0;
                }
                socket.receive(incomingPacket);
                while(bfclient.flag_work_done == 0){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                
                String update_information = new String(incomingPacket.getData());
                bfclient.list_of_received.clear();
                bfclient.port_received = incomingPacket.getPort();
                InetAddress ip_r = incomingPacket.getAddress();
                String str = ip_r.toString();
                bfclient.ip_addr_received = str.substring(1);
                if(bfclient.flag_block == 1){
                    String ip_block = bfclient.ip_change;
                    int port_block = Integer.parseInt(bfclient.port_change) ;
                    if(ip_block.compareTo(bfclient.ip_addr_received) == 0 && port_block == bfclient.port_received){
                        if(update_information.indexOf("LINKUP") <0 && bfclient.flag_relog_confirm ==0){
                            bfclient.flag_work_done = 1;
                            
                            continue;
                            
                        }else if (bfclient.flag_relog_confirm ==1){
                            update_information = "LINKUP";
                            bfclient.flag_block = 0;
                            bfclient.flag_relog_confirm = 0;
                        }else{
                            bfclient.flag_block = 0;
                        }
                        
                    }
                    
                    
                }
                
                
                //System.out.println("THE RECEIVED UPDATE_INFO IS" +update_information);
                if(update_information.indexOf("LINKDOWN")>=0){
                    bfclient.flag_link = 1;
                    bfclient.flag_work_done = 0;
                    continue;
                }
                if(update_information.indexOf("LINKUP")>=0){
                    bfclient.flag_link = 2;
                    bfclient.flag_work_done = 0;
                    continue;
                }
                
                
                
                //System.out.println("Coming from "+ bfcilent.port_received);
                String dividor = " ";
                int count = 0;
                for(int i =0;i<update_information.length();i++){
                    if(update_information.charAt(i) == ' '){
                        count ++;
                    }
                }
                int a,b;
                int c = -1;
                int index_new = 0;
                while (count>=2){
                    index_new = c;
                    update_information = update_information.substring(index_new+1);
                    //System.out.println(update_information);
                    a = update_information.indexOf(dividor);
                    b = update_information.indexOf(dividor,a+1);
                    c = update_information.indexOf(dividor,b+1);
                    bfclient.ip_addr = update_information.substring(0, a);
                    bfclient.port_update = update_information.substring(a+1,b);
                    if(count!=2){
                        bfclient.cost =update_information.substring(b+1,c);
                        
                    }else{
                        bfclient.cost =update_information.substring(b+1);
                    }
                    bfclient.list_of_received.add(bfclient.ip_addr);
                    bfclient.list_of_received.add(bfclient.port_update);
                    bfclient.list_of_received.add(bfclient.cost);
                    count-=3;
                }
                int counter = 0;
                for (int in = 0; in<=bfclient.list_of_nebighor.size()-5; in+=5){
                    String ip_check = bfclient.list_of_nebighor.get(in);
                    int port_check = Integer.parseInt(bfclient.list_of_nebighor.get(in+1));
                    if(ip_check.compareTo(bfclient.ip_addr_received) ==0 && port_check ==bfclient.port_received){
                        counter =1;
                        
                    }
                }
                if(counter ==0){
                    int ct = 1000;
                    for (int x = 0; x <= bfclient.list_of_received.size()-3; x+=3){
                        String aa =bfclient.list_of_received.get(x);
                        int bb = Integer.parseInt(bfclient.list_of_received.get(x+1));
                        if(aa.compareTo(bfclient.ip_host) ==0 && bb == bfclient.port){
                            ct = Integer.parseInt(bfclient.list_of_received.get(x+2));
                        }
                    }
                    bfclient.list_of_nebighor.add(bfclient.ip_addr_received);
                    bfclient.list_of_nebighor.add(String.valueOf(bfclient.port_received));
                    bfclient.list_of_nebighor.add(String.valueOf(ct));
                    bfclient.list_of_nebighor.add(bfclient.ip_addr_received);
                    bfclient.list_of_nebighor.add(String.valueOf(bfclient.port_received));
                    bfclient.list_of_recovery.add(bfclient.ip_addr_received);
                    bfclient.list_of_recovery.add(String.valueOf(bfclient.port_received));
                    bfclient.list_of_recovery.add(String.valueOf(ct));
                    bfclient.list_of_recovery.add(bfclient.ip_addr_received);
                    bfclient.list_of_recovery.add(String.valueOf(bfclient.port_received));
                    bfclient.flag_work_done = 1;
                    if(bfclient.list_of_remote.contains(bfclient.ip_addr_received) && bfclient.list_of_remote.contains(String.valueOf(bfclient.port_received))){
                        for (int x = 0; x <= bfclient.list_of_remote.size()-5; x+=5){
                            String aa =bfclient.list_of_remote.get(x);
                            int bb = Integer.parseInt(bfclient.list_of_remote.get(x+1));
                            if(aa.compareTo(bfclient.ip_addr_received) ==0 && bb == bfclient.port_received){
                                int co = 0;
                                while(co<=4){
                                    co++;
                                    bfclient.list_of_remote.remove(x);
                                }
                                break;
                                
                            }
                        }
                    }
                    continue;
                    
                    
                }
                
                bfclient.flag_update_information = 1;
                bfclient.flag_work_done = 0;
                
                
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }
        
        
    }
}
class Time_out extends Thread
{
    @Override
    public void run(){		
        while (true){
            if (bfclient.stop==1){
                break;
            }			
            while(!Thread.interrupted()){
                try {
                    Thread.sleep(bfclient.time_out_message*1000);
                    bfclient.time_out = 1;
                    //System.out.println("time out flag");
                } catch (InterruptedException e) {
                    break;
                }
            }	
        }
    }
}
class Update_information extends Thread{
    private DatagramSocket socket;
    public Update_information(DatagramSocket sock) {
        socket = sock;
    }
    public void run(){
        ArrayList<String>list_to_send = new ArrayList<String>();
        InetAddress ip = null;
        while(true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if(bfclient.flag_transport ==1|| bfclient.time_out==1){ 
                if(bfclient.flag_transport == 1){
                    bfclient.flag_transport =0;
                }else {
                    bfclient.time_out = 0;			   
                }
                bfclient.flag_transmit_done = 0;
                bfclient.flag_transport = 0;			 
                int dex = 5;		
                list_to_send.clear();
                list_to_send.addAll(bfclient.list_of_nebighor);
                list_to_send.addAll(bfclient.list_of_remote);
                for (dex= 5; dex<=bfclient.list_of_nebighor.size()-5;dex+=5){
                    String data_all_info=  null;
                    String ip_addr_sent = bfclient.list_of_nebighor.get(dex);
                    String port_sent = bfclient.list_of_nebighor.get(dex+1);
                    try {
                        ip = InetAddress.getByName(ip_addr_sent);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    for (int ct = 5; ct<=list_to_send.size()-5;ct+=5){
                        if(data_all_info != null){
                            data_all_info += list_to_send.get(ct)+ " ";
                            
                        }else{
                            data_all_info = list_to_send.get(ct)+ " ";
                        }
                        
                        data_all_info += list_to_send.get(ct+1) + " ";		        				  
                        if(ip_addr_sent.compareTo(list_to_send.get(ct+3))==0 && port_sent.compareTo(list_to_send.get(ct+4))==0){
                            if( ip_addr_sent.compareTo(list_to_send.get(ct))==0 && port_sent.compareTo(list_to_send.get(ct+1))==0){
                                data_all_info += list_to_send.get(ct+2) + " ";    						   
                            }else{
                                data_all_info += "INF" + " "; 					   
                            }
                        }else{
                            data_all_info += list_to_send.get(ct+2) + " ";
                        }
                    }
                    DatagramPacket dp = new DatagramPacket(data_all_info.getBytes(),data_all_info.length(),ip,Integer.parseInt(port_sent));
                    try {
                        socket.send(dp);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                }
                bfclient.flag_transmit_done = 1;
                
            }
            
            
        }
    }	
    
}
class User_input extends Thread{
    @Override
    public void run(){
        while(true){
            Scanner scan = new Scanner(System.in);
            String user_input = scan.nextLine();
            String str = " ";
            if (user_input.indexOf("LINKDOWN")>=0){
                bfclient.flag_userinput = 1;
                bfclient.flag_usercommand = 0;	 
                bfclient.ip_change = user_input.substring(user_input.indexOf(str)+1,user_input.lastIndexOf(str));
                bfclient.port_change = user_input.substring(user_input.lastIndexOf(str)+1);
                System.out.println(bfclient.ip_change);
                System.out.println(bfclient.port_change);		    	
                
            }else if(user_input.indexOf("LINKUP")>=0){	
                bfclient.flag_userinput = 1;
                bfclient.flag_usercommand = 1;
                bfclient.ip_change = user_input.substring(user_input.indexOf(str)+1,user_input.lastIndexOf	(str));
                bfclient.port_change = user_input.substring(user_input.lastIndexOf(str)+1);
                System.out.println(bfclient.ip_change);
                System.out.println(bfclient.port_change);		    		    	
            }else if (user_input.indexOf("SHOWRT")>=0){
                bfclient.flag_userinput = 1;
                bfclient.flag_usercommand = 2;
            }else if (user_input.indexOf("CLOSE")>=0){
                bfclient.flag_userinput =1;
                bfclient.flag_usercommand = 3;
            }else{
                System.out.println("User input not defined");
            }
            
        }
        
        
    }
    
}
class Timer extends Thread{		
    @Override
    public void run(){	
        long time_remain = 0;
        int time = 1000*3* bfclient.time_out_message;
        int flag =0;
        int counter = 0;	
        ArrayList<String>time_info = new ArrayList<String>();
        ArrayList<String>check = new ArrayList<String>();	   
        for (int i = 5; i <= bfclient.list_of_nebighor.size()-5; i+=5){
            time_info.add(bfclient.list_of_nebighor.get(i));
            time_info.add(bfclient.list_of_nebighor.get(i+1));		
            time_info.add(String.valueOf(1000*3* bfclient.time_out_message));
        }
        while (true){			
            if (bfclient.stop==1){
                break;
            }	
            Date date_start = new Date();
            time -=  time_remain;
            if(flag ==1){
                flag = 0;
                time = 3000* bfclient.time_out_message;	
                flag = 0;
            }
            if(time < 0){
                time = 0;
            }
            while(!Thread.interrupted()){	
                try {								
                    Thread.sleep(time);	
                    for(int a = 0; a<= time_info.size()-3;a+=3){
                        int value = Integer.parseInt(time_info.get(a+2));
                        if(value ==0){
                            if(check.contains(time_info.get(a)) && check.contains(time_info.get(a+1))){
                                bfclient.ip_change = time_info.get(a);
                                bfclient.port_change = time_info.get(a+1);
                                counter ++;
                                //System.out.println("Port :" + bfcilent.port_change +" is offline");
                                if(counter == 2){
                                    //System.out.println("ROUTER :" + bfclient.port_change +"Logout Successfully " );
                                    counter =0;
                                }
                                bfclient.flag_relog = 1;
                                bfclient.flag_userinput =1;
                                bfclient.flag_usercommand= 0;
                                break;	
                                
                            }
                            
                        }									
                    }
                    time_info.clear();
                    for (int i = 5; i <= bfclient.list_of_nebighor.size()-5; i+=5){
                        time_info.add(bfclient.list_of_nebighor.get(i));
                        time_info.add(bfclient.list_of_nebighor.get(i+1));		
                        time_info.add(String.valueOf(0));
                    }
                    flag = 1;
                    
                    break;
                    
                } catch (InterruptedException e) {
                    Date date_received = new Date();
                    time_remain = date_received.getTime() - date_start.getTime();	
                    //System.out.println("Interrupted by: "+ bfcilent.port_received );
                    for (int d = 0; d <= time_info.size()-3; d+=3){
                        String ip_arr = time_info.get(d);
                        String  port_arr = time_info.get(d+1);
                        if( ip_arr.compareTo(bfclient.ip_addr_received) == 0 && port_arr.compareTo(String.valueOf(bfclient.port_received)) == 0){
                            time_info.set(d+2, String.valueOf(3000* bfclient.time_out_message));
                            if(!check.contains(ip_arr)|| !check.contains(port_arr)){
                                check.add(ip_arr);
                                check.add(port_arr);
                                
                            }
                            
                            break;
                            
                        }
                        
                    }
                    break;
                }
                
                
            }	
        }
    }
    
}









