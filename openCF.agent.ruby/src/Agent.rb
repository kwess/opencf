require('socket')
require('json')
require('logger')
require('src/protocol')


$logger = Logger.new(STDOUT)
$logger.level = Logger::Severity::DEBUG

class Agent
  
  private
    @hostname = "localhost"
    @port = 6789
    @socket = nil
    @registered = false
  
  public
  
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
    
    $logger.debug "writing bytes: #{json.bytesize}"
    $logger.debug "writing data:  #{json}"
    
    @socket.write([json.bytesize + 1].pack('N'))
    @socket.puts(json)
    
    $logger.debug "written bytes (BIG_ENDIAN) #{[json.bytesize + 1].pack('N')}"
    
    @socket.flush()
  end
  
  
  def read()
    begin
      loop {
        response = @socket.read(4).unpack('N')
        #bytes = [response].pack('N')
        $logger.debug "#{response[0]} zu lesen"
        json = JSON.parse(@socket.read(response[0]))
        $logger.debug "gelesen #{json}"
        
        handlePacket(json)
      }
    rescue IOError => e
      $logger.error "io error"
    end
  end
  
  
  def handlePacket(json)
     type = json[Protocol::Keys::TYPE]
     $logger.debug "type: #{type}"
     
     case type
     when Protocol::Type::AGENT_HELLO_RESPONSE
       $logger.debug "agent_hello_response"
       handleHelloResponse(json)
     when Protocol::Type::AUTOMATION_CONTROL
       $logger.debug "automation_control"
       handleAutomationControl(json)
     else
       $logger.debug "unexpected packet recieved"
       handleUnexpectedType(json)
     end
  end
  
  
  def handleHelloResponse(json)
    successfull = json[Protocol::Keys::SUCCESSFULL]
    return_code = json[Protocol::Keys::RETURN_CODE]
    message     = json[Protocol::Keys::MESSAGE]
    
    $logger.debug "message:     #{message}"
    $logger.debug "return_code: #{return_code}"
    $logger.debug "successfull: #{successfull}"
    
    if successfull
      $logger.info "successfully registered to server"
      @registered = true
    else
      $logger.warn "registration failed"
      disconnect()
    end
    
  end
  
  
  def handleAutomationControl(json)
    
    action = json[Protocol::Keys::AUTOMATION_ACTION]
    id = json[Protocol::Keys::AUTOMATION_ID]
    $logger.debug "action: #{action}"
    
    case action
    when Protocol::Action::START
      $logger.info "automation start action"
      thread = Thread.new {
        send(Protocol::automation_status(id, Protocol::Status::STARTED, "started"))
        IO.popen("D:/test.bat", "r+")  do |pipe|
          pipe.sync = true
          while str = pipe.gets
              puts str
              send (Protocol::automation_status(id, Protocol::Status::TALKING, str))
            end
        end
        send(Protocol::automation_status(id, Protocol::Status::FINISHED, "finished"))
      }
    when Protocol::Action::STOP
      $logger.info "automation stop action"
    end
    
  end
  
  
  def handleUnexpactedType(json)
    $logger.warn "unexpected type: #{json}"
  end
  
  
  def to_s
    "Agent [hostname=#{@hostname}, port=#{@port}]"
  end
  
  
end



agent = Agent.new("localhost", 5678)
agent.connect

thread = Thread.new{agent.read()}


agent.send(Protocol::agent_hello("test2", "r_v1", "windows"))
  

thread.join()

agent.disconnect()
