require('socket')
require('json')
require('logger')
require('yaml')
require('fileutils')
require('net/http')
require('src/protocol')


$logger = Logger.new(STDOUT)
$logger.level = Logger::Severity::DEBUG


$config = YAML.load_file(File.absolute_path('config/agent.ruby.yaml'))
$os = $config['os']
$version = $config['version']
$server = $config['server']
$port = $config['port']
$heartbeat_sleep = $config['heartbeat']
$agent_id = $config['hostname']

$logger.info("os=#{$port}")
$logger.info("version=#{$version}")
$logger.info("server=#{$server}")
$logger.info("port=#{$port}")

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
    @mutex = Mutex.new
    @jobs = Hash.new
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
    
    @mutex.synchronize do    
      @socket.write(Protocol::htonl(json.bytesize))
      @socket.write(json)
    end
    
    @socket.flush()
  end
  
  
  def read()
    begin
      loop {
        response = Protocol::ntohl(@socket.read(4))
        #bytes = [response].pack('N')
        $logger.debug "#{response} zu lesen"
        json = JSON.parse(@socket.read(response))
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
      Thread.abort_on_exception = true
      thread = Thread.new {
        $logger.info("job thread started")
        $logger.debug(@jobs)
        @jobs[id] = Hash.new
        $logger.debug(@jobs[id])
        send(Protocol::automation_status(id, Protocol::Status::PREPARING, "prepareing"))
        ret = loadJob(json)
        if (ret == true)
          send(Protocol::automation_status(id, Protocol::Status::PREPARED, "prepared"))
        else
          send(Protocol::automation_status(id, Protocol::Status::PREPARE_FAILED, "prepare_failed"))
        end
        send(Protocol::automation_status(id, Protocol::Status::STARTED, "started"))
        Dir.chdir("executing/#{id}") do
          IO.popen("#{@jobs[id]['descriptor']['command']}", "r+")  do |pipe|
            pipe.sync = true
            while str = pipe.gets
                puts str
                send (Protocol::automation_status(id, Protocol::Status::TALKING, str))
              end
          end
        end
        removeJobDir(id)
        send(Protocol::automation_status(id, Protocol::Status::FINISHED, "finished"))
      }
    when Protocol::Action::STOP
      $logger.info "automation stop action"
    end
    
  end
  
  
  def createJobDir(id)
    begin
      Dir.chdir("executing") do
        puts Dir.pwd
        Dir::mkdir("#{id}")
      end
      return true
    rescue
      $logger.warn("job dir already exists")
      return false
    end
  end
  
  
  def removeJobDir(id)
    FileUtils.rm_rf("executing/#{id}")
  end
  
  
  def loadJob(json)
    url = json[Protocol::Keys::REPOSTITORY_URL]
    descriptor = json[Protocol::Keys::AUTOMATION_DESCRIPTOR]
    id = json[Protocol::Keys::AUTOMATION_ID]

    descriptorUri = URI(url + descriptor)
    
    $logger.debug("url: #{url}")
    $logger.debug("descriptor: #{descriptor}")
    $logger.debug("id: #{id}")
    
    $logger.debug("descriptorUri: #{descriptorUri}")
    
    resp = Net::HTTP.get(descriptorUri)
    
    $logger.debug("loaded data: #{resp}")
    
    descriptor = JSON.parse(resp)
    
    $logger.info("descriptor loaded: #{descriptor}")
    
    @jobs[id]['descriptor'] = descriptor
    
    uri = URI(url + descriptor['file'])
      
    $logger.debug("job uri: #{uri}")
    
    Net::HTTP.start(uri.host, uri.port) do |http|
      request = Net::HTTP::Get.new uri.request_uri
    
      createJobDir(id)
      
      http.request request do |response|
      case response
        when Net::HTTPOK
          Dir.chdir "executing/#{id}" do
            open "#{descriptor['file']}", 'w' do |io|
              response.read_body do |chunk|
                io.write chunk
              end
            end
          end
          return true
        when Net::HTTPNotFound
          $logger.warn("job not found")
          return false
        end
      end
    end
  end
  
  
  def handleUnexpactedType(json)
    $logger.warn "unexpected type: #{json}"
  end
  
  
  def to_s
    "Agent [hostname=#{@hostname}, port=#{@port}]"
  end
  
  
end

#{type:13, agent_id:["ruby_agent"], automation_action:"start","repository_url":"http://localhost:8080/jobs/", "automation_descriptor":"test.json"}
$hn = Socket.gethostname
if ($agent_id != nil)
  $hn = $agent_id
end
$logger.info("hostname=#{$hn}")


agent = Agent.new($server, $port)
agent.connect

thread = Thread.new{agent.read()}

agent.send(Protocol::agent_hello($hn, $version, $os))
t = Thread.new begin
  loop {
    $logger.debug("sleeping #{$heartbeat_sleep * 60} seconds")
    sleep($heartbeat_sleep * 60);
    agent.send(Protocol::heartbeat());
  }
end

thread.join()

agent.disconnect()
