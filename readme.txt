How to run:
The code can be invoked by: typing make, then type java, then type java bfclient localport timeout [ipaddress1 port1 weight1 …].

Please note:

Please allow some time for convergence after entering linkdonw. When one router is terminated, please wait to display final results

When enter the LINKDOWN/LINKUP command, please split ip address and port number by space

The code can be used in dynamic networks by allowing routers join in and leave.

Bugs:
No known Bugs.

Protocol:
The inter-communication among router is done by sending String information via UDP. The sender combine the ip port and cost of each router to its neighbor and each information is split by space. The sample output string is like ip1 port 1 cost1 ip2 port2 cost2….. Because it is combined into a string, and I use java. I can use getbyte() to become it into byte stream and send it. The receiver can get the full string by use getdata() built-in function.

When its neighbor receive it, it will get each information by checking the index of space. All the information will be stored into an Array list. Because each router’s information is consist of ip port and cost, the receiver can easily get each router’s information sent by its neighbor like the following code    
for(int i = 0;i<=list_of_received.size()-3;i+=3). 

The protocol use poison reverse. If the router A have to get router B via router C, when A send string message to C, it will put string INF instead of actual cost to B. C will check the cost and if it is INF, it will ignore it and continue to deal with next router’s information.  

The table consist of two parts, one array list contains neighbor information and other one contain non-neighbor routers. This is much easier to manage data like transport and allow new routers join in.


