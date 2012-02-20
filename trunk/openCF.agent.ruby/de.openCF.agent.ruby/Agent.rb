require('socket')
require('json')
require('src/protocol')

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
    loop {
      response = @socket.read(4).unpack('N')
      #bytes = [response].pack('N')
      puts "#{response[0]} zu lesen"
      json = JSON.parse(@socket.read(response[0]))
      puts "gelesen #{json}"
      
      handlePacket(json)
    }
  end
  
  
  def handlePacket(json)
     typ = json[Protocol::Keys::TYPE]
     puts "typ: #{typ}"
  end
  
  
end



agent = Agent.new("localhost", 5678)
agent.connect

thread = Thread.new{agent.read()}


agent.send(Protocol::agent_hello("test", "v1_r", "windows"))


thread.join()

agent.disconnect