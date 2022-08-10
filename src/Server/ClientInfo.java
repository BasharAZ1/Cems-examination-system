package Server;

/**
 * Assistance class for the MyServerBoundary for maintaining the GUI table
 * 
 * @author Ayala Cohen
 *
 */
public class ClientInfo implements Comparable<ClientInfo> {
	
	String ClientIP, Host, Status, Username="";

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public ClientInfo(String clientIP, String host, String username, String status) {
		super();
		ClientIP = clientIP;
		Host = host;
		Status = status;
		Username=username;
	}

	public String getClientIP() {
		return ClientIP;
	}

	public void setClientIP(String clientIP) {
		ClientIP = clientIP;
	}

	public String getHost() {
		return Host;
	}

	public void setHost(String host) {
		Host = host;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ClientIP == null) ? 0 : ClientIP.hashCode());
		result = prime * result + ((Host == null) ? 0 : Host.hashCode());
		result = prime * result + ((Username == null) ? 0 : Username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientInfo other = (ClientInfo) obj;
		if (ClientIP == null) {
			if (other.ClientIP != null)
				return false;
		} else if (!ClientIP.equals(other.ClientIP))
			return false;
		if (Host == null) {
			if (other.Host != null)
				return false;
		} else if (!Host.equals(other.Host))
			return false;
		return true;
	}

	@Override
	public int compareTo(ClientInfo client) {
		return this.Status.compareTo(client.Status);
	}
}