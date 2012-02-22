module Protocol
  
  module Type
    INVALID               = -1
    AGENT_HELLO           = 1
    AGENT_HELLO_RESPONSE  = 2
    AGENT_HEARTBEAT       = 0
    AUTOMATION_CONTROL    = 13
    AUTOMATION_STATUS   = 20
  end
  
  module Keys
    TYPE                = "type"
    SUCCESSFULL         = "successfull"
    RETURN_CODE         = "return_code"
    MESSAGE             = "message"
    AGENT_ID            = "agent_id"
    AGENT_VERSION       = "agent_version"
    AGENT_PLATTFORM     = "agent_plattform"
    AGENT_ENCODING      = "agent_encoding"
    AUTOMATION_STATUS   = "automation_status"
    AUTOMATION_ACTION   = "automation_action"
    AUTOMATION_ID       = "automation_id"
    AUTOMATION_MESSAGE  = "automation_message"
  end
  
  module Action
    START = "start"
    STOP  = "stop"
  end
  
  module Status
    STARTED   = "started"
    STOPED    = "stoped"
    FINISHED  = "finished"
    TALKING   = "talking"
  end
  
  def Protocol.agent_hello(id, version, plattform)
    agent_hello = Hash.new
    agent_hello[Protocol::Keys::TYPE] = Protocol::Type::AGENT_HELLO
    agent_hello[Protocol::Keys::AGENT_ID] = id
    agent_hello[Protocol::Keys::AGENT_VERSION] = version
    agent_hello[Protocol::Keys::AGENT_PLATTFORM] = plattform
    agent_hello[Protocol::Keys::AGENT_ENCODING] = "JSON"
    return agent_hello
  end
  
  def Protocol.automation_status(id, status, message)
    automation_status = Hash.new
    automation_status[Protocol::Keys::TYPE] = Protocol::Type::AUTOMATION_STATUS
    automation_status[Protocol::Keys::AUTOMATION_MESSAGE] = message
    automation_status[Protocol::Keys::AUTOMATION_ID] = id
    automation_status[Protocol::Keys::AUTOMATION_STATUS] = status
    return automation_status
  end
  
end