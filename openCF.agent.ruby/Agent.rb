require('socket')
require('json')
require('protocol')

class Agent
  
  def initialize(host, port)
    @hostname = host
    @port = port
  end
  
  
  def connect()
    @socket = TCPSocket.open(@hostname, @port)
  end
  
  
  def disconnect()
    @socket.close
  end
  
  
  def send(valueHash)
    json = JSON.generate valueHash
    
    puts "writing bytes: #{json.bytesize}"
    puts "writing data:  #{json}"
    
    @socket.puts([json.bytesize + 1].pack('N'))
    @socket.puts(json)
    @socket.flush()
  end
  
  
  def read()
    puts "read"
    loop {
      puts "reading"
      response = @socket.read(4).unpack('N')
      #bytes = [response].pack('N')
      puts "#{response[0]} zu lesen"
      puts JSON.parse(@socket.read(response[0]))
      sleep(2)
    }
  end
  
  
end



agent = Agent.new("localhost", 5678)
agent.connect

thread = Thread.new{agent.read()}


agent.send(Protocol::agent_hello("test", "v1_r", "windows"))


thread.join()

agent.disconnect