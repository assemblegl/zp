package zp;

import java.net.InetAddress;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

public class zp {
	private final static String DEFAULT_HOST = "192.168.41.7";
	private final static int DEFAULT_PORT = 2181;
	private final static int DEFAULT_TIMEOUT = 30000;
	private static String zkHost = DEFAULT_HOST;
	private static int zkPort = DEFAULT_PORT;
	private static int zkTimeout = DEFAULT_TIMEOUT;
	private static ZooKeeper zk = null;
	  private  Mutex mutex = new Mutex();
	  
	public zp() {
		
		try {
			zk = new ZooKeeper(zkHost + ":" + zkPort, zkTimeout, new Watcher() {

				// 监控所有被触发的事件
				public void process(WatchedEvent event) {
					System.out.println("已经触发了[" + event.getType() + "]事件！");
				}
			});
			
			//zk.create("/leader", "leader".getBytes(), Ids.OPEN_ACL_UNSAFE,
			//		   CreateMode.EPHEMERAL_SEQUENTIAL); 
			
			//zk.delete("/leader",-1);
			
			//Thread.sleep(2000);
			
			findLeader();
			 
			System.out.println(zk.getChildren("/",true)); 
			
			System.out.println(new String(zk.getData("/leader", true, null))); 
			Thread.sleep(5000);
			
			zk.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}
	
	void findLeader() throws InterruptedException { 
        byte[] leader = null; 
        try { 
            leader = zk.getData("/leader", true, null); 
        } catch (Exception e) { 
           System.out.println(e.getMessage()); 
        } 
        if (leader != null) { 
        	System.out.println("following"); 
        } else { 
            String newLeader = null; 
            try { 
                byte[] localhost = InetAddress.getLocalHost().getAddress(); 
                newLeader = zk.create("/leader", localhost, 
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL); 
            } catch (Exception e) { 
            	System.out.println(e.getMessage());
            } 
            if (newLeader != null) { 
            	System.out.println("leading"); 
            } else { 
                mutex.wait(); 
            } 
        } 
    }

}
